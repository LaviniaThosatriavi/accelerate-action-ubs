import React, { useState, useEffect } from 'react';
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

import type { AchievementProfile, Badge, LeaderboardData, LeaderboardResponse, LeaderboardUser } from '../types/AchievementsTypes';
import {
  Container,
  TopSection,
  LeftSection,
  InfoButton,
  LeaderboardButton,
  WelcomeSection,
  LevelBadge,
  Username,
  ProgressSection,
  ProgressTitle,
  NextLevelProgressContainer,
  NextLevelProgress,
  ProgressText,
  ProgressGrid,
  ProgressCard,
  ProgressCardTitle,
  ProgressValue,
  ProgressIcon,
  BadgesSection,
  BadgesTitle,
  BadgesGrid,
  BadgeCard,
  BadgeIcon,
  LockOverlay,
  BadgeTitle,
  BadgeDescription,
  PointsRequired,
  ModalOverlay,
  ModalContent,
  InfoModalContent,
  CloseButton,
  LeaderboardTitle,
  LeaderboardList,
  LeaderboardItem,
  RankIcon,
  UserInfo,
  UserName,
  UserLevel,
  UserPoints,
  InfoTitle,
  InfoSection,
  InfoSectionTitle,
  InfoList,
  InfoListItem,
  BadgeGrid,
  BadgeItem,
  BadgeItemIcon,
  BadgeItemTitle,
  BadgeItemPoints,
  HighlightText,
  WarningText,
  LoadingContainer,
  LoadingIcon,
  LoadingText,
  ErrorContainer,
  EmptyState,
  EmptyStateIcon,
  EmptyStateText
} from '../styles/AchievementsStyles';

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

export const Achievements: React.FC = () => {
  const [achievementProfile, setAchievementProfile] = useState<AchievementProfile | null>(null);
  const [badges, setBadges] = useState<Badge[]>([]);
  const [leaderboard, setLeaderboard] = useState<LeaderboardData | null>(null);
  const [showLeaderboard, setShowLeaderboard] = useState(false);
  const [showInfo, setShowInfo] = useState(false);
  const [loading, setLoading] = useState(true);

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
      setBadges([]);
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
      await triggerDailyLogin();
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
        <LoadingContainer>
          <LoadingIcon>
            <FaFire />
          </LoadingIcon>
          <LoadingText>Loading Achievements...</LoadingText>
        </LoadingContainer>
      </Container>
    );
  }

  if (!achievementProfile) {
    return (
      <Container>
        <ErrorContainer>
          <h2>Error loading achievements</h2>
        </ErrorContainer>
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
        <ProgressTitle>Your Progress</ProgressTitle>
        
        <NextLevelProgressContainer>
          <NextLevelProgress>
            <CircularProgressbar
              value={progressPercentage}
              styles={buildStyles({
                pathColor: '#60a5fa',
                trailColor: 'rgba(255, 255, 255, 0.1)',
                backgroundColor: 'transparent',
                strokeLinecap: 'round',
                pathTransitionDuration: 0.8,
              })}
            />
            <ProgressText>
              {achievementProfile.pointsToNextLevel} pts to<br />
              {achievementProfile.nextBadgeLevel}
            </ProgressText>
          </NextLevelProgress>
        </NextLevelProgressContainer>

        <ProgressGrid>
          <ProgressCard>
            <ProgressCardTitle>Total Points</ProgressCardTitle>
            <ProgressValue>{achievementProfile.totalPoints}</ProgressValue>
            <ProgressIcon>
              <FaChartLine style={{ color: '#10b981' }} />
            </ProgressIcon>
          </ProgressCard>
          
          <ProgressCard>
            <ProgressCardTitle>Login Streak</ProgressCardTitle>
            <ProgressValue>{achievementProfile.loginStreak}</ProgressValue>
            <ProgressIcon>
              <FaFire style={{ color: '#f97316' }} />
            </ProgressIcon>
          </ProgressCard>
          
          <ProgressCard>
            <ProgressCardTitle>Completed Courses</ProgressCardTitle>
            <ProgressValue>{achievementProfile.completedCourses}</ProgressValue>
            <ProgressIcon>
              <FaBook style={{ color: '#3b82f6' }} />
            </ProgressIcon>
          </ProgressCard>
          
          <ProgressCard>
            <ProgressCardTitle>Average Score</ProgressCardTitle>
            <ProgressValue>{achievementProfile.averageScore?.toFixed(1) || '0.0'}%</ProgressValue>
            <ProgressIcon>
              <TbTargetArrow style={{ color: '#8b5cf6' }} />
            </ProgressIcon>
          </ProgressCard>
        </ProgressGrid>
      </ProgressSection>

      <BadgesSection>
        <BadgesTitle>Badge Collection</BadgesTitle>
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
            <EmptyState>
              <EmptyStateIcon>
                <FaLock />
              </EmptyStateIcon>
              <EmptyStateText>
                No badges available. Complete your first course to unlock achievements!
              </EmptyStateText>
            </EmptyState>
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
              </InfoList>
            </InfoSection>

            <InfoSection>
              <InfoSectionTitle>
                <FaTrophy />
                Badge Levels
              </InfoSectionTitle>
              <BadgeGrid>
                <BadgeItem>
                  <BadgeItemIcon color="#9e9e9e">
                    <FaGraduationCap />
                  </BadgeItemIcon>
                  <BadgeItemTitle>NOVICE</BadgeItemTitle>
                  <BadgeItemPoints>0-99 points</BadgeItemPoints>
                </BadgeItem>
                <BadgeItem>
                  <BadgeItemIcon color="#2196f3">
                    <FaBook />
                  </BadgeItemIcon>
                  <BadgeItemTitle>APPRENTICE</BadgeItemTitle>
                  <BadgeItemPoints>100-299 points</BadgeItemPoints>
                </BadgeItem>
                <BadgeItem>
                  <BadgeItemIcon color="#9c27b0">
                    <TbTargetArrow />
                  </BadgeItemIcon>
                  <BadgeItemTitle>SCHOLAR</BadgeItemTitle>
                  <BadgeItemPoints>300-699 points</BadgeItemPoints>
                </BadgeItem>
                <BadgeItem>
                  <BadgeItemIcon color="#ff9800">
                    <FaMedal />
                  </BadgeItemIcon>
                  <BadgeItemTitle>EXPERT</BadgeItemTitle>
                  <BadgeItemPoints>700-1499 points</BadgeItemPoints>
                </BadgeItem>
                <BadgeItem>
                  <BadgeItemIcon color="#ffd700">
                    <FaCrown />
                  </BadgeItemIcon>
                  <BadgeItemTitle>MASTER</BadgeItemTitle>
                  <BadgeItemPoints>1500+ points</BadgeItemPoints>
                </BadgeItem>
              </BadgeGrid>
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
                <EmptyState>
                  <EmptyStateIcon>
                    <MdLeaderboard />
                  </EmptyStateIcon>
                  <EmptyStateText>
                    No leaderboard data available yet.<br />
                    Start earning points to see rankings!
                  </EmptyStateText>
                </EmptyState>
              )}
            </LeaderboardList>
          </ModalContent>
        </ModalOverlay>
      )}
    </Container>
  );
};