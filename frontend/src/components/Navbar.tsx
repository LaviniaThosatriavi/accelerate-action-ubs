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
`;

const LogoContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem; /* Space between logos */
`;

const Logo = styled.img`
  height: 5vh; /* Adjust as needed */
  object-fit: contain;
`;

const Title = styled.div`
  font-size: 1.4rem;
  margin-left: 4rem;
  font-weight: bold;
  color: white;
`;

const Italic = styled.span`
  font-style: italic;
  font-size: 1.7rem;
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