import React, { useState } from 'react';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Loader2, Mail, Lock, ArrowLeft, KeyRound } from 'lucide-react';
import { toast } from 'sonner';

// Mantenha o import da logo original
import vitallanceLogo from 'figma:asset/0c586fb721656615088a4c71c31639f5e45324d9.png';

interface ForgotPasswordScreenProps {
  onBackToLogin: () => void;
}

export function ForgotPasswordScreen({ onBackToLogin }: ForgotPasswordScreenProps) {
  const [step, setStep] = useState<'email' | 'reset'>('email');
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState<{ type: 'error' | 'success'; text: string } | null>(null);
  
  const [formData, setFormData] = useState({
    email: '',
    code: '',
    newPassword: '',
    confirmPassword: ''
  });

  const handleSendCode = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setMessage(null);

    try {
      const response = await fetch('http://localhost:8082/api/auth/reset-request', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: formData.email })
      });

      if (!response.ok) {
        const errorBody = await response.text().then(text => text ? JSON.parse(text) : {}).catch(() => ({}));
        throw new Error(errorBody.message || `Erro ${response.status}: Falha ao enviar código.`);
      }
      
      const data = await response.json();
      
      toast.success(data.message || "Código enviado. Verifique seu email.");
      setMessage({ type: 'success', text: data.message || "Código enviado. Prossiga para redefinir." });
      setStep('reset');

    } catch (err: any) {
      setMessage({ type: 'error', text: err.message || "Erro ao enviar código. Verifique sua conexão e email." });
    } finally {
      setIsLoading(false);
    }
  };

  const handleResetPassword = async (e: React.FormEvent) => {
    e.preventDefault();

    if (formData.newPassword !== formData.confirmPassword) {
      setMessage({ type: 'error', text: "As senhas não coincidem." });
      return;
    }

    setIsLoading(true);
    setMessage(null);
    
    const codeToken = formData.code; 

    try {
        const response = await fetch(`http://localhost:8082/api/auth/reset-password/${codeToken}`,  {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                password: formData.newPassword,
                confirmPassword: formData.confirmPassword
            })
        });
        
        let data;
        const contentType = response.headers.get("content-type");
        
        // Trata o erro de JSON, garantindo que o corpo só é lido se for JSON
        if (response.ok && contentType && contentType.indexOf("application/json") !== -1) {
             data = await response.json(); 
        } else if (!response.ok) {
            const errorBody = await response.text().then(text => text ? JSON.parse(text) : {}).catch(() => ({}));
            throw new Error(errorBody.message || `Erro ${response.status}: Falha na comunicação.`);
        }
        
        if (!response.ok) {
            throw new Error(data?.message || "Falha na redefinição. O código pode ser inválido/expirado.");
        }
        
        // Sucesso
        toast.success("Senha redefinida com sucesso!");
        setMessage({ type: 'success', text: data?.message || "Senha redefinida com sucesso! Redirecionando..." });
        
        setTimeout(() => {
            onBackToLogin();
        }, 2500);

    } catch (err: any) {
        setMessage({ type: 'error', text: err.message || "Erro de redefinição. Tente novamente." });
    } finally {
      setIsLoading(false);
    }
  };

  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    setMessage(null); // Limpa a mensagem ao digitar
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
        <p className="text-gray-600">Recuperar senha</p>
      </div>

      <Card className="w-full max-w-md mx-auto shadow-lg">
        <CardHeader className="text-center pb-2">
          <CardTitle className="text-2xl">
            {step === 'email' ? 'Esqueci minha senha' : 'Redefinir senha'}
          </CardTitle>
          <p className="text-muted-foreground text-sm">
            {step === 'email' 
              ? 'Digite seu email para receber o código' 
              : 'Digite o código e sua nova senha (Mínimo 6, forte)'
            }
          </p>
        </CardHeader>
        
        <CardContent className="space-y-4">
          
          {/* Mensagens de Alerta */}
          {message && (
            <div className={`p-3 rounded-md text-sm ${
                message.type === 'error' 
                ? 'bg-red-100 text-red-700 border border-red-200' 
                : 'bg-green-100 text-green-700 border border-green-200'
            }`}>
              {message.text}
            </div>
          )}
          
          {step === 'email' ? (
            <form onSubmit={handleSendCode} className="space-y-4">
              <div className="space-y-2">
                <div className="relative">
                  <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                  <Input
                    type="email"
                    placeholder="Seu email"
                    value={formData.email}
                    onChange={(e) => handleInputChange('email', e.target.value)}
                    className="pl-10"
                    required
                  />
                </div>
              </div>
              
              <Button 
                type="submit" 
                className="w-full bg-gradient-to-r from-blue-500 to-cyan-500 hover:from-blue-600 hover:to-cyan-600"
                disabled={isLoading}
              >
                {isLoading ? (
                  <>
                    <Loader2 className="w-4 h-4 animate-spin mr-2" />
                    Enviando código...
                  </>
                ) : (
                  'Enviar código'
                )}
              </Button>
            </form>
          ) : (
            <form onSubmit={handleResetPassword} className="space-y-4">
              
              {/* Campo do Código */}
              <div className="relative">
                <KeyRound className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                <Input
                  type="text"
                  placeholder="Código de verificação (4 dígitos)"
                  value={formData.code}
                  onChange={(e) => handleInputChange('code', e.target.value)}
                  className="pl-10"
                  required
                />
              </div>
              
              {/* Nova Senha */}
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                <Input
                  type="password"
                  placeholder="Nova senha"
                  value={formData.newPassword}
                  onChange={(e) => handleInputChange('newPassword', e.target.value)}
                  className="pl-10"
                  required
                />
              </div>
              
              {/* Confirmação */}
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                <Input
                  type="password"
                  placeholder="Confirmar nova senha"
                  value={formData.confirmPassword}
                  onChange={(e) => handleInputChange('confirmPassword', e.target.value)}
                  className="pl-10"
                  required
                />
              </div>
              
              {/* Aviso de Senhas Diferentes */}
              {formData.newPassword && formData.confirmPassword && formData.newPassword !== formData.confirmPassword && (
                <div className="bg-red-100 text-red-700 p-3 rounded-md text-sm">
                  As senhas não coincidem.
                </div>
              )}
              
              <Button 
                type="submit" 
                className="w-full bg-gradient-to-r from-blue-500 to-cyan-500 hover:from-blue-600 hover:to-cyan-600"
                disabled={isLoading || formData.newPassword !== formData.confirmPassword || !formData.code}
              >
                {isLoading ? (
                  <>
                    <Loader2 className="w-4 h-4 animate-spin mr-2" />
                    Redefinindo senha...
                  </>
                ) : (
                  'Redefinir senha'
                )}
              </Button>
              
              <Button
                type="button"
                variant="ghost"
                className="w-full"
                onClick={() => setStep('email')}
                disabled={isLoading}
              >
                Voltar
              </Button>
            </form>
          )}
          
          <div className="text-center pt-2 border-t">
            <button
              type="button"
              onClick={onBackToLogin}
              className="text-blue-600 hover:text-blue-800 text-sm inline-flex items-center gap-1"
            >
              <ArrowLeft className="w-4 h-4" />
              Voltar para o login
            </button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
