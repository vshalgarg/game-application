import gameBg from "../../assets/images/game_bg.jpg";

const AuthLayout = ({ children }) => {
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
