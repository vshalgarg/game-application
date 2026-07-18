import { boardLayout } from "../../data/boardLayout";
import { ludoPath } from "../../data/ludoPath";
import LudoCell from "./LudoCell";
import CenterHome from "./CenterHome";
import HomeArea from "./HomeArea";
import Token from "./Token";

const LudoBoard = ({gameState, selectedToken, setSelectedToken, handleTokenClick}) => {
  const getTokenAtCell = (row, col) => {
  if (!gameState?.board?.players) return null;

  for (const player of gameState.board.players) {
    const color = player.color.toLowerCase();

    for (const token of player.tokens) {
      if (token.state === "BASE") continue;

      const realIndex =
        (token.position + START_INDEX[color]) %
        ludoPath.length;

      const currentCell = ludoPath[realIndex];

      if (
        currentCell.row === row &&
        currentCell.col === col
      ) {
        return {
          color,
          token,
        };
      }
    }
  }

  return null;
};

const redPlayer = gameState?.board?.players?.find(
  (player) => player.color === "RED"
);

const greenPlayer = gameState?.board?.players?.find(
  (player) => player.color === "GREEN"
);

const bluePlayer = gameState?.board?.players?.find(
  (player) => player.color === "BLUE"
);

const yellowPlayer = gameState?.board?.players?.find(
  (player) => player.color === "YELLOW"
);

  return (
    <div className="relative w-[450px] h-[450px]">
      
      {/* Board */}
      <div className="absolute inset-0 grid grid-cols-15 grid-rows-15">
        {boardLayout.map((row, rowIndex) =>
          row.map((cell, colIndex) => {
            const tokenData = getTokenAtCell(
              rowIndex,
              colIndex
            );

            return (
              <div
                key={`${rowIndex}-${colIndex}`}
                className="relative"
              >
                <LudoCell type={cell} />

                {tokenData && (
                  <div className="absolute inset-0 flex items-center justify-center">
                    <Token
                      color={tokenData.color}
                      selected={selectedToken === tokenData.token.tokenId}
                      onHandleClick={() => {
                        console.log("handleTokenClick called")
                        setSelectedToken(tokenData.token.tokenId);
                        handleTokenClick(tokenData.token.tokenId);
                      }}
                    />
                  </div>
                )}
              </div>
            );
          })
        )}
      </div>

      <div
        className="absolute"
        style={{
          left: "40%",
          top: "40%",
          width: "20%",
          height: "20%",
        }}
      >
        <CenterHome />
      </div>

      <div
        className="absolute"
        style={{
          left: "6.66%",
          top: "6.66%",
          width: "26.66%",
          height: "26.66%",
        }}
      >
        <HomeArea
          color="red"
          tokens={redPlayer?.tokens ?? []}
          selectedToken={selectedToken}
          setSelectedToken={setSelectedToken}
          handleTokenClick={handleTokenClick}
          />
      </div>

      <div
        className="absolute"
        style={{
          right: "6.66%",
          top: "6.66%",
          width: "26.66%",
          height: "26.66%",
        }}
      >
        <HomeArea
          color="green"
          tokens={greenPlayer?.tokens ?? []}
          selectedToken={selectedToken}
          setSelectedToken={setSelectedToken}
          handleTokenClick={handleTokenClick}
          />
      </div>

      <div
        className="absolute"
        style={{
          left: "6.66%",
          bottom: "6.66%",
          width: "26.66%",
          height: "26.66%",
        }}
      >
        <HomeArea
          color="blue"
          tokens={bluePlayer?.tokens ?? []}
          selectedToken={selectedToken}
          setSelectedToken={setSelectedToken}
          handleTokenClick={handleTokenClick}
        />
      </div>

      <div
        className="absolute"
        style={{
          right: "6.66%",
          bottom: "6.66%",
          width: "26.66%",
          height: "26.66%",
        }}
      >
        <HomeArea
          color="yellow"
          tokens={yellowPlayer?.tokens ?? []}
          selectedToken={selectedToken}
          setSelectedToken={setSelectedToken}
          handleTokenClick={handleTokenClick}
          />
      </div>
    </div>
  );
};

export default LudoBoard;