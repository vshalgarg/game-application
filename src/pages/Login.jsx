import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUser } from "../services/authService";
import { useSnackbar } from "../context/SnackbarContext";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import { useAuth } from "../context/AuthContext";

const Login = () => {
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();
  const { login } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const handleLogin = async () => {
    if (!email || !password) {
      showSnackbar("Both fields are required", "error");
      return;
    }

    try {
      const response = await loginUser({
        email,
        password,
      });

      const { token, userId, username, roles, permissions, userProfile } = response;

      login({ token, userId, username, roles, permissions, userProfile });
      showSnackbar(response.message || "Login Successful", "success");
      navigate("/", { replace: true });
    } catch (error) {
      console.error("Login Error:", error);
      showSnackbar(error.message || "Login Failed.", "error");
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    await handleLogin();
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-black via-gray-900 to-black px-4">
      <div className="w-full max-w-md bg-white/10 backdrop-blur-lg border border-white/20 rounded-3xl p-8 shadow-2xl">
        <h1 className="text-4xl text-white font-bold text-center mb-6">Login 🎮</h1>

        <p className="text-gray-300 text-center mb-8">Enter your login credentials</p>

        <form onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full mb-4 px-4 py-3 rounded-xl bg-black/30 text-white outline-none border border-blue-500/30 focus:border-blue-500"
          />

          <div className="relative mb-6">
            <input
              type={showPassword ? "text" : "password"}
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full px-4 py-3 pr-12  rounded-xl bg-black/30 text-white outline-none border border-purple-500/30 focus:border-purple-500"
            />

            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 cursor-pointer"
            >
              {showPassword ? <FaEye size={20} /> : <FaEyeSlash size={20} />}
            </button>
          </div>
          <button
            type="submit"
            className="w-full bg-blue-500 hover:bg-blue-600 py-3 rounded-xl text-white font-semibold transition cursor-pointer"
          >
            Login
          </button>
        </form>

        <p
          onClick={() => navigate("/signup", { replace: true })}
          className="text-center text-gray-300 mt-6 cursor-pointer hover:text-white"
        >
          Don’t have an account? Sign up
        </p>
      </div>
    </div>
  );
};

export default Login;