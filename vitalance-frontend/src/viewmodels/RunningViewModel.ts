import { useState, useEffect, useRef } from 'react';
import { RunningSession, Activity } from '../models/types';

export const useRunningViewModel = () => {
  const [session, setSession] = useState<RunningSession>({
    isRunning: false,
    isPaused: false,
    startTime: null,
    elapsedTime: 0,
    distance: 0,
    currentPace: 0
  });

  const intervalRef = useRef<NodeJS.Timeout | null>(null);

  // Efeito do Cronômetro
  useEffect(() => {
    if (session.isRunning && !session.isPaused) {
      intervalRef.current = setInterval(() => {
        setSession(prev => {
          const newElapsedTime = prev.elapsedTime + 1;
          
          // --- SIMULAÇÃO PARA TESTE DE APK ---
          // Como você vai testar sentado no PC, mantivemos esse incremento falso.
          // Ele simula uma corrida a ~5:30 min/km (0.003 km por segundo)
          const newDistance = prev.distance + 0.003; 
          
          // Proteção contra divisão por zero no cálculo do Pace
          const currentPace = newDistance > 0 ? (newElapsedTime / 60) / newDistance : 0;
          
          return {
            ...prev,
            elapsedTime: newElapsedTime,
            distance: newDistance,
            currentPace: currentPace
          };
        });
      }, 1000);
    } else {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
        intervalRef.current = null;
      }
    }

    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, [session.isRunning, session.isPaused]);

  const startRun = () => {
    setSession(prev => ({
      ...prev,
      isRunning: true,
      isPaused: false,
      startTime: prev.startTime || Date.now()
    }));
  };

  const pauseRun = () => {
    setSession(prev => ({
      ...prev,
      isPaused: true
    }));
  };

  const resumeRun = () => {
    setSession(prev => ({
      ...prev,
      isPaused: false
    }));
  };

  // Essa função prepara os dados para serem salvos
  const stopRun = (): Activity => {
    const activity: Activity = {
      id: Date.now().toString(), // ID temporário (o backend vai gerar o real)
      type: 'running',
      distance: parseFloat(session.distance.toFixed(2)), // Arredonda para 2 casas
      duration: session.elapsedTime,
      date: new Date(),
      calories: Math.round(session.distance * 60), // Cálculo estimado de calorias
      pace: session.currentPace
    };

    // Reseta o estado
    setSession({
      isRunning: false,
      isPaused: false,
      startTime: null,
      elapsedTime: 0,
      distance: 0,
      currentPace: 0
    });

    return activity; // Retorna a atividade para a tela salvar no Backend
  };

  const formatTime = (seconds: number): string => {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const formatDistance = (km: number): string => {
    return km.toFixed(2);
  };

  return {
    session,
    startRun,
    pauseRun,
    resumeRun,
    stopRun,
    formatTime,
    formatDistance
  };
};