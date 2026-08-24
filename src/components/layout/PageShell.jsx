import gameBg from "../../assets/images/game_bg.jpg";

const PageShell = ({ children, className = "" }) => {
  return (
    <div className={`gz-page-shell ${className}`}>
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
