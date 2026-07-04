import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { signupUser } from "../services/authService";

import { useSnackbar } from "../context/SnackbarContext";
import { FaEye, FaEyeSlash } from "react-icons/fa";

const Signup = () => {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const { showSnackbar } = useSnackbar();

  const handleSignup = async () => {
    console.log("Signup Button Clicked");

    if (!email || !password) {
      showSnackbar("Both fields are required.","error");
      return;
    }

    console.log("Email:", email);
    // console.log("Password:", password);

    try {
      const response = await signupUser({
        email,
        password,
      });

      console.log("Signup Success:", response);

      showSnackbar(response.message || "Account Created Successfully", "success");

      navigate("/login");
    } catch (error) {
      console.log("Status:", error.response?.status);
      console.log("Data:", error.response?.data);
      console.log("Full Error:", error);
      const errorMessage = error.response?.data?.message || error.message || "Failed to create account.";

      showSnackbar(errorMessage, "error");
    }
  };

  const handleSubmit = async (e) => {
  e.preventDefault();
  await handleSignup();
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
          Create Account 🎮
        </h1>

        <p className="text-gray-300 text-center mb-8">
          Join the game arena
        </p>

        <form onSubmit={handleSubmit}>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="w-full mb-4 px-4 py-3 rounded-xl bg-black/30 text-white outline-none border border-purple-500/30 focus:border-purple-500"
        />

        <div className="relative mb-6">
        <input
          type={showPassword ? "text" : "password"}
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="w-full px-4 py-3 pr-12 rounded-xl bg-black/30 text-white outline-none border border-green-500/30 focus:border-green-500"
        />

        <button
          type="button"
          onClick={() => setShowPassword(!showPassword)}
          className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-white"
        >
              {showPassword ? <FaEye size={20} /> : <FaEyeSlash size={20} />}
         </button>
        </div>

        <button
          type="submit"
          className="
            w-full
            bg-green-500
            hover:bg-green-600
            py-3
            rounded-xl
            text-white
            font-semibold
            transition
          "
        >
          Create Account
        </button>
        </form>

        <p
          onClick={() => navigate("/login")}
          className="text-center text-gray-300 mt-6 cursor-pointer hover:text-white"
        >
          Already have an account? Login
        </p>
      </div>
    </div>
  );
};

export default Signup;