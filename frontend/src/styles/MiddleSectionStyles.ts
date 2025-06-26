import styled from 'styled-components';

export const Title = styled.h1`
  color: black;
  font-size: clamp(1rem, 2vw, 1.2rem);
  margin-left: clamp(0.2rem, 0.8vw, 0.4rem);
  word-wrap: break-word;
  overflow-wrap: break-word;
  max-width: 100%;
`;

export const TabContainer = styled.div`
  display: flex;
  border-bottom: 1px solid #ddd;
  margin-bottom: 1.5vh;
  width: 100%;
  flex-wrap: wrap;
  
  @media (max-width: 768px) {
    overflow-x: auto;
    flex-wrap: nowrap;
    -webkit-overflow-scrolling: touch;
    &::-webkit-scrollbar {
      display: none;
    }
  }
`;

export const Tab = styled.button<{ active: boolean }>`
  padding: clamp(0.5rem, 1.5vw, 0.75rem) clamp(0.5rem, 1.8vw, 1rem);
  background-color: ${props => props.active ? '#db2b45' : 'white'};
  color: ${props => props.active ? 'white' : 'black'};
  font-size: clamp(0.7rem, 1.4vw, 0.8rem);
  border: none;
  border-bottom: ${props => props.active ? '2px solid #4285f4' : '2px solid transparent'};
  cursor: pointer;
  font-weight: ${props => props.active ? 'bold' : 'normal'};
  transition: all 0.2s;
  flex: 1;
  white-space: nowrap;
  min-width: fit-content;
  word-wrap: break-word;
  overflow-wrap: break-word;

  &:hover {
    background-color: #f5f5f5;
  }
  
  @media (max-width: 768px) {
    flex: 0 0 auto;
  }
`;

export const StatsContainer = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1vh;
  max-width: 100%;
`;

export const StatCard = styled.div`
  display: flex;
  flex: 1;
  flex-direction: column;
  align-items: center;  
  background-color: #f8f9fa;
  width: 100%; 
  box-sizing: border-box; 
  border-radius: clamp(4px, 1vw, 8px);
  padding: clamp(0.2rem, 1.8vw, 1rem);
  text-align: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  word-wrap: break-word;
  overflow-wrap: break-word;
  min-width: 0;
`;

export const StatValue = styled.div`
  font-size: clamp(1rem, 3.5vw, 2rem);
  font-weight: bold;
  color: #4285f4;
  margin-bottom: clamp(0.2rem, 0.8vh, 0.4rem);
  word-wrap: break-word;
  overflow-wrap: break-word;
`;

export const StatLabel = styled.div`
  font-size: clamp(0.8rem, 1.4vw, 0.9rem);
  line-height: clamp(0.8rem, 1.6vw, 0.9rem);
  color: #666;
  word-wrap: break-word;
  overflow-wrap: break-word;
  text-align: center;
`;

export const WeeklyHoursContainer = styled.div`
  background-color: #f8f9fa;
  border-radius: clamp(4px, 1vw, 8px);
  padding: 0rem clamp(0.75rem, 2.5vw, 1.5rem);
  margin-bottom: 1.5vh;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: clamp(40px, 10vh, 60px);
  max-width: 100%;
  word-wrap: break-word;
  overflow-wrap: break-word;
  
  @media (max-width: 768px) {
    padding: clamp(0.1rem, 3vw, 0.8rem);
  }
`;

export const WeeklyHoursContent = styled.div`
  display: flex;
  align-items: center;
  gap: clamp(0.2rem, 1.8vw, 0.8rem);
  word-wrap: break-word;
  overflow-wrap: break-word;
  
  @media (max-width: 768px) {
    flex-direction: column;
    gap: 0.5vh;
    text-align: center;
  }
`;

export const WeeklyHoursValue = styled.div`
  font-size: clamp(1rem, 2.8vw, 2rem);
  font-weight: bold;
  color: #4285f4;
  word-wrap: break-word;
  overflow-wrap: break-word;
