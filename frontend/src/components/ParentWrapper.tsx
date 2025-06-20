import React, { useState } from 'react';
import MiddleSection from './MiddleSection';
import ReportComponent from './report/ReportComponent';

interface ParentWrapperProps {
  // Add any other props that your main component needs
  currentView?: 'middle' | 'report';
}

const ParentWrapper: React.FC<ParentWrapperProps> = ({ currentView = 'middle' }) => {
  const [weeklyHours, setWeeklyHours] = useState<number>(0);

  const handleWeeklyHoursChange = (hours: number) => {
    console.log('🕐 ParentWrapper: Received weeklyHours update:', hours);
    setWeeklyHours(hours);
  };

  return (
    <>
      {currentView === 'middle' && (
        <MiddleSection onWeeklyHoursChange={handleWeeklyHoursChange} />
      )}
      {currentView === 'report' && (
        <ReportComponent weeklyHoursFromMiddle={weeklyHours} />
      )}
    </>
  );
};

export default ParentWrapper;