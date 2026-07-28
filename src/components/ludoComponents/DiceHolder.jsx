import boardData from "../../data/board.json";
import Dice from "./Dice";
import TurnIndicator from "./TurnIndicator";

const DiceHolder = ({turnColorIndex, diceValue, onRoll, rolling,}) => {

  const colors = boardData.metadata.colors;

  const turnColor =
    turnColorIndex != null
      ? colors[turnColorIndex]
      : "white";

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
      <TurnIndicator color={turnColor} />

      <Dice
        value={diceValue}
        onRoll={onRoll}
        rolling={rolling}
      />
    </div>
  );
};

export default DiceHolder;