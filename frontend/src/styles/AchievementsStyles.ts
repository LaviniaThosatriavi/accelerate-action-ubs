import styled, { keyframes, css } from 'styled-components';
import { IoMdInformationCircleOutline } from 'react-icons/io';

// Animations
const shimmer = keyframes`
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
`;

const pulse = keyframes`
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.05); opacity: 0.8; }
`;

const fadeIn = keyframes`
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
`;

// Helper functions
export const getBadgeColor = (level: string): string => {
    switch (level) {
        case 'NOVICE': return '#9e9e9e';
        case 'APPRENTICE': return '#2196f3';
        case 'SCHOLAR': return '#9c27b0';
        case 'EXPERT': return '#ff9800';
        case 'MASTER': return '#ffd700';
        default: return '#9e9e9e';
    }
};

export const getBadgeGradient = (level: string): string => {
    switch (level) {
        case 'NOVICE': return 'linear-gradient(45deg, #757575, #9e9e9e)';
        case 'APPRENTICE': return 'linear-gradient(45deg, #1976d2, #42a5f5)';
        case 'SCHOLAR': return 'linear-gradient(45deg, #7b1fa2, #ba68c8)';
        case 'EXPERT': return 'linear-gradient(45deg, #f57c00, #ffb74d)';
        case 'MASTER': return 'linear-gradient(45deg, #ffc107, #fff176)';
        default: return 'linear-gradient(45deg, #757575, #9e9e9e)';
    }
};

export const getBadgeBoxShadow = (level: string): string => {
    switch (level) {
        case 'NOVICE': return 'rgba(158, 158, 158, 0.3)';
        case 'APPRENTICE': return 'rgba(33, 150, 243, 0.3)';
        case 'SCHOLAR': return 'rgba(156, 39, 176, 0.3)';
        case 'EXPERT': return 'rgba(255, 152, 0, 0.3)';
        case 'MASTER': return 'rgba(255, 193, 7, 0.4)';
        default: return 'rgba(158, 158, 158, 0.3)';
    }
};

// Main Container
export const Container = styled.div`
  padding: 2rem;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #334155 100%);
  min-height: 100vh;
  color: white;
  animation: ${fadeIn} 0.6s ease-out;
`;

// Header Section
export const TopSection = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2rem;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(15px);
  border-radius: 20px;
  padding: 1rem 2rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
`;

export const LeftSection = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
`;

export const InfoButton = styled(IoMdInformationCircleOutline)`
  width: 45px;
  height: 45px;
  color: #60a5fa;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 50%;
  padding: 8px;
  background: rgba(96, 165, 250, 0.1);
  border: 2px solid rgba(96, 165, 250, 0.3);

  &:hover {
    transform: translateY(-3px);
    background: rgba(96, 165, 250, 0.2);
    box-shadow: 0 8px 25px rgba(96, 165, 250, 0.3);
  }
`;

export const LeaderboardButton = styled.button`
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  border: none;
  border-radius: 25px;
  padding: 12px 24px;
  color: white;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(245, 158, 11, 0.3);
  font-size: 0.95rem;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 30px rgba(245, 158, 11, 0.4);
    background: linear-gradient(135deg, #d97706 0%, #b45309 100%);
  }
`;

export const WelcomeSection = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 1.1rem;
`;

export const LevelBadge = styled.div<{ $level: string }>`
  background: ${props => getBadgeGradient(props.$level)};
  padding: 10px 20px;
  border-radius: 30px;
  font-weight: 700;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 1px;
  box-shadow: 0 4px 15px ${props => getBadgeBoxShadow(props.$level)};
  border: 2px solid rgba(255, 255, 255, 0.2);
`;

export const Username = styled.span`
  font-weight: 600;
  font-size: 1.3rem;
  color: #e2e8f0;
`;

export const ProgressSection = styled.div`
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(15px);
  border-radius: 20px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
`;

export const ProgressTitle = styled.h3`
  margin-bottom: 2rem;
  margin-top: 0;
  text-align: center;
  font-size: 1.8rem;
  font-weight: 700;
  color: #60a5fa;
  text-shadow: 0 2px 10px rgba(96, 165, 250, 0.3);
