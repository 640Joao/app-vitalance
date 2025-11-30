import { useState, useEffect } from 'react';
import { Activity, Goal } from '../models/types';
import api from '../services/api';


export const useHomeViewModel = () => {
  const [activities, setActivities] = useState<Activity[]>([]);
  const [goals, setGoals] = useState<Goal[]>([]);
  const [isLoading, setIsLoading] = useState(false); // Adicionei estado de carregamento
  const [weeklyStats, setWeeklyStats] = useState({
    totalDistance: 0,
    totalRuns: 0,
    totalTime: 0,
    averagePace: 0
  });

  // Função auxiliar para calcular estatísticas (reutilizada)
  const calculateStats = (currentActivities: Activity[]) => {
    const weekAgo = new Date(Date.now() - 7 * 86400000);
    const weeklyActivities = currentActivities.filter(activity => new Date(activity.date) >= weekAgo);
    
    const totalDistance = weeklyActivities.reduce((sum, activity) => sum + activity.distance, 0);
    const totalRuns = weeklyActivities.length;
    const totalTime = weeklyActivities.reduce((sum, activity) => sum + activity.duration, 0);
    const averagePace = weeklyActivities.length > 0 
      ? weeklyActivities.reduce((sum, activity) => sum + activity.pace, 0) / weeklyActivities.length 
      : 0;

    setWeeklyStats({
      totalDistance,
      totalRuns,
      totalTime,
      averagePace
    });

    return { totalDistance, totalRuns }; // Retorna para atualizar metas
  };

  // Função para carregar dados do Backend
  const fetchData = async () => {
    setIsLoading(true);
    try {
      console.log("Buscando atividades no backend...");
      
      // 1. Busca as atividades do servidor
      // O endpoint '/activities' deve existir no seu Controller Kotlin
      const response = await api.get('/activities');
      
      // O Backend devolve datas como String (ISO), precisamos converter para Date
      const realActivities = response.data.map((item: any) => ({
        ...item,
        date: new Date(item.date)
      }));

      // Ordena da mais recente para a mais antiga
      realActivities.sort((a: Activity, b: Activity) => b.date.getTime() - a.date.getTime());

      setActivities(realActivities);

      // 2. Calcula as estatísticas com os dados reais
      const stats = calculateStats(realActivities);

      // 3. Define as Metas (Aqui mantive estático, mas atualizando o progresso 'current')
      const updatedGoals: Goal[] = [
        {
          id: '1',
          type: 'weekly',
          target: 20,
          current: stats.totalDistance, // Atualiza com o valor real
          unit: 'km',
          title: 'Meta Semanal de Distância'
        },
        {
          id: '2',
          type: 'weekly',
          target: 4,
          current: stats.totalRuns, // Atualiza com o valor real
          unit: 'runs',
          title: 'Meta Semanal de Corridas'
        }
      ];
      setGoals(updatedGoals);

    } catch (error) {
      console.error("Erro ao buscar dados da Home:", error);
      // Se der erro, não quebramos o app, apenas deixamos vazio ou mostramos alerta
    } finally {
      setIsLoading(false);
    }
  };

  // Carrega os dados quando a tela abre
  useEffect(() => {
    fetchData();
  }, []);

  // Função para Adicionar Nova Atividade (Salva no Backend)
  const addActivity = async (activityData: Omit<Activity, 'id'>) => {
    try {
      // Envia para o backend salvar
      const response = await api.post('/activities', activityData);
      
      console.log("Atividade salva:", response.data);

      // O backend devolve a atividade salva com ID
      const newActivity = {
        ...response.data,
        date: new Date(response.data.date)
      };

      // Atualiza a lista localmente (para não precisar buscar tudo de novo)
      setActivities(prev => {
        const updatedList = [newActivity, ...prev];
        // Recalcula estatísticas e metas
        const stats = calculateStats(updatedList);
        
        setGoals(prevGoals => prevGoals.map(goal => {
          if (goal.unit === 'km') return { ...goal, current: stats.totalDistance };
          if (goal.unit === 'runs') return { ...goal, current: stats.totalRuns };
          return goal;
        }));
        
        return updatedList;
      });

    } catch (error) {
      console.error("Erro ao salvar atividade:", error);
      alert("Erro ao salvar. Verifique a conexão.");
    }
  };

  const getChartData = () => {
    const completedGoal = goals.find(g => g.unit === 'km');
    if (!completedGoal) return [];

    const completed = completedGoal.current;
    const remaining = Math.max(0, completedGoal.target - completed);
    
    return [
      { name: 'Concluído', value: completed, fill: '#22c55e' },
      { name: 'Restante', value: remaining, fill: '#e5e7eb' }
    ];
  };

  return {
    activities,
    goals,
    weeklyStats,
    isLoading, // Pode usar isso para mostrar um "spinner" na tela
    addActivity,
    getChartData,
    refresh: fetchData // Exporta a função para puxar dados (ex: puxar para atualizar)
  };
};