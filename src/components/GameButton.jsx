const GameButton = ({ title, onClick, color }) => {
  return (
    <button
      onClick={onClick}
      className={`
        w-full
        py-4
        rounded-2xl
        text-white
        text-xl
        font-semibold
        transition
        duration-300
        hover:scale-105
        shadow-lg
        ${color}
      `}
    >
      {title}
    </button>
  );
};

export default GameButton;