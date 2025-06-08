// src/components/Calendar.tsx
import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { format, addMonths, subMonths, startOfMonth, endOfMonth, eachDayOfInterval, isSameMonth, isSameDay } from 'date-fns';
import type { CalendarEvent } from '../types/ToDosTypes';

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

interface CalendarProps {
  onDateSelect: (date: Date) => void;
  events: CalendarEvent[];
}

const Calendar: React.FC<CalendarProps> = ({ onDateSelect, events }) => {
  const [currentDate, setCurrentDate] = useState<Date>(new Date());
  const [calendarDays, setCalendarDays] = useState<Date[]>([]);
  const [selectedDate, setSelectedDate] = useState<Date>(new Date());
  
  useEffect(() => {
    const monthStart = startOfMonth(currentDate);
    const monthEnd = endOfMonth(currentDate);
    const daysInMonth = eachDayOfInterval({ start: monthStart, end: monthEnd });
    setCalendarDays(daysInMonth);
  }, [currentDate]);
  
  const nextMonth = (): void => {
    setCurrentDate(prevDate => addMonths(prevDate, 1));
  };
  
  const prevMonth = (): void => {
    setCurrentDate(prevDate => subMonths(prevDate, 1));
  };
  
  const handleDateClick = (date: Date): void => {
    setSelectedDate(date);
    onDateSelect(date);
  };
  
  const hasEventsOnDate = (date: Date): boolean => {
    const dateString = format(date, 'yyyy-MM-dd');
    return events.some(event => event.eventDate === dateString);
  };
  
  const getEventsForSelectedDate = (): CalendarEvent[] => {
    const dateString = format(selectedDate, 'yyyy-MM-dd');
    return events.filter(event => event.eventDate === dateString);
  };
  
  const weekdays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
  const selectedDateEvents = getEventsForSelectedDate();
  
  return (
    <CalendarContainer>
      <div>
        <CalendarHeader>
          <NavigationButton onClick={prevMonth}>
            &lt;
          </NavigationButton>
          <MonthTitle>{format(currentDate, 'MMMM yyyy')}</MonthTitle>
          <NavigationButton onClick={nextMonth}>
            &gt;
          </NavigationButton>
        </CalendarHeader>
        
        <WeekdaysRow>
          {weekdays.map(day => (
            <Weekday key={day}>{day}</Weekday>
          ))}
        </WeekdaysRow>
        
        <DaysGrid>
          {calendarDays.map(day => (
            <Day 
              key={day.toString()}
              $isCurrentMonth={isSameMonth(day, currentDate)}
              $isToday={isSameDay(day, new Date())}
              $hasEvents={hasEventsOnDate(day)}
              $isSelected={isSameDay(day, selectedDate)}
              onClick={() => handleDateClick(day)}
            >
              {format(day, 'd')}
              {hasEventsOnDate(day) && <EventMarker>*</EventMarker>}
            </Day>
          ))}
        </DaysGrid>
      </div>
      
      <EventsDetails>
        <h3>Events for {format(selectedDate, 'MMMM d, yyyy')}</h3>
        
        {selectedDateEvents.length > 0 ? (
          selectedDateEvents.map(event => (
            <EventItem key={event.id}>
              <h4>{event.title}</h4>
              <p>{event.description}</p>
              <p><strong>Type:</strong> {event.eventType}</p>
              {event.enrolledCourseId && (
                <p><strong>Course ID:</strong> {event.enrolledCourseId}</p>
              )}
            </EventItem>
          ))
        ) : (
          <NoEventsMessage>No events scheduled for this date</NoEventsMessage>
        )}
      </EventsDetails>
    </CalendarContainer>
  );
};

export default Calendar;