`;

export const NextLevelProgressContainer = styled.div`
  display: flex;
  justify-content: center;
  margin-bottom: 2.5rem;
`;

export const NextLevelProgress = styled.div`
  width: 140px;
  height: 140px;
  position: relative;
`;

export const ProgressText = styled.div`
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  font-size: 0.75rem;
  font-weight: 600;
  color: #e2e8f0;
  line-height: 1.2;
  z-index: 10;
  background: rgba(15, 23, 42, 0.8);
  padding: 8px;
  border-radius: 10px;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.1);
`;

export const ProgressGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1.5rem;
`;

export const ProgressCard = styled.div`
  text-align: center;
  background: linear-gradient(145deg, rgba(30, 41, 59, 0.8) 0%, rgba(51, 65, 85, 0.4) 100%);
  padding: 1.5rem;
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
    background: linear-gradient(145deg, rgba(30, 41, 59, 0.9) 0%, rgba(51, 65, 85, 0.6) 100%);
  }
`;

export const ProgressCardTitle = styled.h4`
  margin: 0 0 0.8rem 0;
  font-size: 0.95rem;
  opacity: 0.8;
  font-weight: 500;
  color: #cbd5e1;
`;

export const ProgressValue = styled.div`
  font-size: 2.2rem;
  font-weight: 700;
  margin-bottom: 0.8rem;
  color: #60a5fa;
`;

export const ProgressIcon = styled.div`
  font-size: 1.8rem;
  opacity: 0.7;
`;

export const BadgesSection = styled.div`
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(15px);
  border-radius: 20px;
  padding: 2rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
`;

export const BadgesTitle = styled.h3`
  margin-bottom: 2rem;
  margin-top: 0;
  text-align: center;
  font-size: 1.8rem;
  font-weight: 700;
  color: #fbbf24;
  text-shadow: 0 2px 10px rgba(251, 191, 36, 0.3);
`;

