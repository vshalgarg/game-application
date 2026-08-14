import Navbar from "../components/Navbar";

const MainLayout = ({ children }) => {
  return (
    <div className="min-h-dvh bg-gz-popup-dark">
      <Navbar />
      <main className="pt-[var(--gz-navbar-height)]">{children}</main>
    </div>
  );
};

export default MainLayout;
