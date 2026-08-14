import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaGamepad } from "react-icons/fa";
import gameBg from "../assets/images/game_bg.jpg";
import GameZoneLogo from "../components/brand/GameZoneLogo";
import SelectField from "../components/ui/SelectField";

const gameOptions = [
  { label: "Select a game", value: "", path: null },
  { label: "Tic Tac Toe", value: "Tic Tac Toe", path: "/game-mode" },
  { label: "Ludo", value: "Ludo", path: "/ludoGame-mode" },
];

const Landing = () => {
  const navigate = useNavigate();
  const [selectedGame, setSelectedGame] = useState("");

  const handleSelect = (value) => {
    setSelectedGame(value);

    const selected = gameOptions.find((option) => option.value === value);
    if (selected?.path) {
      navigate(selected.path);
    }
  };

  return (
    <div className="gz-page-shell">
      <div
        className="gz-page-shell__bg"
        style={{ backgroundImage: `url(${gameBg})` }}
        aria-hidden="true"
      />

      <div className="gz-select-card">
        <div className="mb-6 flex flex-col items-center">
          <GameZoneLogo className="mb-4 h-14 w-14 text-gz-primary-cyan" />
          <h1 className="text-3xl font-bold text-gz-text sm:text-4xl">Select Game</h1>
          <p className="mt-2 text-sm text-gz-text-secondary sm:text-base">
            Choose a game to continue
          </p>
        </div>

        <SelectField
          id="select-game"
          value={selectedGame}
          onChange={(e) => handleSelect(e.target.value)}
          options={gameOptions}
          leftIcon={<FaGamepad size={16} />}
          aria-label="Select a game"
        />
      </div>
    </div>
  );
};

export default Landing;