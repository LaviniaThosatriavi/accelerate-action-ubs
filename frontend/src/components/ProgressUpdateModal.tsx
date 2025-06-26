import React, { useState } from 'react';
import styled from 'styled-components';
import type { EnrolledCourse } from '../types/ToDosTypes';

const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
`;

const ModalContainer = styled.div`
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  padding: 2rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
`;

const ModalHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
`;

const ModalTitle = styled.h2`
  color: #3367d6;
  margin: 0;
  font-weight: 500;
`;

const FormGroup = styled.div`
  margin-bottom: 15px;
`;

const Label = styled.label`
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #5f6368;
`;

const Input = styled.input`
  width: 95%;
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #3367d6;
  background-color: white;
  color: black;
  
  &:focus {
    outline: none;
    border-color: #4285f4;
  }
`;

const CourseInfo = styled.div`
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f1f3f4;
  border-radius: 4px;
`;

const ButtonGroup = styled.div`
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
`;

const Button = styled.button`
  padding: 10px 15px;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
`;

const CancelButton = styled(Button)`
  background-color: #f1f3f4;
  color: #5f6368;
  border: none;
  
  &:hover {
    background-color: #e8eaed;
  }
`;

const SubmitButton = styled(Button)`
  background-color: #4285f4;
  color: white;
  border: none;
  
  &:hover {
    background-color: #3367d6;
  }
`;

const ProgressDetail = styled.p`
  color: black;
`;

interface ProgressUpdateModalProps {
  enrolledCourse: EnrolledCourse;
  onClose: () => void;
  onSubmit: (progressPercentage: number, additionalHoursSpent: number) => void;
}

const ProgressUpdateModal: React.FC<ProgressUpdateModalProps> = ({ 
  enrolledCourse, 
  onClose, 
  onSubmit 
}) => {
  const [progressPercentage, setProgressPercentage] = useState<number>(enrolledCourse.progressPercentage || 0);
  const [additionalHoursSpent, setAdditionalHoursSpent] = useState<number>(0);
  
  const handleSubmit = (e: React.FormEvent): void => {
    e.preventDefault();
    onSubmit(progressPercentage, additionalHoursSpent);
  };
  
  return (
    <ModalOverlay onClick={onClose}>
      <ModalContainer onClick={e => e.stopPropagation()}>
        <ModalHeader>
          <ModalTitle>Update Course Progress</ModalTitle>
        </ModalHeader>
        
        <CourseInfo>
          <ProgressDetail><strong>Course:</strong> {enrolledCourse.courseTitle}</ProgressDetail>
          <ProgressDetail><strong>Current Progress:</strong> {enrolledCourse.progressPercentage}%</ProgressDetail>
          <ProgressDetail><strong>Hours Spent:</strong> {enrolledCourse.hoursSpent} of {enrolledCourse.estimatedHours} hours</ProgressDetail>
        </CourseInfo>
        
        <form onSubmit={handleSubmit}>
          <FormGroup>
            <Label htmlFor="progressPercentage">New Progress Percentage</Label>
            <Input 
              type="number" 
              id="progressPercentage"
              min="0"
              max="100"
              value={progressPercentage}
              onChange={(e) => setProgressPercentage(parseInt(e.target.value) || 0)}
              required
            />
          </FormGroup>
          
          <FormGroup>
            <Label htmlFor="additionalHoursSpent">Additional Hours Spent</Label>
            <Input 
              type="number" 
              id="additionalHoursSpent"
              min="0"
              step="0.5"
              value={additionalHoursSpent}
              onChange={(e) => setAdditionalHoursSpent(parseFloat(e.target.value) || 0)}
              required
            />
          </FormGroup>
          
          <ButtonGroup>
            <CancelButton type="button" onClick={onClose}>
              Cancel
            </CancelButton>
            <SubmitButton type="submit">
              Update Progress
            </SubmitButton>
          </ButtonGroup>
        </form>
      </ModalContainer>
    </ModalOverlay>
  );
};

export default ProgressUpdateModal;