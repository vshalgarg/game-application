const Square = ({ value, onClick, isWinningCell,}) => {
 
  return (
    <button
      onClick={onClick}
      className={`
        w-14
        h-14
        md:w-16
        md:h-16
        bg-white/10
        border
        border-cyan-400/30
        rounded-xl
        text-2xl
        font-bold
        text-white
        hover:bg-cyan-500/20
        transition
        duration-300
        shadow-md

         ${
      isWinningCell
        ? `
          bg-green-500
          border-green-300
          text-white
          scale-110
          shadow-lg
          shadow-green-400/70
        `
        : `
          bg-white/10
          border-cyan-400/30
          text-white
          hover:bg-cyan-500/20
        ` }
      `}
    >
      {value}          
    </button>
  );
};

export default Square;