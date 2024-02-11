import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import styled from "styled-components";
import "./App.css";

const Slide = styled("div")`
  width: 100vw;
  height: 100vh;
  max-width: 100vw;
  max-height: 100vh;
`;

const App = () => {
  return (
    <div className="app-container">
      <Router>
        <Routes>
          <Route path="/clock">
            <Slide>
              <h1>Test 1</h1>
            </Slide>
          </Route>
          <Route path="/weather">
            <Slide>
              <h1>Test 2</h1>
            </Slide>
          </Route>
          <Route path="/pt">
            <Slide>
              <h1>Test 3</h1>
            </Slide>
          </Route>
        </Routes>
      </Router>
    </div>
  );
};

export default App;