`;

export const WeeklyHoursLabel = styled.div`
  font-size: clamp(0.8rem, 1.8vw, 0.9rem);
  color: #666;
  font-weight: 500;
  word-wrap: break-word;
  overflow-wrap: break-word;
  text-align: center;
`;

export const ProgressContainer = styled.div`
  background-color: #f8f9fa;
  border-radius: clamp(4px, 1vw, 8px);
  padding: clamp(0.75rem, 2.5vw, 1.5rem);
  margin-bottom: 3vh;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  max-width: 100%;
  word-wrap: break-word;
  overflow-wrap: break-word;
`;

export const ProgressTitle = styled.h3`
  color: black;
  margin: 0;
  font-size: clamp(0.9rem, 1.8vw, 1.125rem);
  word-wrap: break-word;
  overflow-wrap: break-word;
  max-width: 100%;
`;

export const ProgressBar = styled.div`
  height: clamp(0.6rem, 1.4vw, 0.75rem);
  background-color: #e6e6e6;
  border-radius: clamp(0.3rem, 0.8vw, 0.5rem);
  margin: 1vh auto;
  overflow: hidden;
  max-width: 100%;
`;

export const ProgressFill = styled.div<{ percentage: number }>`
  height: 100%;
  width: ${props => Math.min(100, Math.max(0, props.percentage))}%;
  background-color: #4285f4;
  border-radius: clamp(0.3rem, 0.8vw, 0.5rem);
  transition: width 0.3s ease;
`;

export const TimeStatus = styled.div<{ available: boolean }>`
  margin-top: 0.5vh;
  padding: 1vh 1vw;
  background-color: ${props => props.available ? '#e6f4ea' : '#fce8e6'};
  border-radius: clamp(2px, 0.8vw, 4px);
  display: flex;
  align-items: center;
  gap: clamp(0.25rem, 1vw, 0.5rem);
  color: ${props => props.available ? '#34a853' : '#ea4335'};
  font-weight: 500;
  font-size: clamp(0.8rem, 1.2vw, 1rem);
  word-wrap: break-word;
  overflow-wrap: break-word;
  max-width: 100%;
`;

export const StatusIndicator = styled.span<{ status: string }>`
  display: inline-block;
  width: clamp(0.6rem, 1.4vw, 0.75rem);
  height: clamp(0.6rem, 1.4vw, 0.75rem);
  border-radius: 50%;
  margin-right: clamp(0.2rem, 0.6vw, 0.3rem);
  background-color: ${props => {
    switch (props.status) {
      case 'COMPLETED': return '#34a853';
      case 'IN_PROGRESS': return '#fbbc04';
      case 'NOT_STARTED': return '#ea4335';
      default: return '#dadce0';
    }
  }};
  flex-shrink: 0;
`;

export const CourseContainer = styled.div`
  display: grid;
  gap: clamp(0.5rem, 1.8vw, 1rem);
  max-width: 100%;
`;

export const CourseCard = styled.div`
  border: 1px solid #ddd;
  border-radius: clamp(4px, 1vw, 8px);
  padding: clamp(0.5rem, 2.2vw, 1.25rem);
  transition: transform 0.2s, box-shadow 0.2s;
  word-wrap: break-word;
  overflow-wrap: break-word;
  max-width: 100%;
  min-width: 0;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
`;

export const CourseHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: clamp(0.1rem, 1.8vh, 1rem);
  gap: clamp(0.5rem, 1vw, 1rem);
  flex-wrap: wrap;
`;

export const CourseTitle = styled.h3`
  margin: 0;
  font-size: clamp(0.9rem, 2vw, 1.15rem);
  color: black;
  word-wrap: break-word;
  overflow-wrap: break-word;
  flex: 1;
  min-width: 0;
`;

