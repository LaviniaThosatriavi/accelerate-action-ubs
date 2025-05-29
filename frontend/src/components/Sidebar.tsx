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
  width: ${(props) => (props.isOpen ? "250px" : "0")};
  height: calc(100vh - 14vh);
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
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 1rem;
  color: #333;

  &:hover {
    color: #db2b45;
  }
`;

const IconWrapper = styled.div`
  margin-right: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
`;

const SubHeading = styled.div<{ active?: boolean }>`
  font-size: 1.2rem;
  font-weight: bold;
  margin-top: 2rem;
  color: ${props => props.active ? '#db2b45' : '#555'};
  cursor: pointer;
  transition: color 0.2s ease;

  &:hover {
    color: #db2b45;
  }
`;

const CloseButton = styled.div`
  margin-top: auto;
  padding: 1rem;
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 1rem;
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
            <MdHome size={20} />
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
            <HiOutlineChevronDoubleLeft size={20} />
          </IconWrapper>
          {isOpen ? "Close" : ""}
        </CloseButton>
      </SidebarContainer>
    </>
  );
};

export default Sidebar;