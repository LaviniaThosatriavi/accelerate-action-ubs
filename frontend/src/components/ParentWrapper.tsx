import React from 'react';
import MiddleSection from './MiddleSection';
import ReportComponent from './report/ReportComponent';

interface ParentWrapperProps {
  currentView?: 'middle' | 'report';
}

const ParentWrapper: React.FC<ParentWrapperProps> = ({ currentView = 'middle' }) => {
  return (
    <>
      {currentView === 'middle' && (
        <MiddleSection />
      )}
      {currentView === 'report' && (
        <ReportComponent />
      )}
    </>
  );
};

export default ParentWrapper;