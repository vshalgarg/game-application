import Navbar from "../components/Navbar";

const MainLayout = ({ children }) => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-black to-gray-800">

      <Navbar />

      {/* Page Content */}
      <div>
        {children}
      </div>

    </div>
  );
};

export default MainLayout;