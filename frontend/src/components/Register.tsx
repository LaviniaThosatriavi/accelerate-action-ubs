import { useState, type FormEvent, type ChangeEvent } from "react";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import { API_BASE_URL } from "../config/api";

const RegisterContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100vw;
  min-height: calc(100vh - 10vh);
  background-color: #f5f5f5;
  padding: clamp(1rem, 3vw, 2rem);
  box-sizing: border-box;

  @media (max-height: 700px) {
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
  max-width: clamp(450px, 90vw, 650px);
  box-sizing: border-box;
`;

const FormWrapper = styled.form`
  width: 100%;
  display: flex;
  flex-direction: column;
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
  margin-top: 0;
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

  &.error {
    border-color: #e74c3c;
    box-shadow: 0 0 0 2px rgba(231, 76, 60, 0.2);
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

const SuccessText = styled.div`
  color: #04AA6D;
  font-size: clamp(0.75rem, 2vw, 0.875rem);
  background-color: #f0f9f4;
  border: 1px solid #a7f3d0;
  border-radius: clamp(3px, 1vw, 6px);
  padding: clamp(8px, 2vw, 12px);
  margin-bottom: clamp(1rem, 3vw, 1.5rem);
  text-align: center;
  line-height: 1.4;
`;

const NavigationText = styled.div`
  margin-top: clamp(1rem, 3vw, 1.5rem);
  font-size: clamp(0.8rem, 2.2vw, 0.95rem);
  color: #666;
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

const PasswordStrengthIndicator = styled.div<{ strength: number }>`
  height: 4px;
  background-color: #e0e0e0;
  border-radius: 2px;
  margin-top: 5px;
  overflow: hidden;

  &::after {
    content: '';
    display: block;
    height: 100%;
    width: ${props => props.strength}%;
    background-color: ${props => 
      props.strength < 30 ? '#e74c3c' :
      props.strength < 60 ? '#f39c12' :
      props.strength < 80 ? '#f1c40f' : '#04AA6D'
    };
    transition: all 0.3s ease;
  }
`;

const PasswordHint = styled.div`
  font-size: clamp(0.7rem, 1.8vw, 0.8rem);
  color: #666;
  margin-top: 4px;
  line-height: 1.3;
`;

const Register = () => {
  const [username, setUsername] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [confirmPassword, setConfirmPassword] = useState<string>('');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const navigate = useNavigate();

  const calculatePasswordStrength = (password: string): number => {
    let strength = 0;
    if (password.length >= 6) strength += 25;
    if (password.length >= 8) strength += 25;
    if (/[A-Z]/.test(password)) strength += 25;
    if (/[0-9]/.test(password)) strength += 25;
    return strength;
  };

  const passwordStrength = calculatePasswordStrength(password);

  const handleRegister = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    // Validation
    if (!username.trim()) {
      setError('Username is required');
      return;
    }
    if (username.length < 3) {
      setError('Username must be at least 3 characters long');
      return;
    }
    if (!password) {
      setError('Password is required');
      return;
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters long');
      return;
    }
    if (password !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    try {
     const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
        credentials: 'include'
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message || 'Registration failed');
      }
      
      setSuccess('Registration successful! Redirecting to login...');
      setTimeout(() => navigate('/'), 2000);
    } catch (err) {
      console.error('Registration error:', err);
      setError(err instanceof Error ? err.message : 'Username already exists or registration failed');
    }
  };

  return (
    <RegisterContainer>
      <Container>
        <FormWrapper onSubmit={handleRegister}>
          <Legend>Create Account</Legend>
          
          {error && <ErrorText>{error}</ErrorText>}
          {success && <SuccessText>{success}</SuccessText>}
          
          <Row>
            <Col25>
              <Label htmlFor="username">Username</Label>
            </Col25>
            <Col75>
              <Input 
                type="text" 
                id="username"
                name="username"
                placeholder="Choose a username (min 3 characters)" 
                value={username}
                onChange={(e: ChangeEvent<HTMLInputElement>) => setUsername(e.target.value)}
                className={error && !username.trim() ? 'error' : ''}
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
                placeholder="Create a password (min 6 characters)" 
                value={password}
                onChange={(e: ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)}
                className={error && !password ? 'error' : ''}
                required
              />
              {password && (
                <>
                  <PasswordStrengthIndicator strength={passwordStrength} />
                  <PasswordHint>
                    Password strength: {
                      passwordStrength < 30 ? 'Weak' :
                      passwordStrength < 60 ? 'Fair' :
                      passwordStrength < 80 ? 'Good' : 'Strong'
                    }
                  </PasswordHint>
                </>
              )}
            </Col75>
          </Row>

          <Row>
            <Col25>
              <Label htmlFor="confirmPassword">Confirm Password</Label>
            </Col25>
            <Col75>
              <Input 
                type="password" 
                id="confirmPassword"
                name="confirmPassword"
                placeholder="Confirm your password" 
                value={confirmPassword}
                onChange={(e: ChangeEvent<HTMLInputElement>) => setConfirmPassword(e.target.value)}
                className={error && password !== confirmPassword ? 'error' : ''}
                required
              />
              {confirmPassword && password !== confirmPassword && (
                <PasswordHint style={{ color: '#e74c3c' }}>
                  Passwords do not match
                </PasswordHint>
              )}
              {confirmPassword && password === confirmPassword && password && (
                <PasswordHint style={{ color: '#04AA6D' }}>
                  Passwords match ✓
                </PasswordHint>
              )}
            </Col75>
          </Row>

          <FullWidthRow>
            <SubmitButton type="submit" value="Create Account" />
          </FullWidthRow>

          <NavigationText>
            Already have an account?
            <StyledLink onClick={() => navigate('/')}>Sign in here</StyledLink>
          </NavigationText>
        </FormWrapper>
      </Container>
    </RegisterContainer>
  );
};

export default Register;