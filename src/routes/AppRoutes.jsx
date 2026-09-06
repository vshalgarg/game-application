import { lazy, Suspense } from "react";
import { Navigate, Routes, Route } from "react-router-dom";

const MainLayout = lazy(() => import("../layouts/MainLayout"));
const ProtectedRoute = lazy(() => import("./ProtectedRoute"));
const Login = lazy(() => import("../pages/Login"));
const Signup = lazy(() => import("../pages/Signup"));
const Landing = lazy(() => import("../pages/Landing"));
const Home = lazy(() => import("../pages/Home"));
const WaitingRoom = lazy(() => import("../pages/WaitingRoom"));
const GameRoom = lazy(() => import("../pages/GameRoom"));
const LudoCreateJoinRoom = lazy(() => import("../pages/ludoPages/LudoCreateJoinRoom"));
const LudoWaitingRoom = lazy(() => import("../pages/ludoPages/LudoWaitingRoom"));
const LudoGameRoom = lazy(() => import("../pages/ludoPages/LudoGameRoom"));
const About = lazy(() => import("../pages/About"));
const Contact = lazy(() => import("../pages/Contact"));
const Profile = lazy(() => import("../pages/Profile"));

const AppRoutes = () => {
  return (
    <Suspense fallback={<div>Loading...</div>}>
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
    </Suspense>
  );
};

export default AppRoutes;
