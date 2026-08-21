import { useRef, useState, useLayoutEffect } from "react";
import Token from "./Token";
import LudoCell from "./LudoCell";
import CenterHome from "./CenterHome";
import HighlightLayer from "./HighlightLayer";

// Board responsiveness
const BASE_BOARD_SIZE = 450;

// Token overlapping without gap in a horizontal line
const getLineOffsets = (count, scaleFactor = 1) => {
  if (count <= 1) return [{ x: 0, y: 0 }];

  const spacing = Math.max(3, 10 - count * 1.2) * scaleFactor;
  const totalWidth = spacing * (count - 1);
  const startX = -totalWidth / 2;

  return Array.from({ length: count }, (_, i) => ({
    x: startX + i * spacing,
    y: 0,
  }));
};

// Token shrink on overlapping
const getStackScale = (count) => {
  if (count <= 1) return { x: 1, y: 1 };
  if (count === 2) return { x: 0.8, y: 0.9 };
  if (count === 3) return { x: 0.68, y: 0.8 };
  if (count === 4) return { x: 0.58, y: 0.72 };
  return { x: 0.5, y: 0.65 };
};

const LudoBoard = ({boardData, gameState, selectedToken, setSelectedToken, handleTokenClick, legalMoves, movableTokenIds, currentTurnColorIndex}) => {
  const boardRef = useRef(null);
  const [scaleFactor, setScaleFactor] = useState(1);

  // Board scaling
  useLayoutEffect(() => {
    const el = boardRef.current;
    if (!el) return;

    const updateScale = (width) => {
      setScaleFactor(width / BASE_BOARD_SIZE);
    };

    updateScale(el.getBoundingClientRect().width);

    const observer = new ResizeObserver((entries) => {
      const width = entries[0]?.contentRect?.width;
      if (width) updateScale(width);
    });

    observer.observe(el);
    return () => observer.disconnect();
  }, []);

  if (!boardData) return null;

  const { metadata, centerArea, grid, paths } = boardData;
  const colors = metadata?.colors ?? [];

  // Convert object of cells into array
  const cells = Object.entries(grid?.[0] ?? {})
    .map(([cellId, cell]) => ({
      cellId: Number(cellId),
      ...cell,
    }))
    .sort((a, b) => a.cellId - b.cellId);

  // Show base tokens on slot cells
  const getTokenAtCell = (cell) => {
    if (!gameState?.board?.players) return null;

    if (cell.type === "S") {
      if (cell.tokenColorIndex == null) return null;

      const player = gameState.board.players.find(
        (player) => player.colorIndex === cell.tokenColorIndex
      );

      if (!player) return null;

      const token = player.tokens.find(
        (token) => token.state === "BASE" && token.baseSlotId === cell.cellId
      );

      if (!token) return null;

      return {
        token,
        color: colors[player.colorIndex],
      };
    }
    return null;
  };

  const getTrackTokenAtCell = (cell) => {
    if (!gameState?.board?.players) return [];

    const tokens = [];

    for (const player of gameState.board.players) {
      for (const token of player.tokens) {
        if (
          (token.state === "TRACK" || token.state === "FINISHED") &&
          token.pathId === cell.cellId
        ) {
          tokens.push({
            token,
            color: colors[player.colorIndex],
          });
        }
      }
    }

    return tokens;
  };

  return (
    <div
      ref={boardRef}
      className="relative w-[clamp(280px,min(90vw,75vh),650px)] aspect-square mx-auto"
    >
      {/* Board (grid, cells, tokens) */}
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
          const tokensToRender = baseToken ? [baseToken] : trackTokens;

          return (
            <div key={cell.cellId} className="relative w-full h-full">
              <LudoCell
                type={cell.type}
                color={cell.colorIndex != null ? colors[cell.colorIndex] : undefined}
                arrowDirection={cell.arrowDirection}
                arrowColor={
                  cell.arrowColorIndex != null ? colors[cell.arrowColorIndex] : undefined
                }
              />

              {tokensToRender.length > 0 && (
                <div className="absolute inset-0 flex items-center justify-center">
                  {(() => {
                    const count = tokensToRender.length;
                    const offsets = getLineOffsets(count, scaleFactor);
                    const scale = getStackScale(count);

                    return tokensToRender.map((tokenData, index) => {
                      const isMovable = movableTokenIds.includes(
                        tokenData.token.tokenId
                      );
                      const isOverlapping = count > 1;
                      const offset = offsets[index] ?? { x: 0, y: 0 };

                      return (
                        <div
                          key={`${tokenData.token.tokenId}-${index}`}
                          className="absolute z-[999]"
                          style={{
                            transform: `translate(${offset.x}px, ${offset.y}px) scale(${scale.x}, ${scale.y})`,
                          }}
                        >
                          <Token
                            color={tokenData.color}
                            selected={selectedToken === tokenData.token.tokenId}
                            isMovable={isMovable}
                            isOverlapping={isOverlapping}
                            scaleFactor={scaleFactor}
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
                    });
                  })()}
                </div>
              )}
            </div>
          );
        })}
      </div>

      {/* Highlight Layer of all player */}
      <div className="absolute inset-0">
        <HighlightLayer
          cells={cells}
          metadata={metadata}
          currentTurnColorIndex={currentTurnColorIndex}
        />
      </div>

      {/* Center Area */}
      {centerArea && (
        <div
          className="absolute"
          style={{
            left: `${(centerArea.startCol / metadata.boardSize.columns) * 100}%`,
            top: `${(centerArea.startRow / metadata.boardSize.rows) * 100}%`,
            width: `${(centerArea.cols / metadata.boardSize.columns) * 100}%`,
            height: `${(centerArea.rows / metadata.boardSize.rows) * 100}%`,
          }}
        >
          <CenterHome centerArea={centerArea} colors={colors} />
        </div>
      )}
    </div>
  );
};

export default LudoBoard;