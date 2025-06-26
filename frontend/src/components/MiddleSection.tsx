import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import { useWeeklyHours } from './WeeklyHoursContext';
import { API_BASE_URL } from '../config/api';
import type { EnrollmentStats, ProgressUpdateData, CourseScore, CourseScoreRequest } from '../types/MiddleSectionTypes';
import type { EnrolledCourse } from '../types/ToDosTypes';
import { ActionButton, ButtonGroup, CancelButton, CourseActions, CourseCard, CourseContainer, CourseDetails, CourseHeader, CourseProgress, CourseTitle, DetailItem, DetailLabel, DetailValue, EmptyState, FormGroup, HelpText, Input, Label, Modal, ModalContent, ModalTitle, OpenLinkButton, PlatformBadge, ProgressBar, ProgressContainer, ProgressFill, ProgressTitle, ScoreBadge, ScoreButton, ScoreDisplay, StatCard, StatLabel, StatsContainer, StatusIndicator, StatValue, SubmitButton, Tab, TabContainer, TimeStatus, Title, WeeklyHoursContainer, WeeklyHoursContent, WeeklyHoursLabel, WeeklyHoursValue } from '../styles/MiddleSectionStyles';
import { Section } from './CoursePlanner';

const MiddleSection: React.FC = () => {
    const { setWeeklyHours } = useWeeklyHours();
    
    const [enrolledCourses, setEnrolledCourses] = useState<EnrolledCourse[]>([]);
    const [filteredCourses, setFilteredCourses] = useState<EnrolledCourse[]>([]);
    const [activeTab, setActiveTab] = useState<string>('ALL');
    const [stats, setStats] = useState<EnrollmentStats | null>(null);
    const [weeklyHours, setLocalWeeklyHours] = useState<number>(0);
    const [hasAvailableTime, setHasAvailableTime] = useState<boolean>(false);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>('');
    const [showProgressModal, setShowProgressModal] = useState<boolean>(false);
    const [showScoreModal, setShowScoreModal] = useState<boolean>(false);
    const [courseScores, setCourseScores] = useState<Map<number, CourseScore>>(new Map());
    const [progressData, setProgressData] = useState<ProgressUpdateData>({
        enrolledCourseId: 0,
        progressPercentage: 0,
        additionalHoursSpent: 0
    });
    const [scoreData, setScoreData] = useState<CourseScoreRequest>({
        enrolledCourseId: 0,     
        score: 0,
        completionDate: new Date().toISOString()  
    });

    const updateWeeklyHours = useCallback((hours: number) => {
        setLocalWeeklyHours(hours);
        setWeeklyHours(hours);
    }, [setWeeklyHours]);

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
                
                // Update all API calls to use full URL
                const coursesResponse = await axios.get(`${API_BASE_URL}/api/enrolled-courses`, { headers });
                setEnrolledCourses(coursesResponse.data);
                setFilteredCourses(coursesResponse.data);
                
                const statsResponse = await axios.get(`${API_BASE_URL}/api/enrolled-courses/stats`, { headers });
                setStats(statsResponse.data);
                
                const weeklyHoursResponse = await axios.get(`${API_BASE_URL}/api/enrolled-courses/total-hours-this-week`, { headers });
                updateWeeklyHours(weeklyHoursResponse.data);

                const timeResponse = await axios.get(`${API_BASE_URL}/api/enrolled-courses/has-available-time`, { headers });
                setHasAvailableTime(timeResponse.data);

                const scoresResponse = await axios.get(`${API_BASE_URL}/api/course-scores/user-scores`, { headers });
                
                const scoresMap = new Map<number, CourseScore>();
                scoresResponse.data.forEach((score: CourseScore) => {
                    const courseId = score.courseId || score.enrolledCourse?.id;
                    if (courseId) {
                        scoresMap.set(courseId, score);
                    }
                });
                
                setCourseScores(scoresMap);
                
            } catch (err) {
                console.error('Error fetching data:', err);
                setError('Failed to load your courses. Please try again later.');
            } finally {
                setLoading(false);
            }
        };
        
        fetchData();
    }, [updateWeeklyHours]);

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
            setProgressData({
                enrolledCourseId: courseId,
                progressPercentage: course.progressPercentage,
                additionalHoursSpent: 0
            });
            setShowProgressModal(true);
        }
    };

    const handleEnterScore = (course: EnrolledCourse) => {
        // Only allow if score doesn't exist
        const courseScore = courseScores.get(course.id);
        if (courseScore) {
            return; // Do nothing if score already exists
        }
        
        setScoreData({
            enrolledCourseId: course.id, 
            score: 0,
            completionDate: new Date().toISOString()
        });
        setShowScoreModal(true);
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

    const handleScoreInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setScoreData({
            ...scoreData,
            [name]: name === 'score' ? Math.max(0, parseInt(value) || 0) : value
        });
    };

    const handleProgressSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const token = localStorage.getItem('token');
            const response = await axios.put(`${API_BASE_URL}/api/enrolled-courses/progress`, progressData, {
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
            const statsResponse = await axios.get(`${API_BASE_URL}/api/enrolled-courses/stats`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setStats(statsResponse.data);
            
            // Refresh weekly hours after progress update
            const weeklyHoursResponse = await axios.get(`${API_BASE_URL}/api/enrolled-courses/total-hours-this-week`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            updateWeeklyHours(weeklyHoursResponse.data);
            
            // Check if user still has available time
            const timeResponse = await axios.get(`${API_BASE_URL}/api/enrolled-courses/has-available-time`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setHasAvailableTime(timeResponse.data);
            
            setShowProgressModal(false);
            
        } catch (err) {
            console.error('Error updating progress:', err);
            alert('Failed to update course progress. Please try again.');
        }
    };

    const handleScoreSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const token = localStorage.getItem('token');
            
            const response = await axios.post(`${API_BASE_URL}/api/course-scores`, scoreData, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            
            // Update the course scores map
            const newScore = response.data;
            const courseId = newScore.courseId || newScore.enrolledCourse?.id || scoreData.enrolledCourseId;
            
            setCourseScores(prev => {
                const updated = new Map(prev);
                updated.set(courseId, newScore);
                return updated;
            });

            // Close modal
            setShowScoreModal(false);
            
        } catch (err) {
            console.error('Error recording course score:', err);
            
            if (axios.isAxiosError(err)) {
                console.error('Error response:', err.response?.data);
                const errorMessage = err.response?.data?.message || err.message || 'Failed to record course score';
                alert(`Error: ${errorMessage}. Please try again.`);
            } else {
                console.error('Unknown error:', err);
                alert('An unexpected error occurred. Please try again.');
            }
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
            
            <WeeklyHoursContainer>
                <WeeklyHoursContent>
                    <WeeklyHoursValue>{weeklyHours}</WeeklyHoursValue>
                    <WeeklyHoursLabel>Hours of Learning This Week</WeeklyHoursLabel>
                </WeeklyHoursContent>
            </WeeklyHoursContainer>
            
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
                    {filteredCourses.map(course => {
                        const courseScore = courseScores.get(course.id);
                        
                        return (
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
                                    {courseScore && (
                                        <ScoreDisplay>
                                            <DetailLabel>Final Score:</DetailLabel>
                                            <ScoreBadge>
                                                {courseScore.score}/100 ({courseScore.percentage}%)
                                            </ScoreBadge>
                                        </ScoreDisplay>
                                    )}
                                </CourseProgress>
                                
                                <CourseActions>
                                    {course.status === 'COMPLETED' ? (
                                        <ScoreButton 
                                            onClick={() => handleEnterScore(course)}
                                            disabled={!!courseScore}
                                        >
                                            {courseScore ? 'Score Entered' : 'Enter Score'}
                                        </ScoreButton>
                                    ) : (
                                        <ActionButton onClick={() => handleUpdateProgress(course.id)}>
                                            Update Progress
                                        </ActionButton>
                                    )}
                                    <OpenLinkButton 
                                        href={course.url} 
                                        target="_blank" 
                                        rel="noopener noreferrer"
                                    >
                                        Open Course
                                    </OpenLinkButton>
                                </CourseActions>
                            </CourseCard>
                        );
                    })}
                </CourseContainer>
            )}
            
            {showProgressModal && (
                <Modal>
                    <ModalContent>
                        <ModalTitle>Update Course Progress</ModalTitle>
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

            {showScoreModal && (
                <Modal>
                    <ModalContent>
                        <ModalTitle>Enter Course Score</ModalTitle>
                        <form onSubmit={handleScoreSubmit}>
                            <FormGroup>
                                <Label htmlFor="score">Your Score (out of 100)</Label>
                                <Input
                                    type="number"
                                    id="score"
                                    name="score"
                                    min="0"
                                    max="100"
                                    value={scoreData.score}
                                    onChange={handleScoreInputChange}
                                    required
                                />
                                <HelpText>
                                    Please convert your score to out of 100 (e.g., 85/100 = 85, 17/20 = 85)
                                </HelpText>
                            </FormGroup>
                            
                            <ButtonGroup>
                                <CancelButton type="button" onClick={() => setShowScoreModal(false)}>
                                    Cancel
                                </CancelButton>
                                <SubmitButton type="submit">
                                    Save Score
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