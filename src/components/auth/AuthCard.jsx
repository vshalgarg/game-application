import GameZoneLogo from "../brand/GameZoneLogo";

const AuthCard = ({
  eyebrow,
  title,
  subtitle,
  children,
  footer,
}) => {
  return (
    <div className="gz-auth-card">
      <div className="mb-4 flex flex-col items-center text-center">
        <GameZoneLogo className="mb-1.5 h-10 w-10 text-gz-primary-cyan" />
        <p className="text-xl font-bold tracking-wide text-gz-text">GameZone</p>

        {eyebrow && (
          <div className="gz-divider mt-3 w-full max-w-[200px] justify-center">
            <span className="text-sm text-gz-primary-cyan">{eyebrow}</span>
          </div>
        )}

        <h1 className="mt-2 text-3xl font-bold text-gz-text">{title}</h1>
        {subtitle && (
          <p className="mt-1 text-sm text-gz-text-secondary">{subtitle}</p>
        )}
      </div>

      {children}

      {footer && (
        <div className="mt-4 text-center text-sm text-gz-text-secondary">{footer}</div>
      )}
    </div>
  );
};

export default AuthCard;
