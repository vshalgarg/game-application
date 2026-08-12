import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useSnackbar } from "../context/SnackbarContext";

const Navbar = () => {
  const navigate = useNavigate();
  const { auth, logout } = useAuth();
  const { showSnackbar } = useSnackbar();

  const handleLogout = () => {
    logout();
    showSnackbar("Logged out successfully", "success");
    navigate("/login", { replace: true });
  };

  return (
    <div className=" w-full bg-gray-800/70 backdrop-blur-lg border-b border-white/10 px-4 md:px-6 py-4 flex justify-between items-center ">
      {/* Logo */}

      <h1
        onClick={() => navigate("/")}
        className="text-xl md:text-2xl font-bold text-white cursor-pointer"
      >
        GameZone
      </h1>

      {/* Right Section */}

      <div className="flex items-center gap-3 md:gap-6">
        {/* Navigation Links */}

        <div className="flex gap-3 md:gap-6 text-gray-300 text-sm md:text-base">
          <p onClick={() => navigate("/")} className="cursor-pointer hover:text-white">
            Home
          </p>

          <p onClick={() => navigate("/about")} className="cursor-pointer hover:text-white">
            About
          </p>

          <p onClick={() => navigate("/contact")} className="cursor-pointer hover:text-white">
            Contact
          </p>

          <p onClick={() => navigate("/profile")} className="cursor-pointer hover:text-white">
            Profile
          </p>
        </div>

        {auth && <span className="hidden md:block text-gray-400 text-sm">{auth.username}</span>}

        {/* Logout Button */}
        <button
          onClick={handleLogout}
          className="bg-red-500 hover:bg-red-600 text-white px-3 md:px-4 py-1 md:py-2 rounded-lg text-sm transition"
        >
          Logout
        </button>
      </div>
    </div>
  );
};

export default Navbar;
