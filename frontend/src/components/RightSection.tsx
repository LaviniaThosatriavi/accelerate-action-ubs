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
margin-left: 0.4rem;`;

const CategoryTitle = styled.h2`
  font-size: 1.25rem;
  margin-bottom: 1rem;
  color: black;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 0.5rem;
`;

const LinkButton = styled.a`
  display: inline-block;
  background-color: #db2b45;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  text-decoration: none;
  margin-right: 0.5rem;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
  transition: background-color 0.2s;
  
  &:hover {
    background-color: #34a853;
    color: white;
  }
`;

const VideoContainer = styled.div`
  margin-top: 1rem;
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
`;

const EnrollButton = styled.button`
  display: block;
  width: 100%;
  padding: 0.75rem;
  margin-top: 1.5rem;
  background-color: #34a853;
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
  
  &:hover {
    background-color: #2d9249;
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
    width: 40vw;    
    background-color: white;
    padding: 1rem 2.5rem 1rem 2rem;
    border-radius: 8px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
`;

const ModalTitle = styled.h2`
    color: black;
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
    border: 1px solid #34a853;
    border-radius: 4px;
    background-color: white;
    color: black;
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

interface CourseReference {
  category: string;
  courseraUrl: string;
  freeCodeCampPlaylistUrl: string;
  suggestedVideos: string[];
}

interface EnrollmentFormData {
  courseName: string;
  platform: string;
  courseUrl: string;
  estimatedHours: number;
  targetCompletionDate: string;
}

const RightSection: React.FC = () => {
  const [courseReferences, setCourseReferences] = useState<CourseReference[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState<EnrollmentFormData>({
    courseName: "",
    platform: "Coursera",
    courseUrl: "",
    estimatedHours: 20,
    targetCompletionDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
  });

  useEffect(() => {
    const fetchCourseReferences = async () => {
      try {
        setLoading(true);
        const token = localStorage.getItem('token');
        const response = await axios.get('/api/course-references', {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        setCourseReferences(response.data);
      } catch (err) {
        console.error('Error fetching course references:', err);
        setError("Failed to load course references. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchCourseReferences();
  }, []);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: name === "estimatedHours" ? parseInt(value) : value
    });
  };

  const handleEnrollSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      await axios.post('/api/enrolled-courses/enroll-external', formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      // Reset form and close modal
      setFormData({
        courseName: "",
        platform: "Coursera",
        courseUrl: "",
        estimatedHours: 20,
        targetCompletionDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
      });
      setShowModal(false);
      
      // Show success message or update UI accordingly
      alert("Course enrolled successfully!");
    } catch (err) {
      console.error('Error enrolling in course:', err);
      alert("Failed to enroll in course. Please try again.");
    }
  };

  if (loading) {
    return <Section>Loading course references...</Section>;
  }

  if (error) {
    return <Section>{error}</Section>;
  }

  return (
    <Section>
      <Title>Learning Resources</Title>
      
      {courseReferences.map((reference, index) => (
        <div key={index}>
          <CategoryTitle>{reference.category}</CategoryTitle>
          
          <div>
            <LinkButton href={reference.courseraUrl} target="_blank" rel="noopener noreferrer">
              View on Coursera
            </LinkButton>
            <LinkButton href={reference.freeCodeCampPlaylistUrl} target="_blank" rel="noopener noreferrer">
              freeCodeCamp Playlist
            </LinkButton>
          </div>
          
          <VideoContainer>
            {reference.suggestedVideos.map((video, vidIndex) => (
              <LinkButton key={vidIndex} href={video} target="_blank" rel="noopener noreferrer">
                Tutorial {vidIndex + 1}
              </LinkButton>
            ))}
          </VideoContainer>
        </div>
      ))}
      
      <EnrollButton onClick={() => setShowModal(true)}>
        Enroll in a New Course
      </EnrollButton>
      
      {showModal && (
        <Modal>
          <ModalContent>
            <ModalTitle>Enroll in External Course</ModalTitle>
            <form onSubmit={handleEnrollSubmit}>
              <FormGroup>
                <Label htmlFor="courseName">Course Name</Label>
                <Input
                  type="text"
                  id="courseName"
                  name="courseName"
                  value={formData.courseName}
                  onChange={handleInputChange}
                  required
                />
              </FormGroup>
              
              <FormGroup>
                <Label htmlFor="platform">Platform</Label>
                <Input
                  type="text"
                  id="platform"
                  name="platform"
                  value={formData.platform}
                  onChange={handleInputChange}
                  required
                />
              </FormGroup>
              
              <FormGroup>
                <Label htmlFor="courseUrl">Course URL</Label>
                <Input
                  type="url"
                  id="courseUrl"
                  name="courseUrl"
                  value={formData.courseUrl}
                  onChange={handleInputChange}
                  required
                />
              </FormGroup>
              
              <FormGroup>
                <Label htmlFor="estimatedHours">Estimated Hours</Label>
                <Input
                  type="number"
                  id="estimatedHours"
                  name="estimatedHours"
                  min="1"
                  value={formData.estimatedHours}
                  onChange={handleInputChange}
                  required
                />
              </FormGroup>
              
              <FormGroup>
                <Label htmlFor="targetCompletionDate">Target Completion Date</Label>
                <Input
                  type="date"
                  id="targetCompletionDate"
                  name="targetCompletionDate"
                  value={formData.targetCompletionDate}
                  onChange={handleInputChange}
                  required
                />
              </FormGroup>
              
              <ButtonGroup>
                <CancelButton type="button" onClick={() => setShowModal(false)}>
                  Cancel
                </CancelButton>
                <SubmitButton type="submit">
                  Enroll
                </SubmitButton>
              </ButtonGroup>
            </form>
          </ModalContent>
        </Modal>
      )}
    </Section>
  );
};

export default RightSection;