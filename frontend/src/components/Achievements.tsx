import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { 
  FaTrophy, 
  FaMedal, 
  FaCrown, 
  FaStar, 
  FaLock, 
  FaUnlock, 
  FaFire, 
  FaBook, 
  FaChartLine,
  FaTimes,
  FaGraduationCap
} from 'react-icons/fa';
import { TbTargetArrow } from "react-icons/tb";
import { MdLeaderboard } from 'react-icons/md';
import { IoMdInformationCircleOutline } from 'react-icons/io';
import { CircularProgressbar, buildStyles } from 'react-circular-progressbar';
import 'react-circular-progressbar/dist/styles.css';

interface AchievementProfile {
  userId: number;
  username: string;
  totalPoints: number;
  currentBadgeLevel: string;
  pointsToNextLevel: number;
  nextBadgeLevel: string;
  loginStreak: number;
  completedCourses: number;
  averageScore: number;
}

interface Badge {
  level: string;
  minPoints: number;
  maxPoints: number | null;
  description: string;
  iconUrl: string;
}

interface LeaderboardUser {
  userId: number;
  username: string;
  points: number;
  rank: number;
  badgeLevel: string;
  isCurrentUser: boolean;
}

interface LeaderboardData {
  topUsers: LeaderboardUser[];
  currentUser: LeaderboardUser | null;
  period: string;
}

// Styled Components
const Container = styled.div`
  padding: 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  color: white;
`;

const TopSection = styled.div`
  display: flex;
  align-items: center;
  margin-bottom: 2rem;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  padding: 1rem 1.5rem;
  border: 1px solid rgba(255, 255, 255, 0.2);
`;

const LeftSection = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
`;

const InfoButton = styled(IoMdInformationCircleOutline)`
  width: 40px;
  height: 40px;
  color: white;
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(76, 175, 80, 0.4);
  }
`;

const LeaderboardButton = styled.button`
  background: linear-gradient(45deg, #FFD700, #FFA500);
  border: none;
  border-radius: 50px;
  padding: 12px 20px;
  color: white;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 215, 0, 0.3);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(255, 215, 0, 0.4);
  }
`;

const WelcomeSection = styled.div`
  display: flex;
  align-items: center;
  margin-left: 1rem;
  font-size: 1rem;
  gap: 1rem;
`;

const LevelBadge = styled.div<{ $level: string }>`
  background: ${props => getBadgeColor(props.$level)};
  padding: 8px 16px;
  border-radius: 25px;
  font-weight: bold;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
`;

const Username = styled.span`
  font-weight: 500;
  font-style: italic;
  font-size: 1.2rem;
`;

const ProgressSection = styled.div`
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  border: 1px solid rgba(255, 255, 255, 0.2);
`;

const ProgressGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 1.5rem;
`;

const ProgressCard = styled.div`
  text-align: center;
  background: rgba(255, 255, 255, 0.05);
  padding: 1rem;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.1);
`;

const ProgressTitle = styled.h4`
  margin: 0 0 0.5rem 0;
  font-size: 0.9rem;
  opacity: 0.8;
`;

const ProgressValue = styled.div`
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
`;

const BadgesSection = styled.div`
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  padding: 1.5rem;
  border: 1px solid rgba(255, 255, 255, 0.2);
`;

const BadgesGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 10px;

  /* Custom scrollbar */
  &::-webkit-scrollbar {
    width: 8px;
  }

  &::-webkit-scrollbar-track {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 4px;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.3);
    border-radius: 4px;
  }

  &::-webkit-scrollbar-thumb:hover {
    background: rgba(255, 255, 255, 0.5);
  }