export const PlatformBadge = styled.span`
  background-color: #f1f3f4;
  padding: clamp(0.2rem, 0.5vw, 0.25rem) clamp(0.3rem, 1vw, 0.5rem);
  border-radius: clamp(2px, 0.8vw, 4px);
  font-size: clamp(0.7rem, 1.4vw, 0.8rem);
  color: #666;
  white-space: nowrap;
  flex-shrink: 0;
`;

export const CourseDetails = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: clamp(0.5rem, 1.4vw, 0.75rem);
  margin-bottom: clamp(0.1rem, 1.8vh, 1rem);
  font-size: clamp(0.8rem, 1.6vw, 0.9rem);
  max-width: 100%;
  
  @media (max-width: 500px) {
    grid-template-columns: 1fr;
  }
`;

export const DetailItem = styled.div`
  display: flex;
  flex-direction: column;
  word-wrap: break-word;
  overflow-wrap: break-word;
  min-width: 0;
`;

export const DetailLabel = styled.span`
  color: #666;
  font-size: clamp(0.7rem, 1.4vw, 0.8rem);
  word-wrap: break-word;
  overflow-wrap: break-word;
`;

export const DetailValue = styled.span`
  font-weight: 500;
  color: black;
  font-size: clamp(0.65rem, 1.3vw, 0.75rem);
  word-wrap: break-word;
  overflow-wrap: break-word;
`;

export const CourseProgress = styled.div`
  margin-top: clamp(0.25rem, 1vh, 0.5rem);
  word-wrap: break-word;
  overflow-wrap: break-word;
  max-width: 100%;
`;

export const CourseActions = styled.div`
  display: flex;
  justify-content: space-between;
  margin-top: clamp(0.1rem, 1.8vh, 1rem);
  gap: clamp(0.4rem, 1.4vw, 0.8rem);
  flex-wrap: wrap;
`;

export const ActionButton = styled.button`
  flex: 1;
  padding: clamp(0.1rem, 1vw, 0.5rem) clamp(0.5rem, 1.8vw, 1rem);
  border: none;
  border-radius: clamp(2px, 0.8vw, 4px);
  background-color: #4285f4;
  color: white;
  cursor: pointer;
  font-size: clamp(0.8rem, 1.6vw, 0.9rem);
  transition: background-color 0.2s;
  text-align: center;
  min-width: 0;
  word-wrap: break-word;
  overflow-wrap: break-word;
  
  &:hover {
    background-color: #3367d6;
  }
  
  &:disabled {
    background-color: #dadce0;
    cursor: not-allowed;
  }
`;

export const ScoreButton = styled.button`
  flex: 1;
  padding: clamp(0.4rem, 1vw, 0.5rem) clamp(0.5rem, 1.8vw, 1rem);
  border: none;
  border-radius: clamp(2px, 0.8vw, 4px);
  background-color: #34a853;
  color: white;
  cursor: pointer;
  font-size: clamp(0.7rem, 1.8vw, 0.9rem);
  transition: background-color 0.2s;
  text-align: center;
  min-width: 0;
  word-wrap: break-word;
  overflow-wrap: break-word;
  
  &:hover {
    background-color: #2d9249;
  }
  
  &:disabled {
    background-color: #dadce0;
    color: #666;
    cursor: not-allowed;
    
    &:hover {
      background-color: #dadce0;
    }
  }
`;

export const OpenLinkButton = styled.a`
  flex: 1;
  padding: clamp(0.4rem, 1vw, 0.5rem) clamp(0.5rem, 1.8vw, 1rem);
  border: none;
  border-radius: clamp(2px, 0.8vw, 4px);
  background-color: #f1f3f4;
  color: #333;
  text-decoration: none;
  font-size: clamp(0.8rem, 1.6vw, 0.9rem);
  display: flex; 
  align-items: center;
  justify-content: center; 
  transition: background-color 0.2s;
  min-width: 0;
  word-wrap: break-word;
  overflow-wrap: break-word;
  text-align: center;
  
  &:hover {
    background-color: #e8eaed;
  }
