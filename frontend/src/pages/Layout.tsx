import React from "react";
import Navbar from "../components/Navbar";
import styled from "styled-components";
import { AppBar } from "@mui/material";

const StyledAppBar = styled(AppBar)`
  flex-shrink: 0;
`;

interface Props {
  children: React.ReactNode;
}

const Layout = ({ children }: Props) => {
  return (
    <>
      <StyledAppBar color="default" position="fixed">
        <Navbar />
      </StyledAppBar>
      <ContentWrapper>{children}</ContentWrapper>
    </>
  );
};

const ContentWrapper = styled.div`
  margin-top: 10vh; /* Adjust to match the navbar height */
`;

export default Layout;