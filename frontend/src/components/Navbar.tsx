import styled from "styled-components";
import ubsLogo from "../assets/ubsLogo.png";
import empowHerTechLogo from "../assets/empowerHerTechLogo.png";

const NavbarContainer = styled.div`
  background-color: black;
  width: 100%;
  height: 10vh;
  display: flex;
  align-items: center;
  padding: 0 1rem;
  color: white;

  @media (max-width: 400px) {
    flex-direction: column;
    align-items: flex-start;
    justify-content: center;
  }
`;

const LogoContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 1vw;
`;

const Logo = styled.img`
  height: 5vh;
  object-fit: contain;

  @media (max-width: 768px) {
    height: 3vh;
  }
`;

const Title = styled.div`
  font-size: 1.4rem;
  margin-left: 3vw;
  font-weight: bold;
  color: white;

  @media (max-width: 768px) {
    font-size: 0.9rem;
    margin-left: 2vw;
  }

  @media (max-width: 400px) {
    font-size: 0.7rem;
    margin-left: 0;
    margin-top: 1vh;
    text-align: left;
  }
`;

const Italic = styled.span`
  font-style: italic;
  font-size: 1.7rem;

  @media (max-width: 768px) {
    font-size: 1rem;
  }

  @media (max-width: 400px) {
    font-size: 0.8rem;
  }
`;

const Navbar = () => {
  return (
    <NavbarContainer>
      <LogoContainer>
        <Logo src={ubsLogo} alt="UBS" />
        <Logo src={empowHerTechLogo} alt="EmpowHerTech" />
      </LogoContainer>
      <Title>Women in  <Italic>Action</Italic></Title>
    </NavbarContainer>
  );
};

export default Navbar;