import React, { useState, useEffect, useCallback } from 'react';
import styled from 'styled-components';
import { format, addMonths, subMonths, startOfMonth, endOfMonth, eachDayOfInterval, isSameMonth, isSameDay, startOfWeek, endOfWeek } from 'date-fns';
import axios from 'axios';

const CalendarContainer = styled.div`
  background-color: white;
  border-radius: clamp(6px, 1.5vw, 8px);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  padding: clamp(15px, 3vw, 20px);
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: clamp(400px, 60vh, 600px);
  
  @media (max-width: 768px) {
    min-height: clamp(350px, 50vh, 500px);
  }
`;

const CalendarHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: clamp(15px, 3vw, 20px);
`;

const MonthTitle = styled.h2`
  color: #3367d6;
  margin: 0;
  font-weight: 500;
  font-size: clamp(1.125rem, 3vw, 1.5rem);
  text-align: center;
  flex: 1;
  
  @media (max-width: 480px) {
    font-size: clamp(1rem, 4vw, 1.25rem);
  }
`;

const NavigationButton = styled.button`
  background-color: #f1f3f4;
  border: none;
  color: #3367d6;
  width: clamp(32px, 6vw, 40px);
  height: clamp(32px, 6vw, 40px);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: clamp(14px, 2.5vw, 18px);
  font-weight: bold;
  
  &:hover {
    background-color: #dadce0;
    transform: scale(1.05);
  }
  
  &:active {
    transform: scale(0.95);
  }
`;

const WeekdaysRow = styled.div`
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-bottom: clamp(8px, 2vw, 12px);
  gap: clamp(2px, 1vw, 4px);
`;

const Weekday = styled.div`
  text-align: center;
  font-weight: 500;
  color: #5f6368;
  font-size: clamp(11px, 2.2vw, 14px);
  padding: clamp(4px, 1vw, 6px);
  
  @media (max-width: 480px) {
    font-size: clamp(10px, 2vw, 12px);
  }
`;

const DaysGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: clamp(4px, 1.5vw, 8px);
  flex-grow: 1;
`;

interface DayProps {
  $isCurrentMonth: boolean;
  $isToday: boolean;
  $hasEvents: boolean;
  $isSelected: boolean;
}

const Day = styled.div<DayProps>`
  aspect-ratio: 1;
  border-radius: clamp(6px, 1.5vw, 8px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: clamp(3px, 1vw, 5px);
  cursor: pointer;
  background-color: ${props => 
    props.$isSelected ? '#e8f0fe' : 
    props.$isToday ? '#f1f3f4' : 'white'};
  color: ${props => 
    !props.$isCurrentMonth ? '#dadce0' : 
    props.$isToday ? '#3367d6' : 'black'};
  position: relative;
  font-size: clamp(12px, 2.5vw, 16px);
  font-weight: ${props => props.$isToday ? 'bold' : 'normal'};
  transition: all 0.2s ease;
  min-height: clamp(32px, 6vw, 48px);
  
  &:hover {
    background-color: #f1f3f4;
    transform: scale(1.05);
  }
  
  &:active {
    transform: scale(0.95);
  }
  
  @media (max-width: 480px) {
    font-size: clamp(10px, 2.2vw, 14px);
    min-height: clamp(28px, 5vw, 40px);
  }
`;

const EventMarker = styled.span`
  color: #db2b45;
  font-weight: bold;
  font-size: clamp(12px, 3vw, 16px);
  position: absolute;
  bottom: clamp(1px, 0.5vw, 3px);
  
  @media (max-width: 480px) {
    font-size: clamp(10px, 2.5vw, 14px);
  }
`;

const EventsDetails = styled.div`
  margin-top: clamp(15px, 3vw, 20px);
  padding-top: clamp(12px, 2.5vw, 15px);
  border-top: 1px solid #dadce0;
  flex-grow: 1;
  overflow-y: auto;
  min-height: 0;
`;

const EventTitle = styled.h3`
  color: black;
  margin-top: 0;
  margin-bottom: clamp(10px, 2vw, 15px);
  font-size: clamp(0.9rem, 2.5vw, 1.125rem);
  line-height: 1.3;
  
  @media (max-width: 480px) {
    font-size: clamp(0.8rem, 2.2vw, 1rem);
  }
`;

