import { FaBolt, FaCrown, FaTrophy } from "react-icons/fa";

const features = [
  { id: "games", label: "Exciting Games", hint: "Play amazing games", Icon: FaTrophy, tone: "cyan" },
  { id: "realtime", label: "Real-Time Action", hint: "Compete in real-time", Icon: FaBolt, tone: "cyan" },
  { id: "leaders", label: "Leaderboards", hint: "Be the top player", Icon: FaCrown, tone: "purple" },
];

const FeatureStrip = () => {
  return (
    <div className="gz-feature-strip">
      {features.map(({ id, label, hint, Icon, tone }) => (
        <div key={id} className="gz-feature-strip__item">
          <span className={`gz-feature-strip__icon gz-feature-strip__icon--${tone}`}>
            <Icon size={18} />
          </span>
          <div className="min-w-0 text-left">
            <p className="text-sm font-semibold text-gz-text">{label}</p>
            <p className="gz-feature-strip__hint text-xs text-gz-text-secondary">{hint}</p>
          </div>
        </div>
      ))}
    </div>
  );
};

export default FeatureStrip;
