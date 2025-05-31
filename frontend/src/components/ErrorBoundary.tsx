import { Component, type ErrorInfo, type ReactNode } from 'react';
import styled from 'styled-components';

interface ErrorBoundaryProps {
  children: ReactNode;
}

interface ErrorBoundaryState {
  hasError: boolean;
  error?: Error;
}

const ErrorContainer = styled.div`
  padding: 2rem;
  text-align: center;
  background: #fff;
  border-radius: 8px;
  margin: 2rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
`;

const ErrorTitle = styled.h2`
  color: #ff4757;
  margin-bottom: 1rem;
`;

const ErrorMessage = styled.p`
  color: #2d3436;
  margin-bottom: 1.5rem;
`;

const ReloadButton = styled.button`
  background: #007bff;
  color: white;
  border: none;
  padding: 0.8rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #0056b3;
  }
`;

const ReportLink = styled.a`
  display: block;
  margin-top: 1rem;
  color: #007bff;
  text-decoration: none;
`;

class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  state: ErrorBoundaryState = {
    hasError: false,
    error: undefined
  };

  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    return {
      hasError: true,
      error: error
    };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error("Error Boundary caught an error:", error, errorInfo);
    // Add error logging service call here
  }

  handleReload = () => {
    window.location.reload();
  };

  render() {
    if (this.state.hasError) {
      return (
        <ErrorContainer>
          <ErrorTitle>Something went wrong</ErrorTitle>
          <ErrorMessage>
            {this.state.error?.message || 'An unexpected error occurred'}
          </ErrorMessage>
          <ReloadButton onClick={this.handleReload}>
            Reload Page
          </ReloadButton>
          <ReportLink href="/support">
            Report this issue
          </ReportLink>
        </ErrorContainer>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;