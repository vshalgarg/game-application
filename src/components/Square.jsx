const Square = ({ value, onClick }) => {
  return (
    <button
      onClick={onClick}
      className="
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
      "
    >
      {value}          
    </button>
  );
};

export default Square;