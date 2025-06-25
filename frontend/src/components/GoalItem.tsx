import React from 'react';
import styled from 'styled-components';
import type { Goal } from '../types/ToDosTypes';

interface GoalItemContainerProps {
  $isCompleted: boolean; 
}

const GoalItemContainer = styled.div<GoalItemContainerProps>`
  background-color: white;
  border-radius: clamp(6px, 1.5vw, 8px);
  padding: clamp(12px, 3vw, 15px);
  margin-bottom: clamp(8px, 2vw, 10px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  border-left: clamp(3px, 0.8vw, 4px) solid ${props => props.$isCompleted ? '#34a853' : '#4285f4'};
  opacity: ${props => props.$isCompleted ? 0.8 : 1};
  transition: all 0.2s ease;
  
  &:hover {
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    transform: translateY(-1px);
  }
  
  @media (max-width: 480px) {
    padding: clamp(10px, 2.5vw, 12px);
    margin-bottom: clamp(6px, 1.5vw, 8px);
  }
`;

const GoalTitle = styled.h3`
  margin: 0 0 clamp(6px, 1.5vw, 8px) 0;
  color: #3367d6;
  font-weight: 500;
  font-size: clamp(14px, 3vw, 16px);
  display: flex;
  align-items: center;
  line-height: 1.3;
  word-wrap: break-word;
  
  @media (max-width: 600px) {
    flex-direction: column;
    align-items: flex-start;
    gap: clamp(4px, 1vw, 6px);
  }
  
  @media (max-width: 480px) {
    font-size: clamp(13px, 2.8vw, 15px);
  }
`;

const GoalDescription = styled.p`
  margin: 0;
  color: #5f6368;
  font-size: clamp(12px, 2.5vw, 14px);
  line-height: 1.4;
  word-wrap: break-word;
  
  @media (max-width: 480px) {
    font-size: clamp(11px, 2.3vw, 13px);
  }
`;

const GoalMeta = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: clamp(8px, 2vw, 10px);
  font-size: clamp(10px, 2.2vw, 12px);
  color: #5f6368;
  gap: clamp(8px, 2vw, 12px);
  
  @media (max-width: 600px) {
    flex-direction: column;
    align-items: flex-start;
    gap: clamp(6px, 1.5vw, 8px);
  }
  
  @media (max-width: 480px) {
    font-size: clamp(9px, 2vw, 11px);
  }
`;

const GoalHours = styled.span`
  background-color: #f1f3f4;
  padding: clamp(2px, 0.8vw, 3px) clamp(6px, 1.5vw, 8px);
  border-radius: clamp(10px, 2vw, 12px);
  font-size: clamp(10px, 2.2vw, 12px);
  font-weight: 500;
  white-space: nowrap;
  
  @media (max-width: 480px) {
    font-size: clamp(9px, 2vw, 11px);
    padding: clamp(1px, 0.5vw, 2px) clamp(5px, 1.2vw, 6px);
  }
`;

const GoalResourceType = styled.span`
  background-color: #f1f3f4;
  padding: clamp(2px, 0.8vw, 3px) clamp(6px, 1.5vw, 8px);
  border-radius: clamp(10px, 2vw, 12px);
  font-size: clamp(10px, 2.2vw, 12px);
  font-weight: 500;
  white-space: nowrap;
  
  @media (max-width: 480px) {
    font-size: clamp(9px, 2vw, 11px);
    padding: clamp(1px, 0.5vw, 2px) clamp(5px, 1.2vw, 6px);
  }
`;

const MetaGroup = styled.div`
  display: flex;
  gap: clamp(6px, 1.5vw, 8px);
  align-items: center;
  flex-wrap: wrap;
  
  @media (max-width: 600px) {
    width: 100%;
    justify-content: flex-start;
  }
`;

const ResourceLink = styled.a`
  display: inline-block;
  margin-top: clamp(8px, 2vw, 10px);
  color: #4285f4;
  text-decoration: none;
  font-size: clamp(12px, 2.5vw, 14px);
  padding: clamp(3px, 1vw, 4px) clamp(6px, 1.5vw, 8px);
  border-radius: clamp(3px, 1vw, 4px);
  background-color: #f1f3f4;
  transition: all 0.2s ease;
  word-wrap: break-word;
  max-width: 100%;
  
  &:hover {
    background-color: #e8f0fe;
    text-decoration: underline;
    transform: translateY(-1px);
  }
  
  &:active {
    transform: translateY(0);
  }
  
  @media (max-width: 480px) {
    font-size: clamp(11px, 2.3vw, 13px);
    padding: clamp(2px, 0.8vw, 3px) clamp(5px, 1.2vw, 6px);
  }
`;

const CourseInfo = styled.div`
  margin-top: clamp(6px, 1.5vw, 8px);
  font-size: clamp(10px, 2.2vw, 12px);
  color: #5f6368;
  background-color: #f1f3f4;
  padding: clamp(3px, 1vw, 4px) clamp(6px, 1.5vw, 8px);
  border-radius: clamp(3px, 1vw, 4px);
  display: inline-block;
  word-wrap: break-word;
  max-width: 100%;
  box-sizing: border-box;
  
  @media (max-width: 480px) {
    font-size: clamp(9px, 2vw, 11px);
    padding: clamp(2px, 0.8vw, 3px) clamp(5px, 1.2vw, 6px);
  }
`;

const CompletedBadge = styled.div`
  background-color: #34a853;
  color: white;
  font-size: clamp(9px, 2vw, 11px);
  font-weight: bold;
  padding: clamp(1px, 0.5vw, 2px) clamp(4px, 1vw, 6px);
  border-radius: clamp(8px, 1.5vw, 10px);
  margin-left: clamp(6px, 1.5vw, 8px);
  display: inline-block;
  white-space: nowrap;
  
  @media (max-width: 600px) {
    margin-left: 0;
    margin-top: clamp(4px, 1vw, 6px);
  }
  
  @media (max-width: 480px) {
    font-size: clamp(8px, 1.8vw, 10px);
    padding: clamp(1px, 0.4vw, 1px) clamp(3px, 0.8vw, 5px);
  }
`;

const TitleContent = styled.span`
  flex: 1;
  word-wrap: break-word;
  overflow-wrap: break-word;
`;

interface GoalItemProps {
  goal: Goal;
  courseName?: string;
}

const GoalItem: React.FC<GoalItemProps> = ({ goal, courseName }) => {
  return (
    <GoalItemContainer $isCompleted={goal.isCompleted}>
      <GoalTitle>
        <TitleContent>{goal.title}</TitleContent>
        {goal.isCompleted && <CompletedBadge>Completed</CompletedBadge>}
      </GoalTitle>
      
      {goal.description && (
        <GoalDescription>{goal.description}</GoalDescription>
      )}
      
      {goal.resourceUrl && (
        <ResourceLink href={goal.resourceUrl} target="_blank" rel="noopener noreferrer">
          View Resource
        </ResourceLink>
      )}
      
      {(courseName || goal.courseName) && goal.enrolledCourseId && (
        <CourseInfo>Course: {courseName || goal.courseName}</CourseInfo>
      )}
      
      <GoalMeta>
        <MetaGroup>
          <GoalHours>{goal.allocatedHours} hours</GoalHours>
          <GoalResourceType>{goal.resourceType}</GoalResourceType>
        </MetaGroup>
      </GoalMeta>
    </GoalItemContainer>
  );
};

export default GoalItem;