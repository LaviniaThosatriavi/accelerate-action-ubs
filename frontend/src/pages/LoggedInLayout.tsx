import React, { useState } from "react";
import Navbar from "../components/Navbar";
import styled from "styled-components";
import { AppBar } from "@mui/material";
import Sidebar from "../components/Sidebar";
import { WeeklyHoursContext } from "../components/WeeklyHoursContext";

const StyledAppBar = styled(AppBar)`
  flex-shrink: 0;
`;

const LayoutContainer = styled.div`
  display: flex;
  position: fixed;
  top: 10vh;
  left: 0;
  right: 0;
  bottom: 0;
`;

const ContentWrapper = styled.div<{ $sidebarWidth: number }>`
  flex: 1;
  width: calc(100% - ${(props) => props.$sidebarWidth}px);
  transition: width 0.3s ease;
  overflow-y: auto;
`;

interface Props {
  children: React.ReactNode;
}

const LoggedInLayout = ({ children }: Props) => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const [weeklyHours, setWeeklyHours] = useState<number>(0); 
  
  const sidebarWidth = isSidebarOpen ? 250 : 0; 

  return (
    <WeeklyHoursContext.Provider value={{ weeklyHours, setWeeklyHours }}>
      <StyledAppBar color="default" position="fixed">
        <Navbar />
      </StyledAppBar>

      <LayoutContainer>
        <Sidebar isOpen={isSidebarOpen} onToggle={() => setIsSidebarOpen(!isSidebarOpen)} />
        <ContentWrapper $sidebarWidth={sidebarWidth}>{children}</ContentWrapper>
      </LayoutContainer>
    </WeeklyHoursContext.Provider>
  );
};

export default LoggedInLayout;