`;

const BadgeCard = styled.div<{ $isUnlocked: boolean }>`
  background: ${props => props.$isUnlocked 
    ? 'linear-gradient(135deg, rgba(255, 215, 0, 0.2) 0%, rgba(255, 165, 0, 0.2) 100%)'
    : 'rgba(255, 255, 255, 0.05)'
  };
  border: 2px solid ${props => props.$isUnlocked 
    ? 'rgba(255, 215, 0, 0.5)' 
    : 'rgba(255, 255, 255, 0.1)'
  };
  border-radius: 15px;
  padding: 1.5rem;
  text-align: center;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;

  ${props => props.$isUnlocked && `
    box-shadow: 0 0 20px rgba(255, 215, 0, 0.3);
    
    &::before {
      content: '';
      position: absolute;
      top: -50%;
      left: -50%;
      width: 200%;
      height: 200%;
      background: linear-gradient(45deg, transparent, rgba(255, 215, 0, 0.1), transparent);
      animation: shimmer 3s infinite;
    }
  `}

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  }

  @keyframes shimmer {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
`;

const BadgeIcon = styled.div<{ $isUnlocked: boolean; $level: string }>`
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: ${props => props.$isUnlocked ? 1 : 0.3};
  color: ${props => props.$isUnlocked ? getBadgeColor(props.$level) : 'rgba(255, 255, 255, 0.5)'};
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
`;

const LockOverlay = styled.div`
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 1.5rem;
  color: rgba(255, 255, 255, 0.7);
`;

const BadgeTitle = styled.h3`
  margin: 0 0 0.5rem 0;
  font-size: 1.2rem;
`;

const BadgeDescription = styled.p`
  margin: 0 0 1rem 0;
  opacity: 0.8;
  font-size: 0.9rem;
`;

const PointsRequired = styled.div<{ $isUnlocked: boolean }>`
  font-size: 0.8rem;
  padding: 5px 10px;
  border-radius: 15px;
  background: ${props => props.$isUnlocked 
    ? 'rgba(76, 175, 80, 0.2)' 
    : 'rgba(255, 255, 255, 0.1)'
  };
  color: ${props => props.$isUnlocked ? '#4CAF50' : 'rgba(255, 255, 255, 0.7)'};
  display: inline-block;
`;

const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
`;

const ModalContent = styled.div`
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 2rem;
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
  border: 2px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
`;

const CloseButton = styled.button`
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: none;
  border: none;
  font-size: 1.5rem;
  color: white;
  cursor: pointer;
  padding: 5px;
  border-radius: 50%;
  transition: background 0.3s ease;

  &:hover {
    background: rgba(255, 255, 255, 0.1);
  }
`;

const LeaderboardTitle = styled.h2`
  text-align: center;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
`;

const LeaderboardList = styled.div`
  max-height: 400px;
  overflow-y: auto;
`;

const LeaderboardItem = styled.div<{ $rank: number; $isCurrentUser: boolean }>`
  display: flex;
  align-items: center;
  padding: 1rem;
  margin-bottom: 0.5rem;
  background: ${props => props.$isCurrentUser 
    ? 'rgba(255, 215, 0, 0.2)' 
    : 'rgba(255, 255, 255, 0.1)'
  };
  border-radius: 10px;
  border: ${props => props.$isCurrentUser 
    ? '2px solid rgba(255, 215, 0, 0.5)' 
    : '1px solid rgba(255, 255, 255, 0.1)'
  };
  transition: all 0.3s ease;

  &:hover {
    background: rgba(255, 255, 255, 0.15);
  }
`;

const RankIcon = styled.div<{ $rank: number }>`
  font-size: 1.5rem;
  margin-right: 1rem;
  min-width: 40px;
  text-align: center;
  color: ${props => {
    if (props.$rank === 1) return '#FFD700';
    if (props.$rank === 2) return '#C0C0C0';
    if (props.$rank === 3) return '#CD7F32';
    return '#fff';
  }};
`;

const UserInfo = styled.div`
  flex: 1;
`;

const UserName = styled.div`
  font-weight: bold;
  margin-bottom: 0.25rem;
`;

const UserLevel = styled.div`
  font-size: 0.8rem;
  opacity: 0.7;
`;

const UserPoints = styled.div`
  font-weight: bold;
  color: #FFD700;
`;

const InfoModalContent = styled.div`
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 2rem;
  max-width: 700px;
  width: 90%;
  max-height: 85vh;
  overflow-y: auto;
  position: relative;
  border: 2px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
`;

const InfoTitle = styled.h2`
  text-align: center;
  margin-bottom: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #FFD700;
`;

const InfoSection = styled.div`
  margin-bottom: 2rem;
  background: rgba(255, 255, 255, 0.05);
  padding: 1.5rem;
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.1);
`;

const InfoSectionTitle = styled.h3`
  color: #FFD700;
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  gap: 10px;
`;

const InfoList = styled.ul`
  list-style: none;
  padding: 0;
  margin: 0;
`;

const InfoListItem = styled.li`
  padding: 0.5rem 0;
  display: flex;
  align-items: center;
  gap: 10px;
  color: rgba(255, 255, 255, 0.9);
  
  &::before {
    content: '•';
    color: #4CAF50;
    font-weight: bold;
    font-size: 1.2rem;
  }
`;

const BadgeGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
`;

const BadgeItem = styled.div`
  text-align: center;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.1);
