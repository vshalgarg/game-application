import Token from "./Token";
import LudoCell from "./LudoCell";
import CenterHome from "./CenterHome";

const LudoBoard = ({ boardData, gameState, selectedToken, setSelectedToken, handleTokenClick, legalMoves}) => {
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

//   const getTrackTokenAtCell = (cell) => {

//   if (!gameState?.board?.players)
//     return null;

//   for (const player of gameState.board.players) {

//     const playerPath = paths?.[player.colorIndex];
//     if (!playerPath)
//       continue;

//     const token = player.tokens.find((token) => {

//       if (token.state !== "TRACK")
//         return false;

//       const actualCellId = playerPath[token.pathIndex];
//       return actualCellId === cell.cellId;
//     });

//     if (token) {

//       return {
//         token,
//         color: colors[player.colorIndex]
//       };
//     }
//   }
//   return null;
// };

    const getTrackTokenAtCell = (cell) => {

  if (!gameState?.board?.players)
    return null;

  for (const player of gameState.board.players) {

    const token = player.tokens.find(token => token.state === "TRACK" && token.pathId === cell.cellId);

    if (token) {
      return {
        token,
        color: colors[player.colorIndex]
      };
    }
  }

  return null;
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
          const tokenData = getTokenAtCell(cell) || getTrackTokenAtCell(cell);
          const canMove = tokenData?.token ? legalMoves?.some( move => move.tokenId === tokenData.token.tokenId) : false;
          // const canMove = legalMoves?.some( move => move.tokenId === tokenData.token.tokenId);
          return (
            <div
              key={cell.cellId}
              className="relative w-full h-full"
            >
              <LudoCell
                type={cell.type}
                color={
                  cell.colorIndex != null
                    ? colors[cell.colorIndex]
                    : undefined
                }
                arrowDirection={cell.arrowDirection}
                arrowColor={
                  cell.arrowColorIndex != null
                    ? colors[cell.arrowColorIndex]
                    : undefined
                }
              />

              {tokenData && (
                <div className="absolute inset-0 flex items-center justify-center">
                  <Token
                    color={tokenData.color}
                    selected={selectedToken === tokenData.token.tokenId}
                    onHandleClick={() => {
                      if(!canMove) 
                        return;
                      setSelectedToken(tokenData.token.tokenId);
                      handleTokenClick(tokenData.token.tokenId);
                    }}
                  />
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



