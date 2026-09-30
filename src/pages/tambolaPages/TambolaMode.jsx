import { useNavigate } from "react-router-dom";
import { FaGamepad, FaGift, FaCrown } from "react-icons/fa";
import PageShell from "../../components/layout/PageShell";
import GameZoneLogo from "../../components/brand/GameZoneLogo";
import ModeOption from "../../components/ui/ModeOption";

const TambolaMode = () => {
  const navigate = useNavigate();

  const handleFree = () => {
    navigate("/tcreate-join");
  };

  const handlePaid = () => {
    navigate("/tcreate-join");
  };

  return (
    <PageShell>
      <div className="gz-select-card w-full">
        {/* Header */}
        <div className="mb-6 flex flex-col items-center">
          <GameZoneLogo className="mb-3 h-10 w-10" />

          <h1 className="text-2xl font-bold text-gz-text sm:text-3xl">
            Tambola
          </h1>

          <div className="gz-divider mt-3 w-full max-w-[200px] justify-center">
            <FaGamepad
              className="text-gz-primary-cyan"
              size={12}
            />
          </div>

          <p className="mt-2 text-center text-sm text-gz-text-secondary">
            Choose how you want to play
          </p>
        </div>

        {/* Free */}
        <ModeOption
          icon={FaGift}
          label="Free"
          tone="cyan"
          onClick={handleFree}
        />

        {/* OR */}
        <div className="gz-divider my-4">
          <span className="text-xs font-semibold tracking-widest text-gz-text-secondary">
            OR
          </span>
        </div>

        {/* Paid */}
        <ModeOption
          icon={FaCrown}
          label="Paid"
          tone="purple"
          onClick={handlePaid}
        />
      </div>
    </PageShell>
  );
};

export default TambolaMode;