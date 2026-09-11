import gameBg from "../../assets/images/game_bg.webp";

const AuthLayout = ({ children, scrollable = false }) => {
  if (scrollable) {
    return (
      <div className="h-dvh overflow-x-hidden overflow-y-auto">
        <div
          className="pointer-events-none fixed inset-0 bg-cover bg-center bg-no-repeat"
          style={{ backgroundImage: `url(${gameBg})` }}
          aria-hidden="true"
        />
        <div
          className="pointer-events-none fixed inset-0"
          style={{ background: "var(--gz-bg-overlay)" }}
          aria-hidden="true"
        />
        <div className="relative z-10 mx-auto w-full max-w-2xl px-4 py-6 sm:px-6">
          {children}
        </div>
      </div>
    );
  }

  return (
    <div className="gz-page-auth">
      <div
        className="gz-page-auth__bg"
        style={{ backgroundImage: `url(${gameBg})` }}
        aria-hidden="true"
      />
      {children}
    </div>
  );
};

export default AuthLayout;
