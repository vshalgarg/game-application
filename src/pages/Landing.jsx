import { useNavigate } from "react-router-dom";
import { useState } from "react";

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
    <div className="flex items-center justify-center h-[80vh] px-4">
      {/* Card */}

      <div className="w-full max-w-md bg-white/10 backdrop-blur-lg border border-white/20 rounded-3xl p-8 text-center shadow-2xl">
        <h1 className="text-4xl font-bold text-white mb-4">Select Game</h1>
        <p className="text-gray-300 mb-8">Choose a game to continue</p>

        {/* Dropdown */}
        <select
          value={selectedGame}
          onChange={(e) => handleSelect(e.target.value)}
          className="w-full bg-black/30 border border-cyan-400/30 text-white px-4 py-3 rounded-xl outline-none focus:border-cyan-400 transition cursor-pointer"
        >
          {gameOptions.map(({ label, value }) => {
            return (
              <option key={value || "default"} value={value}>
                {label}
              </option>
            );
          })}
        </select>
      </div>
    </div>
  );
};

export default Landing;