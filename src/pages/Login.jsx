import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaEnvelope, FaEye, FaEyeSlash, FaLock } from "react-icons/fa";
import { loginUser, socialLogin } from "../services/authService";
import { useSnackbar } from "../context/SnackbarContext";
import { useAuth } from "../context/AuthContext";
import { useGoogleAuth } from "../hooks/useGoogleAuth";
import { useFacebookAuth } from "../hooks/useFacebookAuth";

import {
  clearRememberedEmail,
  loadRememberedEmail,
  updateRememberedEmail,
} from "../storage/rememberStorage";
import AuthLayout from "../components/auth/AuthLayout";
import AuthCard from "../components/auth/AuthCard";
import SocialAuthButtons from "../components/auth/SocialAuthButtons";
import TextField from "../components/ui/TextField";
import Button from "../components/ui/Button";

const Login = () => {
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();
  const { login } = useAuth();

  const savedEmail = loadRememberedEmail();
  const [email, setEmail] = useState(savedEmail || "");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(Boolean(savedEmail));
  const [loading, setLoading] = useState(false);

  const handleLoginSuccess = (response) => {
    const { token, userId, username, roles, permissions, userProfile } = response;

    login({
      token,
      userId,
      username,
      roles,
      permissions,
      userProfile,
    });

    if (rememberMe) {
      updateRememberedEmail(email);
    } else {
      clearRememberedEmail();
    }

    showSnackbar(response.message || "Login Successful", "success");
    navigate("/", { replace: true });
  };

  const handleLogin = async () => {
    if (!email || !password) {
      showSnackbar("Both fields are required", "error");
      return;
    }

    try {
      setLoading(true);
      const response = await loginUser({ email, password });
      handleLoginSuccess(response);
    } catch (error) {
      console.error("Login Error:", error);
      showSnackbar(error.message || "Login Failed.", "error");
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    await handleLogin();
  };

  const handleSocialSuccess = async (provider, credential) => {
    try {
      const payload = {
        provider,
        credential,
      };

      const response = await socialLogin(payload);
      handleLoginSuccess(response);
    } catch (error) {
      console.error(`${provider} Login Error:`, error);
      showSnackbar(`${provider} login failed.`, "error");
    }
  };

  const handleGoogleError = (error) => {
    console.error("Google Login Error:", error);
    showSnackbar("Google login failed.", "error");
  };

  const handleFacebookError = (error) => {
    console.error("Facebook Login Error:", error);

    if (error?.error === "facebook_login_cancelled") {
      return;
    }

    showSnackbar("Facebook login failed.", "error");
  };

  const { loginWithGoogle } = useGoogleAuth({
    onSuccess: handleSocialSuccess,
    onError: handleGoogleError,
  });

  const { loginWithFacebook, isReady: isFacebookReady } = useFacebookAuth({
    onSuccess: handleSocialSuccess,
    onError: handleFacebookError,
  });

  const handleSocialSelect = (provider) => {
    if (provider === "google") {
      loginWithGoogle(provider);
      return;
    } else if (provider === "facebook") {
      loginWithFacebook(provider);
      return;
    }
    showSnackbar(`${provider} login coming soon`, "info");
  };

  return (
    <AuthLayout>
      <AuthCard
        eyebrow="Welcome Back!"
        title="Login"
        subtitle="Enter your login credentials"
        footer={
          <>
            Don&apos;t have an account?{" "}
            <span
              className="gz-link"
              onClick={() => navigate("/signup", { replace: true })}
              role="link"
              tabIndex={0}
              onKeyDown={(e) => {
                if (e.key === "Enter" || e.key === " ") {
                  navigate("/signup", { replace: true });
                }
              }}
            >
              Create Account
            </span>
          </>
        }
      >
        <form onSubmit={handleSubmit} className="space-y-3">
          <TextField
            id="login-email"
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            autoComplete="email"
            leftIcon={<FaEnvelope size={16} />}
          />

          <TextField
            id="login-password"
            type={showPassword ? "text" : "password"}
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
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

          <div className="flex items-center justify-between gap-3 text-sm">
            <label className="flex cursor-pointer items-center gap-2 text-gz-text-secondary">
              <input
                type="checkbox"
                checked={rememberMe}
                onChange={(e) => setRememberMe(e.target.checked)}
                className="size-4 accent-gz-primary-cyan"
              />
              Remember me
            </label>

            <button
              type="button"
              className="gz-link bg-transparent text-sm"
              onClick={() => showSnackbar("Password reset coming soon", "info")}
            >
              Forgot Password?
            </button>
          </div>

          <Button type="submit" disabled={loading}>
            {loading ? "Logging in..." : "Login"}
          </Button>
        </form>

        <SocialAuthButtons onSelect={handleSocialSelect} />
      </AuthCard>
    </AuthLayout>
  );
};

export default Login;
