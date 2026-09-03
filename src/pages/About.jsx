import { useNavigate } from "react-router-dom";
import {
  FaBolt,
  FaGamepad,
  FaGlobe,
  FaHeart,
  FaShieldAlt,
  FaStar,
  FaTrophy,
  FaUsers,
} from "react-icons/fa";
import Button from "../components/ui/Button";
import { popularGames } from "../data/games";
import controllerHero from "../assets/images/gaming-controller-hero.webp";
import controllerHands from "../assets/images/gaming-controller-with-hands.webp";

const differentiators = [
  {
    id: "multiplayer",
    title: "Multiplayer Fun",
    description: "Play with friends and compete against real players in live matches.",
    Icon: FaUsers,
    tone: "cyan",
  },
  {
    id: "fair-play",
    title: "Fair Play",
    description: "Equal rules, honest matchmaking, and a level playing field for everyone.",
    Icon: FaShieldAlt,
    tone: "purple",
  },
  {
    id: "smooth",
    title: "Smooth & Fast",
    description: "Quick load times and lag-free sessions so you stay in the action.",
    Icon: FaBolt,
    tone: "cyan",
  },
];

const stats = [
  { id: "players", value: "50K+", label: "Active Players", Icon: FaUsers },
  { id: "games", value: "1M+", label: "Games Played", Icon: FaGamepad },
  { id: "tournaments", value: "100K+", label: "Tournaments", Icon: FaTrophy },
  { id: "countries", value: "150+", label: "Countries", Icon: FaGlobe },
];

const SectionHeading = ({ children }) => (
  <>
    <h2 className="gz-about__heading">{children}</h2>
    <div className="gz-about__underline" aria-hidden="true" />
  </>
);

const About = () => {
  const navigate = useNavigate();

  return (
    <div className="gz-page-shell gz-page-shell--about">
      <div className="gz-about">
        <section className="gz-about__hero">
          <div>
            <p className="gz-about__kicker">ABOUT US</p>
            <h1 className="gz-about__title">
              Play. <span className="gz-text-neon">Compete.</span>{" "}
              <span className="gz-text-purple">Win.</span>
            </h1>
            <p className="mt-3 max-w-xl text-sm leading-relaxed text-gz-text-secondary xl:text-base">
              GameZone is your destination for multiplayer games, challenging real players, and
              climbing the leaderboards.
            </p>
            <Button variant="nav" className="gz-about__cta" onClick={() => navigate("/")}>
              <FaGamepad size={16} />
              Join the Game
            </Button>
          </div>

          <div className="gz-about__hero-art">
            <FaTrophy size={20} className="gz-about__float-icon gz-about__float-icon--trophy" />
            <FaStar size={18} className="gz-about__float-icon gz-about__float-icon--star" />
            <img src={controllerHero} alt="Glowing GameZone gaming controller" />
          </div>
        </section>

        <section className="gz-about__section">
          <SectionHeading>Our Mission</SectionHeading>
          <p className="gz-about__mission mx-auto mt-3 text-sm leading-relaxed text-gz-text-secondary xl:text-base">
            We&apos;re building a fun, fair, and competitive gaming platform where every match feels
            exciting and every player has a real shot at the top.
          </p>
        </section>

        <section className="gz-about__section">
          <SectionHeading>What Makes Us Different</SectionHeading>
          <div className="gz-about__features">
            {differentiators.map(({ id, title, description, Icon, tone }) => (
              <article key={id} className="gz-about-card">
                <span className={`gz-about-card__icon gz-about-card__icon--${tone}`}>
                  <Icon />
                </span>
                <h3
                  className={`text-sm font-semibold sm:text-[0.95rem] ${
                    tone === "purple" ? "text-gz-purple-accent" : "text-gz-primary-cyan"
                  }`}
                >
                  {title}
                </h3>
                <p className="mt-1.5 text-xs leading-relaxed text-gz-text-secondary sm:text-[0.8rem]">
                  {description}
                </p>
              </article>
            ))}
          </div>
        </section>

        <section className="gz-about__section">
          <SectionHeading>Our Games</SectionHeading>
          <div className="gz-about__games">
            {popularGames.map((game) => (
              <figure key={game.id} className="m-0">
                <div className="gz-about-game">
                  <img alt={game.title} />
                </div>
              </figure>
            ))}
          </div>
        </section>

        <section className="gz-about-stats" aria-label="GameZone stats">
          {stats.map(({ id, value, label, Icon }) => (
            <div key={id} className="gz-about-stats__item">
              <Icon />
              <p className="text-base font-bold text-gz-text sm:text-lg">{value}</p>
              <p className="text-[11px] font-medium tracking-wide text-gz-primary-cyan sm:text-xs">
                {label}
              </p>
            </div>
          ))}
        </section>

        <section className="gz-about-close">
          <FaHeart size={32} className="gz-about-close__heart" aria-hidden="true" />
          <p className="flex-1 text-sm leading-relaxed text-gz-text">
            Made with passion for gamers like you. Thank you for being a part of{" "}
            <span className="gz-text-neon font-semibold">GameZone</span>!
          </p>
          <div className="gz-about-close__art">
            <img src={controllerHands} alt="Hands holding a glowing GameZone controller" />
          </div>
        </section>
      </div>
    </div>
  );
};

export default About;
