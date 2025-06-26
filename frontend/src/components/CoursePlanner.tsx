import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import axios from 'axios';
import { FaUser, FaRoad, FaEdit, FaPlusCircle, FaCode, FaGithub, FaDatabase } from 'react-icons/fa';
import ReactMarkdown from 'react-markdown';
import MiddleSection from './MiddleSection';
import RightSection from './RightSection';
import { API_BASE_URL } from '../config/api';

interface Profile {
  id?: number;
  careerStage: string;
  skills: string[];
  goals: string;
  hoursPerWeek: number;
  learningPath?: string;
}

const CoursePlannerContainer = styled.div`
  background-color: #f8f8f8;
  width: 100%;
  height: 100vh;
  padding: clamp(0.5rem, 2vw, 2rem);
  display: flex;
  flex-direction: row;
  gap: clamp(0.25rem, 1vw, 2rem);
  overflow: hidden;
  box-sizing: border-box;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  font-size: clamp(0.875rem, 1vw, 1rem);
  
  @media (max-width: 768px) {
    flex-direction: column;
    height: auto;
    min-height: 100vh;
    gap: clamp(0.5rem, 2vw, 1rem);
  }
`;

const Section = styled.div`
  flex: 1;
  border: 1px solid #ddd;
  border-radius: clamp(4px, 1vw, 8px);
  padding: clamp(0.75rem, 2vw, 1.5rem);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  min-height: 0;
  background-color: white;
  max-width: 100%;
  word-wrap: break-word;
  overflow-wrap: break-word;
`;

const LeftSection = styled(Section)`
  display: flex;
  flex-direction: column;
  gap: clamp(0.75rem, 2vh, 1.5rem);
  min-height: 0;
`;

const ActionButton = styled.button`
  padding: clamp(0.5rem, 1.5vw, 0.75rem) clamp(0.75rem, 2vw, 1.25rem);
  margin: 0 auto;
  background-color: #db2b45;
  color: white;
  border: none;
  border-radius: clamp(3px, 0.5vw, 5px);
  cursor: pointer;
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: clamp(4px, 1vw, 8px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  max-width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;

  &:hover {
    background-color: #bf243a;
    transform: translateY(-1px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  }

  &:active {
    transform: translateY(0);
  }
  
  @media (max-width: 768px) {
    white-space: normal;
    text-align: center;
  }
`;

const ModalHeader = styled.div`
  display: flex;
  align-items: center;
  margin-bottom: clamp(0.75rem, 2vh, 1.5rem);
  
  h3 {
    margin: 0;
    font-size: clamp(1.1rem, 2.5vw, 1.5rem);
    color: #333;
    word-wrap: break-word;
    flex: 1;
  }
  
  svg {
    margin-right: clamp(5px, 1vw, 10px);
    color: #db2b45;
    font-size: clamp(1rem, 2vw, 1.2rem);
    flex-shrink: 0;
  }
`;

const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  z-index: 999;
  backdrop-filter: blur(3px);
