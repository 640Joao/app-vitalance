import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Textarea } from './ui/textarea';
import { Avatar, AvatarFallback, AvatarImage } from './ui/avatar';
import { ArrowLeft, User, Edit3, Save, X, Trophy, MapPin, Clock, Camera, BarChart3, History } from 'lucide-react';
import { User as UserType } from '../models/types';
import { HamburgerMenu } from './HamburgerMenu';

interface ProfileScreenProps {
  user: UserType;
  onBack: () => void;
  onUpdateProfile: (updates: Partial<UserType>) => void;
  onNavigate: (screen: string) => void;
  onStartRun: () => void;
  onLogout: () => void;
}

export function ProfileScreen({ user, onBack, onUpdateProfile, onNavigate, onStartRun, onLogout }: ProfileScreenProps) {
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    name: user.name,
    bio: user.bio || ''
  });

  const handleSave = () => {
    onUpdateProfile(formData);
    setIsEditing(false);
  };

  const handleCancel = () => {
    setFormData({
      name: user.name,
      bio: user.bio || ''
    });
    setIsEditing(false);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-50 via-white to-blue-50">
      {/* Header */}
      <div className="bg-gradient-to-r from-indigo-500 to-blue-500 text-white p-4">
        <div className="flex items-center justify-between">
          <HamburgerMenu
            user={user}
            onNavigate={onNavigate}
            onStartRun={onStartRun}
            onLogout={onLogout}
          />
          <h1 className="text-lg font-semibold">Perfil</h1>
          <Button
            variant="ghost"
            size="sm"
            onClick={() => isEditing ? handleSave() : setIsEditing(true)}
            className="text-white hover:bg-white/20 p-2"
          >
            {isEditing ? <Save className="w-5 h-5" /> : <Edit3 className="w-5 h-5" />}
          </Button>
        </div>
      </div>

      <div className="px-4 py-6 space-y-6">
        {/* Profile Info */}
        <Card className="shadow-lg">
          <CardContent className="pt-6">
            <div className="text-center space-y-4">
              <div className="relative inline-block">
                <Avatar className="w-24 h-24 mx-auto">
                  <AvatarImage src={user.profileImage} />
                  <AvatarFallback className="bg-gradient-to-r from-indigo-500 to-blue-500 text-white text-2xl">
                    {user.name.charAt(0).toUpperCase()}
                  </AvatarFallback>
                </Avatar>
                <Button
                  size="sm"
                  variant="outline"
                  className="absolute -bottom-2 -right-2 h-8 w-8 rounded-full bg-white shadow-md hover:shadow-lg"
                  onClick={() => {
                    // Simulate photo change
                    const newImageUrl = `https://api.dicebear.com/7.x/avataaars/svg?seed=${Date.now()}`;
                    onUpdateProfile({ profileImage: newImageUrl });
                  }}
                >
                  <Camera className="w-3 h-3" />
                </Button>
              </div>
              
              {isEditing ? (
                <div className="space-y-3">
                  <Input
                    value={formData.name}
                    onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
                    placeholder="Nome"
                    className="text-center"
                  />
                  <Textarea
                    value={formData.bio}
                    onChange={(e) => setFormData(prev => ({ ...prev, bio: e.target.value }))}
                    placeholder="Bio"
                    rows={3}
                    className="text-center resize-none"
                  />
                  <div className="flex gap-2 justify-center">
                    <Button onClick={handleSave} size="sm" className="bg-green-500 hover:bg-green-600">
                      Salvar
                    </Button>
                    <Button onClick={handleCancel} size="sm" variant="outline">
                      Cancelar
                    </Button>
                  </div>
                </div>
              ) : (
                <div className="space-y-2">
                  <h2 className="text-xl font-semibold">{user.name}</h2>
                  <p className="text-muted-foreground">{user.email}</p>
                  {user.bio && (
                    <p className="text-sm text-muted-foreground italic">{user.bio}</p>
                  )}
                </div>
              )}
            </div>
          </CardContent>
        </Card>

        {/* Weekly Training History */}
        <Card className="shadow-lg">
          <CardHeader className="pb-3">
            <CardTitle className="flex items-center gap-2 text-base">
              <History className="w-4 h-4" />
              Histórico Semanal de Treinos
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            <div className="flex items-center justify-between p-3 bg-green-50 rounded-lg">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center">
                  <Clock className="w-5 h-5 text-green-600" />
                </div>
                <div>
                  <p className="font-medium">Segunda-feira</p>
                  <p className="text-sm text-muted-foreground">Peito e Tríceps - 60min</p>
                </div>
              </div>
              <Trophy className="w-5 h-5 text-green-600" />
            </div>
            
            <div className="flex items-center justify-between p-3 bg-green-50 rounded-lg">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center">
                  <Clock className="w-5 h-5 text-green-600" />
                </div>
                <div>
                  <p className="font-medium">Quarta-feira</p>
                  <p className="text-sm text-muted-foreground">Costas e Bíceps - 60min</p>
                </div>
              </div>
              <Trophy className="w-5 h-5 text-green-600" />
            </div>
            
            <div className="flex items-center justify-between p-3 bg-green-50 rounded-lg">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center">
                  <Clock className="w-5 h-5 text-green-600" />
                </div>
                <div>
                  <p className="font-medium">Sexta-feira</p>
                  <p className="text-sm text-muted-foreground">Pernas - 70min</p>
                </div>
              </div>
              <Trophy className="w-5 h-5 text-green-600" />
            </div>
            
            <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg opacity-50">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center">
                  <Clock className="w-5 h-5 text-gray-400" />
                </div>
                <div>
                  <p className="font-medium text-gray-500">Domingo</p>
                  <p className="text-sm text-muted-foreground">Descanso</p>
                </div>
              </div>
            </div>
            
            <div className="mt-4 pt-4 border-t">
              <div className="flex items-center justify-between">
                <p className="text-sm text-muted-foreground">Total da semana</p>
                <p className="font-semibold text-primary">3 treinos • 190min</p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Stats */}
        <div className="grid grid-cols-1 gap-4">
          <Card className="shadow-md">
            <CardHeader className="pb-3">
              <CardTitle className="flex items-center gap-2 text-base">
                <Trophy className="w-4 h-4" />
                Estatísticas Gerais
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                    <MapPin className="w-5 h-5 text-blue-600" />
                  </div>
                  <div>
                    <p className="font-medium">Distância Total</p>
                    <p className="text-sm text-muted-foreground">Desde o início</p>
                  </div>
                </div>
                <p className="text-lg font-bold text-primary">
                  {user.totalDistance.toFixed(1)} km
                </p>
              </div>
              
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center">
                    <Clock className="w-5 h-5 text-green-600" />
                  </div>
                  <div>
                    <p className="font-medium">Total de Corridas</p>
                    <p className="text-sm text-muted-foreground">Atividades concluídas</p>
                  </div>
                </div>
                <p className="text-lg font-bold text-primary">
                  {user.totalRuns}
                </p>
              </div>
              
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-orange-100 rounded-full flex items-center justify-center">
                    <Trophy className="w-5 h-5 text-orange-600" />
                  </div>
                  <div>
                    <p className="font-medium">Sequência Atual</p>
                    <p className="text-sm text-muted-foreground">Dias consecutivos</p>
                  </div>
                </div>
                <p className="text-lg font-bold text-primary">
                  {user.currentStreak} dias
                </p>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Achievement Badges */}
        <Card className="shadow-md">
          <CardHeader className="pb-3">
            <CardTitle className="flex items-center gap-2 text-base">
              <Trophy className="w-4 h-4" />
              Conquistas
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-3 gap-3">
              {user.totalDistance >= 10 && (
                <div className="text-center p-3 bg-yellow-50 rounded-lg">
                  <div className="w-12 h-12 bg-yellow-100 rounded-full flex items-center justify-center mx-auto mb-2">
                    <Trophy className="w-6 h-6 text-yellow-600" />
                  </div>
                  <p className="text-xs font-medium">10km Total</p>
                </div>
              )}
              
              {user.totalRuns >= 5 && (
                <div className="text-center p-3 bg-blue-50 rounded-lg">
                  <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-2">
                    <MapPin className="w-6 h-6 text-blue-600" />
                  </div>
                  <p className="text-xs font-medium">5 Corridas</p>
                </div>
              )}
              
              {user.currentStreak >= 3 && (
                <div className="text-center p-3 bg-green-50 rounded-lg">
                  <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-2">
                    <Clock className="w-6 h-6 text-green-600" />
                  </div>
                  <p className="text-xs font-medium">Consistente</p>
                </div>
              )}
              
              {user.totalDistance < 10 && user.totalRuns < 5 && user.currentStreak < 3 && (
                <div className="col-span-3 text-center p-6 text-muted-foreground">
                  <p className="text-sm">Continue se exercitando para desbloquear conquistas!</p>
                </div>
              )}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}