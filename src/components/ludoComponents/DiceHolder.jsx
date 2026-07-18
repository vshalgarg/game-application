import Dice from "./Dice";
import TurnIndicator from "./TurnIndicator";

const colors = {
  red: "#ef4444",
  green: "#22c55e",
  yellow: "#facc15",
  blue: "#3b82f6",
};

const DiceHolder = ({ turn, diceValue, onRoll, rolling,}) => {
  return (
    <div
      className="
        flex
        items-center
        gap-2
        bg-black/25
        backdrop-blur-md
        border
        border-white/20
        rounded-2xl
        p-2
        shadow-xl
      "
    >
      <TurnIndicator color={colors[turn]} />

      <Dice value={diceValue} 
      onRoll={onRoll} 
      rolling={rolling}
      />
    </div>
  );
};

export default DiceHolder;