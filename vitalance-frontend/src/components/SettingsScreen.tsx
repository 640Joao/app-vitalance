import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';
import { Input } from './ui/input'; // Importado para edição inline
import { Switch } from './ui/switch';
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger } from './ui/alert-dialog';
import { Bell, Target, LogOut, Shield, Settings, ChevronRight, Check, X } from 'lucide-react';
import { HamburgerMenu } from './HamburgerMenu';
import { User } from '../models/types';
import { toast } from 'sonner';

interface SettingsScreenProps {
  onBack: () => void;
  onLogout: () => void;
  user: User;
  onNavigate: (screen: string) => void;
  onStartRun: () => void;
  onAdvancedSettings: () => void;
}

export function SettingsScreen({ onBack, onLogout, user, onNavigate, onStartRun, onAdvancedSettings }: SettingsScreenProps) {
  const [settings, setSettings] = useState(() => {
    const saved = localStorage.getItem('appSettings');
    return saved ? JSON.parse(saved) : {
      notifications: true,
      weeklyGoalReminders: true,
      soundEffects: true,
      vibration: true,
      dataSharing: false
    };
  });

  // --- ESTADOS PARA AS METAS ---
  const [distanceGoal, setDistanceGoal] = useState(20);
  const [frequencyGoal, setFrequencyGoal] = useState(4);
  const [editingField, setEditingField] = useState<'distance' | 'frequency' | null>(null);
  const [tempValue, setTempValue] = useState('');

  // Efeito principal: Carrega as metas do backend
  useEffect(() => {
    localStorage.setItem('appSettings', JSON.stringify(settings));
    document.documentElement.classList.remove('dark'); // Garante Modo Claro
    loadGoals();
  }, [settings]);

  const loadGoals = async () => {
      try {
          const token = localStorage.getItem('token');
          const response = await fetch('http://localhost:8082/api/profile', {
              headers: { 'Authorization': `Bearer ${token}` }
          });
          if(response.ok) {
              const data = await response.json();
              if(data.weeklyDistanceGoal) setDistanceGoal(data.weeklyDistanceGoal);
              if(data.weeklyFrequencyGoal) setFrequencyGoal(data.weeklyFrequencyGoal);
          }
      } catch(e) {
          console.error("Erro ao carregar metas", e);
      }
  }

  const startEditing = (field: 'distance' | 'frequency', currentValue: number) => {
      setEditingField(field);
      setTempValue(currentValue.toString());
  };

  const saveGoal = async (type: 'distance' | 'frequency') => {
      const token = localStorage.getItem('token');
      const val = Number(tempValue);
      
      if (!val || val <= 0) {
          toast.error("Digite um valor válido maior que zero");
          return;
      }

      const body = type === 'distance' 
          ? { weeklyDistanceGoal: val } 
          : { weeklyFrequencyGoal: val };

      try {
          const response = await fetch('http://localhost:8082/api/profile', {
              method: 'PUT',
              headers: { 
                  'Content-Type': 'application/json',
                  'Authorization': `Bearer ${token}`
              },
              body: JSON.stringify(body)
          });

          if (response.ok) {
              if (type === 'distance') setDistanceGoal(val);
              else setFrequencyGoal(val);
              
              setEditingField(null);
              toast.success("Meta atualizada com sucesso! 🎯");
          } else {
              toast.error("Erro ao salvar meta.");
          }
      } catch (e) {
          toast.error("Erro de conexão.");
      }
  };


  const handleSettingChange = (key: keyof typeof settings) => {
    setSettings((prev: any) => {
      const newSettings = { ...prev, [key]: !prev[key] };
      if (key === 'notifications') {
        toast.success(newSettings[key] ? 'Notificações ativadas 🔔' : 'Notificações desativadas 🔕');
      }
      return newSettings;
    });
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    toast.success('Logout realizado com sucesso 👋');
    onLogout();
  };

  const SettingItem = ({ icon: Icon, title, description, settingKey, type = 'switch', onClick }: any) => (
    <div className={`flex items-center justify-between p-4 ${type === 'navigation' ? 'cursor-pointer hover:bg-slate-50 transition-colors' : ''}`} onClick={onClick}>
      <div className="flex items-center gap-3 flex-1">
        <div className="w-10 h-10 rounded-full flex items-center justify-center bg-slate-100 text-slate-600">
          <Icon className="w-5 h-5" />
        </div>
        <div className="flex-1">
          <p className="font-medium text-slate-900">{title}</p>
          <p className="text-sm text-slate-500">{description}</p>
        </div>
      </div>
      {type === 'switch' && settingKey && (
        <Switch checked={settings[settingKey]} onCheckedChange={() => handleSettingChange(settingKey)} />
      )}
      {type === 'navigation' && <ChevronRight className="w-4 h-4 text-slate-400" />}
    </div>
  );

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-gray-50">
      <div className="bg-gradient-to-r from-slate-700 to-gray-700 text-white p-4 shadow-md">
        <div className="flex items-center justify-between">
          <HamburgerMenu user={user} onNavigate={onNavigate} onStartRun={onStartRun} onLogout={handleLogout} />
          <h1 className="text-lg font-semibold">Configurações</h1>
          <div className="w-9" />
        </div>
      </div>

      <div className="px-4 py-6 space-y-6 pb-20">
        
        {/* Geral */}
        <Card className="shadow-sm border-slate-100">
          <CardHeader className="pb-3 border-b border-slate-50">
            <CardTitle className="flex items-center gap-2 text-base text-slate-700">
              <Settings className="w-4 h-4" /> Geral
            </CardTitle>
          </CardHeader>
          <CardContent className="p-0 divide-y divide-slate-50">
            <SettingItem icon={Bell} title="Notificações" description="Alertas de treinos e metas" settingKey="notifications" />
          </CardContent>
        </Card>

        {/* --- METAS EDITÁVEIS --- */}
        <Card className="shadow-sm border-slate-100">
          <CardHeader className="pb-3 border-b border-slate-50">
            <CardTitle className="flex items-center gap-2 text-base text-slate-700">
              <Target className="w-4 h-4" /> Metas
            </CardTitle>
          </CardHeader>
          <CardContent className="p-4 space-y-4">
            <div className="space-y-4">
              
              {/* Meta de Distância */}
              <div className="flex justify-between items-center h-10">
                <div>
                  <p className="font-medium text-slate-900">Distância Semanal</p>
                  <p className="text-sm text-slate-500">Quilômetros alvo</p>
                </div>
                <div className="text-right flex items-center gap-2">
                  {editingField === 'distance' ? (
                    <>
                        <Input 
                            type="number" 
                            value={tempValue} 
                            onChange={(e) => setTempValue(e.target.value)}
                            className="w-20 h-8 text-right p-1"
                            autoFocus
                        />
                        <Button size="sm" variant="ghost" onClick={() => saveGoal('distance')} className="h-8 w-8 p-0 text-green-600 hover:bg-green-50">
                            <Check className="w-4 h-4" />
                        </Button>
                        <Button size="sm" variant="ghost" onClick={() => setEditingField(null)} className="h-8 w-8 p-0 text-red-500 hover:bg-red-50">
                            <X className="w-4 h-4" />
                        </Button>
                    </>
                  ) : (
                    <>
                        <p className="text-lg font-bold text-slate-700">{distanceGoal} km</p>
                        <Button 
                            variant="link" 
                            size="sm" 
                            className="text-xs p-0 h-auto text-blue-600"
                            onClick={() => startEditing('distance', distanceGoal)}
                        >
                            Editar
                        </Button>
                    </>
                  )}
                </div>
              </div>
              
              {/* Meta de Frequência */}
              <div className="flex justify-between items-center h-10">
                <div>
                  <p className="font-medium text-slate-900">Frequência</p>
                  <p className="text-sm text-slate-500">Treinos por semana</p>
                </div>
                <div className="text-right flex items-center gap-2">
                  {editingField === 'frequency' ? (
                    <>
                        <Input 
                            type="number" 
                            value={tempValue} 
                            onChange={(e) => setTempValue(e.target.value)}
                            className="w-20 h-8 text-right p-1"
                            autoFocus
                        />
                        <Button size="sm" variant="ghost" onClick={() => saveGoal('frequency')} className="h-8 w-8 p-0 text-green-600 hover:bg-green-50">
                            <Check className="w-4 h-4" />
                        </Button>
                        <Button size="sm" variant="ghost" onClick={() => setEditingField(null)} className="h-8 w-8 p-0 text-red-500 hover:bg-red-50">
                            <X className="w-4 h-4" />
                        </Button>
                    </>
                  ) : (
                    <>
                        <p className="text-lg font-bold text-slate-700">{frequencyGoal} treinos</p>
                        <Button 
                            variant="link" 
                            size="sm" 
                            className="text-xs p-0 h-auto text-blue-600"
                            onClick={() => startEditing('frequency', frequencyGoal)}
                        >
                            Editar
                        </Button>
                    </>
                  )}
                </div>
              </div>

            </div>
          </CardContent>
        </Card>

        {/* Conta */}
        <Card className="shadow-sm border-slate-100">
          <CardHeader className="pb-3 border-b border-slate-50">
            <CardTitle className="flex items-center gap-2 text-base text-slate-700">
              <Shield className="w-4 h-4" /> Conta
            </CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            <SettingItem icon={Settings} title="Avançado" description="Gerenciar dados e privacidade" type="navigation" onClick={onAdvancedSettings} />
          </CardContent>
        </Card>

        {/* Logout */}
        <Card className="shadow-sm border-red-100 bg-red-50/30">
          <CardContent className="p-4">
            <AlertDialog>
              <AlertDialogTrigger asChild>
                <Button variant="destructive" className="w-full justify-center gap-2 bg-red-500 hover:bg-red-600 shadow-md transition-all active:scale-95">
                  <LogOut className="w-4 h-4" /> Sair da Conta
                </Button>
              </AlertDialogTrigger>
              <AlertDialogContent>
                <AlertDialogHeader>
                  <AlertDialogTitle>Sair do VitalLance?</AlertDialogTitle>
                  <AlertDialogDescription>Você precisará fazer login novamente.</AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                  <AlertDialogCancel>Cancelar</AlertDialogCancel>
                  <AlertDialogAction onClick={handleLogout} className="bg-red-600 hover:bg-red-700">Sair</AlertDialogAction>
                </AlertDialogFooter>
              </AlertDialogContent>
            </AlertDialog>
          </CardContent>
        </Card>

        <div className="text-center text-xs text-slate-400 space-y-1 pb-4">
          <p>VitalLance v1.0.0</p>
          <p>Feito para você superar limites 🚀</p>
        </div>
      </div>
    </div>
  );
}