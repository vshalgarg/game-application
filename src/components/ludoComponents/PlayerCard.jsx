const PlayerCard = ({name, userId, color, avatarUrl, isCurrentTurnPlayer = false, isMyTurn = false,
  playerTurnStage, pendingDice = [], diceOptions = [], onDiceSelect, currentUserId, celebrating = false, visiblePendingDiceCount = 0 }) => {

  const visibleDice = pendingDice.slice(0, visiblePendingDiceCount);
  const showDiceSection = isCurrentTurnPlayer && visibleDice.length > 0;
  const isCurrentUser = userId === currentUserId;

  return (
    <div
      className="flex flex-col gap-2 bg-white/5 backdrop-blur-md border rounded-xl px-1 py-2 w-35"
      style={{ borderColor: color }}
    >
      {celebrating &&
        [...Array(32)].map((_, i) => {
          const angle = (360 / 32) * i;
          const distance = 70 + (i % 4) * 18;

          return (
            <span
              key={i}
              className="celebration-piece"
              style={{
                "--angle": `${angle}deg`,
                "--distance": `${distance}px`,
                "--delay": `${i * 15}ms`,
                backgroundColor: [
                  "#FFD700",
                  "#FF4081",
                  "#00E5FF",
                  "#7CFF6B",
                  "#FFFFFF",
                ][i % 5],
              }}
            />
          );
        })}
      <div className="flex items-center gap-3">

        {/* Avatar */}
        {avatarUrl ? (
          <img
            src={avatarUrl}
            alt={name}
            className="w-10 h-10 sm:w-8 sm:h-8 rounded-full border-2 shrink-0 object-cover"
            style={{ borderColor: color }}
          />
        ) : (
          <div
            className="w-10 h-10 sm:w-12 sm:h-12 rounded-full border-2 shrink-0 bg-white/10"
            style={{ borderColor: color }}
          />
        )}

        <div className="flex flex-col overflow-hidden">
          <span className="font-semibold text-white text-sm truncate">
            {name}
          </span>
          <span className="text-xs text-white truncate text-bold">{isCurrentUser ? "You" : ""}</span>
        </div>
      </div>

      {/* Dice numbers selection to current turn user and others*/}
      {showDiceSection && (
        <div>
          {/* {isCurrentUser && (
            <span className="w-full text-[9px] uppercase tracking-wide text-white text-center">
              Select number
            </span>
          )} */}

          <div className="flex flex-nowrap gap-1 bg-grey rounded-lg shadow-lg p-1 max-w-[180px] mt-1">
            {visibleDice.map((dice, index) => {
              const isSelectable = isMyTurn && 
              diceOptions.some((move) => move.dice === dice);

              return (
                <button
                  key={index}
                  disabled={!isSelectable}
                  onClick={() => {
                    if (isSelectable) {
                      onDiceSelect(dice);
                    }
                  }}
                  className={`w-8 h-8 sm:w-9 sm:h-9 rounded font-bold text-black transition-all
                    ${isSelectable
                        ? "bg-yellow-500 hover:bg-yellow-600 cursor-pointer"
                        : "bg-yellow-500/60 cursor-not-allowed opacity-70"
                    }
                  `}
                >
                  {dice}
                </button>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
};

export default PlayerCard;