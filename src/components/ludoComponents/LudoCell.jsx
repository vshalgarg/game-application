const LudoCell = ({ type }) => {
  if (type === "red-arrow") {
  return (
    <div className="border border-gray-400 bg-white flex items-center justify-center">
      <span className="text-red-500 text-sm font-bold">
        →
      </span>
    </div>
  );
}

if (type === "green-arrow") {
  return (
    <div className="border border-gray-400 bg-white flex items-center justify-center">
      <span className="text-green-500 text-sm font-bold">
        ↓
      </span>
    </div>
  );
}

if (type === "blue-arrow") {
  return (
    <div className="border border-gray-400 bg-white flex items-center justify-center">
      <span className="text-blue-500 text-sm font-bold">
        ↑
      </span>
    </div>
  );
}

if (type === "yellow-arrow") {
  return (
    <div className="border border-gray-400 bg-white flex items-center justify-center">
      <span className="text-yellow-500 text-sm font-bold">
        ←
      </span>
    </div>
  );
}
  if (type === "safe") {
  return (
    <div
      className="
        border
        border-gray-400
        flex
        items-center
        justify-center
        bg-white
      "
    >
      <span className="text-gray-500 text-xs">
        ★
      </span>
    </div>
  );
}
  return (
    <div
      className="border border-gray-400"
      style={{ backgroundColor: type, }}
    />
  );
};

export default LudoCell;