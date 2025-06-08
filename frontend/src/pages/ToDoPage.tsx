import ToDo from '../components/ToDo'
import LoggedInLayout from './LoggedInLayout'

const ToDoPage = () => {
  return (
    <LoggedInLayout>
        <ToDo />
    </LoggedInLayout>
  )
}

export default ToDoPage