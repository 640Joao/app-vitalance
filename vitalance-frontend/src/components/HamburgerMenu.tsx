import React from 'react';
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetTrigger } from './ui/sheet';
import { Button } from './ui/button';
import { Menu, Play, Settings, User, LogOut } from 'lucide-react';
import { User as UserType } from '../models/types';

interface HamburgerMenuProps {
  user: UserType;
  onNavigate: (screen: string) => void;
  onStartRun: () => void;
  onLogout: () => void;
}

export function HamburgerMenu({ user, onNavigate, onStartRun, onLogout }: HamburgerMenuProps) {
  const [open, setOpen] = React.useState(false);

  const handleNavigation = (screen: string) => {
    onNavigate(screen);
    setOpen(false);
  };

  const handleStartRun = () => {
    onStartRun();
    setOpen(false);
  };

  const handleLogout = () => {
    onLogout();
    setOpen(false);
  };

  return (
    <Sheet open={open} onOpenChange={setOpen}>
      <SheetTrigger asChild>
        <Button variant="ghost" size="icon" className="h-9 w-9">
          <Menu className="h-5 w-5" />
          <span className="sr-only">Abrir menu</span>
        </Button>
      </SheetTrigger>
      <SheetContent side="left" className="w-80 p-0">
        <div className="flex flex-col h-full">
          <SheetHeader className="px-6 py-4 border-b bg-gradient-to-r from-blue-600 to-purple-600 text-white">
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 bg-white/20 rounded-full flex items-center justify-center">
                <User className="h-6 w-6" />
              </div>
              <div>
                <SheetTitle className="text-white text-left">
                  {user.name}
                </SheetTitle>
                <p className="text-white/80 text-sm">{user.email}</p>
              </div>
            </div>
          </SheetHeader>

          <div className="flex-1 py-4">
            <nav className="space-y-2 px-4">
              <Button
                variant="ghost"
                className="w-full justify-start gap-3 h-12"
                onClick={handleStartRun}
              >
                <div className="w-8 h-8 bg-green-100 dark:bg-green-900/30 rounded-lg flex items-center justify-center">
                  <Play className="h-4 w-4 text-green-600 dark:text-green-400" />
                </div>
                <span>Iniciar Corrida</span>
              </Button>

              <Button
                variant="ghost"
                className="w-full justify-start gap-3 h-12"
                onClick={() => handleNavigation('profile')}
              >
                <div className="w-8 h-8 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
                  <User className="h-4 w-4 text-blue-600 dark:text-blue-400" />
                </div>
                <span>Perfil</span>
              </Button>

              <Button
                variant="ghost"
                className="w-full justify-start gap-3 h-12"
                onClick={() => handleNavigation('settings')}
              >
                <div className="w-8 h-8 bg-gray-100 dark:bg-gray-900/30 rounded-lg flex items-center justify-center">
                  <Settings className="h-4 w-4 text-gray-600 dark:text-gray-400" />
                </div>
                <span>Configurações</span>
              </Button>
            </nav>
          </div>

          <div className="border-t p-4">
            <Button
              variant="ghost"
              className="w-full justify-start gap-3 h-12 text-red-600 hover:text-red-700 hover:bg-red-50 dark:text-red-400 dark:hover:text-red-300 dark:hover:bg-red-900/20"
              onClick={handleLogout}
            >
              <div className="w-8 h-8 bg-red-100 dark:bg-red-900/30 rounded-lg flex items-center justify-center">
                <LogOut className="h-4 w-4 text-red-600 dark:text-red-400" />
              </div>
              <span>Sair</span>
            </Button>
          </div>
        </div>
      </SheetContent>
    </Sheet>
  );
}