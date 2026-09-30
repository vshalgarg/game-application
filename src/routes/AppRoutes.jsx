import { lazy, Suspense } from "react";
import { Navigate, Routes, Route } from "react-router-dom";
import Login from "../pages/Login";

const MainLayout = lazy(() => import("../layouts/MainLayout"));
const ProtectedRoute = lazy(() => import("./ProtectedRoute"));
const Landing = lazy(() => import("../pages/Landing"));
const Home = lazy(() => import("../pages/Home"));
const WaitingRoom = lazy(() => import("../pages/WaitingRoom"));
const GameRoom = lazy(() => import("../pages/GameRoom"));
const GameLayout = lazy(() => import("../layouts/GameLayout"));
const LudoCreateJoinRoom = lazy(() => import("../pages/ludoPages/LudoCreateJoinRoom"));
const LudoWaitingRoom = lazy(() => import("../pages/ludoPages/LudoWaitingRoom"));
const LudoGameRoom = lazy(() => import("../pages/ludoPages/LudoGameRoom"));
const About = lazy(() => import("../pages/About"));
const Contact = lazy(() => import("../pages/Contact"));
const Profile = lazy(() => import("../pages/Profile"));
const CompleteProfile = lazy(() => import("../pages/CompleteProfile"));
const TambolaCreateJoin = lazy(() => import("../pages/tambolaPages/TambolaCreateJoin"));
const TambolaMode = lazy(() => import("../pages/tambolaPages/TambolaMode"));
const TambolaWaiting = lazy(() => import("../pages/tambolaPages/TambolaWaiting"));
const TambolaGameRoom = lazy(() => import("../pages/tambolaPages/TambolaGameRoom"));
const ChiefDropFrame = lazy(() => import("../pages/ChiefDropFrame"));

const AppRoutes = () => {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <Routes>
        {/* Public Routes */}
        <Route path="/login" element={<Login />} />
        <Route
          path="/complete-profile"
          element={
            <ProtectedRoute>
              <CompleteProfile />
            </ProtectedRoute>
          }
        />

        {/* Protected Routes with Navbar */}
        <Route
          path="/*"
          element={
            <ProtectedRoute requireCompleteProfile>
              <MainLayout>
                <Routes>
                  <Route path="/" element={<Landing />} />
                  <Route element={<GameLayout gameType="TIC_TAC_TOE" />}>
                    <Route path="/tic-tac-toe" element={<Home />} />
                    <Route path="/waiting-room/:roomCode" element={<WaitingRoom />} />
                    <Route path="/game-room/:roomCode" element={<GameRoom />} />
                  </Route>
                  <Route path="/ludoGame-mode" element={<Navigate to="/createjoin-room" replace />} />
                  <Route element={<GameLayout gameType="LUDO" />}>
                    <Route path="/createjoin-room" element={<LudoCreateJoinRoom />} />
                    <Route path="/ludowaiting-room/:roomCode" element={<LudoWaitingRoom />} />
                    <Route path="/ludogame-room/:roomCode" element={<LudoGameRoom />} />
                  </Route>

                  <Route element={<GameLayout gameType="TAMBOLA" />}>
                    <Route path="/tcreate-join" element={<TambolaCreateJoin />} />
                    <Route path="/tambola-mode" element={<TambolaMode />} />
                    <Route path="/tambola-waiting/:roomCode" element={<TambolaWaiting />} />
                    <Route path="/tambola-gameroom/:roomCode" element={<TambolaGameRoom />} />
                  </Route>
                  
                  {/* Navbar */}
                  <Route path="/chief-drop" element={<ChiefDropFrame />} />
                  <Route path="/about" element={<About />} /> {/*about page for of navigation bar */}
                  <Route path="/contact" element={<Contact />} />{" "}    {/*contact page of navigation bar */}
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
