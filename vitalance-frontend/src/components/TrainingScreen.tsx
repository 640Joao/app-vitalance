import React, { useState } from 'react';
import { Card, CardContent } from './ui/card';
import { Button } from './ui/button';
import { Badge } from './ui/badge';
import { ArrowLeft, Clock, Zap, Dumbbell, Play, Bike, MapPin, Timer } from 'lucide-react';
import { Exercise } from '../models/types'; 
// Importações de componentes Radix UI para o Modal (Dialog)
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter, DialogTrigger } from './ui/dialog';

interface TrainingScreenProps {
  onBack: () => void;
  onStartRun?: () => void;
}

// Tipo customizado para a rotina semanal
type WeeklyRoutine = {
    day: string;
    focus: string;
    duration: string; 
    exercises: string; // Lista de exercícios
    local: string;
    color: 'red' | 'green' | 'purple' | 'orange' | 'blue' | 'yellow';
};

// --- ROTINA DE TREINO FINAL ---
const weeklyRoutine: WeeklyRoutine[] = [
    { day: 'SEGUNDA', focus: 'Peito + Tríceps', duration: '45–60 min', color: 'red',
      exercises: '1. Flexão Tradicional 4x15 • 2. Flexão Abertas 3x12 • 3. Flexão Declinado (pés no banco) 3x10 • 4. Flexão Diamante 3x12 • 5. Tríceps no Banco 3x15 • 6. Flexão Lenta (negativa 4s) 2x8',
      local: 'Chão + Banco da Praça'
    },
    { day: 'TERÇA', focus: 'Costas + Bíceps', duration: '45–60 min', color: 'red',
      exercises: '1. Barra Fixa 4x10 (ou alternativa) • 2. Remada Australiana 4x12 • 3. Remada Unilateral (tronco apoiado) 3x12 • 4. Rosca Direta c/ Mochila 3x15 • 5. Rosca Martelo c/ Mochila 3x15 • 6. Superman (lombar) 3x15',
      local: 'Barra da Praça, Mochila, Chão'
    },
    { day: 'QUARTA', focus: 'Pernas', duration: '50–70 min', color: 'green',
      exercises: '1. Agachamento Livre 4x15 • 2. Agachamento Búlgaro 3x12/perna • 3. Elevação Pélvica 3x20 • 4. Passada (avanço) 3x15/perna • 5. Agachamento Isométrico 3x45s • 6. Panturrilha no degrau 4x20',
      local: 'Banco, Degrau, Parede, Chão'
    },
    { day: 'QUINTA', focus: 'Ombros + Trapézio', duration: '45–60 min', color: 'purple',
      exercises: '1. Pike Push-Up 4x12 • 2. Elevação Lateral c/ Mochila 3x15 • 3. Elevação Frontal c/ Mochila 3x15 • 4. Remada Alta c/ Mochila 3x15 • 5. Encolhimento (Shrugs) c/ Mochila 3x20',
      local: 'Chão + Mochila'
    },
    { day: 'SEXTA', focus: 'Full Body', duration: '45–55 min', color: 'orange',
      exercises: '1. Flexão 3x12 • 2. Agachamento 3x15 • 3. Remada Australiana 3x10 • 4. Elevação Pélvica 3x15 • 5. Tríceps Diamante 3x12 • 6. Prancha 3x1min',
      local: 'Chão + Barra (Opcional)'
    },
    { day: 'SÁBADO', focus: 'Cardio + Core', duration: '40–60 min', color: 'yellow',
      exercises: 'CARDIO: Corrida leve / Caminhada rápida / Pular corda (20–40 min). ABDÔMEN: 1. Prancha 3x1min • 2. Abdominal Supra 4x20 • 3. Abdominal Infra 4x15 • 4. Oblíquo 3x20/lado • 5. Prancha Lateral 3x30s/lado',
      local: 'Chão da Praça'
    }
];

