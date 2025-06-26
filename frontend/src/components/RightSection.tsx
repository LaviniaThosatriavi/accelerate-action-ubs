import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import axios from 'axios';
import { API_BASE_URL } from '../config/api';

const Section = styled.div`
  flex: 1;
  border: 1px solid #ddd;
  border-radius: clamp(4px, 1vw, 8px);
  padding: clamp(0.5rem, 2vw, 1rem) clamp(0.75rem, 2.5vw, 1.5rem);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  min-height: 0;
  background-color: white;
  max-width: 100%;
  word-wrap: break-word;
  overflow-wrap: break-word;
  
  @media (max-width: 768px) {
    padding: clamp(0.5rem, 3vw, 1rem);
  }
`;

const Title = styled.h1`
  color: black;
  font-size: clamp(1rem, 2vw, 1.2rem);
  margin-left: clamp(0.2rem, 0.8vw, 0.4rem);
  word-wrap: break-word;
  overflow-wrap: break-word;
  max-width: 100%;
`;

const RegenerateButton = styled.button`
  background-color: #4285f4;
  color: white;
  padding: clamp(0.5rem, 1.4vw, 0.75rem) clamp(0.75rem, 2.5vw, 1.5rem);
  border: none;
  border-radius: clamp(2px, 0.8vw, 4px);
  cursor: pointer;
  font-size: clamp(0.8rem, 1.6vw, 0.9rem);
  transition: background-color 0.2s;
  word-wrap: break-word;
  overflow-wrap: break-word;
  margin-bottom: clamp(0.75rem, 1.5vh, 1rem);
  
  &:hover {
    background-color: #357abd;
  }
  
  &:active {
    background-color: #2a60a0;
  }
`;

const CategoryTitle = styled.h2`
  font-size: clamp(1.1rem, 2.2vw, 1.25rem);
  margin-bottom: clamp(0.5rem, 1.8vh, 1rem);
  color: black;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: clamp(0.25rem, 1vh, 0.5rem);
  word-wrap: break-word;
  overflow-wrap: break-word;
  max-width: 100%;
`;

const LinkButton = styled.a`
  display: inline-block;
  background-color: #db2b45;
  color: white;
  padding: clamp(0.4rem, 1vw, 0.5rem) clamp(0.5rem, 1.8vw, 1rem);
  border-radius: clamp(2px, 0.8vw, 4px);
  text-decoration: none;
  margin-right: clamp(0.3rem, 1vw, 0.5rem);
  margin-bottom: clamp(0.3rem, 1vh, 0.5rem);
  font-size: clamp(0.8rem, 1.6vw, 0.9rem);
  transition: background-color 0.2s;
  word-wrap: break-word;
  overflow-wrap: break-word;
  text-align: center;
  white-space: nowrap;
  
  &:hover {
    background-color: #34a853;
    color: white;
    text-decoration: none;
  }
`;

const VideoContainer = styled.div`
  margin-top: clamp(0.5rem, 1.8vh, 1rem);
  display: flex;
  flex-wrap: wrap;
  gap: clamp(0.3rem, 1vw, 0.5rem);
  max-width: 100%;
`;

const EnrollButton = styled.button`
  display: block;
  width: 100%;
  padding: clamp(0.5rem, 1.4vw, 0.75rem);
  margin-top: clamp(0.75rem, 2.5vh, 1.5rem);
  background-color: #34a853;
  color: white;
  border: none;
  border-radius: clamp(2px, 0.8vw, 4px);
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  word-wrap: break-word;
  overflow-wrap: break-word;
  box-sizing: border-box;
  
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
  padding: clamp(1rem, 3vw, 2rem);
  box-sizing: border-box;
`;

const ModalContent = styled.div`
  width: clamp(300px, 80vw, 40vw);
  max-width: 500px;
  background-color: white;
  padding: clamp(0.5rem, 1.8vw, 1rem) clamp(1.25rem, 4.5vw, 2.5rem) clamp(0.5rem, 1.8vw, 1rem) clamp(1rem, 3.5vw, 2rem);
  border-radius: clamp(4px, 1vw, 8px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  max-height: 90vh;
  overflow-y: auto;
  word-wrap: break-word;
  overflow-wrap: break-word;
`;

const ModalTitle = styled.h2`
  color: black;
  font-size: clamp(1.1rem, 2.2vw, 1.25rem);
  word-wrap: break-word;
  overflow-wrap: break-word;
  max-width: 100%;
  margin-bottom: clamp(0.75rem, 1.5vh, 1rem);
`;

const FormGroup = styled.div`
  margin-bottom: clamp(0.5rem, 1.8vh, 1rem);
`;

const Label = styled.label`
  display: block;
  margin-bottom: clamp(0.25rem, 1vh, 0.5rem);
  font-weight: 500;
  color: black;
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  word-wrap: break-word;
  overflow-wrap: break-word;
`;

const Input = styled.input`
  width: 100%;
  padding: clamp(0.4rem, 1vw, 0.5rem);
  border: 1px solid #34a853;
  border-radius: clamp(2px, 0.8vw, 4px);
  background-color: white;
  color: black;
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  box-sizing: border-box;
  
  &:focus {
    outline: none;
    box-shadow: 0 0 0 2px rgba(52, 168, 83, 0.2);
  }
`;

const ButtonGroup = styled.div`
  display: flex;
  justify-content: flex-end;
  gap: clamp(0.25rem, 1vw, 0.5rem);
  margin-top: clamp(0.75rem, 2.5vh, 1.5rem);
  flex-wrap: wrap;
`;

const SubmitButton = styled.button`
  padding: clamp(0.4rem, 1vw, 0.5rem) clamp(0.5rem, 1.8vw, 1rem);
  background-color: #34a853;
  color: white;
  border: none;
  border-radius: clamp(2px, 0.8vw, 4px);
  cursor: pointer;
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  
  &:hover {
    background-color: #2d9249;
  }
`;

const CancelButton = styled.button`
  padding: clamp(0.4rem, 1vw, 0.5rem) clamp(0.5rem, 1.8vw, 1rem);
  background-color: #f1f1f1;
  color: #333;
  border: none;
  border-radius: clamp(2px, 0.8vw, 4px);
  cursor: pointer;
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  
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
            const response = await axios.get(`${API_BASE_URL}/api/course-references`, {
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
        await axios.post(`${API_BASE_URL}/api/enrolled-courses/enroll-external`, formData, {
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

    const fetchCourseReferences = async () => {
        try {
        setLoading(true);
        const token = localStorage.getItem('token');
        const response = await axios.get(`${API_BASE_URL}/api/course-references`, {
            headers: {
            Authorization: `Bearer ${token}`
            }
        });
        setCourseReferences(response.data);
        setError("");
        } catch (err) {
        console.error('Error fetching course references:', err);
        setError("Failed to load course references. Please try again later.");
        } finally {
        setLoading(false);
        }
    };

    useEffect(() => {
        fetchCourseReferences();
    }, []);

    if (loading) {
        return <Section>Loading course references...</Section>;
    }

    if (error) {
        return <Section>{error}</Section>;
    }

    return (
        <Section>
        <Title>Learning Resources</Title>
        <RegenerateButton onClick={fetchCourseReferences}>
            Regenerate Course References
        </RegenerateButton>
        
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