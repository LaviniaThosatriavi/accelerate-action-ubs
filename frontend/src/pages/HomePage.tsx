import styled from 'styled-components';
import Home from "../components/Home";
import LoggedInLayout from './LoggedInLayout';

const PageContainer = styled.div`
  background-color: white;
  color: black;
  min-height: 100vh;
  display: flex;
  flex-direction: column;

  @media (max-width: 768px) {
    padding: 0 10px;
  }
`;

const HomePage = () => {
  return (
    <LoggedInLayout>
      <PageContainer>
        <Home />
      </PageContainer>
    </LoggedInLayout>
  );
};

export default HomePage;