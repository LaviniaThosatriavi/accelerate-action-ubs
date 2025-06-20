/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext } from 'react';

// Create context for sharing weekly hours between pages
interface WeeklyHoursContextType {
  weeklyHours: number;
  setWeeklyHours: (hours: number) => void;
}

const WeeklyHoursContext = createContext<WeeklyHoursContextType | undefined>(undefined);

// Hook to use the context
export const useWeeklyHours = () => {
  const context = useContext(WeeklyHoursContext);
  if (!context) {
    throw new Error('useWeeklyHours must be used within WeeklyHoursProvider');
  }
  return context;
};

export { WeeklyHoursContext };