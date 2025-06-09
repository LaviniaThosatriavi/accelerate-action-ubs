import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { format } from 'date-fns';
import Calendar from '../components/Calendar';
import GoalItem from '../components/GoalItem';
import ProgressUpdateModal from '../components/ProgressUpdateModal';
import type { Goal, CalendarEvent, EnrolledCourse, ResourceType } from '../types/ToDosTypes';
import { TodoService } from '../service/ToDoService';

const PageContainer = styled.div`
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f8f9fa;
  padding: 20px;
  gap: 5vh;
`;

const TopSection = styled.div`
  display: flex;
  height: 85vh;
  margin-bottom: 5vh;
  
  @media (max-width: 768px) {
    flex-direction: column;
    gap: 20px;
  }
`;

const LeftSection = styled.div`
  flex: 1;
  margin-right: 20px;
  
  @media (max-width: 768px) {
    margin-right: 0;
    margin-bottom: 20px;
  }
`;

const RightSection = styled.div`
  flex: 1;
`;

const CompletedTasksSection = styled.div`
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
`;

const TodayGoalsSection = styled.div`
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  height: 100%;
  overflow-y: auto;
`;

const SectionTitle = styled.h2`
  color: #3367d6;
  margin-top: 0;
  margin-bottom: 20px;
  font-weight: 500;
`;

const NewGoalForm = styled.form`
  margin-bottom: 20px;
  display: grid;
  grid-template-columns: 1fr 120px 150px;
  gap: 10px;
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`;

const InputGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: 5px;
`;

const Label = styled.label`
  font-size: 12px;
  color: #5f6368;
`;

const Input = styled.input`
  padding: 10px 15px;
  border-radius: 4px;
  border: 1px solid #3367d6;
  font-size: 14px;
  background-color: white;
  color: black;
  
  &:focus {
    outline: none;
    border-color: #4285f4;
  }
`;

const Select = styled.select`
  padding: 10px 15px;
  border-radius: 4px;
  border: 1px solid #3367d6;
  font-size: 1.2rem;
  background-color: white;
  color: black;
  
  &:focus {
    outline: none;
    border-color: #4285f4;
  }
`;

const Button = styled.button`
  background-color: #4285f4;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 10px 15px;
  cursor: pointer;
  font-size: 1.2rem;
  font-weight: 500;
  transition: background-color 0.2s;
  
  &:hover {
    background-color: #3367d6;
  }
  
  &:disabled {
    background-color: #dadce0;
    cursor: not-allowed;
  }
`;

const NumberedList = styled.ol`
  padding-left: 20px;
`;

const CompleteTaskForm = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 10px;
  margin-bottom: 15px;
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`;

const MultiSelect = styled.select`
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #3367d6;
  background-color: white;
  color: black;
  font-size: 1.2rem;
  white-space: normal; /* Allow text wrapping */
  word-wrap: break-word; /* Break long words */
  word-break: break-word; 
  max-width: 100%; /* Ensure it doesn't exceed container width */
  
  &:focus {
    outline: none;
    border-color: #4285f4;
  }
  
  option {
    white-space: normal;
    word-wrap: break-word;
    padding: 5px;
  }
`;

const CompletedTasksList = styled.div`
  margin-top: 20px;
`;

const CompletedTitle = styled.h3`
  color: #2d9249;
`;

const EmptyState = styled.div`
  text-align: center;
  color: #5f6368;
  padding: 30px 0;
`;

const LoadingIndicator = styled.div`
  text-align: center;
  color: #4285f4;
  padding: 20px;
`;

