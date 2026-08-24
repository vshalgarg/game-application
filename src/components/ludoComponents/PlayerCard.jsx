const PlayerCard = ({name, userId, color, avatarUrl, isCurrentTurnPlayer = false, isMyTurn = false,
  playerTurnStage, pendingDice = [], diceOptions = [], onDiceSelect, currentUserId}) => {

  const showDiceSection = isCurrentTurnPlayer && playerTurnStage === "TOKEN_MOVE" && pendingDice.length > 0;
  const isCurrentUser = userId === currentUserId;

  return (
    <div
      className="
      flex
      flex-col
      gap-2
      bg-white/5
      backdrop-blur-md
      border
      rounded-xl
      px-1
      py-2
      w-35
    "
      style={{ borderColor: color }}
    >
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

          {isMyTurn ? (
            diceOptions.length > 1 && (
              <div className="flex flex-nowrap gap-1 bg-grey rounded-lg shadow-lg p-1 max-w-[180px] mt-1">
                {diceOptions.map((move, index) => (
                  <button
                    key={index}
                    onClick={() => onDiceSelect(move.dice)}
                    className="w-8 h-8 sm:w-9 sm:h-9 rounded bg-yellow-500 hover:bg-yellow-600 font-bold text-black cursor-pointer"
                  >
                    {move.dice}
                  </button>
                ))}
              </div>
            )
          ) : (
            <div className="flex flex-nowrap gap-2 mt-1">
              {pendingDice.map((dice, index) => (
                <div
                  key={index}
                  className="w-7 h-7 sm:w-8 sm:h-8 rounded bg-yellow-500 text-black flex items-center justify-center font-bold text-xs cursor-not-allowed"
                >
                  {dice}
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default PlayerCard;