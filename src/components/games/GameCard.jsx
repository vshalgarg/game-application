const GameCard = ({ game, onSelect }) => {
  const { title, genre, icon: Icon, accent, path } = game;
  const isAvailable = Boolean(path);

  return (
    <button
      type="button"
      disabled={!isAvailable}
      onClick={() => isAvailable && onSelect?.(game)}
      className={`gz-game-card gz-game-card--${accent} ${isAvailable ? "" : "gz-game-card--soon"}`}
    >
      <div className="gz-game-card__art">
        <Icon size={52} />
        {!isAvailable && <span className="gz-game-card__badge">Soon</span>}
      </div>
      <div className="gz-game-card__meta">
        <p className="truncate font-semibold text-gz-text">{title}</p>
        <p className="text-xs text-gz-text-secondary">{genre}</p>
      </div>
    </button>
  );
};

export default GameCard;
