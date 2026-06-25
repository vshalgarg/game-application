const HomeArea = ({ color }) => {
  return (
    <div
      className="
        w-full
        h-full
        bg-white
        border-1
        border-gray-500
        rounded-sm
        p-4
        grid
        grid-cols-2
        gap-4
      "
    >
      <div
        className="rounded-full border-1"
        style={{ backgroundColor: color }}
      />

      <div
        className="rounded-full border-1"
        style={{ backgroundColor: color }}
      />

      <div
        className="rounded-full border-1"
        style={{ backgroundColor: color }}
      />

      <div
        className="rounded-full border-1"
        style={{ backgroundColor: color }}
      />
    </div>
  );
};

export default HomeArea;