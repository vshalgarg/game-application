import { FaGoogle, FaFacebookF } from "react-icons/fa";
import Button from "../ui/Button";

const providers = [
  { id: "google", label: "Continue with Google", Icon: FaGoogle },
  { id: "facebook", label: "Continue with facebook", Icon: FaFacebookF },
];

const SocialAuthButtons = ({ onSelect, disabled }) => {
  return (
    <div className="mt-4">
      <div className="gz-divider mb-4 justify-center">
        <span>OR</span>
      </div>

      <div className="flex items-center justify-center gap-4">
        {providers.map(({ id, label, Icon }) => (
          <Button
            key={id}
            variant="social"
            aria-label={label}
            onClick={() => onSelect?.(id)}
            disabled={disabled}
          >
            <Icon size={20} />
          </Button>
        ))}
      </div>
    </div>
  );
};

export default SocialAuthButtons;
