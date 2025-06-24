import React from "react";
import styled from "styled-components";
import { useNavigate, useLocation } from "react-router-dom";
import { MdHome } from "react-icons/md";
import { HiOutlineChevronDoubleLeft, HiOutlineChevronDoubleRight } from "react-icons/hi";

interface SidebarProps {
  isOpen: boolean;
  onToggle: () => void;
}

const SidebarContainer = styled.div<{ isOpen: boolean }>`
  width: ${(props) => (props.isOpen ? "15%" : "0")};
  height: 100%;
  transition: width 0.3s ease;
  background-color: #f8f9fa;
  display: flex;
  flex-direction: column;
  padding: ${(props) => (props.isOpen ? "1rem" : "0")};
  overflow: hidden;
  white-space: nowrap;
  box-shadow: ${(props) => (props.isOpen ? "2px 0px 5px rgba(0, 0, 0, 0.1)" : "none")};
`;

const SidebarItem = styled.div`
  margin-top: 2vh;
  margin-bottom: 2vh;
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: clamp(0.8rem, 2vw, 1.2rem);
  color: #333;

  &:hover {
    color: #db2b45;
  }
`;

const IconWrapper = styled.div`
  margin-right: clamp(0.2rem, 1vw, 0.5rem);
  display: flex;
  align-items: center;
  justify-content: center;

  svg {
    width: clamp(12px, 3vw, 22px); /* Icon size: 16px to 24px */
    height: clamp(12px, 3vw, 22px);
  }
`;

const SubHeading = styled.div<{ active?: boolean }>`
  font-size: clamp(0.75rem, 2vw, 1.4rem);
  font-weight: bold;
  margin-top: 4vh;
  color: ${props => props.active ? '#db2b45' : '#555'};
  cursor: pointer;
  transition: color 0.2s ease;
  
  white-space: normal; // Allow wrapping 
  word-wrap: break-word; // Break long words 
  overflow-wrap: break-word; 
  line-height: 1.3; // Comfortable line spacing 
  max-width: 100%; 

  &:hover {
    color: #db2b45;
  }
`;

const CloseButton = styled.div`
  margin-top: auto;
  margin-bottom: 3rem;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: clamp(0.8rem, 2.5vw, 1.2rem);
  color: #333;

  &:hover {
    color: #db2b45;
  }
`;

const ReopenButton = styled.div`
  position: fixed;
  top: 50%;
  left: 0;
  transform: translateY(-50%);
  z-index: 1000;
  cursor: pointer;
  background-color: #f8f9fa;
  padding: 0.5rem;
  border-radius: 0 5px 5px 0;
  box-shadow: 2px 0px 5px rgba(0, 0, 0, 0.1);
  color: #333;

  &:hover {
    background-color: #db2b45;
    color: white;
  }
`;

const Sidebar: React.FC<SidebarProps> = ({ isOpen, onToggle }) => {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <>
      {!isOpen && (
        <ReopenButton onClick={onToggle}>
          <HiOutlineChevronDoubleRight size={20} />
        </ReopenButton>
      )}
      <SidebarContainer isOpen={isOpen}>
        <SidebarItem onClick={() => navigate("/home")}>
          <IconWrapper>
            <MdHome />
          </IconWrapper>
          Home
        </SidebarItem>

        <SubHeading 
          onClick={() => navigate("/todos")}
          active={location.pathname === "/todos"}
        >
          TO DOs
        </SubHeading>
        <SubHeading 
          onClick={() => navigate("/course-planner")}
          active={location.pathname === "/course-planner"}
        >
          Course Planner
        </SubHeading>
        <SubHeading 
          onClick={() => navigate("/report")}
          active={location.pathname === "/report"}
        >
          Report
        </SubHeading>
        <SubHeading 
          onClick={() => navigate("/achievements")}
          active={location.pathname === "/achievements"}
        >
          Achievements
        </SubHeading>
        
        <CloseButton onClick={onToggle}>
          <IconWrapper>
            <HiOutlineChevronDoubleLeft  />
          </IconWrapper>
          {isOpen ? "Close" : ""}
        </CloseButton>
      </SidebarContainer>
    </>
  );
};

export default Sidebar;