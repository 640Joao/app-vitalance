import React, { useState, useEffect } from 'react';
import { AuthScreen } from './components/AuthScreen';
import { ForgotPasswordScreen } from './components/ForgotPasswordScreen';
import { HomeScreen } from './components/HomeScreen';
import { RunningScreen } from './components/RunningScreen';
import { TrainingScreen } from './components/TrainingScreen';
import { ProfileScreen } from './components/ProfileScreen';
import { SettingsScreen } from './components/SettingsScreen';
import { AdvancedSettingsScreen } from './components/AdvancedSettingsScreen';
import { BottomNavigation } from './components/BottomNavigation';
import { useAuthViewModel } from './viewmodels/AuthViewModel';
import { useRunningViewModel } from './viewmodels/RunningViewModel';
import { useHomeViewModel } from './viewmodels/HomeViewModel';
import { User } from './models/types';
import { toast } from 'sonner';



export default function App() {
  const [activeScreen, setActiveScreen] = useState('home');
  const [showRunningScreen, setShowRunningScreen] = useState(false);
  const [showAdvancedSettings, setShowAdvancedSettings] = useState(false);
  const [showForgotPassword, setShowForgotPassword] = useState(false);
  
  // ViewModels
  const { user, isLoading, error, success, login, register, logout, sendResetCode, resetPassword, clearMessages } = useAuthViewModel();
  const { 
    session, 
    startRun, 
    pauseRun, 
    resumeRun, 
    stopRun, 
    formatTime, 
    formatDistance 
  } = useRunningViewModel();
  const { 
    activities, 
    goals, 
    weeklyStats, 
    addActivity, 
    getChartData 
  } = useHomeViewModel();

  // Handle successful login
  useEffect(() => {
    if (user) {
      toast.success(`Bem-vindo, ${user.name}!`);
    }
  }, [user]);

  // Handle running session completion
  const handleStopRun = () => {
    const activity = stopRun();
    addActivity(activity);
    setShowRunningScreen(false);
    setActiveScreen('home');
    toast.success(`Corrida concluída! ${formatDistance(activity.distance)} km em ${formatTime(activity.duration)}`);
  };

  // Handle profile updates
  const handleUpdateProfile = (updates: Partial<User>) => {
    // In a real app, this would update the backend
    toast.success('Perfil atualizado com sucesso!');
  };

  // Handle logout
  const handleLogout = () => {
    logout();
    setActiveScreen('home');
    toast.success('Logout realizado com sucesso');
  };

  // Navigation handlers
  const handleStartRun = () => {
    setShowRunningScreen(true);
    setActiveScreen('running');
  };

  const handleBackFromRunning = () => {
    if (session.isRunning) {
      // Don't allow going back while running
      return;
    }
    setShowRunningScreen(false);
    setActiveScreen('home');
  };

  const handleNavigate = (screen: string) => {
    setShowRunningScreen(false);
    setShowAdvancedSettings(false);
    setActiveScreen(screen);
  };

  const handleAdvancedSettings = () => {
    setShowAdvancedSettings(true);
    setShowRunningScreen(false);
  };

  const handleBackFromAdvancedSettings = () => {
    setShowAdvancedSettings(false);
  };

  // Handle forgot password navigation
  const handleForgotPassword = () => {
    clearMessages();
    setShowForgotPassword(true);
  };

  const handleBackToLogin = () => {
    clearMessages();
    setShowForgotPassword(false);
  };

  // Show auth screen if not logged in
  if (!user) {
    if (showForgotPassword) {
      return (
        <div className="h-screen">
          <ForgotPasswordScreen
            onResetPassword={resetPassword}
            onSendResetCode={sendResetCode}
            onBackToLogin={handleBackToLogin}
            isLoading={isLoading}
            error={error}
            success={success}
          />
        </div>
      );
    }
    
    return (
      <div className="h-screen">
        <AuthScreen
          onLogin={login}
          onRegister={register}
          onForgotPassword={handleForgotPassword}
          isLoading={isLoading}
          error={error}
        />
      </div>
    );
  }

  // Render current screen
  const renderCurrentScreen = () => {
    if (showAdvancedSettings) {
      return (
        <AdvancedSettingsScreen
          onBack={handleBackFromAdvancedSettings}
          onLogout={handleLogout}
          user={user}
        />
      );
    }

    if (showRunningScreen) {
      return (
        <RunningScreen
          session={session}
          onStart={startRun}
          onPause={pauseRun}
          onResume={resumeRun}
          onStop={handleStopRun}
          onBack={handleBackFromRunning}
          formatTime={formatTime}
          formatDistance={formatDistance}
        />
      );
    }

    switch (activeScreen) {
      case 'home':
        return (
          <HomeScreen
            goals={goals}
            weeklyStats={weeklyStats}
            chartData={getChartData()}
            onStartRun={handleStartRun}
            userName={user.name}
            user={user}
            onNavigate={handleNavigate}
            onLogout={handleLogout}
          />
        );
      case 'training':
        return (
          <TrainingScreen
            onBack={() => setActiveScreen('home')}
            onStartRun={handleStartRun}
          />
        );
      case 'profile':
        return (
          <ProfileScreen
            user={user}
            onBack={() => setActiveScreen('home')}
            onUpdateProfile={handleUpdateProfile}
            onNavigate={handleNavigate}
            onStartRun={handleStartRun}
            onLogout={handleLogout}
          />
        );
      case 'settings':
        return (
          <SettingsScreen
            onBack={() => setActiveScreen('home')}
            onLogout={handleLogout}
            user={user}
            onNavigate={handleNavigate}
            onStartRun={handleStartRun}
            onAdvancedSettings={handleAdvancedSettings}
          />
        );
      default:
        return (
          <HomeScreen
            goals={goals}
            weeklyStats={weeklyStats}
            chartData={getChartData()}
            onStartRun={handleStartRun}
            userName={user.name}
            user={user}
            onNavigate={handleNavigate}
            onLogout={handleLogout}
          />
        );
    }
  };

  return (
    <div className="h-screen bg-background overflow-hidden">
      {/* Main Content */}
      <div className={`h-full ${!showRunningScreen && !showAdvancedSettings ? 'pb-16' : ''} overflow-y-auto`}>
        {renderCurrentScreen()}
      </div>

      {/* Bottom Navigation - Hide during running session and advanced settings */}
      {!showRunningScreen && !showAdvancedSettings && (
        <BottomNavigation
          activeScreen={activeScreen}
          onNavigate={handleNavigate}
        />
      )}
    </div>
  );
}