import { Navigate, Routes, Route } from "react-router-dom";
import MainLayout from "../layouts/MainLayout";
import ProtectedRoute from "./ProtectedRoute";
import Login from "../pages/Login";
import Signup from "../pages/Signup";
import Landing from "../pages/Landing";
import Home from "../pages/Home";
import WaitingRoom from "../pages/WaitingRoom";
import GameRoom from "../pages/GameRoom";
import LudoCreateJoinRoom from "../pages/ludoPages/LudoCreateJoinRoom";
import LudoWaitingRoom from "../pages/ludoPages/LudoWaitingRoom";
import LudoGameRoom from "../pages/ludoPages/LudoGameRoom";
import About from "../pages/About";
import Contact from "../pages/Contact";
import Profile from "../pages/Profile";

const AppRoutes = () => {
  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/login" element={<Login />} />
      <Route path="/signup" element={<Signup />} />

      {/* Protected Routes with Navbar */}
      <Route
        path="/*"
        element={
          <ProtectedRoute>
            <MainLayout>
              <Routes>
                <Route path="/" element={<Landing />} /> {/*select game page */}

                {/* tic-tac-toe */}
                <Route path="/tic-tac-toe" element={<Home />} />{" "}
                {/*create room, enter room ID, and join on the same screen */}
                <Route path="/waiting-room/:roomCode" element={<WaitingRoom />} />{" "}
                {/*waiting room page for both host and player */}
                <Route path="/game-room/:roomCode" element={<GameRoom />} />{" "}
                {/*actual tic tac toe game room page */}
                
                {/* Ludo */}
                <Route path="/ludoGame-mode" element={<Navigate to="/createjoin-room" replace />} />
                <Route path="/createjoin-room" element={<LudoCreateJoinRoom />} />{" "}
                {/*create and join room button page */}
                <Route path="/ludowaiting-room/:roomCode" element={<LudoWaitingRoom />} />
                <Route path="/ludogame-room/:roomCode" element={<LudoGameRoom />} />
                
                {/* Navbar */}
                <Route path="/about" element={<About />} /> {/*about page for of navigation bar */}
                <Route path="/contact" element={<Contact />} />{" "}
                {/*contact page of navigation bar */}
                <Route path="/profile" element={<Profile />} /> {/*profile page of navigation bar*/}
              </Routes>
            </MainLayout>
          </ProtectedRoute>
        }
      />
    </Routes>
  );
};

export default AppRoutes;
