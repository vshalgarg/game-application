import gameBg from "../../assets/images/game_bg.webp";

const PageShell = ({ children, className = "", scrollable = false }) => {
  return (
    <div
      className={
        scrollable
          ? `relative min-h-full px-4 py-6 sm:px-6 ${className}`
          : `gz-page-shell ${className}`
      }
    >
      <div
        className="gz-page-shell__bg"
        style={{ backgroundImage: `url(${gameBg})` }}
        aria-hidden="true"
      />
      {children}
    </div>
  );
};

export default PageShell;
