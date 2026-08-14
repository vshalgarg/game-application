import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaEnvelope, FaEye, FaEyeSlash, FaLock } from "react-icons/fa";
import { signupUser } from "../services/authService";
import { useSnackbar } from "../context/SnackbarContext";
import AuthLayout from "../components/auth/AuthLayout";
import AuthCard from "../components/auth/AuthCard";
import SocialAuthButtons from "../components/auth/SocialAuthButtons";
import TextField from "../components/ui/TextField";
import Button from "../components/ui/Button";

const Signup = () => {
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleSignup = async () => {
    if (!email || !password) {
      showSnackbar("Both fields are required.", "error");
      return;
    }

    try {
      setLoading(true);
      const response = await signupUser({ email, password });
      showSnackbar(response.message || "Account Created Successfully", "success");
      navigate("/login", { replace: true });
    } catch (error) {
      console.error("Status:", error.response?.status);
      showSnackbar(error.message || "Failed to create account.", "error");
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    await handleSignup();
  };

  const handleSocialSelect = (provider) => {
    showSnackbar(`${provider} sign up coming soon`, "info");
  };

  return (
    <AuthLayout>
      <AuthCard
        eyebrow="Join GameZone"
        title="Sign Up"
        subtitle="Create your account to start playing"
        footer={
          <>
            Already have an account?{" "}
            <span
              className="gz-link"
              onClick={() => navigate("/login", { replace: true })}
              role="link"
              tabIndex={0}
              onKeyDown={(e) => {
                if (e.key === "Enter" || e.key === " ") {
                  navigate("/login", { replace: true });
                }
              }}
            >
              Log in
            </span>
          </>
        }
      >
        <form onSubmit={handleSubmit} className="space-y-3">
          <TextField
            id="signup-email"
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            autoComplete="email"
            leftIcon={<FaEnvelope size={16} />}
          />

          <TextField
            id="signup-password"
            type={showPassword ? "text" : "password"}
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="new-password"
            leftIcon={<FaLock size={16} />}
            rightSlot={
              <button
                type="button"
                aria-label={showPassword ? "Hide password" : "Show password"}
                onClick={() => setShowPassword((prev) => !prev)}
                className="cursor-pointer text-gz-icon transition hover:text-gz-primary-cyan"
              >
                {showPassword ? <FaEye size={18} /> : <FaEyeSlash size={18} />}
              </button>
            }
          />

          <Button type="submit" disabled={loading}>
            {loading ? "Creating account..." : "Sign Up"}
          </Button>
        </form>

        <SocialAuthButtons onSelect={handleSocialSelect} />
      </AuthCard>
    </AuthLayout>
  );
};

export default Signup;