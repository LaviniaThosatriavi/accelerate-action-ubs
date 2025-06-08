import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import axios from 'axios';

const Section = styled.div`
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

const Title = styled.h1`
    color: black;
    font-size: 1.2rem;
    margin-left: 0.4rem;
`;

const TabContainer = styled.div`
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

const Tab = styled.button<{ active: boolean }>`
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

const StatsContainer = styled.div`
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0.8rem;
  margin-bottom: 1.5rem;
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`;

const StatCard = styled.div`
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

const StatValue = styled.div`
  font-size: 2rem;
  font-weight: bold;
  color: #4285f4;
  margin-bottom: 0.4rem;
`;

const StatLabel = styled.div`
  font-size: 0.8rem;
  line-height: 0.9rem;
  color: #666;
`;

const ProgressContainer = styled.div`
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
`;

const ProgressTitle = styled.h3`
color: black;
margin-top: 0px;
`;

const ProgressBar = styled.div`
  height: 0.75rem;
  background-color: #e6e6e6;
  border-radius: 0.5rem;
  margin: 0.5rem 0 1rem 0;
  overflow: hidden;
`;

const ProgressFill = styled.div<{ percentage: number }>`
  height: 100%;
  width: ${props => props.percentage}%;
  background-color: #4285f4;
  border-radius: 0.5rem;
`;

const TimeStatus = styled.div<{ available: boolean }>`
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

const StatusIndicator = styled.span<{ status: string }>`
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

const CourseContainer = styled.div`
  display: grid;
  gap: 1rem;
`;

const CourseCard = styled.div`
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1.25rem;
  transition: transform 0.2s, box-shadow 0.2s;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
`;

const CourseHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
`;

const CourseTitle = styled.h3`
  margin: 0;
  font-size: 1.15rem;
  color: black;
`;

const PlatformBadge = styled.span`
  background-color: #f1f3f4;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
  color: #666;
`;

const CourseDetails = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
  margin-bottom: 1rem;
  font-size: 0.9rem;
  
  @media (max-width: 500px) {
    grid-template-columns: 1fr;
  }
`;

const DetailItem = styled.div`
  display: flex;
  flex-direction: column;
`;

const DetailLabel = styled.span`
  color: #666;
  font-size: 0.8rem;
`;

const DetailValue = styled.span`
  font-weight: 500;
  color: black;
  font-size: 0.75rem;
`;

const CourseProgress = styled.div`
  margin-top: 0.5rem;
`;

const CourseActions = styled.div`
  display: flex;
  justify-content: space-between;
  margin-top: 1rem;
  gap: 0.8rem;
`;

const ActionButton = styled.button`
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

const OpenLinkButton = styled.a`
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

const Modal = styled.div`
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

const ModalContent = styled.div`
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
`;

const FormGroup = styled.div`
  margin-bottom: 1rem;
`;

const Label = styled.label`
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: black;
`;

const Input = styled.input`
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
`;

const ButtonGroup = styled.div`
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  margin-top: 1.5rem;
`;

const SubmitButton = styled.button`
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

const CancelButton = styled.button`
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

const EmptyState = styled.div`
  text-align: center;
  padding: 2rem;
  color: #666;
