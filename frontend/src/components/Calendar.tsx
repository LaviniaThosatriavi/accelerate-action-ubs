import React, { useState, useEffect, useCallback } from 'react';
import styled from 'styled-components';
import { format, addMonths, subMonths, startOfMonth, endOfMonth, eachDayOfInterval, isSameMonth, isSameDay, startOfWeek, endOfWeek } from 'date-fns';
import axios from 'axios';

const CalendarContainer = styled.div`
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
`;

const CalendarHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
`;

const MonthTitle = styled.h2`
  color: #3367d6;
  margin: 0;
  font-weight: 500;
`;

const NavigationButton = styled.button`
  background-color: #f1f3f4;
  border: none;
  color: #3367d6;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.2s;
  
  &:hover {
    background-color: #dadce0;
  }
`;

const WeekdaysRow = styled.div`
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-bottom: 10px;
`;

const Weekday = styled.div`
  text-align: center;
  font-weight: 500;
  color: #5f6368;
  font-size: 14px;
`;

const DaysGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
`;

interface DayProps {
  $isCurrentMonth: boolean;
  $isToday: boolean;
  $hasEvents: boolean;
  $isSelected: boolean;
}

const Day = styled.div<DayProps>`
  aspect-ratio: 1;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 5px;
  cursor: pointer;
  background-color: ${props => 
    props.$isSelected ? '#e8f0fe' : 
    props.$isToday ? '#f1f3f4' : 'white'};
  color: ${props => 
    !props.$isCurrentMonth ? '#dadce0' : 
    props.$isToday ? '#3367d6' : 'black'};
  position: relative;
  
  &:hover {
    background-color: #f1f3f4;
  }
`;

const EventMarker = styled.span`
  color: #db2b45;
  font-weight: bold;
  font-size: 14px;
  position: absolute;
  bottom: 2px;
`;

const EventsDetails = styled.div`
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #dadce0;
  flex-grow: 1;
  overflow-y: auto;
`;

const EventItem = styled.div`
  padding: 10px;
  margin-bottom: 8px;
  border-radius: 4px;
  background-color: #f1f3f4;
  border-left: 3px solid #db2b45;
  
  h4 {
    margin: 0 0 5px 0;
    color: #3367d6;
  }
  
  p {
    margin: 0;
    font-size: 14px;
    color: #5f6368;
  }
`;

const NoEventsMessage = styled.p`
  text-align: center;
  color: #5f6368;
  font-style: italic;
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
    } catch {
      return null;
    }
  }, [getAuthHeader]);

  const fetchAllRelevantData = useCallback(async (date: Date) => {
    const currentMonth = date;
    const prevMonth = subMonths(date, 1);
    const nextMonth = addMonths(date, 1);

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
    } catch {
      // Handle error silently or add proper error handling
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
    
    // Get API events
    const year = selectedDate.getFullYear();
    const month = (selectedDate.getMonth() + 1).toString().padStart(2, '0');
    const monthKey = `${year}-${month}`;
    const monthData = adjacentMonthsData[monthKey];
    const apiEvents = monthData?.events?.[dateString] || [];
    
    // Get prop events
    const propEventMatches = propEvents ? propEvents.filter(event => event.eventDate === dateString) : [];
    
    // Combine both sources
    const allEvents = [...apiEvents, ...propEventMatches];
    return allEvents;
  }, [selectedDate, propEvents, adjacentMonthsData]);

  const weekdays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
  const selectedDateEvents = getEventsForSelectedDate();

  return (
    <CalendarContainer>
      <div>
        <CalendarHeader>
          <NavigationButton onClick={() => handleMonthChange('prev')}>
            &lt;
          </NavigationButton>
          <MonthTitle>{format(currentDate, 'MMMM yyyy')}</MonthTitle>
          <NavigationButton onClick={() => handleMonthChange('next')}>
            &gt;
          </NavigationButton>
        </CalendarHeader>

        <WeekdaysRow>
          {weekdays.map(day => (
            <Weekday key={day}>{day}</Weekday>
          ))}
        </WeekdaysRow>

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
      </div>

      <EventsDetails>
        <h3>Course Deadlines for {format(selectedDate, 'MMMM d, yyyy')}</h3>
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