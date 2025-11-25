import React, { useEffect, useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';
import { PieChart, Pie, Cell, ResponsiveContainer } from 'recharts';
import { Play, Target, Trophy, TrendingUp, Clock, MapPin, Settings, Flame, Award, Loader2 } from 'lucide-react';
import { User } from '../models/types';
import { HamburgerMenu } from './HamburgerMenu';

interface HomeScreenProps {
  user: User;
  onStartRun: () => void;
  onNavigate: (screen: string) => void;
  onLogout: () => void;
}

export function HomeScreen({ user, onStartRun, onNavigate, onLogout }: HomeScreenProps) {
  const [loading, setLoading] = useState(true);
  
  const [stats, setStats] = useState({
    userName: '',
    weeklyDistance: 0,
    weeklyRuns: 0,
    weeklyTime: 0,
    weeklyPace: 0,
    weeklyGoalProgress: 0,
    currentStreak: 0,
    medals: 0
  });

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const token = localStorage.getItem('token');
        const response = await fetch('http://localhost:8082/api/dashboard', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (response.ok) {
            const data = await response.json();
            
            const weeklyDist = data.lastSevenDaysSummary.totalDistanceKm;
            const weeklyTimeMinutes = data.lastSevenDaysSummary.totalDurationMinutes;
            const pace = weeklyDist > 0 ? weeklyTimeMinutes / weeklyDist : 0;
            const calculatedMedals = Math.floor(weeklyDist / 5);

            setStats({
                userName: data.userName,
                currentStreak: data.currentStreak,
                weeklyDistance: weeklyDist,
                weeklyRuns: data.lastSevenDaysSummary.totalActivities,
                weeklyTime: weeklyTimeMinutes * 60,
                weeklyPace: pace, 
                weeklyGoalProgress: (weeklyDist / 20) * 100,
                medals: data.achievementsCount // Usa o valor real do backend
            });
        }
      } catch (error) {
        console.error("Erro dashboard", error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboard();
  }, []);

  const formatTime = (seconds: number) => {
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    return h > 0 ? `${h}h ${m}m` : `${m}m`;
  };

  const formatPace = (pace: number) => {
    const m = Math.floor(pace);
    const s = Math.floor((pace - m) * 60);
    return `${m}:${s.toString().padStart(2, '0')} /km`;
  };

  const chartData = [
    { name: 'Concluído', value: stats.weeklyDistance, fill: '#10b981' },
    { name: 'Restante', value: Math.max(0, 20 - stats.weeklyDistance), fill: '#e5e7eb' }
  ];

  if (loading) return <div className="h-screen flex items-center justify-center"><Loader2 className="animate-spin text-blue-500"/></div>;

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-cyan-50">
      {/* Header */}
      <div className="bg-gradient-to-r from-blue-500 to-cyan-500 text-white">
        <div className="flex items-center justify-between p-4">
          <HamburgerMenu user={user} onNavigate={onNavigate} onStartRun={onStartRun} onLogout={onLogout} />
          <div className="w-12 h-12 bg-white/20 rounded-full flex items-center justify-center">
            <Trophy className="w-6 h-6" />
          </div>
          <Button variant="ghost" size="icon" onClick={() => onNavigate('settings')} className="text-white hover:bg-white/20">
            <Settings className="h-5 w-5" />
          </Button>
        </div>
        <div className="text-center pb-8">
          <h1 className="text-xl font-semibold mb-1">Olá, {stats.userName}!</h1>
          <p className="text-blue-100">Pronto para mais uma corrida?</p>
        </div>
      </div>

      <div className="px-4 -mt-4 pb-4 space-y-4">
        <div className="grid grid-cols-2 gap-3">
          
          {/* Card Ofensiva */}
          <Card className="shadow-lg bg-gradient-to-br from-orange-500 to-red-500 text-white border-none">
            <CardContent className="p-4 text-center">
              <div className="w-12 h-12 bg-white/20 rounded-full flex items-center justify-center mx-auto mb-3">
                <Flame className="w-6 h-6" />
              </div>
              <h3 className="font-semibold mb-1">Ofensiva</h3>
              <p className="text-xs text-white/80">
                {stats.currentStreak} {stats.currentStreak === 1 ? 'dia' : 'dias'} consecutivos
              </p>
              <div className="mt-2 bg-white/20 rounded-full h-2">
                <div className="bg-white rounded-full h-full transition-all duration-500" style={{ width: `${Math.min((stats.currentStreak / 7) * 100, 100)}%` }}></div>
              </div>
            </CardContent>
          </Card>

          {/* Card Conquistas */}
          <Card className="shadow-lg bg-gradient-to-br from-purple-500 to-indigo-500 text-white border-none">
            <CardContent className="p-4 text-center">
              <div className="w-12 h-12 bg-white/20 rounded-full flex items-center justify-center mx-auto mb-3">
                <Award className="w-6 h-6" />
              </div>
              <h3 className="font-semibold mb-1">Conquistas</h3>
              <p className="text-xs text-white/80">
                {stats.medals} {stats.medals === 1 ? 'medalha' : 'medalhas'}
              </p>
              <div className="mt-2 flex justify-center gap-1">
                {[...Array(3)].map((_, i) => (
                    <div key={i} className={`w-2 h-2 rounded-full ${i < stats.medals ? 'bg-yellow-400' : 'bg-white/30'}`} />
                ))}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* --- GRÁFICO CORRIGIDO (O texto não se mexe mais) --- */}
        <Card className="shadow-lg border-none">
          <CardHeader className="text-center pb-2">
            <CardTitle className="text-lg">Meta Semanal (20km)</CardTitle>
          </CardHeader>
          <CardContent>
            {/* Adicionei 'relative' aqui para prender o texto absoluto dentro deste bloco */}
            <div className="h-48 flex items-center justify-center relative">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={chartData}
                    cx="50%"
                    cy="50%"
                    innerRadius={60}
                    outerRadius={80}
                    paddingAngle={5}
                    dataKey="value"
                    stroke="none"
                  >
                    {chartData.map((entry, index) => <Cell key={`cell-${index}`} fill={entry.fill} />)}
                  </Pie>
                </PieChart>
              </ResponsiveContainer>
              
              {/* Texto Centralizado com 'absolute inset-0' para ficar perfeitamente no meio */}
              <div className="absolute inset-0 flex items-center justify-center pointer-events-none">
                  <p className="text-3xl font-bold text-gray-800">{Math.round(stats.weeklyGoalProgress)}%</p>
              </div>
            </div>
            
            <div className="text-center mt-2">
              <p className="text-2xl font-bold text-primary">{stats.weeklyDistance.toFixed(2)} km</p>
              <p className="text-sm text-muted-foreground">percorridos esta semana</p>
            </div>
          </CardContent>
        </Card>

        {/* Quick Stats */}
        <div className="grid grid-cols-2 gap-3">
          <Card className="shadow-md border-none">
            <CardContent className="p-4 text-center">
              <div className="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-2">
                <MapPin className="w-5 h-5 text-green-600" />
              </div>
              <p className="text-2xl font-bold text-primary">{stats.weeklyRuns}</p>
              <p className="text-xs text-muted-foreground">Corridas</p>
            </CardContent>
          </Card>

          <Card className="shadow-md border-none">
            <CardContent className="p-4 text-center">
              <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-2">
                <Clock className="w-5 h-5 text-blue-600" />
              </div>
              <p className="text-2xl font-bold text-primary">{formatTime(stats.weeklyTime)}</p>
              <p className="text-xs text-muted-foreground">Tempo Total</p>
            </CardContent>
          </Card>
        </div>

        {/* Average Pace */}
        {stats.weeklyPace > 0 && (
            <Card className="shadow-md border-none">
                <CardContent className="p-4 flex items-center justify-between">
                <div className="flex items-center gap-3">
                    <div className="w-10 h-10 bg-orange-100 rounded-full flex items-center justify-center">
                    <TrendingUp className="w-5 h-5 text-orange-600" />
                    </div>
                    <div>
                    <p className="font-medium">Ritmo Médio</p>
                    <p className="text-sm text-muted-foreground">Esta semana</p>
                    </div>
                </div>
                <p className="text-lg font-bold text-primary">
                    {formatPace(stats.weeklyPace)}
                </p>
                </CardContent>
            </Card>
        )}

        {/* Start Run Button */}
        <Button 
          onClick={onStartRun}
          className="w-full h-14 bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600 text-lg font-medium shadow-lg transition-transform active:scale-95"
        >
          <Play className="w-6 h-6 mr-2" />
          Iniciar Corrida
        </Button>
      </div>
    </div>
  );
}