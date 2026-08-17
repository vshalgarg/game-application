import { useNavigate } from "react-router-dom";
import { FaDesktop, FaGamepad, FaUserFriends } from "react-icons/fa";
import PageShell from "../components/layout/PageShell";
import GameZoneLogo from "../components/brand/GameZoneLogo";
import ModeOption from "../components/ui/ModeOption";

const GameMode = () => {
  const navigate = useNavigate();

  return (
    <PageShell>
      <div className="gz-select-card w-full">
        <div className="mb-5 flex flex-col items-center">
          <GameZoneLogo className="mb-3 h-10 w-10" />
          <h1 className="text-2xl font-bold text-gz-text sm:text-3xl">Tic Tac Toe</h1>
          <div className="gz-divider mt-3 w-full max-w-[200px] justify-center">
            <FaGamepad className="text-gz-primary-cyan" size={12} />
          </div>
          <p className="mt-2 text-sm text-gz-text-secondary">Choose Game Mode</p>
        </div>

        <div className="flex flex-col gap-2.5">
          <ModeOption
            icon={FaUserFriends}
            label="Play With Person"
            tone="cyan"
            onClick={() => navigate("/tic-tac-toe")}
          />
          <ModeOption
            icon={FaDesktop}
            label="Play With Computer"
            tone="purple"
            onClick={() => navigate("/computer-room")}
          />
        </div>
      </div>
    </PageShell>
  );
};

export default GameMode;
