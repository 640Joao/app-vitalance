export interface User {
  id: string;
  name: string;
  email: string;
  profileImage?: string;
  bio?: string;
  totalDistance: number;
  totalRuns: number;
  currentStreak: number;
}

export interface Activity {
  id: string;
  type: 'running' | 'walking' | 'cycling';
  distance: number;
  duration: number; // in seconds
  date: Date;
  calories: number;
  pace: number; // min/km
}

export interface Goal {
  id: string;
  type: 'weekly' | 'monthly';
  target: number;
  current: number;
  unit: 'km' | 'minutes' | 'runs';
  title: string;
}

export interface Exercise {
  id: string;
  name: string;
  category: string;
  duration?: number;
  difficulty: 'easy' | 'medium' | 'hard';
  description: string;
}

export interface RunningSession {
  isRunning: boolean;
  isPaused: boolean;
  startTime: number | null;
  elapsedTime: number;
  distance: number;
  currentPace: number;
}