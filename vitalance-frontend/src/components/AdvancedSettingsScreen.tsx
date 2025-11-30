import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger } from './ui/alert-dialog';
import { ArrowLeft, Lock, Mail, Trash2, Eye, EyeOff } from 'lucide-react';
import { User } from '../models/types';
import { toast } from 'sonner@2.0.3';

interface AdvancedSettingsScreenProps {
  onBack: () => void;
  onLogout: () => void;
  user: User;
}

export function AdvancedSettingsScreen({ onBack, onLogout, user }: AdvancedSettingsScreenProps) {
  const [showCurrentPassword, setShowCurrentPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [passwordForm, setPasswordForm] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  });
  const [emailForm, setEmailForm] = useState({
    newEmail: '',
    password: ''
  });

  const handlePasswordChange = () => {
    if (!passwordForm.currentPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
      toast.error('Preencha todos os campos');
      return;
    }

    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      toast.error('As senhas não coincidem');
      return;
    }

    if (passwordForm.newPassword.length < 6) {
      toast.error('A nova senha deve ter pelo menos 6 caracteres');
      return;
    }

    // In a real app, this would call an API
    toast.success('Senha alterada com sucesso');
    setPasswordForm({
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    });
  };

  const handleEmailChange = () => {
    if (!emailForm.newEmail || !emailForm.password) {
      toast.error('Preencha todos os campos');
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(emailForm.newEmail)) {
      toast.error('Email inválido');
      return;
    }

    // In a real app, this would call an API
    toast.success('Email alterado com sucesso');
    setEmailForm({
      newEmail: '',
      password: ''
    });
  };

  const handleDeleteAccount = () => {
    // In a real app, this would call an API to delete the account
    toast.success('Conta excluída com sucesso');
    onLogout();
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-gray-50">
      {/* Header */}
      <div className="bg-gradient-to-r from-slate-600 to-gray-600 text-white p-4">
        <div className="flex items-center gap-3">
          <Button
            variant="ghost"
            size="sm"
            onClick={onBack}
            className="text-white hover:bg-white/20"
          >
            <ArrowLeft className="w-4 h-4" />
          </Button>
          <h1 className="text-lg font-semibold">Configurações Avançadas</h1>
        </div>
      </div>

      <div className="px-4 py-6 space-y-6">
        {/* Change Password */}
        <Card className="shadow-md">
          <CardHeader className="pb-3">
            <CardTitle className="flex items-center gap-2 text-base">
              <Lock className="w-4 h-4" />
              Alterar Senha
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="currentPassword">Senha Atual</Label>
              <div className="relative">
                <Input
                  id="currentPassword"
                  type={showCurrentPassword ? "text" : "password"}
                  value={passwordForm.currentPassword}
                  onChange={(e) => setPasswordForm(prev => ({
                    ...prev,
                    currentPassword: e.target.value
                  }))}
                  placeholder="Digite sua senha atual"
                />
                <Button
                  type="button"
                  variant="ghost"
                  size="sm"
                  className="absolute right-0 top-0 h-full px-3 hover:bg-transparent"
                  onClick={() => setShowCurrentPassword(!showCurrentPassword)}
                >
                  {showCurrentPassword ? (
                    <EyeOff className="h-4 w-4" />
                  ) : (
                    <Eye className="h-4 w-4" />
                  )}
                </Button>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="newPassword">Nova Senha</Label>
              <div className="relative">
                <Input
                  id="newPassword"
                  type={showNewPassword ? "text" : "password"}
                  value={passwordForm.newPassword}
                  onChange={(e) => setPasswordForm(prev => ({
                    ...prev,
                    newPassword: e.target.value
                  }))}
                  placeholder="Digite sua nova senha"
                />
                <Button
                  type="button"
                  variant="ghost"
                  size="sm"
                  className="absolute right-0 top-0 h-full px-3 hover:bg-transparent"
                  onClick={() => setShowNewPassword(!showNewPassword)}
                >
                  {showNewPassword ? (
                    <EyeOff className="h-4 w-4" />
                  ) : (
                    <Eye className="h-4 w-4" />
                  )}
                </Button>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="confirmPassword">Confirmar Nova Senha</Label>
              <Input
                id="confirmPassword"
                type="password"
                value={passwordForm.confirmPassword}
                onChange={(e) => setPasswordForm(prev => ({
                  ...prev,
                  confirmPassword: e.target.value
                }))}
                placeholder="Confirme sua nova senha"
              />
            </div>

            <Button 
              onClick={handlePasswordChange}
              className="w-full"
            >
              Alterar Senha
            </Button>
          </CardContent>
        </Card>

        {/* Change Email */}
        <Card className="shadow-md">
          <CardHeader className="pb-3">
            <CardTitle className="flex items-center gap-2 text-base">
              <Mail className="w-4 h-4" />
              Alterar Email
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <Label>Email Atual</Label>
              <Input
                value={user.email}
                disabled
                className="bg-muted"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="newEmail">Novo Email</Label>
              <Input
                id="newEmail"
                type="email"
                value={emailForm.newEmail}
                onChange={(e) => setEmailForm(prev => ({
                  ...prev,
                  newEmail: e.target.value
                }))}
                placeholder="Digite seu novo email"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="emailPassword">Confirme sua Senha</Label>
              <Input
                id="emailPassword"
                type="password"
                value={emailForm.password}
                onChange={(e) => setEmailForm(prev => ({
                  ...prev,
                  password: e.target.value
                }))}
                placeholder="Digite sua senha para confirmar"
              />
            </div>

            <Button 
              onClick={handleEmailChange}
              className="w-full"
              variant="secondary"
            >
              Alterar Email
            </Button>
          </CardContent>
        </Card>

        {/* Delete Account */}
        <Card className="shadow-md border-destructive/20">
          <CardHeader className="pb-3">
            <CardTitle className="flex items-center gap-2 text-base text-destructive">
              <Trash2 className="w-4 h-4" />
              Zona de Perigo
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <p className="text-sm text-muted-foreground">
                Esta ação não pode ser desfeita. Isso excluirá permanentemente sua conta
                e removerá todos os seus dados de nossos servidores.
              </p>
            </div>

            <AlertDialog>
              <AlertDialogTrigger asChild>
                <Button 
                  variant="destructive" 
                  className="w-full"
                >
                  <Trash2 className="w-4 h-4 mr-2" />
                  Excluir Conta
                </Button>
              </AlertDialogTrigger>
              <AlertDialogContent>
                <AlertDialogHeader>
                  <AlertDialogTitle>Tem certeza que deseja excluir sua conta?</AlertDialogTitle>
                  <AlertDialogDescription>
                    Esta ação não pode ser desfeita. Isso excluirá permanentemente sua conta
                    e removerá todos os seus dados, incluindo histórico de corridas, metas e configurações.
                  </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                  <AlertDialogCancel>Cancelar</AlertDialogCancel>
                  <AlertDialogAction 
                    onClick={handleDeleteAccount}
                    className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
                  >
                    Sim, excluir conta
                  </AlertDialogAction>
                </AlertDialogFooter>
              </AlertDialogContent>
            </AlertDialog>
          </CardContent>
        </Card>

        {/* Warning */}
        <div className="text-center text-sm text-muted-foreground space-y-1 bg-yellow-50 p-4 rounded-lg border border-yellow-200">
          <p className="font-medium text-yellow-800">⚠️ Importante</p>
          <p className="text-yellow-700">
            Mantenha suas credenciais seguras. Em caso de problemas, 
            entre em contato com o suporte.
          </p>
        </div>
      </div>
    </div>
  );
}