const ToDo: React.FC = () => {
  const [, setSelectedDate] = useState<Date>(new Date());
  const [allTodayGoals, setAllTodayGoals] = useState<Goal[]>([]);
  const [activeGoals, setActiveGoals] = useState<Goal[]>([]);
  const [events, setEvents] = useState<CalendarEvent[]>([]);
  const [enrolledCourses, setEnrolledCourses] = useState<EnrolledCourse[]>([]);
  const [selectedCourseId, setSelectedCourseId] = useState<number | undefined>(undefined);
  const [selectedCourse, setSelectedCourse] = useState<EnrolledCourse | null>(null);
  const [showProgressModal, setShowProgressModal] = useState<boolean>(false);
  
  // New goal form state
  const [newGoalTitle, setNewGoalTitle] = useState<string>('');
  const [newGoalHours, setNewGoalHours] = useState<string>('1');
  const [newGoalResourceType, setNewGoalResourceType] = useState<ResourceType>('OTHER');
  const [newGoalCourseId, setNewGoalCourseId] = useState<string>('');
  const [newGoalDescription, setNewGoalDescription] = useState<string>('');
  const [newGoalResourceUrl, setNewGoalResourceUrl] = useState<string>('');
  
  // Complete goals form state
  const [selectedGoalIds, setSelectedGoalIds] = useState<number[]>([]);
  
  const [isLoading, setIsLoading] = useState<boolean>(false);
  
  useEffect(() => {
    console.log("ToDo component mounted");
    fetchData();
  }, []);
  
  const fetchData = async (): Promise<void> => {
    try {
      setIsLoading(true);
      await Promise.all([
        fetchAllTodayGoals(),
        fetchActiveTodayGoals(),
        fetchCalendarMonth(),
        fetchEnrolledCourses()
      ]);
    } catch (error) {
      console.error("Error fetching initial data:", error);
    } finally {
      setIsLoading(false);
    }
  };
  
  const fetchAllTodayGoals = async (): Promise<void> => {
    try {
      const goals = await TodoService.getTodayGoals();
      console.log("All today's goals:", goals);
      setAllTodayGoals(goals);
    } catch (error) {
      console.error('Error fetching all today\'s goals:', error);
    }
  };
  
  const fetchActiveTodayGoals = async (): Promise<void> => {
    try {
      const goals = await TodoService.getActiveTodayGoals();
      console.log("Active today's goals:", goals);
      setActiveGoals(goals);
    } catch (error) {
      console.error('Error fetching active today\'s goals:', error);
    }
  };
  
  const fetchCalendarMonth = async (): Promise<void> => {
    try {
      const calendarData = await TodoService.getCalendarMonth();
      
      // Extract all events from the month data
      const allEvents: CalendarEvent[] = [];
      
      if (calendarData && calendarData.events) {
        // Flatten events from all days
        Object.keys(calendarData.events).forEach(dateKey => {
          const dateEvents = calendarData.events[dateKey];
          if (dateEvents && dateEvents.length > 0) {
            allEvents.push(...dateEvents);
          }
        });
      }
      
      console.log("Extracted calendar events:", allEvents);
      setEvents(allEvents);
    } catch (error) {
      console.error('Error fetching calendar events:', error);
    }
  };
  
  const fetchEnrolledCourses = async (): Promise<void> => {
    try {
      const courses = await TodoService.getEnrolledCourses();
      console.log("Enrolled courses:", courses);
      setEnrolledCourses(courses);
    } catch (error) {
      console.error('Error fetching enrolled courses:', error);
    }
  };
  
  const handleDateSelect = (date: Date): void => {
    setSelectedDate(date);
  };
  
  const handleAddGoal = async (e: React.FormEvent): Promise<void> => {
    e.preventDefault();
    if (!newGoalTitle.trim()) return;
    
    try {
      const newGoal = {
        title: newGoalTitle,
        description: newGoalDescription,
        allocatedHours: parseFloat(newGoalHours) || 1,
        resourceType: newGoalResourceType,
        resourceUrl: newGoalResourceUrl || undefined,
        enrolledCourseId: newGoalCourseId ? parseInt(newGoalCourseId) : undefined
      };
      
      await TodoService.createGoal(newGoal);
      
      // Reset form
      setNewGoalTitle('');
      setNewGoalDescription('');
      setNewGoalHours('1');
      setNewGoalResourceType('OTHER');
      setNewGoalResourceUrl('');
      setNewGoalCourseId('');
      
      // Refresh goals
      await Promise.all([
        fetchAllTodayGoals(),
        fetchActiveTodayGoals()
      ]);
    } catch (error) {
      console.error('Error adding goal:', error);
    }
  };
  
  const handleCourseSelect = (e: React.ChangeEvent<HTMLSelectElement>): void => {
    const value = e.target.value;
    if (value) {
      const courseId = parseInt(value);
      setSelectedCourseId(courseId);
    } else {
      setSelectedCourseId(undefined);
    }
  };
  
  const handleGoalSelect = (e: React.ChangeEvent<HTMLSelectElement>): void => {
    const selected = Array.from(e.target.selectedOptions, option => parseInt(option.value));
    setSelectedGoalIds(selected);
  };
  
  const handleCompleteGoalsClick = (): void => {
    console.log("Complete goals clicked with selection:", selectedGoalIds);
    
    if (selectedGoalIds.length === 0) {
      alert("Please select at least one goal to complete");
      return;
    }
    
    if (selectedCourseId) {
      console.log("Selected course ID:", selectedCourseId);
      const course = enrolledCourses.find(c => c.id === selectedCourseId);
      if (course) {
        console.log("Found course for progress update:", course);
        setSelectedCourse(course);
        setShowProgressModal(true);
      } else {
        console.error('Selected course not found:', selectedCourseId);
        alert("Selected course not found. Please try again.");
      }
    } else {
      // No course selected, complete goals without updating course progress
      console.log("No course selected, completing goals without progress update");
      completeGoalsAndUpdateProgress(selectedGoalIds);
    }
  };
  
  const completeGoalsAndUpdateProgress = async (goalIds: number[], progressUpdate?: { courseId: number, percentage: number, hours: number }): Promise<void> => {
    try {
      console.log("Completing goals:", goalIds);
      
      // 1. Immediately update UI to remove completed goals from active list
      setActiveGoals(prevGoals => {
        const updatedGoals = prevGoals.filter(goal => !goalIds.includes(goal.id));
        console.log("Updated active goals:", updatedGoals);
        return updatedGoals;
      });
      
      // 2. Update all today's goals to mark selected goals as completed
      setAllTodayGoals(prevGoals => {
        const updatedGoals = prevGoals.map(goal => 
          goalIds.includes(goal.id) 
            ? { ...goal, isCompleted: true, completedAt: new Date().toISOString() } 
            : goal
        );
        console.log("Updated all today's goals:", updatedGoals);
        return updatedGoals;
      });
      
      // 3. Clear selection
      setSelectedGoalIds([]);
      
      // 4. Make the API call to complete goals
      const completedGoalsResponse = await TodoService.completeGoals(goalIds);
      console.log("Complete goals API response:", completedGoalsResponse);
      
      // 5. If progress update is needed
      if (progressUpdate) {
        console.log("Updating course progress:", progressUpdate);
        await TodoService.updateProgress({
          enrolledCourseId: progressUpdate.courseId,
          progressPercentage: progressUpdate.percentage,
          additionalHoursSpent: progressUpdate.hours
        });
      }
      
      // 6. Close modal if open
      setShowProgressModal(false);
      setSelectedCourse(null);
      
      // 7. Refresh data to ensure sync with backend
      await Promise.all([
        fetchAllTodayGoals(),
        fetchActiveTodayGoals(),
        fetchCalendarMonth(),
        fetchEnrolledCourses()
      ]);
      
    } catch (error) {
      console.error("Error in goal completion process:", error);
      alert("There was an error completing the selected goals. Please try again.");
      
      // Revert state changes on error
      fetchActiveTodayGoals();
      fetchAllTodayGoals();
    }
  };
  
  const handleProgressModalSubmit = (progressPercentage: number, additionalHoursSpent: number): void => {
    if (!selectedCourse) {
      console.error("No course selected for progress update");
      return;
    }
    
    completeGoalsAndUpdateProgress(selectedGoalIds, {
      courseId: selectedCourse.id,
      percentage: progressPercentage,
      hours: additionalHoursSpent
    });
  };
  
  const handleProgressModalClose = (): void => {
    setShowProgressModal(false);
    setSelectedCourse(null);
  };
  
  const getCourseNameById = (courseId?: number): string => {
    if (!courseId) return '';
    const course = enrolledCourses.find(c => c.id === courseId);
    return course ? course.courseTitle : '';
  };
  
  const completedGoals = allTodayGoals.filter(goal => goal.isCompleted);
  
  return (
    <PageContainer>
      <TopSection>
        <LeftSection>
          <Calendar onDateSelect={handleDateSelect} events={events} />
        </LeftSection>
        
        <RightSection>
          <TodayGoalsSection>
            <SectionTitle>Today's Goals - {format(new Date(), 'MMMM d, yyyy')}</SectionTitle>
            
            <NewGoalForm onSubmit={handleAddGoal}>
              <InputGroup>
                <Label htmlFor="goalTitle">Title</Label>
                <Input 
                  id="goalTitle"
                  type="text" 
                  placeholder="Add a new goal..." 
                  value={newGoalTitle}
                  onChange={(e) => setNewGoalTitle(e.target.value)}
                  required
                />
              </InputGroup>
              
              <InputGroup>
                <Label htmlFor="goalHours">Hours</Label>
                <Input 
                  id="goalHours"
                  type="number" 
                  placeholder="Hours" 
                  min="0.5"
                  step="0.5"
                  value={newGoalHours}
                  onChange={(e) => setNewGoalHours(e.target.value)}
                  required
                />
              </InputGroup>
              
              <InputGroup>
                <Label htmlFor="goalResourceType">Resource Type</Label>
                <Select 
                  id="goalResourceType"
                  value={newGoalResourceType}
                  onChange={(e) => setNewGoalResourceType(e.target.value as ResourceType)}
                  required
                >
                  <option value="DOCUMENTATION">Documentation</option>
                  <option value="COURSE">Course</option>
                  <option value="VIDEO">Video</option>
                  <option value="ARTICLE">Article</option>
                  <option value="OTHER">Other</option>
                </Select>
              </InputGroup>
              
              <InputGroup>
                <Label htmlFor="goalDescription">Description</Label>
                <Input 
                  id="goalDescription"
                  type="text" 
                  placeholder="Description (optional)" 
                  value={newGoalDescription}
                  onChange={(e) => setNewGoalDescription(e.target.value)}
                />
              </InputGroup>
              
              <InputGroup>
                <Label htmlFor="goalResourceUrl">Resource URL</Label>
                <Input 
                  id="goalResourceUrl"
                  type="url" 
                  placeholder="Resource URL (optional)" 
                  value={newGoalResourceUrl}
                  onChange={(e) => setNewGoalResourceUrl(e.target.value)}
                />
              </InputGroup>
              
              <InputGroup>
                <Label htmlFor="goalCourse">Related Course</Label>
                <Select 
                  id="goalCourse"
                  value={newGoalCourseId}
                  onChange={(e) => setNewGoalCourseId(e.target.value)}
                >
                  <option value="">-- No course --</option>
                  {enrolledCourses.map(course => (
                    <option key={course.id} value={course.id.toString()}>
                      {course.courseTitle}
                    </option>
                  ))}
                </Select>
              </InputGroup>
              
              <Button type="submit">Add Goal</Button>
            </NewGoalForm>
            
            {isLoading ? (
              <LoadingIndicator>Loading goals...</LoadingIndicator>
            ) : activeGoals.length > 0 ? (
              <NumberedList>
                {activeGoals.map(goal => (
                  <li key={goal.id}>
                    <GoalItem 
                      goal={goal} 
                      courseName={getCourseNameById(goal.enrolledCourseId)}
                    />
                  </li>
                ))}
              </NumberedList>
            ) : (
              <EmptyState>No active goals for today. Add one to get started!</EmptyState>
            )}
          </TodayGoalsSection>
        </RightSection>
      </TopSection>
      
      <CompletedTasksSection>
        <SectionTitle>Complete Tasks</SectionTitle>
        
        <CompleteTaskForm>
          <MultiSelect 
            multiple 
            size={3} 
            value={selectedGoalIds.map(String)} 
            onChange={handleGoalSelect}
          >
            {activeGoals.map(goal => (
              <option key={goal.id} value={goal.id}>
                {goal.title} {goal.enrolledCourseId ? `(${getCourseNameById(goal.enrolledCourseId) || goal.courseName})` : ''}
              </option>
            ))}
          </MultiSelect>
          
          <Select 
            value={selectedCourseId?.toString() || ''} 
            onChange={handleCourseSelect}
          >
            <option value="">-- No course --</option>
            {enrolledCourses.map(course => (
              <option key={course.id} value={course.id.toString()}>
                {course.courseTitle}
              </option>
            ))}
          </Select>
          
          <Button 
            onClick={handleCompleteGoalsClick}
            disabled={selectedGoalIds.length === 0}
          >
            Mark as Completed
          </Button>
        </CompleteTaskForm>
        
        <CompletedTasksList>
          <CompletedTitle>Completed Today:</CompletedTitle>
          {completedGoals.length > 0 ? (
            completedGoals.map(goal => (
              <GoalItem 
                key={goal.id} 
                goal={goal} 
                courseName={getCourseNameById(goal.enrolledCourseId) || goal.courseName}
              />
            ))
          ) : (
            <EmptyState>No completed tasks yet for today.</EmptyState>
          )}
        </CompletedTasksList>
      </CompletedTasksSection>
      
      {selectedCourse && showProgressModal && (
        <ProgressUpdateModal 
          enrolledCourse={selectedCourse}
          onClose={handleProgressModalClose}
          onSubmit={handleProgressModalSubmit}
        />
      )}
    </PageContainer>
  );
};

export default ToDo;