const EventItem = styled.div`
  padding: clamp(8px, 2vw, 10px);
  margin-bottom: clamp(6px, 1.5vw, 8px);
  border-radius: clamp(3px, 1vw, 4px);
  background-color: #f1f3f4;
  border-left: clamp(2px, 0.5vw, 3px) solid #db2b45;
  transition: all 0.2s ease;
  
  &:hover {
    background-color: #e8eaed;
    transform: translateX(2px);
  }
  
  h4 {
    margin: 0 0 clamp(3px, 1vw, 5px) 0;
    color: #3367d6;
    font-size: clamp(0.8rem, 2.2vw, 1rem);
    line-height: 1.3;
  }
  
  p {
    margin: 0;
    font-size: clamp(0.75rem, 2vw, 0.875rem);
    color: #5f6368;
    line-height: 1.4;
    
    &:not(:last-child) {
      margin-bottom: clamp(2px, 0.5vw, 4px);
    }
    
    strong {
      font-weight: 600;
      color: #202124;
    }
  }
  
  @media (max-width: 480px) {
    padding: clamp(6px, 1.5vw, 8px);
    
    h4 {
      font-size: clamp(0.75rem, 2vw, 0.9rem);
    }
    
    p {
      font-size: clamp(0.7rem, 1.8vw, 0.8rem);
    }
  }
`;

const NoEventsMessage = styled.p`
  text-align: center;
  color: #5f6368;
  font-style: italic;
  font-size: clamp(0.8rem, 2vw, 0.9rem);
  padding: clamp(15px, 3vw, 20px);
  margin: 0;
  
  @media (max-width: 480px) {
    font-size: clamp(0.75rem, 1.8vw, 0.85rem);
    padding: clamp(12px, 2.5vw, 15px);
  }
`;

// Loading state component
const LoadingContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  height: clamp(100px, 20vh, 150px);
  color: #3367d6;
  font-size: clamp(0.8rem, 2vw, 1rem);
`;

// Error state component
const ErrorMessage = styled.div`
  text-align: center;
  color: #db2b45;
  font-size: clamp(0.8rem, 2vw, 0.9rem);
  padding: clamp(10px, 2vw, 15px);
  background-color: #fdf2f2;
  border-radius: clamp(4px, 1vw, 6px);
  margin: clamp(10px, 2vw, 15px) 0;
