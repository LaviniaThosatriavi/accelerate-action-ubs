import React, { useState } from 'react';
import styled from 'styled-components';

const CoursePlannerContainer = styled.div`
  background-color: white;
  width: 100%;
  height: 100%;
  padding: 2rem;
  display: flex;
  gap: 2rem;
  overflow: hidden;
  box-sizing: border-box; // Add this to include padding in height calculation
`;

const Section = styled.div`
  flex: 1;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow-y: auto; // Add scroll for content
  min-height: 0; // Fixes flex item overflow issue
  background-color: white; // Ensure sections are visible against container
`;

const LeftSection = styled(Section)`
  display: flex;
  flex-direction: column;
  gap: 1rem;
  min-height: 0; // Important for proper flex item sizing
`;

const GenerateButton = styled.button`
  padding: 1rem;
  background-color: #db2b45;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.2s;

  &:hover {
    background-color: #bf243a;
  }
`;

const ProfileModal = styled.div`
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  z-index: 1000;
  width: 400px;
`;

const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
`;

const FormGroup = styled.div`
  margin-bottom: 1rem;
`;

const Label = styled.label`
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
`;

const Input = styled.input`
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
`;

const Select = styled.select`
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
`;

const TextArea = styled.textarea`
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
`;

const SubmitButton = styled.button`
  background-color: #db2b45;
  color: white;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  float: right;

  &:hover {
    background-color: #bf243a;
  }
`;

const CoursePlanner = () => {
  const [showProfileModal, setShowProfileModal] = useState(false);
  const [profile, setProfile] = useState({
    careerStage: '',
    skills: '',
    goals: '',
    timePerWeek: ''
  });

  const handleGenerateClick = () => {
    setShowProfileModal(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // Handle form submission here
    setShowProfileModal(false);
  };

  return (
    <CoursePlannerContainer>
      {/* Left Section - Personalized Learning Path */}
      <LeftSection>
        <h2>Personalized Learning Path</h2>
        <GenerateButton onClick={handleGenerateClick}>
          Generate Learning Path
        </GenerateButton>

        {showProfileModal && (
          <>
            <ModalOverlay onClick={() => setShowProfileModal(false)} />
            <ProfileModal>
              <h3>Create Learning Profile</h3>
              <form onSubmit={handleSubmit}>
                <FormGroup>
                  <Label>Career Stage</Label>
                  <Select
                    value={profile.careerStage}
                    onChange={(e) => setProfile({...profile, careerStage: e.target.value})}
                  >
                    <option value="">Select Career Stage</option>
                    <option value="student">Student</option>
                    <option value="junior">Junior Developer</option>
                    <option value="mid">Mid-Level Developer</option>
                    <option value="senior">Senior Developer</option>
                  </Select>
                </FormGroup>

                <FormGroup>
                  <Label>Technical Skills</Label>
                  <Input
                    type="text"
                    placeholder="List your current technical skills"
                    value={profile.skills}
                    onChange={(e) => setProfile({...profile, skills: e.target.value})}
                  />
                </FormGroup>

                <FormGroup>
                  <Label>Learning Goals</Label>
                  <TextArea
                    rows={3}
                    placeholder="Describe your learning goals"
                    value={profile.goals}
                    onChange={(e) => setProfile({...profile, goals: e.target.value})}
                  />
                </FormGroup>

                <FormGroup>
                  <Label>Available Time (hours/week)</Label>
                  <Input
                    type="number"
                    min="1"
                    value={profile.timePerWeek}
                    onChange={(e) => setProfile({...profile, timePerWeek: e.target.value})}
                  />
                </FormGroup>

                <SubmitButton type="submit">
                  Create Profile
                </SubmitButton>
              </form>
            </ProfileModal>
          </>
        )}
      </LeftSection>

      {/* Middle Section - Placeholder */}
      <Section>
        <h2>Learning Path Overview</h2>
        <p>Your personalized learning path will appear here</p>
      </Section>

      {/* Right Section - Placeholder */}
      <Section>
        <h2>Progress Tracking</h2>
        <p>Progress indicators and milestones will be shown here</p>
      </Section>
    </CoursePlannerContainer>
  );
};

export default CoursePlanner;