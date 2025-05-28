import styled from "styled-components";

const LoginContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100vw; /* Full width */
  height: calc(100vh - 10vh); 
  background-color: white; 
`;

const FormWrapper = styled.fieldset`
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

const Login = () => {
  return (
    <LoginContainer>
      <FormWrapper>
        <Legend>Login</Legend>
        <Input type="text" placeholder="Enter Email" />
        <Input type="password" placeholder="Enter Password" />
        <Button>Login</Button>
        <Button
          style={{
            backgroundColor: "#DB2B45",
            marginTop: "0.5rem",
          }}
        >
          Register
        </Button>
      </FormWrapper>
    </LoginContainer>
  );
};

export default Login;