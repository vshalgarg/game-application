import { BrowserRouter, Routes, Route } from "react-router-dom";

import MainLayout from "../layouts/MainLayout";
import ProtectedRoute from "./ProtectedRoute";

import Login from "../pages/Login";
import Signup from "../pages/Signup";

import Landing from "../pages/Landing";
import Home from "../pages/Home";
import CreateRoom from "../pages/CreateRoom";
import JoinRoom from "../pages/JoinRoom";
import WaitingRoom from "../pages/WaitingRoom";
import GameRoom from "../pages/GameRoom";
import GameMode from "../pages/GameMode";
import ComputerRoom from "../pages/ComputerRoom";

import LudoGameMode from "../pages/ludoPages/LudoGameMode";
import LudoCreateJoinRoom from "../pages/ludoPages/LudoCreateJoinRoom";
import LudoBotCreateRoom from "../pages/ludoPages/LudoBotCreateRoom";
import LudoCreateRoom from "../pages/ludoPages/LudoCreateRoom";
import LudoJoinRoom from "../pages/ludoPages/LudoJoinRoom";
import LudoWaitingRoom from "../pages/ludoPages/LudoWaitingRoom";
import LudoGameRoom from "../pages/ludoPages/LudoGameRoom";

import About from "../pages/About";
import Contact from "../pages/Contact";
import Profile from "../pages/Profile";

const AppRoutes = () => {
  return (

    <BrowserRouter>

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

                  <Route path="/" element={<Landing />} />          {/*select game page */}

                  {/* tic-tac-toe */}

                  <Route path="/game-mode" element={<GameMode />} />   {/* Play With Person / Play With Computer */}
                  <Route path="/computer-room" element={<ComputerRoom />} />   {/* Play with computer room created and start game button page */}
                  <Route path="/tic-tac-toe" element={<Home />} />      {/*create and join room button page */}
                  <Route path="/create-room" element={<CreateRoom />} />      {/*Room created page for host only */}
                  <Route path="/join-room" element={<JoinRoom />} />     {/*join room page for player only */}
                  <Route path="/waiting-room/:roomCode" element={<WaitingRoom />} />      {/*waiting room page for both host and player */}
                  <Route path="/game-room/:roomCode" element={<GameRoom />} />      {/*actual tic tac toe game room page */}

                  {/* Ludo */}
                  
                  <Route path="/ludoGame-mode" element={<LudoGameMode/>} />    {/* Play With Person / Play With Computer */}
                  <Route path="/createjoin-room" element={<LudoCreateJoinRoom/>} />     {/*create and join room button page */}
                  <Route path="/ludobotcreate-room" element={<LudoBotCreateRoom/>} />   {/* Play with computer room created and start game button page */}
                  <Route path="/ludocreate-room" element={<LudoCreateRoom/>} />   {/*Room created page for host only */}
                  <Route path="/ludojoin-room" element={<LudoJoinRoom/>} />
                  <Route path="/ludowaiting-room/:roomCode" element={<LudoWaitingRoom/>} />
                  <Route path="/ludogame-room/:roomCode" element={<LudoGameRoom/>} /> 

                  {/* Navbar */}
                  <Route path="/about" element={<About />} />       {/*about page for of navigation bar */}
                  <Route path="/contact" element={<Contact />} />      {/*contact page of navigation bar */}
                  <Route path="/profile" element={<Profile />} />     {/*profile page of navigation bar*/}

                </Routes>
              </MainLayout>
             </ProtectedRoute>
          }
        />

      </Routes>

    </BrowserRouter>

  );
};

export default AppRoutes;