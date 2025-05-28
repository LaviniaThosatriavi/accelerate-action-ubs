import { useState, type FormEvent, type ChangeEvent } from "react";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";

const RegisterContainer = styled.div`
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
  text-align: center;
  font-size: 0.875rem;
`;

const StyledLink = styled.span`
  color: #ff0000;
  text-decoration: underline;
  cursor: pointer;
  margin-left: 0.25rem;

  &:hover {
    color: #cc0000;
  }
`;

const Register = () => {
  const [username, setUsername] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [confirmPassword, setConfirmPassword] = useState<string>('');
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const handleRegister = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (password !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });

      if (!response.ok) throw new Error();
      
      window.alert('Registration successful!');
      navigate('/');
    } catch {
      setError('Username already exists');
    }
  };

  return (
    <RegisterContainer>
      <FormWrapper onSubmit={handleRegister}>
        <Legend>Register</Legend>
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
        <Input 
          type="password" 
          placeholder="Confirm Password" 
          value={confirmPassword}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setConfirmPassword(e.target.value)}
        />
        <Button type="submit">Register</Button>
        <NavigationText>
          Already have an account?
          <StyledLink onClick={() => navigate('/')}>Back to Login</StyledLink>
        </NavigationText>
      </FormWrapper>
    </RegisterContainer>
  );
};

export default Register;