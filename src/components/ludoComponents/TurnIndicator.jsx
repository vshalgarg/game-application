const TurnIndicator = ({ color }) => {
  return (
    <div
      className="
        w-10
        h-10
        rounded-full
        border-[3px]
        border-white
        shadow-lg
        flex
        items-center
        justify-center
      "
      style={{
        backgroundColor: color || "white",
      }}
    >
      <div
        className="
          w-6
          h-6
          rounded-full
          bg-white/30
          border
          border-white/40
        "
      />
    </div>
  );
};

export default TurnIndicator;