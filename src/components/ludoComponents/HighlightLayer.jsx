const HighlightLayer = ({ cells, metadata, currentTurnColorIndex }) => {
  if (currentTurnColorIndex == null) return null;

  const cellSize = 100 / metadata.boardSize.columns;

  const highlighted = cells.filter(
    (cell) =>
      cell.type === null &&
      cell.colorIndex === currentTurnColorIndex
  );

  return (
    <svg
      className="absolute inset-0 w-full h-full pointer-events-none z-20 current-turn-glow"
      viewBox={`0 0 ${metadata.boardSize.columns} ${metadata.boardSize.rows}`}
      preserveAspectRatio="none"
    >
      {highlighted.map((cell) => (
        <rect
          key={cell.cellId}
          x={cell.col}
          y={cell.row}
          width="1"
          height="1"
          fill="white"
          opacity="0.25"
        />
      ))}
    </svg>
  );
};

export default HighlightLayer;