import axios from 'axios';

// ---------------------------------------------------------------------------
// CONFIGURAÇÃO DO CLIENTE HTTP (VERSÃO WEB)
// ---------------------------------------------------------------------------
const api = axios.create({
  // Use o IP da sua máquina (192.168.1.70) para poder acessar pelo celular via Wi-Fi
  // Ou use 'http://localhost:8082/api' se for testar só no PC.
  baseURL: 'https://vitalance-production.up.railway.app/api',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000, 
});

// ---------------------------------------------------------------------------
// INTERCEPTOR: INJETA O TOKEN AUTOMATICAMENTE
// ---------------------------------------------------------------------------
api.interceptors.request.use(
  (config) => {
    try {
      // --- CONFIGURAÇÃO PARA WEB (REACT JS / VITE) ---
      // Usamos localStorage, que é nativo do navegador
      const token = localStorage.getItem('vitalance_token');

      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    } catch (error) {
      console.error("Erro ao recuperar token", error);
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default api;