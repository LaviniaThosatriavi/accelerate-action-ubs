import LoggedInLayout from "./LoggedInLayout";
import Home from "../components/Home";

const HomePage = () => {
  return (
    <LoggedInLayout>
      <Home />
    </LoggedInLayout>
  );
};

export default HomePage;