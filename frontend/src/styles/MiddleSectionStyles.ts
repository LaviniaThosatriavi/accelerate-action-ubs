import styled from 'styled-components';

export const Section = styled.div`
  flex: 1;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1rem 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  min-height: 0;
  background-color: white;

  @media (max-width: 768px) {
    padding: 1rem;
  }
`;

export const Title = styled.h1`
    color: black;
    font-size: 1.2rem;
    margin-left: 0.4rem;
`;

export const TabContainer = styled.div`
  display: flex;
  border-bottom: 1px solid #ddd;
  margin-bottom: 1.5rem;
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
  padding: 0.75rem 1rem;
  background-color: ${props => props.active ? '#db2b45' : 'white'};
  color: ${props => props.active ? 'white' : 'black'};
  font-size: 0.8rem;
  border: none;
  border-bottom: ${props => props.active ? '2px solid #4285f4' : '2px solid transparent'};
  cursor: pointer;
  font-weight: ${props => props.active ? 'bold' : 'normal'};
  transition: all 0.2s;
  flex: 1;
  white-space: nowrap;
  min-width: fit-content;

  &:hover {
    background-color: #f5f5f5;
  }
  
  @media (max-width: 768px) {
    flex: 0 0 auto;
  }
`;

export const StatsContainer = styled.div`
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0.8rem;
  margin-bottom: 1.5rem;
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`;

export const StatCard = styled.div`
display: flex;
  flex-direction: column;
  align-items: center;  
  background-color: #f8f9fa;
  width: 100%; 
  box-sizing: border-box; 
  border-radius: 8px;
  padding: 1rem;
  text-align: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
`;

export const StatValue = styled.div`
  font-size: 2rem;
  font-weight: bold;
  color: #4285f4;
  margin-bottom: 0.4rem;
`;

export const StatLabel = styled.div`
  font-size: 0.8rem;
  line-height: 0.9rem;
  color: #666;
`;

export const WeeklyHoursContainer = styled.div`
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 0rem 1.5rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60px;
  
  @media (max-width: 768px) {
    padding: 1rem;
  }
`;

export const WeeklyHoursContent = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
  
  @media (max-width: 768px) {
    flex-direction: column;
    gap: 0.5rem;
    text-align: center;
  }
`;

export const WeeklyHoursValue = styled.div`
  font-size: 1.5rem;
  font-weight: bold;
  color: #4285f4;
`;

export const WeeklyHoursLabel = styled.div`
  font-size: 1rem;
  color: #666;
  font-weight: 500;
`;

export const ProgressContainer = styled.div`
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
`;

export const ProgressTitle = styled.h3`
color: black;
margin-top: 0px;
`;

export const ProgressBar = styled.div`
  height: 0.75rem;
  background-color: #e6e6e6;
  border-radius: 0.5rem;
  margin: 0.5rem 0 1rem 0;
  overflow: hidden;
`;

export const ProgressFill = styled.div<{ percentage: number }>`
  height: 100%;
  width: ${props => props.percentage}%;
  background-color: #4285f4;
  border-radius: 0.5rem;
`;

export const TimeStatus = styled.div<{ available: boolean }>`
  margin-top: 1rem;
  padding: 0.75rem;
  background-color: ${props => props.available ? '#e6f4ea' : '#fce8e6'};
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: ${props => props.available ? '#34a853' : '#ea4335'};
  font-weight: 500;
`;

export const StatusIndicator = styled.span<{ status: string }>`
  display: inline-block;
  width: 0.75rem;
  height: 0.75rem;
  border-radius: 50%;
  margin-right: 0.3rem;
  background-color: ${props => {
        switch (props.status) {
            case 'COMPLETED': return '#34a853';
            case 'IN_PROGRESS': return '#fbbc04';
            case 'NOT_STARTED': return '#ea4335';
            default: return '#dadce0';
        }
    }};
`;

export const CourseContainer = styled.div`
  display: grid;
  gap: 1rem;
`;

export const CourseCard = styled.div`
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1.25rem;
  transition: transform 0.2s, box-shadow 0.2s;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
`;

export const CourseHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
`;

export const CourseTitle = styled.h3`
  margin: 0;
  font-size: 1.15rem;
  color: black;
`;

export const PlatformBadge = styled.span`
  background-color: #f1f3f4;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
  color: #666;
`;

export const CourseDetails = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
  margin-bottom: 1rem;
  font-size: 0.9rem;
  
  @media (max-width: 500px) {
    grid-template-columns: 1fr;
  }
`;

export const DetailItem = styled.div`
  display: flex;
  flex-direction: column;
`;

export const DetailLabel = styled.span`
  color: #666;
  font-size: 0.8rem;
`;

export const DetailValue = styled.span`
  font-weight: 500;
  color: black;
  font-size: 0.75rem;
`;

export const CourseProgress = styled.div`
  margin-top: 0.5rem;
`;

export const CourseActions = styled.div`
  display: flex;
  justify-content: space-between;
  margin-top: 1rem;
  gap: 0.8rem;
`;

export const ActionButton = styled.button`
  flex: 1;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  background-color: #4285f4;
  color: white;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s;
  text-align: center;
  min-width: 0;
  
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
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  background-color: #34a853;
  color: white;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s;
  text-align: center;
  min-width: 0;
  
  &:hover {
    background-color: #2d9249;
  }
`;

export const OpenLinkButton = styled.a`
  flex: 1;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  background-color: #f1f3f4;
  color: #333;
  text-decoration: none;
  font-size: 0.9rem;
  display: flex; 
  align-items: center;
  justify-content: center; 
  transition: background-color 0.2s;
  min-width: 0;
  
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
`;

export const ModalContent = styled.div`
  background-color: white;
  padding: 1rem 2.5rem 2rem 2rem;
  border-radius: 8px;
  width: 40vw;
  max-width: 500px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
`;

export const ModalTitle = styled.h2`
    color: black;
`;

export const FormGroup = styled.div`
    margin-bottom: 1rem;
`;

export const Label = styled.label`
    display: block;
    margin-bottom: 0.5rem;
    font-weight: 500;
    color: black;
`;

export const Input = styled.input`
    width: 100%;
    padding: 0.5rem;
    border: 1px solid #34a853;
    border-radius: 4px;
    background-color: white;
    color: black;
`;

export const HelpText = styled.div`
    font-size: 0.75rem;
    color: #666;
    margin-top: 0.25rem;
    font-style: italic;
`;

export const ButtonGroup = styled.div`
    display: flex;
    justify-content: flex-end;
    gap: 0.5rem;
    margin-top: 1.5rem;
`;

export const SubmitButton = styled.button`
    padding: 0.5rem 1rem;
    background-color: #34a853;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    
    &:hover {
        background-color: #2d9249;
    }
`;

export const CancelButton = styled.button`
    padding: 0.5rem 1rem;
    background-color: #f1f1f1;
    color: #333;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    
    &:hover {
        background-color: #e1e1e1;
    }
`;

export const EmptyState = styled.div`
    text-align: center;
    padding: 2rem;
    color: #666;
`;

export const ScoreDisplay = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 0.5rem;
`;

export const ScoreBadge = styled.span`
  background-color: #e6f4ea;
  color: #34a853;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 500;
`;