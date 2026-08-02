import { BrowserRouter } from "react-router-dom";
import { NavBar } from "./components/layout/NavBar";
import { AppRouter } from "./router/AppRouter";

export default function App(){
  return(
    <BrowserRouter>
      <div className="app-shell">
        <NavBar />
        <AppRouter />
      </div>
    </BrowserRouter>
  );
}