import { BrowserRouter, Route, Routes } from 'react-router-dom'

import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import HomePage from './pages/HomePage'

const App = () => {
  return (
    <BrowserRouter>
    <Routes>
      <Route path="/" element={<LoginPage />}></Route>
      <Route path="/register" element={<RegisterPage />}></Route>
      <Route path="/home" element={<HomePage />}></Route>
      </Routes></BrowserRouter>
  )
}

export default App