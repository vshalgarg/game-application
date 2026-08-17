import Navbar from "../components/Navbar";

const MainLayout = ({ children }) => {
  return (
    <div className="gz-app-frame">
      <Navbar />
      <main className="gz-app-main">{children}</main>
    </div>
  );
};

export default MainLayout;
