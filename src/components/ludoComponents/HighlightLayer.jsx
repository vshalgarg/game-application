const HighlightLayer = ({ cells, metadata, currentTurnColorIndex }) => {
  const { columns, rows } = metadata.boardSize;
  const colors = metadata.colors ?? [];

  // Only player path cells — colorIndex 0 is white/neutral, skip it
  const allHighlighted = cells.filter(
    (cell) => cell.type === null && cell.colorIndex != null && cell.colorIndex !== 0
  );

  if (allHighlighted.length === 0) return null;

  return (
    <div className="absolute inset-0 pointer-events-none">
      {allHighlighted.map((cell) => {
        const isCurrentTurn = cell.colorIndex === currentTurnColorIndex;

        return (
          <div
            key={cell.cellId}
            className={`absolute ${isCurrentTurn ? "current-turn-glow" : ""}`}
            style={{
              left: `${(cell.col / columns) * 100}%`,
              top: `${(cell.row / rows) * 100}%`,
              width: `${(1 / columns) * 100}%`,
              height: `${(1 / rows) * 100}%`,
              backgroundColor: colors[cell.colorIndex],
            }}
          />
        );
      })}
    </div>
  );
};

export default HighlightLayer;