import React from 'react';
import { HamburgerMenu } from './HamburgerMenu';
import { Button } from './ui/button';
import { Settings } from 'lucide-react';
import { User } from '../models/types';

interface AppHeaderProps {
  user: User;
  title?: string;
  showSettingsIcon?: boolean;
  onNavigate: (screen: string) => void;
  onStartRun: () => void;
  onLogout: () => void;
  onSettings?: () => void;
}

export function AppHeader({ 
  user, 
  title, 
  showSettingsIcon = false,
  onNavigate, 
  onStartRun, 
  onLogout,
  onSettings 
}: AppHeaderProps) {
  return (
    <header className="bg-background border-b px-4 py-3 flex items-center justify-between">
      <div className="flex items-center gap-3">
        <HamburgerMenu
          user={user}
          onNavigate={onNavigate}
          onStartRun={onStartRun}
          onLogout={onLogout}
        />
        {title && (
          <h1 className="text-lg font-medium">{title}</h1>
        )}
      </div>

      {showSettingsIcon && onSettings && (
        <Button 
          variant="ghost" 
          size="icon" 
          className="h-9 w-9"
          onClick={onSettings}
        >
          <Settings className="h-5 w-5" />
          <span className="sr-only">Configurações</span>
        </Button>
      )}
    </header>
  );
}