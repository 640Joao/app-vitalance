import { useState } from 'react';
import { User } from '../models/types';
import api from '../services/api';


export const useAuthViewModel = () => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // --- FUNÇÃO DE LOGIN REAL ---
  const login = async (email: string, password: string): Promise<boolean> => {
    setIsLoading(true);
    setError(null);
    
    try {
      console.log("Tentando logar em:", api.defaults.baseURL);
      
      // Faz o POST para o seu Backend Spring Boot
      // NOTA: Verifique se no seu Kotlin o endpoint é '/auth/login' ou '/auth/signin'
      const response = await api.post('/auth/login', {
        email: email,       // ou username, dependendo do seu backend
        password: password
      });

      console.log("Login Sucesso:", response.data);

      // Aqui assumimos que o backend retorna os dados do usuário ou um Token
      // Vamos montar o objeto User com o que veio do backend
      const userData: User = {
        id: response.data.id || '1', // Ajuste conforme o JSON do seu backend
        name: response.data.name || response.data.username || 'Usuário',
        email: email,
        // Se o backend ainda não manda estatísticas, usamos 0
        totalDistance: response.data.totalDistance || 0,
        totalRuns: response.data.totalRuns || 0,
        currentStreak: response.data.currentStreak || 0,
        bio: response.data.bio || 'Atleta Vitalance'
      };

      // DICA: Se tiver JWT, seria ideal salvar no AsyncStorage aqui
      setUser(userData);
      return true;

    } catch (err: any) {
      console.error("Erro no Login:", err);
      
      if (err.response) {
        // O servidor respondeu (ex: 401 Senha errada, 403 Proibido)
        setError('Email ou senha inválidos');
      } else if (err.request) {
        // O pedido foi feito mas não houve resposta (Backend desligado ou IP errado)
        setError('Erro de conexão: O servidor não respondeu.');
      } else {
        setError('Erro ao fazer login.');
      }
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  // --- FUNÇÃO DE CADASTRO REAL ---
  const register = async (name: string, email: string, password: string): Promise<boolean> => {
    setIsLoading(true);
    setError(null);
    
    try {
      // Chamada para o endpoint de cadastro
      // NOTA: Verifique se no Kotlin é '/auth/register' ou '/auth/signup'
      const response = await api.post('/auth/register', {
        name: name,
        email: email,     // ou username
        password: password
      });

      console.log("Cadastro Sucesso:", response.data);

      // Geralmente após cadastro, fazemos o login automático ou pedimos para logar
      // Aqui vou simular que ele já logou com os dados básicos
      const newUser: User = {
        id: Date.now().toString(),
        name: name,
        email: email,
        totalDistance: 0,
        totalRuns: 0,
        currentStreak: 0,
        bio: 'Novo atleta'
      };
      
      setUser(newUser);
      return true;

    } catch (err: any) {
      console.error("Erro no Cadastro:", err);
      if (err.response) {
         setError(err.response.data.message || 'Erro ao criar conta (Dados inválidos?)');
      } else {
         setError('Erro de conexão ao criar conta.');
      }
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    setUser(null);
    setError(null);
  };

  // Manteve o Mock para Reset de Senha (pois depende de envio de email real)
  const sendResetCode = async (email: string): Promise<boolean> => {
    setIsLoading(true);
    setError(null);
    setSuccess(null);
    
    try {
      await new Promise(resolve => setTimeout(resolve, 1000));
      if (email && email.includes('@')) {
        setSuccess('Código de verificação enviado para o seu email!');
        return true;
      } else {
        setError('Email inválido');
        return false;
      }
    } catch (err) {
      setError('Erro ao enviar código de verificação');
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const resetPassword = async (email: string, code: string, newPassword: string): Promise<boolean> => {
    setIsLoading(true);
    setError(null);
    setSuccess(null);
    
    try {
      await new Promise(resolve => setTimeout(resolve, 1000));
      if (code === '123456') {
        setSuccess('Senha redefinida com sucesso! Redirecionando...');
        return true;
      } else {
        setError('Código de verificação inválido');
        return false;
      }
    } catch (err) {
      setError('Erro ao redefinir senha');
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const clearMessages = () => {
    setError(null);
    setSuccess(null);
  };

  return {
    user,
    isLoading,
    error,
    success,
    login,
    register,
    logout,
    sendResetCode,
    resetPassword,
    clearMessages
  };
};