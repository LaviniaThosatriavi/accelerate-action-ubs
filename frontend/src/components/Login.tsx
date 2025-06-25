import { useState, type FormEvent, type ChangeEvent } from "react";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";

const LoginContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100vw;
  min-height: calc(100vh - 10vh);
  background-color: #f5f5f5;
  padding: clamp(1rem, 3vw, 2rem);
  box-sizing: border-box;

  @media (max-height: 600px) {
    min-height: 100vh;
    align-items: flex-start;
    padding-top: clamp(1rem, 5vh, 3rem);
  }
`;

const Container = styled.div`
  border-radius: clamp(8px, 1vw, 12px);
  background-color: white;
  padding: clamp(1.5rem, 4vw, 2.5rem);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: clamp(400px, 90vw, 600px);
  box-sizing: border-box;
`;

const FormWrapper = styled.form`
  width: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 25px;
`;

const Legend = styled.h2`
  font-size: clamp(1.5rem, 4vw, 2rem);
  font-weight: bold;
  color: #333;
  text-align: center;
  margin: 0 0 clamp(1.5rem, 4vw, 2rem) 0;
`;

const Row = styled.div`
  display: flex;
  flex-wrap: wrap;
  margin-bottom: clamp(1rem, 3vw, 1.5rem);
  
  &:after {
    content: "";
    display: table;
    clear: both;
  }

  @media screen and (max-width: 600px) {
    flex-direction: column;
  }
`;

const Col25 = styled.div`
  float: left;
  width: 25%;
  margin-top: clamp(4px, 1vw, 8px);
  padding-right: clamp(8px, 2vw, 16px);
  box-sizing: border-box;

  @media screen and (max-width: 600px) {
    width: 100%;
    margin-top: 0;
    padding-right: 0;
    margin-bottom: clamp(0.5rem, 2vw, 0.75rem);
  }
`;

const Col75 = styled.div`
  float: left;
  width: 75%;
  margin-top: clamp(4px, 1vw, 8px);
  box-sizing: border-box;

  @media screen and (max-width: 600px) {
    width: 100%;
    margin-top: 0;
  }
`;

const FullWidthRow = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
  margin-top: clamp(1rem, 3vw, 1.5rem);
`;

const Label = styled.label`
  padding: clamp(8px, 2vw, 12px) clamp(8px, 2vw, 12px) clamp(8px, 2vw, 12px) 0;
  display: inline-block;
  font-size: clamp(0.875rem, 2.5vw, 1.125rem);
  font-weight: 500;
  color: #333;
  
  @media screen and (max-width: 600px) {
    padding: clamp(6px, 1.5vw, 8px) 0;
  }
`;

const Input = styled.input`
  width: 100%;
  padding: clamp(8px, 2vw, 12px);
  border: 1px solid #ccc;
  border-radius: clamp(3px, 1vw, 6px);
  box-sizing: border-box;
  font-size: clamp(0.875rem, 2.5vw, 1rem);
  background-color: white;
  color: #333;
  transition: all 0.3s ease;

  &:focus {
    outline: none;
    border-color: #ff0000;
    box-shadow: 0 0 0 2px rgba(190, 14, 14, 0.2);
  }

  &::placeholder {
    color: #999;
    font-size: clamp(0.8rem, 2.2vw, 0.95rem);
  }
`;

const SubmitButton = styled.input`
  background-color: black;
  color: white;
  padding: clamp(10px, 2.5vw, 14px) clamp(16px, 4vw, 24px);
  border: none;
  border-radius: clamp(3px, 1vw, 6px);
  cursor: pointer;
  font-size: clamp(0.875rem, 2.5vw, 1.125rem);
  font-weight: 600;
  width: 100%;
  box-sizing: border-box;
  transition: all 0.3s ease;

  &:hover {
    background-color: #444;
    transform: translateY(-1px);
  }

  &:active {
    transform: translateY(0);
  }

  @media screen and (min-width: 601px) {
    width: auto;
    float: right;
    min-width: clamp(120px, 20vw, 200px);
  }
`;

const ErrorText = styled.div`
  color: #e74c3c;
  font-size: clamp(0.75rem, 2vw, 0.875rem);
  background-color: #fdf2f2;
  border: 1px solid #f5c6cb;
  border-radius: clamp(3px, 1vw, 6px);
  padding: clamp(8px, 2vw, 12px);
  margin-bottom: clamp(1rem, 3vw, 1.5rem);
  text-align: center;
  line-height: 1.4;
`;

const NavigationText = styled.div`
  margin-top: clamp(1rem, 3vw, 1.5rem);
  font-size: clamp(0.8rem, 2.2vw, 0.95rem);
  color: black;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: clamp(0.25rem, 1vw, 0.5rem);

  @media screen and (max-width: 400px) {
    flex-direction: column;
    gap: clamp(0.25rem, 1vh, 0.5rem);
  }
`;

const StyledLink = styled.span`
  color: #ff0000;
  text-decoration: underline;
  cursor: pointer;
  font-weight: 600;
  transition: color 0.3s ease;

  &:hover {
    color: #038a5a;
  }

  &:active {
    color: #026945;
  }
`;

const Login = () => {
  const [username, setUsername] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const handleLogin = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
        credentials: 'include'
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        console.error('Login failed:', response.status, errorData);
        throw new Error(errorData?.message || 'Login failed');
      }
      
      const data = await response.json();
      const token = data.token;
      
      if (!token) {
        console.error('No token in response', data);
        throw new Error('No token received');
      }
      
      console.log('Login successful, saving token');
      localStorage.setItem('token', token);
      localStorage.setItem('jwtToken', token);
      
      navigate('/home');
    } catch (err) {
      console.error('Login error:', err);
      setError(err instanceof Error ? err.message : 'Invalid username or password');
    }
  };

  return (
    <LoginContainer>
      <Container>
        <FormWrapper onSubmit={handleLogin}>
          <Legend>Login</Legend>
          
          {error && <ErrorText>{error}</ErrorText>}
          
          <Row>
            <Col25>
              <Label htmlFor="username">Username</Label>
            </Col25>
            <Col75>
              <Input 
                type="text" 
                id="username"
                name="username"
                placeholder="Enter your username" 
                value={username}
                onChange={(e: ChangeEvent<HTMLInputElement>) => setUsername(e.target.value)}
                required
              />
            </Col75>
          </Row>

          <Row>
            <Col25>
              <Label htmlFor="password">Password</Label>
            </Col25>
            <Col75>
              <Input 
                type="password" 
                id="password"
                name="password"
                placeholder="Enter your password" 
                value={password}
                onChange={(e: ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)}
                required
              />
            </Col75>
          </Row>

          <FullWidthRow>
            <SubmitButton type="submit" value="Login" />
          </FullWidthRow>

          <NavigationText>
            Don't have an account yet?
            <StyledLink onClick={() => navigate('/register')}>Register here</StyledLink>
          </NavigationText>
        </FormWrapper>
      </Container>
    </LoginContainer>
  );
};

export default Login;