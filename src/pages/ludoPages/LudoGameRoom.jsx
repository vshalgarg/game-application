import LudoBoard from "../../components/ludoComponents/LudoBoard";
import Dice from "../../components/ludoComponents/Dice";

const LudoGameRoom = () => {
  return (
    <div
      className="
        min-h-screen
        bg-gradient-to-br
        from-black
        via-gray-900
        to-black
        flex
        items-center
        justify-center
      "
    >
      <div
        className="
          bg-white/10
          backdrop-blur-lg
          border
          border-white/20
          rounded-3xl
          p-6
        "
      >
        <LudoBoard />
      </div>
      <div className="mt-6 flex justify-center">
    <Dice />
  </div>
    </div>
  );
};

export default LudoGameRoom;