export function TrainingScreen({ onBack, onStartRun }: TrainingScreenProps) {
  const [selectedCategory, setSelectedCategory] = useState<'timer' | 'training'>('timer');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedRoutine, setSelectedRoutine] = useState<WeeklyRoutine | null>(null);


  const timerActivities = [
    {
      id: '1',
      name: 'Corrida (GPS)',
      icon: MapPin,
      description: 'Rastreamento por GPS ao ar livre.',
      color: 'from-green-500 to-emerald-500',
      action: onStartRun
    },
    {
      id: '2',
      name: 'Ciclismo',
      icon: Bike,
      description: 'Pedalada com cronômetro manual.',
      color: 'from-blue-500 to-cyan-500',
      action: () => console.log('Iniciar ciclismo')
    }
  ];

  const getDifficultyColor = (color: WeeklyRoutine['color']) => {
    switch (color) {
      case 'red': return 'bg-red-100 text-red-800 border-red-200';
      case 'green': return 'bg-green-100 text-green-800 border-green-200';
      case 'purple': return 'bg-purple-100 text-purple-800 border-purple-200';
      case 'orange': return 'bg-orange-100 text-orange-800 border-orange-200';
      case 'blue': return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'yellow': return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      default: return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  const getDayColorClass = (color: WeeklyRoutine['color']) => {
    switch (color) {
      case 'red': return 'text-red-600';
      case 'green': return 'text-green-600';
      case 'purple': return 'text-purple-600';
      case 'orange': return 'text-orange-600';
      case 'blue': return 'text-blue-600';
      case 'yellow': return 'text-yellow-600';
      default: return 'text-gray-600';
    }
  };

  const getCategoryIcon = (focus: string) => {
    if (focus.includes('Cardio')) return <Zap className="w-4 h-4" />;
    if (focus.includes('Força Superior')) return <Dumbbell className="w-4 h-4" />;
    if (focus.includes('Resistência')) return <Dumbbell className="w-4 h-4" />;
    if (focus.includes('Corpo Inteiro')) return <Dumbbell className="w-4 h-4" />;
    return <Dumbbell className="w-4 h-4" />;
  };

  const getFocusType = (color: WeeklyRoutine['color']) => {
    switch (color) {
        case 'red': return 'Força Superior';
        case 'green': return 'Resistência / Força';
        case 'purple': return 'Força Superior';
        case 'orange': return 'Corpo Inteiro';
        case 'yellow': return 'Cardio / Core';
        default: return 'Geral';
    }
  };

  const openRoutineModal = (routine: WeeklyRoutine) => {
    setSelectedRoutine(routine);
    setIsModalOpen(true);
  };

  const getImageSource = (day: string) => {
    // Mapeia para os nomes dos arquivos PNG que você tem
    switch (day) {
        case 'SEGUNDA': 
        case 'TERÇA': 
        case 'QUINTA': return '/assets/PeitoTríceps.png'; 
        case 'QUARTA': return '/assets/pernas.png';
        case 'SEXTA': return '/assets/FullBody.png';
        case 'SÁBADO': return '/assets/CardioCore.png';
        default: return 'https://placehold.co/400x200/cccccc/333333?text=IMAGEM+N%C3%83O+ENCONTRADA';
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-50 via-white to-pink-50">
      {/* Header */}
      <div className="bg-gradient-to-r from-purple-500 to-pink-500 text-white p-4 shadow-md">
        <div className="flex items-center justify-between">
          <Button 
            variant="ghost" 
            size="sm" 
            onClick={onBack}
            className="text-white hover:bg-white/20 p-2"
          >
            <ArrowLeft className="w-5 h-5" />
          </Button>
          <h1 className="text-lg font-semibold">Treinos</h1>
          <div className="w-9" />
        </div>
      </div>

      <div className="px-4 py-6">
        {/* Category Selection */}
        <div className="flex bg-white rounded-lg p-1 shadow-md mb-6">
          <Button
            variant={selectedCategory === 'timer' ? 'default' : 'ghost'}
            className={`flex-1 h-10 ${
              selectedCategory === 'timer' 
                ? 'bg-gradient-to-r from-purple-500 to-pink-500 text-white shadow-md' 
                : 'text-gray-600 hover:text-purple-600'
            }`}
            onClick={() => setSelectedCategory('timer')}
          >
            <Timer className="w-4 h-4 mr-2" />
            Cronômetro
          </Button>
          <Button
            variant={selectedCategory === 'training' ? 'default' : 'ghost'}
            className={`flex-1 h-10 ${
              selectedCategory === 'training' 
                ? 'bg-gradient-to-r from-purple-500 to-pink-500 text-white shadow-md' 
                : 'text-gray-600 hover:text-purple-600'
            }`}
            onClick={() => setSelectedCategory('training')}
          >
            <Dumbbell className="w-4 h-4 mr-2" />
            Rotinas
          </Button>
        </div>

        {/* Timer Activities */}
        {selectedCategory === 'timer' && (
          <div className="space-y-4">
            <h2 className="text-lg font-semibold text-gray-800 mb-4">Monitoramento Imediato</h2>
            {timerActivities.map((activity) => {
              const Icon = activity.icon;
              return (
                <Card key={activity.id} className="shadow-md hover:shadow-lg transition-shadow border-none">
                  <CardContent className="p-6">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-4">
                        <div className={`w-12 h-12 bg-gradient-to-r ${activity.color} rounded-full flex items-center justify-center`}>
                          <Icon className="w-6 h-6 text-white" />
                        </div>
                        <div>
                          <h3 className="font-semibold text-lg">{activity.name}</h3>
                          <p className="text-gray-600">{activity.description}</p>
                        </div>
                      </div>
                      <Button 
                        className={`bg-gradient-to-r ${activity.color} hover:shadow-lg transition-shadow`}
                        onClick={activity.action}
                      >
                        <Play className="w-4 h-4 mr-2" />
                        Iniciar
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        )}

        {/* --- ROTINAS SEMANAIS NOVAS --- */}
        {selectedCategory === 'training' && (
          <div className="space-y-4">
            <h2 className="text-lg font-semibold text-gray-800 mb-4">Sua Rotina Semanal (Calistenia)</h2>
            {weeklyRoutine.map((routine, index) => (
              <Card key={routine.day} className="shadow-md hover:shadow-lg transition-shadow border-t-4" style={{ borderColor: getDayColorClass(routine.color) }}>
                <CardContent className="p-4">
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-3 mb-2">
                        <div className={`w-8 h-8 ${getDifficultyColor(routine.color)} rounded-full flex items-center justify-center`}>
                          {getCategoryIcon(routine.focus)}
                        </div>
                        <div>
                          <h3 className={`font-bold text-lg ${getDayColorClass(routine.color)}`}>{routine.day}</h3>
                          <p className="text-sm text-gray-700 font-medium">{routine.focus}</p>
                        </div>
                      </div>
                      
                      {/* Descrição dos Exercícios (Inicial) */}
                      <p className="text-xs text-muted-foreground mb-3 pl-11 italic">
                        {routine.exercises.split(' • ').slice(0, 3).join(' • ')}...
                      </p>
                      
                      {/* Local e Duração */}
                      <div className="flex items-center gap-2 pl-11">
                        <Badge className={getDifficultyColor(routine.color)}>
                          {getFocusType(routine.color)}
                        </Badge>
                        <Badge variant="outline" className="text-xs">
                          <Clock className="w-3 h-3 mr-1" />
                          {routine.duration}
                        </Badge>
                        <Badge variant="outline" className="text-xs text-slate-500 border-slate-300">
                          <MapPin className="w-3 h-3 mr-1" />
                          {routine.local}
                        </Badge>
                      </div>
                    </div>
                    
                    <Dialog>
                        {/* Botão que Abre o Modal */}
                        <DialogTrigger asChild>
                            <Button 
                                size="sm" 
                                variant='ghost'
                                className={`ml-3 text-sm font-semibold border ${getDayColorClass(routine.color)} hover:bg-gray-50`}
                                // O usuário clica, abrimos o modal.
                            >
                                <Play className='w-4 h-4 mr-1' /> Iniciar
                            </Button>
                        </DialogTrigger>

                        {/* --- O MODAL (Janela Pop-up) --- */}
                        <DialogContent className="sm:max-w-md">
                            <DialogHeader>
                                <DialogTitle className="text-center text-xl">
                                    {routine.day} - {routine.focus}
                                </DialogTitle>
                                <DialogDescription className="text-center text-xs">
                                    {routine.duration} • {routine.local}
                                </DialogDescription>
                            </DialogHeader>

                            <div className="space-y-4">
                                {/* IMAGEM AQUI */}
                                <img 
                                    src={getImageSource(routine.day)} 
                                    alt={"Imagem de treino para ".concat(routine.day)}
                                    className="w-full h-auto rounded-lg object-cover shadow-md"
                                    // Adiciona um fallback de erro, caso o arquivo não seja encontrado
                                    onError={(e) => {
                                        e.currentTarget.onerror = null; // Evita loop
                                        e.currentTarget.src = 'https://placehold.co/400x200/cccccc/333333?text=IMAGEM+N%C3%83O+ENCONTRADA'; 
                                    }}
                                />

                                <h4 className="font-bold text-gray-800 border-b pb-1">Detalhes do Treino:</h4>
                                
                                <ul className="text-sm space-y-2 max-h-60 overflow-y-auto pr-2">
                                    {/* Mapeia a lista de exercícios para exibir em formato de lista */}
                                    {routine.exercises.split(' • ').map((exercise, idx) => (
                                        <li key={idx} className="text-gray-700">
                                            <span className="font-medium text-purple-600">{idx + 1}. </span>
                                            {exercise}
                                        </li>
                                    ))}
                                </ul>
                            </div>

                            <DialogFooter>
                                <Button 
                                    onClick={() => {
                                        if (onStartRun) {
                                            onStartRun(); // Inicia o GPS para corrida
                                        } else {
                                            alert("Rotina de ".concat(routine.day).concat(" iniciada! Volte para monitorar."));
                                        }
                                        setIsModalOpen(false); // Fecha o modal
                                    }}
                                    className="w-full bg-gradient-to-r from-green-500 to-emerald-500"
                                >
                                    <Play className='w-4 h-4 mr-2' /> Começar a Treinar
                                </Button>
                            </DialogFooter>
                        </DialogContent>
                    </Dialog>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}