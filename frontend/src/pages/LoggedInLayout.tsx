import React, { useState } from "react";
import Navbar from "../components/Navbar";
import styled from "styled-components";
import { AppBar } from "@mui/material";
import Sidebar from "../components/Sidebar";

const StyledAppBar = styled(AppBar)`
  flex-shrink: 0;
`;

const LayoutContainer = styled.div`
  display: flex;
  height: calc(100vh - 10vh); /* Full height minus the navbar height */
  margin-top: 10vh; /* Offset for the navbar */
`;

const ContentWrapper = styled.div<{ sidebarWidth: number }>`
  flex: 1; /* Take up remaining space */
  width: calc(100% - ${(props) => props.sidebarWidth}px); /* Adjust width dynamically */
  transition: width 0.3s ease; /* Smooth transition when sidebar opens/closes */
  padding: 1rem;
  overflow-y: auto; /* Allow scrolling if content overflows */
`;

interface Props {
  children: React.ReactNode;
}

const LoggedInLayout = ({ children }: Props) => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true); // Sidebar open state
  const sidebarWidth = isSidebarOpen ? 250 : 0; // Sidebar width when open or closed

  return (
    <>
      {/* Navbar */}
      <StyledAppBar color="default" position="fixed">
        <Navbar />
      </StyledAppBar>

      {/* Sidebar and Content on the same level */}
      <LayoutContainer>
        <Sidebar isOpen={isSidebarOpen} onToggle={() => setIsSidebarOpen(!isSidebarOpen)} />
        <ContentWrapper sidebarWidth={sidebarWidth}>{children}</ContentWrapper>
      </LayoutContainer>
    </>
  );
};

export default LoggedInLayout;