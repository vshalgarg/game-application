import { FaChevronRight } from "react-icons/fa";
import HexBadge from "./HexBadge";

const ModeOption = ({ icon: Icon, label, tone = "cyan", onClick }) => {
  return (
    <button type="button" className={`gz-mode-option gz-mode-option--${tone}`} onClick={onClick}>
      <HexBadge className="gz-mode-option__badge">
        <Icon size={14} />
      </HexBadge>
      <span className="min-w-0 flex-1 text-left text-sm font-semibold text-gz-text">
        {label}
      </span>
      <FaChevronRight className="gz-mode-option__chevron shrink-0" size={14} />
    </button>
  );
};

export default ModeOption;
