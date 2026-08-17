import { useRef } from "react";
import { FaChevronLeft, FaChevronRight } from "react-icons/fa";
import GameCard from "./GameCard";

const GameCarousel = ({ games, onSelect }) => {
  const scrollerRef = useRef(null);

  const scrollByCard = (direction) => {
    const scroller = scrollerRef.current;
    if (!scroller) return;

    const card = scroller.querySelector(".gz-game-card");
    const amount = (card?.offsetWidth || 220) + 16;
    scroller.scrollBy({ left: direction * amount, behavior: "smooth" });
  };

  return (
    <section className="gz-carousel-panel">
      <p className="gz-carousel-label mb-2 shrink-0 text-center text-[11px] font-semibold tracking-[0.2em] text-gz-primary-cyan sm:mb-3">
        POPULAR GAMES
      </p>

      <div className="flex min-h-0 flex-1 items-stretch gap-2 sm:gap-3">
        <button
          type="button"
          className="gz-carousel-nav my-auto hidden sm:flex"
          onClick={() => scrollByCard(-1)}
          aria-label="Previous games"
        >
          <FaChevronLeft size={14} />
        </button>

        <div ref={scrollerRef} className="gz-carousel">
          {games.map((game) => (
            <GameCard key={game.id} game={game} onSelect={onSelect} />
          ))}
        </div>

        <button
          type="button"
          className="gz-carousel-nav my-auto hidden sm:flex"
          onClick={() => scrollByCard(1)}
          aria-label="Next games"
        >
          <FaChevronRight size={14} />
        </button>
      </div>

      <div className="gz-carousel-dots mt-2 flex shrink-0 justify-center gap-1.5 sm:mt-3">
        {games.map((game, index) => (
          <span
            key={game.id}
            className={`h-1.5 rounded-full ${index === 0 ? "w-4 bg-gz-primary-cyan" : "w-1.5 bg-gz-input-border"}`}
          />
        ))}
      </div>
    </section>
  );
};

export default GameCarousel;
