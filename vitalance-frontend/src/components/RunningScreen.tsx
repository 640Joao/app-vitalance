import React, { useState, useEffect, useRef } from 'react';
import { Card, CardContent } from './ui/card';
import { Button } from './ui/button';
import { Play, Pause, Square, ArrowLeft, MapPin } from 'lucide-react';

// --- FUNÇÕES AUXILIARES (MATEMÁTICA) ---

// Calcula distância entre dois pontos geográficos (Fórmula de Haversine)
const calculateDistance = (lat1: number, lon1: number, lat2: number, lon2: number) => {
  const R = 6371; // Raio da Terra em km
  const dLat = (lat2 - lat1) * (Math.PI / 180);
  const dLon = (lon2 - lon1) * (Math.PI / 180);
  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(lat1 * (Math.PI / 180)) * Math.cos(lat2 * (Math.PI / 180)) *
    Math.sin(dLon / 2) * Math.sin(dLon / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c; // Retorna em km
};

// Formata segundos para HH:MM:SS
const formatTime = (totalSeconds: number) => {
  const hours = Math.floor(totalSeconds / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = totalSeconds % 60;
  return `${hours.toString().padStart(2, '0')}:${minutes
    .toString()
    .padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
};

interface RunningScreenProps {
  onBack: () => void;
}

export function RunningScreen({ onBack }: RunningScreenProps) {
  // --- ESTADOS ---
  const [status, setStatus] = useState<'idle' | 'running' | 'paused'>('idle');
  const [elapsedTime, setElapsedTime] = useState(0);
  const [distance, setDistance] = useState(0);
  const [gpsError, setGpsError] = useState<string | null>(null);

  // --- REFS (Para valores que não precisam renderizar a tela toda hora) ---
  const watchId = useRef<number | null>(null);
  const lastPosition = useRef<{ lat: number; lon: number } | null>(null);
  const timerRef = useRef<NodeJS.Timeout | null>(null);

  // --- 1. CRONÔMETRO ---
  useEffect(() => {
    if (status === 'running') {
      timerRef.current = setInterval(() => {
        setElapsedTime((prev) => prev + 1);
      }, 1000);
    } else {
      if (timerRef.current) clearInterval(timerRef.current);
    }
    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
    };
  }, [status]);

  // --- 2. LÓGICA DO GPS ---
  const startGPS = () => {
    if (!('geolocation' in navigator)) {
      setGpsError('GPS não suportado.');
      return;
    }

    watchId.current = navigator.geolocation.watchPosition(
      (position) => {
        const { latitude, longitude } = position.coords;

        if (lastPosition.current) {
          const dist = calculateDistance(
            lastPosition.current.lat,
            lastPosition.current.lon,
            latitude,
            longitude
          );

          // Filtro: ignora movimentos menores que 3 metros para evitar "pulo" do GPS parado
          if (dist > 0.003) {
            setDistance((prev) => prev + dist);
            lastPosition.current = { lat: latitude, lon: longitude };
          }
        } else {
          // Primeira posição
          lastPosition.current = { lat: latitude, lon: longitude };
        }
      },
      (error) => {
        console.error('Erro GPS:', error);
        setGpsError('Sinal fraco.');
      },
      {
        enableHighAccuracy: true, // CRUCIAL para corrida
        timeout: 10000,
        maximumAge: 0,
      }
    );
  };

  const stopGPS = () => {
    if (watchId.current !== null) {
      navigator.geolocation.clearWatch(watchId.current);
      watchId.current = null;
    }
  };

  // --- CÁLCULOS DE EXIBIÇÃO ---
  const getCurrentPace = (): string => {
    if (distance === 0 || elapsedTime === 0) return '--:--';
    const paceInMinutes = (elapsedTime / 60) / distance;
    const minutes = Math.floor(paceInMinutes);
    const seconds = Math.floor((paceInMinutes - minutes) * 60);
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  };

  const getCalories = (): number => {
    // Estimativa: ~60 calorias por km
    return Math.round(distance * 60);
  };

  // --- AÇÕES DOS BOTÕES ---
  const handleStart = () => {
    setStatus('running');
    setGpsError(null);
    startGPS();
  };

  const handlePause = () => {
    setStatus('paused');
    stopGPS();
  };

  const handleResume = () => {
    setStatus('running');
    startGPS();
  };

  const handleStop = async () => {
    setStatus('idle');
    stopGPS();

    // --- ENVIA PARA O BACKEND JAVA ---
    const activityData = {
      type: "RUNNING", // Enum do Java
      distanceKm: distance,
      durationMinutes: Math.ceil(elapsedTime / 60),
      date: new Date().toISOString()
    };

    try {
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8082/api/activities', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}` // Token JWT
        },
        body: JSON.stringify(activityData)
      });

      if (response.ok) {
        alert("Corrida salva com sucesso! 🏃‍♂️💨");
        onBack();
      } else {
        alert("Erro ao salvar. Tente novamente.");
      }
    } catch (error) {
      console.error(error);
      alert("Erro de conexão com o servidor.");
    }
  };

  // --- RENDERIZAÇÃO (O VISUAL ORIGINAL) ---
  return (
    <div className="min-h-screen bg-gradient-to-br from-green-50 via-white to-emerald-50">
      {/* Header */}
      <div className="bg-gradient-to-r from-green-500 to-emerald-500 text-white p-4">
        <div className="flex items-center justify-between">
          <Button 
            variant="ghost" 
            size="sm" 
            onClick={() => { stopGPS(); onBack(); }}
            className="text-white hover:bg-white/20 p-2"
          >
            <ArrowLeft className="w-5 h-5" />
          </Button>
          <h1 className="text-lg font-semibold">Corrida</h1>
          <div className="w-9" /> {/* Spacer for center alignment */}
        </div>
      </div>

      <div className="px-4 py-6 space-y-6">
        
        {/* Aviso de GPS se houver erro */}
        {gpsError && (
            <div className="bg-red-100 text-red-600 p-2 text-center rounded-md text-sm">
                ⚠️ {gpsError}
            </div>
        )}

        {/* Timer Display */}
        <Card className="shadow-lg border-none">
          <CardContent className="text-center py-8">
            <div className="space-y-2">
              <p className="text-6xl font-mono font-bold text-gray-800 tracking-widest">
                {formatTime(elapsedTime)}
              </p>
              <p className="text-muted-foreground uppercase tracking-widest text-sm">Tempo</p>
            </div>
          </CardContent>
        </Card>

        {/* Distance and Pace */}
        <div className="grid grid-cols-2 gap-4">
          <Card className="shadow-md border-none">
            <CardContent className="text-center py-6">
              <p className="text-3xl font-bold text-gray-800">
                {distance.toFixed(2)}
              </p>
              <p className="text-xs text-muted-foreground uppercase mt-1">km</p>
            </CardContent>
          </Card>

          <Card className="shadow-md border-none">
            <CardContent className="text-center py-6">
              <p className="text-3xl font-bold text-gray-800">
                {getCurrentPace()}
              </p>
              <p className="text-xs text-muted-foreground uppercase mt-1">min/km</p>
            </CardContent>
          </Card>
        </div>

        {/* Calories */}
        <Card className="shadow-md border-none bg-white/80 backdrop-blur">
          <CardContent className="flex items-center justify-between p-4">
            <div>
              <p className="font-medium text-gray-900">Calorias Queimadas</p>
              <p className="text-xs text-muted-foreground">Estimativa</p>
            </div>
            <p className="text-xl font-bold text-gray-800">
              {getCalories()} kcal
            </p>
          </CardContent>
        </Card>

        {/* Control Buttons */}
        <div className="flex justify-center gap-6 pt-8 pb-8">
          {status === 'idle' ? (
            <Button
              onClick={handleStart}
              size="lg"
              className="w-20 h-20 rounded-full bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600 shadow-xl transition-all hover:scale-105 active:scale-95"
            >
              <Play className="w-8 h-8 fill-current" />
            </Button>
          ) : (
            <>
              <Button
                onClick={status === 'paused' ? handleResume : handlePause}
                size="lg"
                className="w-20 h-20 rounded-full bg-yellow-500 hover:bg-yellow-600 shadow-xl transition-all hover:scale-105 active:scale-95"
              >
                {status === 'paused' ? (
                  <Play className="w-8 h-8 fill-current" />
                ) : (
                  <Pause className="w-8 h-8 fill-current" />
                )}
              </Button>
              
              <Button
                onClick={handleStop}
                size="lg"
                variant="destructive"
                className="w-20 h-20 rounded-full shadow-xl transition-all hover:scale-105 active:scale-95"
              >
                <Square className="w-8 h-8 fill-current" />
              </Button>
            </>
          )}
        </div>

        {/* Status Text */}
        <div className="text-center pb-6">
          {status === 'idle' && (
            <p className="text-muted-foreground animate-pulse">Toque em play para começar</p>
          )}
          {status === 'running' && (
            <div className="flex items-center justify-center gap-2 text-green-600 font-medium">
                <MapPin className="w-4 h-4 animate-bounce" />
                <p>Monitorando GPS...</p>
            </div>
          )}
          {status === 'paused' && (
            <p className="text-yellow-600 font-medium">Corrida pausada</p>
          )}
        </div>
      </div>
    </div>
  );
}