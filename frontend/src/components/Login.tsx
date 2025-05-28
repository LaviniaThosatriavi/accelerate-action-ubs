import { useState, type FormEvent, type ChangeEvent } from "react";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";

const LoginContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100vw;
  height: calc(100vh - 10vh);
  background-color: white;
`;

const FormWrapper = styled.form`
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 2rem;
  background-color: white;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 300px;
  display: flex;
  flex-direction: column;
`;

const Legend = styled.legend`
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
`;

const Input = styled.input`
  margin: 1rem 0;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 1rem;
  background-color: white;
  color: black;
`;

const Button = styled.button`
  margin-top: 1rem;
  padding: 0.5rem;
  border: none;
  border-radius: 5px;
  background-color: black;
  color: white;
  font-size: 1rem;
  cursor: pointer;

  &:hover {
    background-color: #444;
  }
`;

const ErrorText = styled.div`
  color: #ff0000;
  margin-bottom: 1rem;
  font-size: 0.875rem;
`;

const NavigationText = styled.div`
  margin-top: 1rem;
  font-size: 0.875rem;
  color: black;
  text-align: left;
  display: flex;
  align-items: baseline;
  gap: 0.25rem;
`;

const StyledLink = styled.span`
  color: #ff0000;
  text-decoration: underline;
  cursor: pointer;
  /* Remove float property */

  &:hover {
    color: #cc0000;
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
      });

      if (!response.ok) throw new Error();
      
      const token = await response.text();
      localStorage.setItem('jwtToken', token);
      navigate('/home');
    } catch {
      setError('Invalid username or password');
    }
  };

  return (
    <LoginContainer>
      <FormWrapper onSubmit={handleLogin}>
        <Legend>Login</Legend>
        {error && <ErrorText>{error}</ErrorText>}
        <Input 
          type="text" 
          placeholder="Enter Username" 
          value={username}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setUsername(e.target.value)}
        />
        <Input 
          type="password" 
          placeholder="Enter Password" 
          value={password}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)}
        />
        <Button type="submit">Login</Button>
        <NavigationText>
          Don't have an account yet?
          <StyledLink onClick={() => navigate('/register')}>Register</StyledLink>
        </NavigationText>
      </FormWrapper>
    </LoginContainer>
  );
};

export default Login;