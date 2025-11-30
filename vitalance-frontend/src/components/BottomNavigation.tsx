import React from 'react';
import { Button } from './ui/button';
import { Home, Play, List, User, Settings } from 'lucide-react';

interface BottomNavigationProps {
  activeScreen: string;
  onNavigate: (screen: string) => void;
}

export function BottomNavigation({ activeScreen, onNavigate }: BottomNavigationProps) {
  const navItems = [
    { id: 'home', icon: Home, label: 'Início' },
    { id: 'training', icon: List, label: 'Treinos' },
    { id: 'profile', icon: User, label: 'Perfil' },
    { id: 'settings', icon: Settings, label: 'Config' }
  ];

  return (
    <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-border shadow-lg">
      <div className="grid grid-cols-4 h-16">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeScreen === item.id;
          
          return (
            <Button
              key={item.id}
              variant="ghost"
              className={`h-full rounded-none flex flex-col items-center justify-center gap-1 ${
                isActive 
                  ? 'text-primary bg-primary/5' 
                  : 'text-muted-foreground hover:text-primary hover:bg-primary/5'
              }`}
              onClick={() => onNavigate(item.id)}
            >
              <Icon className={`w-5 h-5 ${isActive ? 'text-primary' : ''}`} />
              <span className={`text-xs ${isActive ? 'text-primary font-medium' : ''}`}>
                {item.label}
              </span>
            </Button>
          );
        })}
      </div>
    </div>
  );
}