`;

const ProfileModal = styled.div`
  position: fixed;
  top: calc(50% + 5vh);
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: white;
  padding: clamp(1rem, 3vw, 2.5rem);
  border-radius: clamp(8px, 1.5vw, 12px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  z-index: 1000;
  width: clamp(300px, 90vw, 450px);
  max-height: 80vh;
  overflow-y: auto;
  box-sizing: border-box;
  word-wrap: break-word;
  overflow-wrap: break-word;
`;

const FormGroup = styled.div`
  margin-bottom: clamp(0.75rem, 2vh, 1.5rem);
`;

const Label = styled.label`
  display: block;
  margin-bottom: clamp(0.25rem, 1vh, 0.5rem);
  font-weight: 600;
  color: #333;
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  word-wrap: break-word;
`;

const Input = styled.input`
  width: 100%;
  padding: clamp(0.5rem, 1.5vw, 0.75rem);
  border: 1px solid #db2b45;
  background-color: white;
  color: black;
  border-radius: clamp(3px, 0.8vw, 6px);
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  transition: border 0.2s;
  box-sizing: border-box;
  
  &:focus {
    border-color: #db2b45;
    outline: none;
    box-shadow: 0 0 0 2px rgba(219, 43, 69, 0.2);
  }
`;

const Select = styled.select`
  width: 100%;
  padding: clamp(0.5rem, 1.5vw, 0.75rem);
  border: 1px solid #db2b45;
  background-color: white;
  color: black;
  border-radius: clamp(3px, 0.8vw, 6px);
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  appearance: none;
  background-image: url('data:image/svg+xml;charset=US-ASCII,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24"><path fill="none" d="M0 0h24v24H0z"/><path d="M12 15l-5-5h10l-5 5z"/></svg>');
  background-repeat: no-repeat;
  background-position: right clamp(5px, 1vw, 10px) center;
  background-size: clamp(16px, 2vw, 20px);
  box-sizing: border-box;
  
  &:focus {
    border-color: #db2b45;
    outline: none;
    box-shadow: 0 0 0 2px rgba(219, 43, 69, 0.2);
  }
`;

const TextArea = styled.textarea`
  width: 100%;
  padding: clamp(0.5rem, 1.5vw, 0.75rem);
  border: 1px solid #db2b45;
  background-color: white;
  color: black;
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  resize: vertical;
  min-height: clamp(80px, 15vh, 100px);
  box-sizing: border-box;
  border-radius: clamp(3px, 0.8vw, 6px);
  font-family: inherit;
  line-height: 1.4;
  
  &:focus {
    border-color: #db2b45;
    outline: none;
    box-shadow: 0 0 0 2px rgba(219, 43, 69, 0.2);
  }
`;

const ButtonGroup = styled.div`
  display: flex;
  justify-content: flex-end;
  gap: clamp(5px, 1vw, 10px);
  margin-top: clamp(0.75rem, 2vh, 1.5rem);
  flex-wrap: wrap;
`;

const CancelButton = styled.button`
  padding: clamp(0.5rem, 1.5vw, 0.75rem) clamp(0.75rem, 2vw, 1.5rem);
  background-color: #f1f1f1;
  color: #333;
  border: none;
  border-radius: clamp(3px, 0.8vw, 6px);
  cursor: pointer;
  font-weight: 500;
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  
  &:hover {
    background-color: #e0e0e0;
  }
`;

const SubmitButton = styled.button`
  padding: clamp(0.5rem, 1.5vw, 0.75rem) clamp(0.75rem, 2vw, 1.5rem);
  background-color: #db2b45;
  color: white;
  border: none;
  border-radius: clamp(3px, 0.8vw, 6px);
  cursor: pointer;
  font-weight: 500;
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  
  &:hover {
    background-color: #bf243a;
  }
`;

const TabContainer = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
`;

const TabHeader = styled.div`
  display: flex;
  border-bottom: 1px solid #ddd;
  margin-bottom: clamp(1vh, 2vh, 2vh);
  gap: clamp(0.5vw, 1vw, 1vw);
  position: sticky;
  top: 0;
  background-color: white;
  z-index: 10;
  flex-wrap: nowrap;
  
  @media (max-width: 768px) {
    width: 100%;
    gap: 0;
  }
`;

interface TabButtonProps {
  $active: boolean;
}

const TabButton = styled.button<TabButtonProps>`
  padding: clamp(0.5rem, 1.5vw, 0.75rem) clamp(0.75rem, 2vw, 1.5rem);
  background-color: ${props => props.$active ? '#db2b45' : 'white'};
  color: ${props => props.$active ? 'white' : '#333'};
  border: none;
  border-top-left-radius: ${props => props.$active ? 'clamp(3px, 0.8vw, 6px)' : '0'};
  border-top-right-radius: ${props => props.$active ? 'clamp(3px, 0.8vw, 6px)' : '0'};
  cursor: pointer;
  font-weight: 500;
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  border-bottom: ${props => props.$active ? 'none' : '1px solid #ddd'};
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: clamp(4px, 1vw, 8px);
  flex: 1;
  justify-content: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  min-width: 0;
  
  &:hover {
    background-color: ${props => props.$active ? '#bf243a' : '#f5f5f5'};
  }
  
  svg {
    font-size: clamp(0.875rem, 1.2vw, 1rem);
    flex-shrink: 0;
  }
  
  @media (max-width: 480px) {
    font-size: clamp(0.75rem, 3vw, 0.875rem);
    padding: clamp(0.4rem, 2vw, 0.6rem) clamp(0.5rem, 3vw, 1rem);
    
    svg {
      font-size: clamp(0.75rem, 3vw, 0.875rem);
    }
  }
`;

const TabContent = styled.div`
  flex: 1;
  overflow: auto;
  min-height: 0;
  word-wrap: break-word;
  overflow-wrap: break-word;
`;

const ProfileCard = styled.div`
  background-color: #f9f9f9;
  border-radius: clamp(4px, 1vw, 8px);
  padding: clamp(0.75rem, 2vw, 1.2rem);
  height: 70%;
  margin-top: clamp(1vh, 2vh, 2vh);
  margin-bottom: clamp(2vh, 4vh, 4vh);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  overflow-y: auto;
  word-wrap: break-word;
  overflow-wrap: break-word;
`;

const ProfileCardTitle = styled.h3`
  margin-top: 0;
  margin-bottom: clamp(0.5rem, 1vh, 1rem);
  color: #333;
  display: flex;
  align-items: center;
  gap: clamp(4px, 1vw, 8px);
  font-size: clamp(1rem, 2vw, 1.25rem);
  word-wrap: break-word;
  
  svg {
    font-size: clamp(1rem, 2vw, 1.2rem);
    flex-shrink: 0;
  }
`;

const ProfileCardContent = styled.div`
  margin-top: clamp(0.5rem, 1vh, 1rem);
`;

const ProfileItem = styled.div`
  margin-bottom: clamp(0.25rem, 1vh, 0.5rem);
  word-wrap: break-word;
  overflow-wrap: break-word;
`;

const ProfileLabel = styled.span`
  font-weight: 600;
  color: #555;
  font-size: clamp(0.875rem, 1.2vw, 1.5rem) !important;
  display: inline-block;
`;

const ProfileValue = styled.span`
  margin-left: clamp(0.25rem, 0.5vw, 0.5rem);
  color: black;
  line-height: clamp(1.4, 2vh, 2rem);
  font-size: clamp(0.875rem, 1.2vw, 1.5rem) !important;
  word-wrap: break-word;
  overflow-wrap: break-word;
`;

const SkillsList = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: clamp(4px, 1vw, 8px);
  margin-top: clamp(0.25rem, 1vh, 0.5rem);
`;

const SkillTag = styled.span`
  background-color: #db2b45;
  color: white;
  padding: clamp(2px, 0.5vw, 4px) clamp(6px, 1.5vw, 10px);
  border-radius: clamp(10px, 2vw, 20px);
  font-size: clamp(0.75rem, 1vw, 0.85rem);
  display: flex;
  align-items: center;
  gap: clamp(3px, 0.8vw, 5px);
  white-space: nowrap;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  
  svg {
    font-size: clamp(0.7rem, 1vw, 0.8rem);
    flex-shrink: 0;
  }
`;

const EmptyState = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: clamp(1rem, 3vw, 2rem);
  text-align: center;
  color: #666;
  min-height: 200px;
  
  p {
    font-size: clamp(0.875rem, 1.2vw, 1rem);
    margin: clamp(0.5rem, 1vh, 1rem) 0;
    word-wrap: break-word;
    max-width: 100%;
  }
`;

const EmptyStateIcon = styled.div`
  font-size: clamp(2rem, 5vw, 3rem);
  margin-bottom: clamp(0.5rem, 1vh, 1rem);
  color: #db2b45;
`;

const LearningPathContent = styled.div`
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  line-height: 1.6;
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  word-wrap: break-word;
  overflow-wrap: break-word;
  max-width: 100%;
  
  h1, h2, h3, h4, h5, h6 {
    color: #333;
    word-wrap: break-word;
    overflow-wrap: break-word;
    margin: clamp(0.5rem, 1vh, 1rem) 0;
  }
  
  h1 {
    display: none;
  }
  
  h2 {
    font-size: clamp(1rem, 1.5vw, 1.25rem);
    border-bottom: 1px solid #eee;
    padding-bottom: clamp(0.25rem, 0.5vh, 0.5rem);
  }
  
  h3 {
    font-size: clamp(1rem, 1.3vw, 1.15rem);
  }
  
  a {
    color: #3367d6;
    text-decoration: none;
    word-wrap: break-word;
    overflow-wrap: break-word;
    
    &:hover {
      text-decoration: underline;
    }
  }
  
  ul, ol {
    padding-left: clamp(1rem, 2vw, 1.5rem);
    margin: clamp(0.5rem, 1vh, 1rem) 0;
  }
  
  li {
    color: black;
    margin-bottom: clamp(0.25rem, 0.5vh, 0.5rem);
    word-wrap: break-word;
    overflow-wrap: break-word;
  }
  
  p {
    margin: clamp(0.5rem, 1vh, 1rem) 0;
    word-wrap: break-word;
    overflow-wrap: break-word;
  }
  
  strong {
    color: #333;
    font-weight: 600;
  }
  
  hr {
    border: none;
    border-top: 1px solid #eee;
    margin: clamp(0.5rem, 1vh, 1rem) 0;
  }
  
  code {
    background-color: #f5f5f5;
    padding: clamp(1px, 0.2vw, 2px) clamp(2px, 0.5vw, 4px);
    border-radius: clamp(2px, 0.5vw, 3px);
    font-size: clamp(0.8rem, 1.1vw, 0.9rem);
    word-wrap: break-word;
    overflow-wrap: break-word;
  }
  
  pre {
    background-color: #f5f5f5;
    padding: clamp(0.5rem, 1vw, 1rem);
    border-radius: clamp(3px, 0.8vw, 6px);
    overflow-x: auto;
    font-size: clamp(0.8rem, 1.1vw, 0.9rem);
    
    code {
      background: none;
      padding: 0;
    }
  }
  
  blockquote {
    border-left: clamp(2px, 0.5vw, 4px) solid #db2b45;
    margin: clamp(0.5rem, 1vh, 1rem) 0;
    padding-left: clamp(0.5rem, 1vw, 1rem);
    color: #666;
    font-style: italic;
  }
  
  table {
    width: 100%;
    border-collapse: collapse;
    margin: clamp(0.5rem, 1vh, 1rem) 0;
    font-size: clamp(0.8rem, 1.1vw, 0.9rem);
    overflow-x: auto;
    display: block;
    white-space: nowrap;
    
    @media (max-width: 768px) {
      display: block;
      overflow-x: auto;
      white-space: nowrap;
    }
  }
  
  th, td {
    border: 1px solid #ddd;
    padding: clamp(0.25rem, 1vw, 0.5rem);
    text-align: left;
    word-wrap: break-word;
  }
  
  th {
    background-color: #f5f5f5;
    font-weight: 600;
  }
`;

const CareerStageInput = styled.input`
  width: 100%;
  padding: clamp(0.5rem, 1.5vw, 0.75rem);
  border: 1px solid #ddd;
  border-radius: clamp(3px, 0.8vw, 6px);
  font-size: clamp(0.875rem, 1.2vw, 1rem);
  margin-top: clamp(0.25rem, 1vh, 0.5rem);
  transition: border 0.2s;
  box-sizing: border-box;
  
  &:focus {
    border-color: #db2b45;
    outline: none;
    box-shadow: 0 0 0 2px rgba(219, 43, 69, 0.2);
  }
`;


const CoursePlanner = () => {
  const [activeTab, setActiveTab] = useState<'profile' | 'learningPath'>('profile');
  const [showProfileModal, setShowProfileModal] = useState(false);
  const [profile, setProfile] = useState<Profile | null>(null);
  const [tempProfile, setTempProfile] = useState({
    careerStage: '',
    customCareerStage: '',
    skills: '',
    goals: '',
    hoursPerWeek: '5'
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [useCustomCareerStage, setUseCustomCareerStage] = useState(false);
  
  const api = axios.create({
    baseURL: API_BASE_URL,
    withCredentials: true
  });

  useEffect(() => {
    // Try to get token from both possible storage keys
    const token = localStorage.getItem('token') || localStorage.getItem('jwtToken');
    console.log('Token found in localStorage:', token ? 'YES' : 'NO');
    
    if (token) {
      // Make sure we're setting the header for all requests
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      console.log('Authorization header set with token');
    } else {
      console.warn('No authentication token found, user may need to log in');
    }
  }, []);

  // Fetch profile on component mount
  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      setLoading(true);
      console.log(`Fetching profile from ${API_BASE_URL}/api/profiles`);
      
      const response = await api.get('/api/profiles');
      if (response.data) {
        console.log("Fetched profile:", response.data);
        setProfile(response.data);
      }
    } catch (err) {
      console.error('Error fetching profile:', err);
      setError('Failed to fetch profile');
      console.log(error);
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateClick = () => {
    if (profile) {
      // If profile exists, just submit it to generate a new learning path
      createOrUpdateProfile(profile);
    } else {
      // Otherwise show modal to create profile
      openProfileModal();
    }
  };

  const openProfileModal = (existingProfile?: Profile) => {
    console.log("Opening modal with profile:", existingProfile);
    
    if (existingProfile) {
      const isCustomCareerStage = !['student', 'junior backend developer', 'junior frontend developer', 'mid-level developer', 'senior developer'].includes(existingProfile.careerStage);
      
      setUseCustomCareerStage(isCustomCareerStage);
      
      setTempProfile({
        careerStage: isCustomCareerStage ? 'other' : existingProfile.careerStage || '',
        customCareerStage: isCustomCareerStage ? existingProfile.careerStage : '',
        skills: existingProfile.skills ? existingProfile.skills.join(', ') : '',
        goals: existingProfile.goals || '',
        hoursPerWeek: existingProfile.hoursPerWeek ? existingProfile.hoursPerWeek.toString() : '5'
      });
    } else {
      setTempProfile({
        careerStage: '',
        customCareerStage: '',
        skills: '',
        goals: '',
        hoursPerWeek: '5'
      });
      setUseCustomCareerStage(false);
    }
    
    // Force this to true to ensure modal shows
    setShowProfileModal(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    // Convert comma-separated skills to array
    const skillsArray = tempProfile.skills
      .split(',')
      .map(skill => skill.trim())
      .filter(skill => skill !== '');
    
    // Determine the career stage (custom or selected)
    const finalCareerStage = useCustomCareerStage ? tempProfile.customCareerStage : tempProfile.careerStage;
    
    const profileData: Profile = {
      careerStage: finalCareerStage,
      skills: skillsArray,
      goals: tempProfile.goals,
      hoursPerWeek: parseInt(tempProfile.hoursPerWeek, 10) || 5
    };
    
    // If profile exists, pass the ID to update it
    if (profile && profile.id) {
      profileData.id = profile.id;
    }
    
    createOrUpdateProfile(profileData);
  };

  const createOrUpdateProfile = async (profileData: Profile) => {
    try {
      setLoading(true);
      console.log(`Saving profile to ${API_BASE_URL}/api/profiles`);
      console.log("Profile data:", profileData);
      
      // Try to get token from both possible storage keys
      const token = localStorage.getItem('token') || localStorage.getItem('jwtToken');
      
      if (!token) {
        console.error('No token found, please login first');
        setError('Authentication required. Please login.');
        return;
      }
      
      // Log the actual request being made
      console.log('Request headers:', {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      });
      
      const response = await api.post('/api/profiles', profileData, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      console.log("Profile saved:", response.data);
      setProfile(response.data);
      setShowProfileModal(false);
      setActiveTab('learningPath');
    } catch (err) {
      console.error('Error creating/updating profile:', err);
      setError('Failed to create or update profile');
      
      if (axios.isAxiosError(err)) {
        console.log('Full error details:', err);
        
        if (err.response?.status === 403 || err.response?.status === 401) {
          console.error(`${err.response.status} - Authentication issue`);
          alert('Authentication error: Your session may have expired. Please log in again.');
          localStorage.removeItem('token');
          localStorage.removeItem('jwtToken');
        } else {
          alert(`API Error (${err.response?.status}): ${err.message}`);
        }
      } else {
        alert('An unexpected error occurred. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };
  
  const formatLearningPath = (markdownContent: string): string => {
    // Split content into lines for processing
    const lines = markdownContent.split('\n');
    
    // More flexible regex to match various week patterns
    const weekHeaderRegex = /^(#{1,6})\s*Week\s+(\d+)(?:\s*[:-]|\s*$)/i;
    const weekNumbers: number[] = [];
    
    // Extract all week numbers
    lines.forEach(line => {
      const match = line.match(weekHeaderRegex);
      if (match) {
        weekNumbers.push(parseInt(match[2], 10));
      }
    });
    
    // Find the highest week number
    const maxWeek = Math.max(...weekNumbers);
    
    // Process lines and modify the last week header
    const processedLines = lines.map(line => {
      const match = line.match(weekHeaderRegex);
      if (match) {
        const weekNumber = parseInt(match[2], 10);
        const headerLevel = match[1]; // # symbols
        const restOfLine = line.substring(match[0].length); // Get any text after "Week X"
        
        // If this is the last week, modify it to show range
        if (weekNumber === maxWeek) {
          return `${headerLevel} Week ${weekNumber}-${weekNumber + 1}${restOfLine}`;
        }
      }
      return line;
    });
    
    return processedLines.join('\n');
  };

  const renderSkillIcon = (skill: string) => {
    const lowerSkill = skill.toLowerCase();
    if (lowerSkill.includes('java') || lowerSkill.includes('spring')) {
      return <FaCode />;
    } else if (lowerSkill.includes('git')) {
      return <FaGithub />;
    } else if (lowerSkill.includes('sql') || lowerSkill.includes('database')) {
      return <FaDatabase />;
    } else if (lowerSkill.includes('api')) {
      return <FaCode />;
    } else {
      return <FaCode />;
    }
  };

  return (
    <CoursePlannerContainer>
      {/* Left Section - Personalized Learning Path */}
      <LeftSection>
        
        <TabContainer>
          <TabHeader>
            <TabButton 
              $active={activeTab === 'profile'} 
              onClick={() => setActiveTab('profile')}
            >
              <FaUser /> Profile
            </TabButton>
            <TabButton 
              $active={activeTab === 'learningPath'} 
              onClick={() => setActiveTab('learningPath')}
            >
              <FaRoad /> Learning Path
            </TabButton>
          </TabHeader>
          
          <TabContent>
            {activeTab === 'profile' && (
              <>
                {profile ? (
                  <>
                    <ProfileCard>
                      <ProfileCardTitle><FaUser /> Your Learning Profile</ProfileCardTitle>
                      <ProfileCardContent>
                        <ProfileItem>
                          <ProfileLabel>Career Stage:</ProfileLabel>
                          <ProfileValue>{profile.careerStage}</ProfileValue>
                        </ProfileItem>
                        <ProfileItem>
                          <ProfileLabel>Skills:</ProfileLabel>
                          <SkillsList>
                            {profile.skills && profile.skills.map((skill, index) => (
                              <SkillTag key={index}>
                                {renderSkillIcon(skill)} {skill}
                              </SkillTag>
                            ))}
                          </SkillsList>
                        </ProfileItem>
                        <ProfileItem>
                          <ProfileLabel>Goals:</ProfileLabel>
                          <ProfileValue>{profile.goals}</ProfileValue>
                        </ProfileItem>
                        <ProfileItem>
                          <ProfileLabel>Time Commitment:</ProfileLabel>
                          <ProfileValue>{profile.hoursPerWeek} hours/week</ProfileValue>
                        </ProfileItem>
                      </ProfileCardContent>
                    </ProfileCard>
                    <ActionButton onClick={() => {
                      console.log("Update profile button clicked");
                      openProfileModal(profile);
                    }}>
                      <FaEdit /> Update Profile
                    </ActionButton>
                  </>
                ) : (
                  <EmptyState>
                    <EmptyStateIcon><FaUser /></EmptyStateIcon>
                    <p>You haven't created a learning profile yet.</p>
                    <ActionButton onClick={() => openProfileModal()}>
                      <FaPlusCircle /> Create Profile
                    </ActionButton>
                  </EmptyState>
                )}
              </>
            )}
            
            {activeTab === 'learningPath' && (
              <>
                {profile && profile.learningPath ? (
                  <>
                    <ActionButton onClick={handleGenerateClick}>
                      <FaRoad /> Regenerate Learning Path
                    </ActionButton>
                    <LearningPathContent>
                      <ReactMarkdown>
                        {formatLearningPath(profile.learningPath)}
                      </ReactMarkdown>
                    </LearningPathContent>
                  </>
                ) : (
                  <EmptyState>
                    <EmptyStateIcon><FaRoad /></EmptyStateIcon>
                    <p>No learning path has been generated yet.</p>
                    <ActionButton onClick={handleGenerateClick}>
                      <FaPlusCircle /> Generate Learning Path
                    </ActionButton>
                  </EmptyState>
                )}
              </>
            )}
          </TabContent>
        </TabContainer>
      </LeftSection>

      <MiddleSection />

      <RightSection />

      {showProfileModal && (
        <>
          <ModalOverlay onClick={() => setShowProfileModal(false)} />
          <ProfileModal>
            <ModalHeader>
              <FaUser />
              <h3>{profile ? 'Update' : 'Create'} Learning Profile</h3>
            </ModalHeader>
            
            <form onSubmit={handleSubmit}>
              <FormGroup>
                <Label>Career Stage</Label>
                <Select
                  value={tempProfile.careerStage}
                  onChange={(e) => {
                    const value = e.target.value;
                    setTempProfile({...tempProfile, careerStage: value});
                    setUseCustomCareerStage(value === 'other');
                  }}
                  required
                >
                  <option value="">Select Career Stage</option>
                  <option value="student">Student</option>
                  <option value="junior backend developer">Junior Backend Developer</option>
                  <option value="junior frontend developer">Junior Frontend Developer</option>
                  <option value="mid-level developer">Mid-Level Developer</option>
                  <option value="senior developer">Senior Developer</option>
                  <option value="other">Other (specify)</option>
                </Select>
                
                {useCustomCareerStage && (
                  <CareerStageInput
                    type="text"
                    placeholder="Enter your career stage"
                    value={tempProfile.customCareerStage}
                    onChange={(e) => setTempProfile({...tempProfile, customCareerStage: e.target.value})}
                    required
                  />
                )}
              </FormGroup>

              <FormGroup>
                <Label>Technical Skills (comma-separated)</Label>
                <Input
                  type="text"
                  placeholder="e.g. java, python, git"
                  value={tempProfile.skills}
                  onChange={(e) => setTempProfile({...tempProfile, skills: e.target.value})}
                  required
                />
              </FormGroup>

              <FormGroup>
                <Label>Learning Goals</Label>
                <TextArea
                  rows={3}
                  placeholder="Describe your learning goals"
                  value={tempProfile.goals}
                  onChange={(e) => setTempProfile({...tempProfile, goals: e.target.value})}
                  required
                />
              </FormGroup>

              <FormGroup>
                <Label>Available Time (hours/week)</Label>
                <Input
                  type="number"
                  min="1"
                  max="40"
                  value={tempProfile.hoursPerWeek}
                  onChange={(e) => setTempProfile({...tempProfile, hoursPerWeek: e.target.value})}
                  required
                />
              </FormGroup>

              <ButtonGroup>
                <CancelButton type="button" onClick={() => setShowProfileModal(false)}>
                  Cancel
                </CancelButton>
                <SubmitButton type="submit" disabled={loading}>
                  {loading ? 'Processing...' : profile ? 'Update Profile' : 'Create Profile'}
                </SubmitButton>
              </ButtonGroup>
            </form>
          </ProfileModal>
        </>
      )}
    </CoursePlannerContainer>
  );
};

export default CoursePlanner;