export const BadgesGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  max-height: 75vh;
  overflow-y: auto;
  padding-right: 10px;

  &::-webkit-scrollbar {
    width: 8px;
  }

  &::-webkit-scrollbar-track {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 10px;
  }

  &::-webkit-scrollbar-thumb {
    background: linear-gradient(45deg, #60a5fa, #3b82f6);
    border-radius: 10px;
  }

  &::-webkit-scrollbar-thumb:hover {
    background: linear-gradient(45deg, #3b82f6, #1d4ed8);
  }
`;

export const BadgeCard = styled.div<{ $isUnlocked: boolean }>`
  background: ${props => props.$isUnlocked
        ? 'linear-gradient(145deg, rgba(251, 191, 36, 0.15) 0%, rgba(245, 158, 11, 0.08) 100%)'
        : 'linear-gradient(145deg, rgba(30, 41, 59, 0.6) 0%, rgba(51, 65, 85, 0.3) 100%)'
    };
  border: 2px solid ${props => props.$isUnlocked
        ? 'rgba(251, 191, 36, 0.4)'
        : 'rgba(255, 255, 255, 0.1)'
    };
  border-radius: 20px;
  padding: 2rem;
  text-align: center;
  transition: all 0.4s ease;
  position: relative;
  overflow: hidden;

  ${props => props.$isUnlocked && css`
    box-shadow: 0 8px 32px rgba(251, 191, 36, 0.2);
    
    &::before {
      content: '';
      position: absolute;
      top: -50%;
      left: -50%;
      width: 200%;
      height: 200%;
      background: linear-gradient(45deg, transparent, rgba(251, 191, 36, 0.08), transparent);
      animation: ${shimmer} 4s infinite;
    }
  `}

  &:hover {
    transform: translateY(-8px);
    box-shadow: ${props => props.$isUnlocked
        ? '0 12px 40px rgba(251, 191, 36, 0.3)'
        : '0 12px 40px rgba(0, 0, 0, 0.2)'
    };
  }
`;

export const BadgeIcon = styled.div<{ $isUnlocked: boolean; $level: string }>`
  font-size: 3.5rem;
  margin-bottom: 1.5rem;
  opacity: ${props => props.$isUnlocked ? 1 : 0.3};
  color: ${props => props.$isUnlocked ? getBadgeColor(props.$level) : 'rgba(255, 255, 255, 0.4)'};
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.3s ease;

  ${props => props.$isUnlocked && css`
    animation: ${pulse} 3s infinite;
  `}
`;

export const LockOverlay = styled.div`
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 1.8rem;
  color: rgba(255, 255, 255, 0.6);
`;

export const BadgeTitle = styled.h3`
  margin: 0 0 1rem 0;
  font-size: 1.4rem;
  font-weight: 700;
  color: #e2e8f0;
`;

export const BadgeDescription = styled.p`
  margin: 0 0 1.5rem 0;
  opacity: 0.8;
  font-size: 1rem;
  line-height: 1.4;
  color: #cbd5e1;
`;

export const PointsRequired = styled.div<{ $isUnlocked: boolean }>`
  font-size: 0.9rem;
  padding: 8px 16px;
  border-radius: 20px;
  background: ${props => props.$isUnlocked
        ? 'linear-gradient(45deg, #10b981, #34d399)'
        : 'rgba(255, 255, 255, 0.08)'
    };
  color: ${props => props.$isUnlocked ? 'white' : 'rgba(255, 255, 255, 0.7)'};
  display: inline-block;
  font-weight: 600;
  box-shadow: ${props => props.$isUnlocked
        ? '0 4px 15px rgba(16, 185, 129, 0.3)'
        : 'none'
    };
`;

export const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(8px);
`;

export const ModalContent = styled.div`
  background: linear-gradient(145deg, #0f172a 0%, #1e293b 100%);
  border-radius: 25px;
  padding: 1.5rem 2.5rem; 
  max-width: 800px;
  top: 5vh;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
  border: 2px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.4);
`;

export const InfoModalContent = styled(ModalContent)`
  max-width: 750px;
`;

export const CloseButton = styled.button`
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  font-size: 1rem;
  color: white;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: all 0.3s ease;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    background: rgba(239, 68, 68, 0.2);
    transform: scale(1.1);
  }
`;

export const LeaderboardTitle = styled.h2`
  text-align: center;
  margin-bottom: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  font-size: 1.8rem;
  font-weight: 700;
  color: #f59e0b;
  text-shadow: 0 2px 10px rgba(245, 158, 11, 0.3);
`;

export const LeaderboardList = styled.div`
  max-height: 450px;
  overflow-y: auto;
  padding-right: 1.5rem;

  &::-webkit-scrollbar {
    width: 8px;
  }

  &::-webkit-scrollbar-track {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 10px;
  }

  &::-webkit-scrollbar-thumb {
    background: linear-gradient(45deg, #f59e0b, #d97706);
    border-radius: 10px;
  }
`;

export const LeaderboardItem = styled.div<{ $rank: number; $isCurrentUser: boolean }>`
  display: flex;
  align-items: center;
  padding: 1.2rem;
  margin-bottom: 0.8rem;
  background: ${props => props.$isCurrentUser
        ? 'linear-gradient(145deg, rgba(251, 191, 36, 0.2) 0%, rgba(245, 158, 11, 0.1) 100%)'
        : 'linear-gradient(145deg, rgba(30, 41, 59, 0.8) 0%, rgba(51, 65, 85, 0.4) 100%)'
    };
  border-radius: 15px;
  border: ${props => props.$isCurrentUser
        ? '2px solid rgba(251, 191, 36, 0.4)'
        : '1px solid rgba(255, 255, 255, 0.1)'
    };
  transition: all 0.3s ease;
  box-shadow: ${props => props.$isCurrentUser
        ? '0 4px 20px rgba(251, 191, 36, 0.2)'
        : '0 2px 10px rgba(0, 0, 0, 0.1)'
    };

  &:hover {
    background: ${props => props.$isCurrentUser
        ? 'linear-gradient(145deg, rgba(251, 191, 36, 0.25) 0%, rgba(245, 158, 11, 0.15) 100%)'
        : 'linear-gradient(145deg, rgba(30, 41, 59, 0.9) 0%, rgba(51, 65, 85, 0.6) 100%)'
    };
    transform: translateY(-2px);
  }
`;

export const RankIcon = styled.div<{ $rank: number }>`
  font-size: 1.8rem;
  margin-right: 1.5rem;
  min-width: 50px;
  text-align: center;
  color: ${props => {
        if (props.$rank === 1) return '#fbbf24';
        if (props.$rank === 2) return '#cbd5e1';
        if (props.$rank === 3) return '#f97316';
        return '#60a5fa';
    }};
  font-weight: 700;
`;

export const UserInfo = styled.div`
  flex: 1;
`;

export const UserName = styled.div`
  font-weight: 700;
  margin-bottom: 0.3rem;
  font-size: 1.1rem;
  color: #e2e8f0;
`;

export const UserLevel = styled.div`
  font-size: 0.85rem;
  opacity: 0.7;
  text-transform: uppercase;
  font-weight: 500;
  letter-spacing: 0.5px;
  color: #cbd5e1;
`;

export const UserPoints = styled.div`
  font-weight: 700;
  color: #fbbf24;
  font-size: 1.1rem;
  text-shadow: 0 1px 5px rgba(251, 191, 36, 0.3);
`;

export const InfoTitle = styled.h2`
  text-align: center;
  margin-bottom: 1rem;
  margin-top: 0.8rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #60a5fa;
  font-size: 2rem;
  font-weight: 700;
  text-shadow: 0 2px 10px rgba(96, 165, 250, 0.3);
`;

export const InfoSection = styled.div`
  margin-bottom: 1.5rem;
  background: linear-gradient(145deg, rgba(30, 41, 59, 0.6) 0%, rgba(51, 65, 85, 0.3) 100%);
  padding: 1rem 2rem;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
`;

export const InfoSectionTitle = styled.h3`
  color: #fbbf24;
  margin-bottom: 1.5rem;
  margin-top: 0;
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 1.4rem;
  font-weight: 600;
`;

export const InfoList = styled.ul`
  list-style: none;
  padding: 0;
  margin: 0;
`;

export const InfoListItem = styled.li`
  padding: 0.8rem 0;
  display: flex;
  align-items: flex-start;
  gap: 12px;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.5;
  
  &::before {
    content: '•';
    color: #10b981;
    font-weight: bold;
    font-size: 1.2rem;
    margin-top: 0.1rem;
  }
`;

export const BadgeGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
`;

export const BadgeItem = styled.div`
  text-align: center;
  padding: 1rem;
  background: rgba(30, 41, 59, 0.5);
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.08);
`;

export const BadgeItemIcon = styled.div<{ color?: string }>`
  font-size: 2rem;
  margin-bottom: 0.5rem;
  color: ${props => props.color || '#fbbf24'};
`;

export const BadgeItemTitle = styled.div`
  font-weight: bold;
  margin-bottom: 0.25rem;
  font-size: 0.9rem;
  color: #e2e8f0;
`;

export const BadgeItemPoints = styled.div`
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.6);
`;

export const HighlightText = styled.span`
  color: #fbbf24;
  font-weight: bold;
`;

export const WarningText = styled.div`
  background: rgba(245, 158, 11, 0.1);
  border: 1px solid rgba(245, 158, 11, 0.3);
  border-radius: 10px;
  padding: 1rem;
  margin-top: 1rem;
  color: #fbbf24;
  font-size: 0.9rem;
`;

// Loading and Empty States
export const LoadingContainer = styled.div`
  text-align: center;
  margin-top: 4rem;
`;

export const LoadingIcon = styled.div`
  font-size: 4rem;
  color: #fbbf24;
  animation: ${pulse} 2s infinite;
  margin-bottom: 1rem;
`;

export const LoadingText = styled.h2`
  color: #60a5fa;
  font-weight: 600;
`;

export const ErrorContainer = styled.div`
  text-align: center;
  margin-top: 4rem;
  color: #ef4444;
`;

export const EmptyState = styled.div`
  grid-column: 1 / -1;
  text-align: center;
  padding: 3rem;
  color: rgba(255, 255, 255, 0.6);
`;

export const EmptyStateIcon = styled.div`
  font-size: 4rem;
  margin-bottom: 1.5rem;
  opacity: 0.5;
`;

export const EmptyStateText = styled.p`
  font-size: 1.1rem;
  line-height: 1.5;
`;