`;

interface EnrolledCourse {
  id: number;
  courseId: number;
  courseTitle: string;
  platform: string;
  url: string;
  enrollmentDate: string;
  targetCompletionDate: string;
  actualCompletionDate: string | null;
  progressPercentage: number;
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED';
  hoursSpent: number;
  estimatedHours: number;
  category: string;
}

interface EnrollmentStats {
  totalCourses: number;
  completedCourses: number;
  inProgressCourses: number;
  notStartedCourses: number;
  totalHoursSpent: number;
  overallProgress: number;
}

interface ProgressUpdateData {
  enrolledCourseId: number;
  progressPercentage: number;
  additionalHoursSpent: number;
}

const MiddleSection: React.FC = () => {
  const [enrolledCourses, setEnrolledCourses] = useState<EnrolledCourse[]>([]);
  const [filteredCourses, setFilteredCourses] = useState<EnrolledCourse[]>([]);
  const [activeTab, setActiveTab] = useState<string>('ALL');
  const [stats, setStats] = useState<EnrollmentStats | null>(null);
  const [hasAvailableTime, setHasAvailableTime] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');
  const [showProgressModal, setShowProgressModal] = useState<boolean>(false);
  const [, setSelectedCourseId] = useState<number | null>(null);
  const [progressData, setProgressData] = useState<ProgressUpdateData>({
    enrolledCourseId: 0,
    progressPercentage: 0,
    additionalHoursSpent: 0
  });

  // Fetch all data on component mount
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const token = localStorage.getItem('token');
        
        if (!token) {
          setError('You must be logged in to view enrolled courses');
          setLoading(false);
          return;
        }
        
        const headers = {
          Authorization: `Bearer ${token}`
        };
        
        // Fetch enrolled courses
        const coursesResponse = await axios.get('/api/enrolled-courses', { headers });
        setEnrolledCourses(coursesResponse.data);
        setFilteredCourses(coursesResponse.data);
        
        // Fetch stats
        const statsResponse = await axios.get('/api/enrolled-courses/stats', { headers });
        setStats(statsResponse.data);
        
        // Check if user has available time
        const timeResponse = await axios.get('/api/enrolled-courses/has-available-time', { headers });
        setHasAvailableTime(timeResponse.data);
        
      } catch (err) {
        console.error('Error fetching data:', err);
        setError('Failed to load your courses. Please try again later.');
      } finally {
        setLoading(false);
      }
    };
    
    fetchData();
  }, []);

  // Filter courses when tab changes
  useEffect(() => {
    if (activeTab === 'ALL') {
      setFilteredCourses(enrolledCourses);
    } else {
      setFilteredCourses(
        enrolledCourses.filter(course => course.status === activeTab)
      );
    }
  }, [activeTab, enrolledCourses]);

  const handleTabChange = (tab: string) => {
    setActiveTab(tab);
  };

  const handleUpdateProgress = (courseId: number) => {
    const course = enrolledCourses.find(c => c.id === courseId);
    if (course) {
      setSelectedCourseId(courseId);
      setProgressData({
        enrolledCourseId: courseId,
        progressPercentage: course.progressPercentage,
        additionalHoursSpent: 0
      });
      setShowProgressModal(true);
    }
  };

  const handleProgressInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setProgressData({
      ...progressData,
      [name]: name === 'progressPercentage' 
        ? Math.min(100, Math.max(0, parseInt(value)))
        : parseInt(value)
    });
  };

  const handleProgressSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      const response = await axios.put('/api/enrolled-courses/progress', progressData, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      // Update the course in the state
      const updatedCourse = response.data;
      setEnrolledCourses(prevCourses => 
        prevCourses.map(course => 
          course.id === updatedCourse.id ? updatedCourse : course
        )
      );
      
      // Refresh stats
      const statsResponse = await axios.get('/api/enrolled-courses/stats', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setStats(statsResponse.data);
      
      // Check if user still has available time
      const timeResponse = await axios.get('/api/enrolled-courses/has-available-time', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setHasAvailableTime(timeResponse.data);
      
      // Close modal
      setShowProgressModal(false);
      
    } catch (err) {
      console.error('Error updating progress:', err);
      alert('Failed to update course progress. Please try again.');
    }
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  if (loading) {
    return <Section>Loading your courses...</Section>;
  }

  if (error) {
    return <Section>{error}</Section>;
  }

  return (
    <Section>
      <Title>My Learning Journey</Title>
      
      {stats && (
        <StatsContainer>
          <StatCard>
            <StatValue>{stats.totalCourses}</StatValue>
            <StatLabel>Total Courses</StatLabel>
          </StatCard>
          <StatCard>
            <StatValue>{stats.completedCourses}</StatValue>
            <StatLabel>Completed</StatLabel>
          </StatCard>
          <StatCard>
            <StatValue>{stats.totalHoursSpent}</StatValue>
            <StatLabel>Hours Spent</StatLabel>
          </StatCard>
        </StatsContainer>
      )}
      
      {stats && (
        <ProgressContainer>
          <ProgressTitle>Overall Learning Progress: {stats.overallProgress}%</ProgressTitle>
          <ProgressBar>
            <ProgressFill percentage={stats.overallProgress} />
          </ProgressBar>
          
          <TimeStatus available={hasAvailableTime}>
            {hasAvailableTime 
              ? "You have time available for additional learning this week!" 
              : "You might be busy this week. Consider adjusting your schedule."}
          </TimeStatus>
        </ProgressContainer>
      )}
      
      <TabContainer>
        <Tab 
          active={activeTab === 'ALL'} 
          onClick={() => handleTabChange('ALL')}
        >
          All Courses
        </Tab>
        <Tab 
          active={activeTab === 'IN_PROGRESS'} 
          onClick={() => handleTabChange('IN_PROGRESS')}
        >
          In Progress
        </Tab>
        <Tab 
          active={activeTab === 'COMPLETED'} 
          onClick={() => handleTabChange('COMPLETED')}
        >
          Completed
        </Tab>
        <Tab 
          active={activeTab === 'NOT_STARTED'} 
          onClick={() => handleTabChange('NOT_STARTED')}
        >
          Not Started
        </Tab>
      </TabContainer>
      
      {filteredCourses.length === 0 ? (
        <EmptyState>
          <p>No courses found in this category.</p>
        </EmptyState>
      ) : (
        <CourseContainer>
          {filteredCourses.map(course => (
            <CourseCard key={course.id}>
              <CourseHeader>
                <CourseTitle>{course.courseTitle}</CourseTitle>
                <PlatformBadge>{course.platform}</PlatformBadge>
              </CourseHeader>
              
              <CourseDetails>
                <DetailItem>
                  <DetailLabel>Status</DetailLabel>
                  <DetailValue>
                    <StatusIndicator status={course.status} />
                    {course.status.replace('_', ' ')}
                  </DetailValue>
                </DetailItem>
                
                <DetailItem>
                  <DetailLabel>Enrolled On</DetailLabel>
                  <DetailValue>{formatDate(course.enrollmentDate)}</DetailValue>
                </DetailItem>
                
                <DetailItem>
                  <DetailLabel>Target Completion</DetailLabel>
                  <DetailValue>{formatDate(course.targetCompletionDate)}</DetailValue>
                </DetailItem>
                
                <DetailItem>
                  <DetailLabel>Hours Spent / Estimated</DetailLabel>
                  <DetailValue>{course.hoursSpent} / {course.estimatedHours}</DetailValue>
                </DetailItem>
              </CourseDetails>
              
              <CourseProgress>
                <DetailLabel>Progress: {course.progressPercentage}%</DetailLabel>
                <ProgressBar>
                  <ProgressFill percentage={course.progressPercentage} />
                </ProgressBar>
              </CourseProgress>
              
              <CourseActions>
                <ActionButton 
                  onClick={() => handleUpdateProgress(course.id)}
                  disabled={course.status === 'COMPLETED'}
                >
                  Update Progress
                </ActionButton>
                <OpenLinkButton 
                  href={course.url} 
                  target="_blank" 
                  rel="noopener noreferrer"
                >
                  Open Course
                </OpenLinkButton>
              </CourseActions>
            </CourseCard>
          ))}
        </CourseContainer>
      )}
      
      {showProgressModal && (
        <Modal>
          <ModalContent>
            <h2>Update Course Progress</h2>
            <form onSubmit={handleProgressSubmit}>
              <FormGroup>
                <Label htmlFor="progressPercentage">Progress Percentage</Label>
                <Input
                  type="number"
                  id="progressPercentage"
                  name="progressPercentage"
                  min="0"
                  max="100"
                  value={progressData.progressPercentage}
                  onChange={handleProgressInputChange}
                  required
                />
              </FormGroup>
              
              <FormGroup>
                <Label htmlFor="additionalHoursSpent">Additional Hours Spent</Label>
                <Input
                  type="number"
                  id="additionalHoursSpent"
                  name="additionalHoursSpent"
                  min="0"
                  value={progressData.additionalHoursSpent}
                  onChange={handleProgressInputChange}
                  required
                />
              </FormGroup>
              
              <ButtonGroup>
                <CancelButton type="button" onClick={() => setShowProgressModal(false)}>
                  Cancel
                </CancelButton>
                <SubmitButton type="submit">
                  Update
                </SubmitButton>
              </ButtonGroup>
            </form>
          </ModalContent>
        </Modal>
      )}
    </Section>
  );
};

export default MiddleSection;