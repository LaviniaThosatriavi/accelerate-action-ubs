import { BrowserRouter, Route, Routes } from 'react-router-dom'

import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import HomePage from './pages/HomePage'
import CoursePlannerPage from './pages/CoursePlannerPage'
import ToDoPage from './pages/ToDoPage'
import AchievementsPage from './pages/AchievementsPage'

const App = () => {
  return (
    <BrowserRouter>
    <Routes>
      <Route path="/" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/home" element={<HomePage />} />
      <Route path="/search" element={<HomePage />} />
      <Route path="/course-planner" element={<CoursePlannerPage />} />
      <Route path="/todos" element={<ToDoPage />} />
      <Route path="/achievements" element={<AchievementsPage />} />
      </Routes></BrowserRouter>
  )
}

export default App