`;

const BadgeItemIcon = styled.div`
  font-size: 2rem;
  margin-bottom: 0.5rem;
  color: ${props => props.color || '#FFD700'};
`;

const BadgeItemTitle = styled.div`
  font-weight: bold;
  margin-bottom: 0.25rem;
  font-size: 0.9rem;
`;

const BadgeItemPoints = styled.div`
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.7);
`;

const HighlightText = styled.span`
  color: #FFD700;
  font-weight: bold;
`;

const WarningText = styled.div`
  background: rgba(255, 193, 7, 0.1);
  border: 1px solid rgba(255, 193, 7, 0.3);
  border-radius: 10px;
  padding: 1rem;
  margin-top: 1rem;
  color: #FFC107;
  font-size: 0.9rem;
`;

const NextLevelProgress = styled.div`
  width: 120px;
  height: 120px;
  margin: 0 auto 1rem;
`;

// Helper function to get badge colors
const getBadgeColor = (level: string): string => {
  switch (level) {
    case 'NOVICE': return '#9E9E9E';
    case 'APPRENTICE': return '#2196F3';
    case 'SCHOLAR': return '#9C27B0';
    case 'EXPERT': return '#FF9800';
    case 'MASTER': return '#FFD700';
    default: return '#9E9E9E';
  }
};

// Helper function to get badge icon
const getBadgeIcon = (level: string) => {
  switch (level) {
    case 'NOVICE': return <FaGraduationCap />;
    case 'APPRENTICE': return <FaBook />;
    case 'SCHOLAR': return <TbTargetArrow />;
    case 'EXPERT': return <FaMedal />;
    case 'MASTER': return <FaCrown />;
    default: return <FaStar />;
  }
};

const getRankIcon = (rank: number) => {
  if (rank === 1) return <FaTrophy />;
  if (rank === 2) return <FaMedal />;
  if (rank === 3) return <FaMedal />;
  return <span>{rank}</span>;
};

interface LeaderboardUser {
  userId: number;
  username: string;
  points: number;
  rank: number;
  badgeLevel: string;
  isCurrentUser: boolean;
}

interface LeaderboardResponse {
  topUsers: LeaderboardUser[];
  currentUser: LeaderboardUser | null;
  period: string;
}

export const Achievements: React.FC = () => {
  const [achievementProfile, setAchievementProfile] = useState<AchievementProfile | null>(null);
  const [badges, setBadges] = useState<Badge[]>([]);
  const [leaderboard, setLeaderboard] = useState<LeaderboardData | null>(null);
  const [showLeaderboard, setShowLeaderboard] = useState(false);
  const [showInfo, setShowInfo] = useState(false);
  const [loading, setLoading] = useState(true);

  // API calls
  const API_BASE_URL = 'http://localhost:8080';
  
  const fetchAchievementProfile = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API_BASE_URL}/api/achievements/profile`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const data = await response.json();
      setAchievementProfile(data);
    } catch (error) {
      console.error('Error fetching achievement profile:', error);
    }
  };

  const fetchBadges = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/achievements/badges`);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const data = await response.json();
      setBadges(data.badgeLevels || []);
    } catch (error) {
      console.error('Error fetching badges:', error);
      setBadges([]); // Set empty array on error
    }
  };

    const fetchLeaderboard = async (): Promise<void> => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_BASE_URL}/api/achievements/leaderboard?period=daily&limit=10`, {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
        });
        
        if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data: LeaderboardResponse = await response.json();
        console.log('=== LEADERBOARD DEBUG ===');
        console.log('Full response:', data);
        console.log('Top users count:', data.topUsers ? data.topUsers.length : 0);
        console.log('Current user:', data.currentUser);
        console.log('Period:', data.period);
        
        if (data.topUsers && data.topUsers.length > 0) {
        console.log('Top 3 users:');
        data.topUsers.slice(0, 3).forEach((user: LeaderboardUser, index: number) => {
            console.log(`${index + 1}. ${user.username} - ${user.points} points (Rank: ${user.rank}, Current: ${user.isCurrentUser})`);
        });
        }
        console.log('========================');
        
        setLeaderboard(data);
    } catch (error) {
        console.error('Error fetching leaderboard:', error);
    }
    };

  const triggerDailyLogin = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API_BASE_URL}/api/achievements/calculate-points`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
    } catch (error) {
      console.error('Error triggering daily login:', error);
    }
  };

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      await triggerDailyLogin(); // Record daily login
      await Promise.all([
        fetchAchievementProfile(),
        fetchBadges(),
        fetchLeaderboard()
      ]);
      setLoading(false);
    };

    loadData();
  }, []);

  if (loading) {
    return (
      <Container>
        <div style={{ textAlign: 'center', marginTop: '2rem' }}>
          <FaFire style={{ fontSize: '3rem', color: '#FFD700', animation: 'pulse 2s infinite' }} />
          <h2>Loading Achievements...</h2>
        </div>
      </Container>
    );
  }

  if (!achievementProfile) {
    return (
      <Container>
        <div style={{ textAlign: 'center', marginTop: '2rem' }}>
          <h2>Error loading achievements</h2>
        </div>
      </Container>
    );
  }

  const progressPercentage = achievementProfile.pointsToNextLevel > 0 
    ? ((achievementProfile.totalPoints % 100) / (achievementProfile.pointsToNextLevel + (achievementProfile.totalPoints % 100))) * 100
    : 100;

  return (
    <Container>
      <TopSection>
        <LeftSection>
          <InfoButton onClick={() => setShowInfo(true)} title="How to earn points" />
          <LeaderboardButton onClick={() => setShowLeaderboard(true)}>
            <MdLeaderboard />
            Leaderboard
          </LeaderboardButton>
        </LeftSection>
        
        <WelcomeSection>
          <span>Welcome,</span>
          <LevelBadge $level={achievementProfile.currentBadgeLevel}>
            {achievementProfile.currentBadgeLevel}
          </LevelBadge>
          <Username>{achievementProfile.username}</Username>
        </WelcomeSection>
      </TopSection>

      <ProgressSection>
        <h3 style={{ marginBottom: '1rem', textAlign: 'center' }}>Your Progress</h3>
        
        <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '2rem' }}>
          <NextLevelProgress>
            <CircularProgressbar
              value={progressPercentage}
              text={`${achievementProfile.pointsToNextLevel} pts to ${achievementProfile.nextBadgeLevel}`}
              styles={buildStyles({
                textSize: '12px',
                pathColor: '#FFD700',
                textColor: '#fff',
                trailColor: 'rgba(255, 255, 255, 0.2)',
                backgroundColor: 'transparent',
              })}
            />
          </NextLevelProgress>
        </div>

        <ProgressGrid>
          <ProgressCard>
            <ProgressTitle>Total Points</ProgressTitle>
            <ProgressValue>{achievementProfile.totalPoints}</ProgressValue>
            <FaChartLine style={{ fontSize: '1.5rem', color: '#4CAF50' }} />
          </ProgressCard>
          
          <ProgressCard>
            <ProgressTitle>Login Streak</ProgressTitle>
            <ProgressValue>{achievementProfile.loginStreak}</ProgressValue>
            <FaFire style={{ fontSize: '1.5rem', color: '#FF5722' }} />
          </ProgressCard>
          
          <ProgressCard>
            <ProgressTitle>Completed Courses</ProgressTitle>
            <ProgressValue>{achievementProfile.completedCourses}</ProgressValue>
            <FaBook style={{ fontSize: '1.5rem', color: '#2196F3' }} />
          </ProgressCard>
          
          <ProgressCard>
            <ProgressTitle>Average Score</ProgressTitle>
            <ProgressValue>{achievementProfile.averageScore?.toFixed(1) || '0.0'}%</ProgressValue>
            <TbTargetArrow style={{ fontSize: '1.5rem', color: '#9C27B0' }} />
          </ProgressCard>
        </ProgressGrid>
      </ProgressSection>

      {/* Badges Section */}
      <BadgesSection>
        <h3 style={{ marginBottom: '1.5rem', textAlign: 'center' }}>Badge Collection</h3>
        <BadgesGrid>
          {badges && badges.length > 0 ? badges.map((badge) => {
            const isUnlocked = achievementProfile.totalPoints >= badge.minPoints;
            return (
              <BadgeCard key={badge.level} $isUnlocked={isUnlocked}>
                <BadgeIcon $isUnlocked={isUnlocked} $level={badge.level}>
                  {getBadgeIcon(badge.level)}
                  {!isUnlocked && <LockOverlay><FaLock /></LockOverlay>}
                </BadgeIcon>
                
                <BadgeTitle>{badge.level}</BadgeTitle>
                <BadgeDescription>{badge.description}</BadgeDescription>
                
                <PointsRequired $isUnlocked={isUnlocked}>
                  {isUnlocked 
                    ? <><FaUnlock style={{ marginRight: '5px' }} />Unlocked!</>
                    : `${badge.minPoints} points required`
                  }
                </PointsRequired>
              </BadgeCard>
            );
          }) : (
            <div style={{ 
              gridColumn: '1 / -1', 
              textAlign: 'center', 
              padding: '2rem',
              color: 'rgba(255, 255, 255, 0.7)'
            }}>
              <FaLock style={{ fontSize: '3rem', marginBottom: '1rem' }} />
              <p>No badges available. Complete your first course to unlock achievements!</p>
            </div>
          )}
        </BadgesGrid>
      </BadgesSection>

      {showInfo && (
        <ModalOverlay onClick={() => setShowInfo(false)}>
          <InfoModalContent onClick={(e) => e.stopPropagation()}>
            <CloseButton onClick={() => setShowInfo(false)}>
              <FaTimes />
            </CloseButton>
            
            <InfoTitle>
              <IoMdInformationCircleOutline />
              Achievement System Guide
            </InfoTitle>
            
            <InfoSection>
              <InfoSectionTitle>
                <FaChartLine />
                How to Earn Points
              </InfoSectionTitle>
              <InfoList>
                <InfoListItem>
                  <strong>Complete Courses:</strong> Earn <HighlightText>20 base points</HighlightText> for each completed course
                </InfoListItem>
                <InfoListItem>
                  <strong>Score Bonuses:</strong> Get extra points based on your course performance:
                  <ul style={{ marginTop: '0.5rem', paddingLeft: '1rem' }}>
                    <li>90%+ score: <HighlightText>+5 bonus points</HighlightText></li>
                    <li>80-89% score: <HighlightText>+3 bonus points</HighlightText></li>
                    <li>70-79% score: <HighlightText>+1 bonus point</HighlightText></li>
                  </ul>
                </InfoListItem>
                <InfoListItem>
                  <strong>Daily Login:</strong> Earn <HighlightText>1 point</HighlightText> for each day you log in
                </InfoListItem>
                <InfoListItem>
                  <strong>Leaderboard Performance:</strong> Daily ranking rewards:
                  <ul style={{ marginTop: '0.5rem', paddingLeft: '1rem' }}>
                    <li>Top 3 positions: <HighlightText>+20 bonus points</HighlightText></li>
                    <li>Top 4-10 positions: <HighlightText>+10 bonus points</HighlightText></li>
                  </ul>
                </InfoListItem>
                <InfoListItem>
                  <strong>Badge Level Up:</strong> Unlock new badge levels for additional bonus points:
                  <ul style={{ marginTop: '0.5rem', paddingLeft: '1rem' }}>
                    <li>Apprentice: <HighlightText>+10 bonus points</HighlightText></li>
                    <li>Scholar: <HighlightText>+25 bonus points</HighlightText></li>
                    <li>Expert: <HighlightText>+50 bonus points</HighlightText></li>
                    <li>Master: <HighlightText>+100 bonus points</HighlightText></li>
                  </ul>
                </InfoListItem>
              </InfoList>
            </InfoSection>

            <InfoSection>
              <InfoSectionTitle>
                <FaTrophy />
                Badge Levels
              </InfoSectionTitle>
              <BadgeGrid>
                <BadgeItem>
                  <BadgeItemIcon color="#9E9E9E">
                    <FaGraduationCap />
                  </BadgeItemIcon>
                  <BadgeItemTitle>NOVICE</BadgeItemTitle>
                  <BadgeItemPoints>0-99 points</BadgeItemPoints>
                </BadgeItem>
                <BadgeItem>
                  <BadgeItemIcon color="#2196F3">
                    <FaBook />
                  </BadgeItemIcon>
                  <BadgeItemTitle>APPRENTICE</BadgeItemTitle>
                  <BadgeItemPoints>100-299 points</BadgeItemPoints>
                </BadgeItem>
                <BadgeItem>
                  <BadgeItemIcon color="#9C27B0">
                    <TbTargetArrow />
                  </BadgeItemIcon>
                  <BadgeItemTitle>SCHOLAR</BadgeItemTitle>
                  <BadgeItemPoints>300-699 points</BadgeItemPoints>
                </BadgeItem>
                <BadgeItem>
                  <BadgeItemIcon color="#FF9800">
                    <FaMedal />
                  </BadgeItemIcon>
                  <BadgeItemTitle>EXPERT</BadgeItemTitle>
                  <BadgeItemPoints>700-1499 points</BadgeItemPoints>
                </BadgeItem>
                <BadgeItem>
                  <BadgeItemIcon color="#FFD700">
                    <FaCrown />
                  </BadgeItemIcon>
                  <BadgeItemTitle>MASTER</BadgeItemTitle>
                  <BadgeItemPoints>1500+ points</BadgeItemPoints>
                </BadgeItem>
              </BadgeGrid>
            </InfoSection>

            <InfoSection>
              <InfoSectionTitle>
                <MdLeaderboard />
                Leaderboard System
              </InfoSectionTitle>
              <InfoList>
                <InfoListItem>
                  <strong>Daily Rankings:</strong> Updated every day at midnight based on points earned that day
                </InfoListItem>
                <InfoListItem>
                  <strong>Tie Breaking:</strong> Users with the same points share the same rank
                </InfoListItem>
                <InfoListItem>
                  <strong>Bonus Points:</strong> Top performers receive additional points to maintain their competitive edge
                </InfoListItem>
                <InfoListItem>
                  <strong>Weekly & Monthly:</strong> Extended period rankings for long-term competition
                </InfoListItem>
              </InfoList>
            </InfoSection>

            <InfoSection>
              <InfoSectionTitle>
                <FaFire />
                Tips for Success
              </InfoSectionTitle>
              <InfoList>
                <InfoListItem>
                  <strong>Consistency is Key:</strong> Log in daily to build your streak and earn points
                </InfoListItem>
                <InfoListItem>
                  <strong>Quality Over Quantity:</strong> Focus on getting high scores (90%+) for maximum bonus points
                </InfoListItem>
                <InfoListItem>
                  <strong>Stay Competitive:</strong> Check the leaderboard regularly and aim for top 10 positions
                </InfoListItem>
                <InfoListItem>
                  <strong>Complete Courses:</strong> Each completed course is worth significant points
                </InfoListItem>
                <InfoListItem>
                  <strong>Track Progress:</strong> Monitor your points and plan your path to the next badge level
                </InfoListItem>
              </InfoList>
            </InfoSection>

            <WarningText>
              <strong>Note:</strong> Points are calculated automatically when you complete activities. 
              Leaderboard bonuses are awarded daily, and badge level bonuses are granted immediately upon reaching new levels.
            </WarningText>
          </InfoModalContent>
        </ModalOverlay>
      )}

      {/* Leaderboard Modal */}
      {showLeaderboard && leaderboard && (
        <ModalOverlay onClick={() => setShowLeaderboard(false)}>
          <ModalContent onClick={(e) => e.stopPropagation()}>
            <CloseButton onClick={() => setShowLeaderboard(false)}>
              <FaTimes />
            </CloseButton>
            
            <LeaderboardTitle>
              <MdLeaderboard />
              Daily Leaderboard
            </LeaderboardTitle>
            
            <LeaderboardList>
              {leaderboard.topUsers && leaderboard.topUsers.length > 0 ? (
                leaderboard.topUsers.map((user) => (
                  <LeaderboardItem 
                    key={user.userId} 
                    $rank={user.rank}
                    $isCurrentUser={user.isCurrentUser}
                  >
                    <RankIcon $rank={user.rank}>
                      {getRankIcon(user.rank)}
                    </RankIcon>
                    
                    <UserInfo>
                      <UserName>{user.username}</UserName>
                      <UserLevel>{user.badgeLevel}</UserLevel>
                    </UserInfo>
                    
                    <UserPoints>{user.points} pts</UserPoints>
                  </LeaderboardItem>
                ))
              ) : (
                <div style={{ 
                  textAlign: 'center', 
                  padding: '2rem',
                  color: 'rgba(255, 255, 255, 0.7)'
                }}>
                  <MdLeaderboard style={{ fontSize: '3rem', marginBottom: '1rem' }} />
                  <p>No leaderboard data available yet. Start earning points to see rankings!</p>
                </div>
              )}
            </LeaderboardList>
          </ModalContent>
        </ModalOverlay>
      )}
    </Container>
  );
};