import Token from "./Token";
import LudoCell from "./LudoCell";
import CenterHome from "./CenterHome";

const LudoBoard = ({ boardData, gameState, selectedToken, setSelectedToken, handleTokenClick, legalMoves, movableTokenIds,}) => {
  if (!boardData) 
    return null;

  const { metadata, centerArea, grid, paths } = boardData;
  const colors = metadata?.colors ?? [];

  // Convert object of cells into array
  const cells = Object.entries(grid?.[0] ?? {})
    .map(([cellId, cell]) => ({
      cellId: Number(cellId),
      ...cell,
    })) 
    .sort((a, b) => a.cellId - b.cellId);

  // Temporary: Show BASE tokens on Slot cells
  const getTokenAtCell = (cell) => {
    if (!gameState?.board?.players)
      return null;

    // BASE SLOT TOKENS
    if (cell.type === "S") {
        console.log(cell);
      if (cell.tokenColorIndex == null)
        return null;

      const player = gameState.board.players.find(player => player.colorIndex === cell.tokenColorIndex);
      console.log("color index",cell.colorIndex); 
      console.log("cell tokencolorindex",cell.tokenColorIndex);
      console.log("player:",player);

      if (!player)
        return null;

    const token = player.tokens.find(token => token.state === "BASE" && token.baseSlotId === cell.cellId);
    console.log("slot:",cell.slotIndex);
    console.log("token:",token);

    if (!token)
      return null;

    return {
      token,
      color: colors[player.colorIndex]
    };
  }
  return null;
};

    const getTrackTokenAtCell = (cell) => {

  if (!gameState?.board?.players)
    return [];

  const tokens = [];

  for (const player of gameState.board.players) {

    for (const token of player.tokens) {

      if ((token.state === "TRACK" || token.state === "FINISHED") && token.pathId === cell.cellId) {
        tokens.push({
          token,
          color: colors[player.colorIndex]
        });
      }
    }
  }

  return tokens;
};
  return (
    <div className="relative w-[450px] h-[450px]">
      {/* Board */}
      <div
        className="grid w-full h-full"
        style={{
          gridTemplateColumns: `repeat(${metadata.boardSize.columns}, 1fr)`,
          gridTemplateRows: `repeat(${metadata.boardSize.rows}, 1fr)`,
        }}
      >
        {cells.map((cell) => {
          
          const baseToken = getTokenAtCell(cell);
          const trackTokens = getTrackTokenAtCell(cell);
          if (cell.cellId === 113) {
            console.log("trackTokens:", trackTokens);
          }
          const tokensToRender = baseToken ? [baseToken] : trackTokens;
          
          return (
            <div
              key={cell.cellId}
              className="relative w-full h-full"
            >
              <LudoCell
                type={cell.type}
                color={cell.colorIndex != null ? colors[cell.colorIndex] : undefined}
                arrowDirection={cell.arrowDirection}
                arrowColor={cell.arrowColorIndex != null ? colors[cell.arrowColorIndex] : undefined}
              />

              {tokensToRender.length > 0 && (
          <div className="absolute inset-0 flex items-center justify-center">
            {tokensToRender.map((tokenData, index) => {
              console.log("Rendering", tokenData.token.pathId);

              const isMovable = movableTokenIds.includes(tokenData.token.tokenId);
              const isOverlapping = tokensToRender.length > 1;

              const offsets = [
                { x: 0, y: 0 },
                { x: -6, y: -6 },
                { x: 6, y: -6 },
                { x: -6, y: 6 },
                { x: 6, y: 6 },
              ];

              const offset = offsets[index] ?? { x: 0, y: 0 };

              return (
                <div
                  key={`${tokenData.token.tokenId}-${index}`}
                  className="absolute z-[999]"
                  style={{transform: `translate(${offset.x}px, ${offset.y}px)`,}}
                >
                  <Token
                    color={tokenData.color}
                    selected={selectedToken === tokenData.token.tokenId}
                    isMovable={isMovable}
                    isOverlapping={isOverlapping}
                    onHandleClick={
                      isMovable
                        ? () => {
                            setSelectedToken(tokenData.token.tokenId);
                            handleTokenClick(tokenData.token.tokenId);
                          }
                        : undefined
            }
          />
        </div>
      );
    })}
  </div>
)}
            </div>
          );
        })}
      </div>

      {/* Center Area */}
      {centerArea && (
        <div
          className="absolute"
          style={{
            left: `${
              (centerArea.startCol / metadata.boardSize.columns) * 100
            }%`,
            top: `${
              (centerArea.startRow / metadata.boardSize.rows) * 100
            }%`,
            width: `${
              (centerArea.cols / metadata.boardSize.columns) * 100
            }%`,
            height: `${
              (centerArea.rows / metadata.boardSize.rows) * 100
            }%`,
          }}
        >
          <CenterHome
            centerArea={centerArea}
            colors={colors}
          />
        </div>
      )}
    </div>
  );
};

export default LudoBoard;