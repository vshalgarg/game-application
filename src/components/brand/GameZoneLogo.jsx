import hexagonIcon from "../../assets/icons/lucide-hexagon.svg";
import gamepadIcon from "../../assets/icons/lucide-gamepad-2.svg";

const GameZoneLogo = ({ className = "h-14 w-14", title = "GameZone" }) => {
  return (
    <span className={`relative inline-block ${className}`} role="img" aria-label={title}>
      <img src={hexagonIcon} alt="" className="h-full w-full" />
      <img
        src={gamepadIcon}
        alt=""
        className="absolute top-1/2 left-1/2 h-[52%] w-[52%] -translate-x-1/2 -translate-y-1/2"
      />
    </span>
  );
};

export default GameZoneLogo;