`;

interface CalendarEvent {
  eventDate: string;
  title: string;
  description: string;
  eventType: string;
  enrolledCourseId: number;
  courseTitle: string;
}

interface CalendarMonthData {
  year: number;
  month: number;
  events: { [key: string]: CalendarEvent[] };
}

interface CalendarProps {
  onDateSelect?: (date: Date) => void;
  events?: CalendarEvent[];
}

const Calendar: React.FC<CalendarProps> = ({ onDateSelect, events: propEvents }) => {
  const [currentDate, setCurrentDate] = useState<Date>(new Date());
  const [calendarDays, setCalendarDays] = useState<Date[]>([]);
  const [selectedDate, setSelectedDate] = useState<Date>(new Date());
  const [adjacentMonthsData, setAdjacentMonthsData] = useState<{ [key: string]: CalendarMonthData }>({});
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const getAuthHeader = useCallback(() => {
    const token = localStorage.getItem('token');
    return {
      Authorization: `Bearer ${token}`
    };
  }, []);

  const fetchCalendarData = useCallback(async (date: Date): Promise<CalendarMonthData | null> => {
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const url = `/api/calendar/month?year=${year}&month=${month}`;
    
    const headers = getAuthHeader();
    
    try {
      const response = await axios.get<CalendarMonthData>(url, { headers });
      return response.data;
    } catch (err) {
      console.error('Error fetching calendar data:', err);
      return null;
    }
  }, [getAuthHeader]);

  const fetchAllRelevantData = useCallback(async (date: Date) => {
    const currentMonth = date;
    const prevMonth = subMonths(date, 1);
    const nextMonth = addMonths(date, 1);

    setIsLoading(true);
    setError(null);

    try {
      const [currentData, prevData, nextData] = await Promise.all([
        fetchCalendarData(currentMonth),
        fetchCalendarData(prevMonth),
        fetchCalendarData(nextMonth)
      ]);

      const allMonthsData: { [key: string]: CalendarMonthData } = {};
      const successfulFetches = [prevData, currentData, nextData].filter(Boolean);
      
      successfulFetches.forEach(data => {
        if (data) {
          const key = `${data.year}-${data.month.toString().padStart(2, '0')}`;
          allMonthsData[key] = data;
        }
      });
      
      setAdjacentMonthsData(allMonthsData);
    } catch (err) {
      setError('Failed to load calendar data');
      console.error('Error fetching calendar data:', err);
    } finally {
      setIsLoading(false);
    }
  }, [fetchCalendarData]);

  useEffect(() => {
    fetchAllRelevantData(currentDate);
  }, [currentDate, fetchAllRelevantData]);

  useEffect(() => {
    const generateCalendarGrid = () => {
      const monthStart = startOfMonth(currentDate);
      const monthEnd = endOfMonth(currentDate);
      const calendarStart = startOfWeek(monthStart);
      const calendarEnd = endOfWeek(monthEnd);
      
      const daysInCalendar = eachDayOfInterval({ start: calendarStart, end: calendarEnd });
      setCalendarDays(daysInCalendar);
    };

    generateCalendarGrid();
  }, [currentDate]);

  const handleMonthChange = useCallback(async (direction: 'next' | 'prev') => {
    const newDate = direction === 'next' ? addMonths(currentDate, 1) : subMonths(currentDate, 1);
    setCurrentDate(newDate);
    await fetchAllRelevantData(newDate);
  }, [currentDate, fetchAllRelevantData]);

  const handleDateClick = useCallback((date: Date) => {
    setSelectedDate(date);
    onDateSelect?.(date);
  }, [onDateSelect]);

  const hasEventsOnDate = useCallback((date: Date): boolean => {
    const dateString = format(date, 'yyyy-MM-dd');
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const monthKey = `${year}-${month}`;

    // Check API data first (always available after fetch)
    const monthData = adjacentMonthsData[monthKey];
    const apiEvents = monthData?.events?.[dateString]?.length || 0;
    
    // Check prop events (if any) for the same date
    const propEventMatches = propEvents ? propEvents.filter(e => e.eventDate === dateString).length : 0;
    
    // Combine both sources
    const totalEvents = apiEvents + propEventMatches;
    return totalEvents > 0;
  }, [propEvents, adjacentMonthsData]);

  const getEventsForSelectedDate = useCallback((): CalendarEvent[] => {
    const dateString = format(selectedDate, 'yyyy-MM-dd');
    
    const year = selectedDate.getFullYear();
    const month = (selectedDate.getMonth() + 1).toString().padStart(2, '0');
    const monthKey = `${year}-${month}`;
    const monthData = adjacentMonthsData[monthKey];
    const apiEvents = monthData?.events?.[dateString] || [];
    
    // Get prop events
    const propEventMatches = propEvents ? propEvents.filter(event => event.eventDate === dateString) : [];
    
    const allEvents = [...apiEvents, ...propEventMatches];
    
    // Using a combination of enrolledCourseId, title, and eventDate as the unique key
    const uniqueEvents = allEvents.reduce((acc, event) => {
        const uniqueKey = `${event.enrolledCourseId}-${event.title}-${event.eventDate}`;
        if (!acc.some(existingEvent => 
        `${existingEvent.enrolledCourseId}-${existingEvent.title}-${existingEvent.eventDate}` === uniqueKey
        )) {
        acc.push(event);
        }
        return acc;
    }, [] as CalendarEvent[]);
    
    return uniqueEvents;
    }, [selectedDate, propEvents, adjacentMonthsData]);

  const weekdays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
  const selectedDateEvents = getEventsForSelectedDate();

  if (error) {
    return (
      <CalendarContainer>
        <ErrorMessage>{error}</ErrorMessage>
      </CalendarContainer>
    );
  }

  return (
    <CalendarContainer>
      <div>
        <CalendarHeader>
          <NavigationButton onClick={() => handleMonthChange('prev')}>
            ‹
          </NavigationButton>
          <MonthTitle>{format(currentDate, 'MMMM yyyy')}</MonthTitle>
          <NavigationButton onClick={() => handleMonthChange('next')}>
            ›
          </NavigationButton>
        </CalendarHeader>

        <WeekdaysRow>
          {weekdays.map(day => (
            <Weekday key={day}>{day}</Weekday>
          ))}
        </WeekdaysRow>

        {isLoading ? (
          <LoadingContainer>Loading calendar...</LoadingContainer>
        ) : (
          <DaysGrid>
            {calendarDays.map(day => {
              const hasEvents = hasEventsOnDate(day);
              const isCurrentMonth = isSameMonth(day, currentDate);
              
              return (
                <Day
                  key={day.toString()}
                  $isCurrentMonth={isCurrentMonth}
                  $isToday={isSameDay(day, new Date())}
                  $hasEvents={hasEvents}
                  $isSelected={isSameDay(day, selectedDate)}
                  onClick={() => handleDateClick(day)}
                >
                  {format(day, 'd')}
                  {hasEvents && <EventMarker>•</EventMarker>}
                </Day>
              );
            })}
          </DaysGrid>
        )}
      </div>

      <EventsDetails>
        <EventTitle>Course Deadlines for {format(selectedDate, 'MMMM d, yyyy')}</EventTitle>
        {selectedDateEvents.length > 0 ? (
          selectedDateEvents.map((event, index) => (
            <EventItem key={`${event.enrolledCourseId}-${index}`}>
              <h4>{event.courseTitle}</h4>
              <p>{event.description}</p>
              <p><strong>Status:</strong> {event.eventType}</p>
            </EventItem>
          ))
        ) : (
          <NoEventsMessage>No course deadlines for this date</NoEventsMessage>
        )}
      </EventsDetails>
    </CalendarContainer>
  );
};

export default Calendar;