`;

export const Modal = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: clamp(1rem, 3vw, 2rem);
  box-sizing: border-box;
`;

export const ModalContent = styled.div`
  background-color: white;
  padding: clamp(0.5rem, 1.8vw, 1rem) clamp(1.25rem, 4.5vw, 2.5rem) clamp(1rem, 3.5vw, 2rem) clamp(1rem, 3.5vw, 2rem);
  border-radius: clamp(4px, 1vw, 8px);
  width: clamp(300px, 80vw, 40vw);
  max-width: 500px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  max-height: 90vh;
  overflow-y: auto;
  word-wrap: break-word;
  overflow-wrap: break-word;
`;

export const ModalTitle = styled.h2`
  color: black;
  font-size: clamp(1.1rem, 2.2vw, 1.25rem);
  word-wrap: break-word;
  overflow-wrap: break-word;
  max-width: 100%;
`;

export const FormGroup = styled.div`
  margin-bottom: clamp(0.5rem, 1.8vh, 1rem);
`;

export const Label = styled.label`
  display: block;
  margin-bottom: clamp(0.25rem, 1vh, 0.5rem);
  font-weight: 500;
  color: black;
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  word-wrap: break-word;
  overflow-wrap: break-word;
`;

export const Input = styled.input`
  width: 100%;
  padding: clamp(0.4rem, 1vw, 0.5rem);
  border: 1px solid #34a853;
  border-radius: clamp(2px, 0.8vw, 4px);
  background-color: white;
  color: black;
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  box-sizing: border-box;
  
  &:focus {
    outline: none;
    box-shadow: 0 0 0 2px rgba(52, 168, 83, 0.2);
  }
`;

export const HelpText = styled.div`
  font-size: clamp(0.65rem, 1.3vw, 0.75rem);
  color: #666;
  margin-top: clamp(0.15rem, 0.5vh, 0.25rem);
  font-style: italic;
  word-wrap: break-word;
  overflow-wrap: break-word;
`;

export const ButtonGroup = styled.div`
  display: flex;
  justify-content: flex-end;
  gap: clamp(0.25rem, 1vw, 0.5rem);
  margin-top: clamp(0.75rem, 2.5vh, 1.5rem);
  flex-wrap: wrap;
`;

export const SubmitButton = styled.button`
  padding: clamp(0.4rem, 1vw, 0.5rem) clamp(0.5rem, 1.8vw, 1rem);
  background-color: #34a853;
  color: white;
  border: none;
  border-radius: clamp(2px, 0.8vw, 4px);
  cursor: pointer;
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  
  &:hover {
    background-color: #2d9249;
  }
`;

export const CancelButton = styled.button`
  padding: clamp(0.4rem, 1vw, 0.5rem) clamp(0.5rem, 1.8vw, 1rem);
  background-color: #f1f1f1;
  color: #333;
  border: none;
  border-radius: clamp(2px, 0.8vw, 4px);
  cursor: pointer;
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  
  &:hover {
    background-color: #e1e1e1;
  }
`;

export const EmptyState = styled.div`
  text-align: center;
  padding: clamp(1rem, 3.5vw, 2rem);
  color: #666;
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  word-wrap: break-word;
  overflow-wrap: break-word;
`;

export const ScoreDisplay = styled.div`
  display: flex;
  align-items: center;
  gap: clamp(0.25rem, 1vw, 0.5rem);
  margin-top: clamp(0.25rem, 1vh, 0.5rem);
  flex-wrap: wrap;
`;

export const ScoreBadge = styled.span`
  background-color: #e6f4ea;
  color: #34a853;
  padding: clamp(0.2rem, 0.5vw, 0.25rem) clamp(0.3rem, 1vw, 0.5rem);
  border-radius: clamp(2px, 0.8vw, 4px);
  font-size: clamp(0.7rem, 1.4vw, 0.8rem);
  font-weight: 500;
  white-space: nowrap;
`;