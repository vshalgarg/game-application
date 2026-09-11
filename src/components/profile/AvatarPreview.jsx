import { FaUser } from "react-icons/fa";
import { getAvatarById } from "../../data/avatars";

const AvatarPreview = ({ avatarId, avatarUrl, name, editable = false, onClick }) => {
  const preset = getAvatarById(avatarId);
  const Art = preset?.Art;
  const label = name || preset?.name || "Player avatar";

  const content = Art ? (
    <Art />
  ) : avatarUrl ? (
    <img src={avatarUrl} alt="" className="h-full w-full object-cover" />
  ) : (
    <FaUser className="text-gz-text-secondary" size={36} />
  );

  const className = `relative flex h-24 w-24 shrink-0 items-center justify-center overflow-hidden rounded-full border-2 border-gz-primary-cyan bg-gz-popup-dark text-gz-primary-cyan shadow-[0_0_18px_rgb(0_217_232/0.35)] sm:h-28 sm:w-28 [&_svg]:h-full [&_svg]:w-full ${
    editable ? "cursor-pointer p-0 transition hover:brightness-110" : ""
  }`;

  if (editable) {
    return (
      <button type="button" className={className} onClick={onClick} aria-label="Change avatar">
        {content}
        <span className="absolute inset-x-0 bottom-0 bg-gz-popup-dark/80 py-1 text-center text-[10px] font-semibold tracking-wide text-gz-primary-cyan uppercase">
          Change
        </span>
      </button>
    );
  }

  return (
    <div className={className} aria-label={label}>
      {content}
    </div>
  );
};

export default AvatarPreview;
