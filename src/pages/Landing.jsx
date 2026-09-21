import { useNavigate } from "react-router-dom";
import { FaGamepad } from "react-icons/fa";
import { popularGames } from "../data/games";
import PageShell from "../components/layout/PageShell";
import CircuitMark from "../components/brand/CircuitMark";
import GameCarousel from "../components/games/GameCarousel";
import FeatureStrip from "../components/games/FeatureStrip";
import { getGameSounds } from "../services/soundService";
import { loadGameSounds, playBackgroundMusic } from "../services/soundManager";

const Landing = () => {
  const navigate = useNavigate();

  const handleSelect = async (game) => {

  try {
    let gameType;

    if (game.id === "ludo") {
      gameType = "LUDO";
    } else if (game.id === "tic-tac-toe") {
      gameType = "TIC_TAC_TOE";
    }

    if (gameType) {

      // sound response api 
      const soundResponse = await getGameSounds(gameType);
      await loadGameSounds(gameType, soundResponse);

      // playing bg music
      
        playBackgroundMusic(gameType);
      
    }

    if (game.path) {
      navigate(game.path);
    }
  } catch (error) {
    console.error("Failed to load game sounds:", error);

    if (game.path) {
      navigate(game.path);
    }
  }
};

  return (
    <PageShell className="gz-page-shell--dashboard">
      <div className="gz-dashboard">
        <header className="flex shrink-0 flex-col items-center gap-2 lg:flex-row lg:items-center lg:justify-between">
          <div className="flex-1 text-center lg:text-left">
            <p className="text-[11px] font-semibold tracking-[0.22em] text-gz-primary-cyan">
              CHOOSE YOUR ADVENTURE
            </p>
            <h1 className="mt-1 text-xl font-bold text-gz-text sm:text-2xl md:text-3xl xl:text-4xl">
              Select Game
            </h1>
            <div className="gz-divider mx-auto mt-1.5 max-w-[220px] justify-center lg:mx-0">
              <FaGamepad className="text-gz-primary-cyan" size={14} />
            </div>
            <p className="mt-1 hidden text-sm text-gz-text-secondary sm:block">
              Choose a game to continue
            </p>
          </div>

          <CircuitMark className="hidden lg:block h-16 w-16 xl:h-20 xl:w-20" />
        </header>

        <GameCarousel games={popularGames} onSelect={handleSelect} />
        <FeatureStrip />
      </div>
    </PageShell>
  );
};

export default Landing;
