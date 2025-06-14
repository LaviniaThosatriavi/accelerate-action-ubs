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
        <ContentWrapper $sidebarWidth={sidebarWidth}>{children}</ContentWrapper>
      </LayoutContainer>
    </>
  );
};

export default LoggedInLayout;