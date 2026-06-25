import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUser } from "../services/authService";

const Login = () => {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async () => {
    if (!email || !password) {
      alert("All fields are required");
      return;
    }

    try {
      const response = await loginUser({
        email,
        password,
      });

      console.log("Login Success:", response);

      // Store backend response (userId, token, username, etc.)
      localStorage.setItem("user", JSON.stringify(response));

      navigate("/");    // select game page 
    } catch (error) {
      console.error("Login Error:", error);

      alert(
        error.response?.data?.message ||
        "Invalid credentials"
      );
    }
  };

  return (
    <div
      className="
        min-h-screen
        flex
        items-center
        justify-center
        bg-gradient-to-br
        from-black
        via-gray-900
        to-black
        px-4
      "
    >
      <div
        className="
          w-full
          max-w-md
          bg-white/10
          backdrop-blur-lg
          border
          border-white/20
          rounded-3xl
          p-8
          shadow-2xl
        "
      >
        <h1 className="text-4xl text-white font-bold text-center mb-6">
          Login 🎮
        </h1>

        <p className="text-gray-300 text-center mb-8">
          Enter the arena
        </p>

        <input
          type="text"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="w-full mb-4 px-4 py-3 rounded-xl bg-black/30 text-white outline-none border border-blue-500/30 focus:border-blue-500"
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="w-full mb-6 px-4 py-3 rounded-xl bg-black/30 text-white outline-none border border-purple-500/30 focus:border-purple-500"
        />

        <button
          onClick={handleLogin}
          className="
            w-full
            bg-blue-500
            hover:bg-blue-600
            py-3
            rounded-xl
            text-white
            font-semibold
            transition
          "
        >
          Login
        </button>

        <p
          onClick={() => navigate("/signup")}
          className="text-center text-gray-300 mt-6 cursor-pointer hover:text-white"
        >
          Create Account
        </p>
      </div>
    </div>
  );
};

export default Login;