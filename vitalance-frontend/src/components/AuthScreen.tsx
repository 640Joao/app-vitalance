import React, { useState } from 'react';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Loader2, User, Lock, Mail } from 'lucide-react';
import vitallanceLogo from 'figma:asset/0c586fb721656615088a4c71c31639f5e45324d9.png';

interface AuthScreenProps {
  onLogin: (email: string, password: string) => Promise<boolean>;
  onRegister: (name: string, email: string, password: string) => Promise<boolean>;
  onForgotPassword: () => void;
  isLoading?: boolean; // Tornamos opcional pois controlaremos localmente
  error?: string | null; // Tornamos opcional
}

export function AuthScreen({ onLogin, onRegister, onForgotPassword }: AuthScreenProps) {
  const [isLoginMode, setIsLoginMode] = useState(true);
  
  // Estados locais para controlar o loading e erros da API
  const [localIsLoading, setLocalIsLoading] = useState(false);
  const [localError, setLocalError] = useState<string | null>(null);

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: ''
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLocalIsLoading(true);
    setLocalError(null);

    try {
      if (isLoginMode) {
        // --- LÓGICA DE LOGIN REAL ---
        const response = await fetch('http://localhost:8082/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email: formData.email,
                password: formData.password
            })
        });

        if (!response.ok) {
            throw new Error('Falha no login. Verifique email e senha.');
        }

        const data = await response.json();
        console.log("Login sucesso, Token:", data.accessToken || data.token);
        
        // 1. Salva o token para usar nas próximas requisições
        if (data.accessToken) localStorage.setItem('token', data.accessToken);
        else if (data.token) localStorage.setItem('token', data.token);

        // 2. Chama a função original do pai para liberar o acesso ao Dashboard
        await onLogin(formData.email, formData.password);

      } else {
        // --- LÓGICA DE REGISTRO REAL ---
        const response = await fetch('http://localhost:8082/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                username: formData.name, // Mapeia 'name' do form para 'username' do backend
                email: formData.email,
                password: formData.password,
                notificationsEnabled: true,
                theme: "LIGHT"
            })
        });

        if (!response.ok) {
            const errData = await response.json().catch(() => null);
            throw new Error(errData?.message || 'Erro ao criar conta. Verifique os dados.');
        }

        // Sucesso no cadastro
        alert("Conta criada com sucesso! Faça login agora.");
        setIsLoginMode(true); // Muda para a tela de login automaticamente
      }

    } catch (err: any) {
      console.error(err);
      setLocalError(err.message || "Ocorreu um erro inesperado.");
    } finally {
      setLocalIsLoading(false);
    }
  };

  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-cyan-50 flex flex-col items-center justify-center p-4">
      {/* Logo Section */}
      <div className="mb-8 text-center">
        <img 
          src={vitallanceLogo} 
          alt="VitalLance App" 
          className="w-32 h-32 mx-auto mb-4 drop-shadow-lg"
        />
        <h1 className="text-3xl font-bold text-gray-800 mb-2">VitalLance</h1>
        <p className="text-gray-600">Sua jornada fitness começa aqui</p>
      </div>

      <Card className="w-full max-w-md mx-auto shadow-lg">
        <CardHeader className="text-center pb-2">
          <CardTitle className="text-2xl">
            {isLoginMode ? 'Entrar' : 'Criar Conta'}
          </CardTitle>
          <p className="text-muted-foreground text-sm">
            {isLoginMode ? 'Entre na sua conta para continuar' : 'Crie sua conta para começar'}
          </p>
        </CardHeader>
        
        <CardContent className="space-y-4">
          <form onSubmit={handleSubmit} className="space-y-4">
            {!isLoginMode && (
              <div className="space-y-2">
                <div className="relative">
                  <User className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                  <Input
                    type="text"
                    placeholder="Nome de usuário"
                    value={formData.name}
                    onChange={(e) => handleInputChange('name', e.target.value)}
                    className="pl-10"
                    required
                  />
                </div>
              </div>
            )}
            
            <div className="space-y-2">
              <div className="relative">
                <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                <Input
                  type="email"
                  placeholder="Email"
                  value={formData.email}
                  onChange={(e) => handleInputChange('email', e.target.value)}
                  className="pl-10"
                  required
                />
              </div>
            </div>
            
            <div className="space-y-2">
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                <Input
                  type="password"
                  placeholder="Senha (Minúsculas, Maiúsculas, Números, Símbolo)"
                  value={formData.password}
                  onChange={(e) => handleInputChange('password', e.target.value)}
                  className="pl-10"
                  required
                />
              </div>
            </div>
            
            {localError && (
              <div className="bg-destructive/10 text-destructive p-3 rounded-md text-sm border border-destructive/20">
                {localError}
              </div>
            )}
            
            <Button 
              type="submit" 
              className="w-full bg-gradient-to-r from-blue-500 to-cyan-500 hover:from-blue-600 hover:to-cyan-600 transition-all duration-200"
              disabled={localIsLoading}
            >
              {localIsLoading ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin mr-2" />
                  {isLoginMode ? 'Conectando...' : 'Registrando...'}
                </>
              ) : (
                isLoginMode ? 'Entrar' : 'Criar conta'
              )}
            </Button>
          </form>
          
          {isLoginMode && (
            <div className="text-center">
              <button
                type="button"
                onClick={onForgotPassword}
                className="text-blue-600 hover:text-blue-800 text-sm transition-colors"
              >
                Esqueci minha senha
              </button>
            </div>
          )}
          
          <div className="text-center pt-4 border-t">
            <button
              type="button"
              onClick={() => {
                  setIsLoginMode(!isLoginMode);
                  setLocalError(null);
              }}
              className="text-blue-600 hover:text-blue-800 text-sm transition-colors font-medium"
            >
              {isLoginMode 
                ? 'Não tem uma conta? Criar conta' 
                : 'Já tem uma conta? Entrar'
              }
            </button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}