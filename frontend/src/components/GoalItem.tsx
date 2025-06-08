import React from 'react';
import styled from 'styled-components';
import type { Goal } from '../types/ToDosTypes';

interface GoalItemContainerProps {
  $isCompleted: boolean; 
}

const GoalItemContainer = styled.div<GoalItemContainerProps>`
  background-color: white;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  border-left: 4px solid ${props => props.$isCompleted ? '#34a853' : '#4285f4'};
  opacity: ${props => props.$isCompleted ? 0.8 : 1};
  transition: all 0.2s;
  
  &:hover {
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
`;

const GoalTitle = styled.h3`
  margin: 0 0 8px 0;
  color: #3367d6;
  font-weight: 500;
  font-size: 16px;
  display: flex;
  align-items: center;
`;

const GoalDescription = styled.p`
  margin: 0;
  color: #5f6368;
  font-size: 14px;
`;

const GoalMeta = styled.div`
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  font-size: 12px;
  color: #5f6368;
`;

const GoalHours = styled.span`
  background-color: #f1f3f4;
  padding: 3px 8px;
  border-radius: 12px;
`;

const GoalResourceType = styled.span`
  background-color: #f1f3f4;
  padding: 3px 8px;
  border-radius: 12px;
`;

const ResourceLink = styled.a`
  display: inline-block;
  margin-top: 10px;
  color: #4285f4;
  text-decoration: none;
  font-size: 14px;
  padding: 4px 8px;
  border-radius: 4px;
  background-color: #f1f3f4;
  
  &:hover {
    background-color: #e8f0fe;
    text-decoration: underline;
  }
`;

const CourseInfo = styled.div`
  margin-top: 8px;
  font-size: 12px;
  color: #5f6368;
  background-color: #f1f3f4;
  padding: 4px 8px;
  border-radius: 4px;
  display: inline-block;
`;

const CompletedBadge = styled.div`
  background-color: #34a853;
  color: white;
  font-size: 11px;
  font-weight: bold;
  padding: 2px 6px;
  border-radius: 10px;
  margin-left: 8px;
  display: inline-block;
`;

interface GoalItemProps {
  goal: Goal;
  courseName?: string;
}

const GoalItem: React.FC<GoalItemProps> = ({ goal, courseName }) => {
  return (
    <GoalItemContainer $isCompleted={goal.isCompleted}>
      <GoalTitle>
        {goal.title} 
        {goal.isCompleted && <CompletedBadge>Completed</CompletedBadge>}
      </GoalTitle>
      
      <GoalDescription>{goal.description}</GoalDescription>
      
      {goal.resourceUrl && (
        <ResourceLink href={goal.resourceUrl} target="_blank" rel="noopener noreferrer">
          View Resource
        </ResourceLink>
      )}
      
      {(courseName || goal.courseName) && goal.enrolledCourseId && (
        <CourseInfo>Course: {courseName || goal.courseName}</CourseInfo>
      )}
      
      <GoalMeta>
        <GoalHours>{goal.allocatedHours} hours</GoalHours>
        <GoalResourceType>{goal.resourceType}</GoalResourceType>
      </GoalMeta>
    </GoalItemContainer>
  );
};

export default GoalItem;