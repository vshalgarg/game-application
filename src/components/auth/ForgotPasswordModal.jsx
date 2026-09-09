import { useEffect, useRef, useState } from "react";
import { FaCheck, FaEye, FaEyeSlash, FaLock } from "react-icons/fa";
import { LuX } from "react-icons/lu";
import {
  forgotPassword,
  resendResetOtp,
  resetPassword,
  verifyResetOtp,
} from "../../services/authService";
import { useSnackbar } from "../../context/SnackbarContext";
import TextField from "../ui/TextField";
import Button from "../ui/Button";

const STEP = {
  PHONE: 1,
  OTP: 2,
  PASSWORD: 3,
  SUCCESS: 4,
};

const OTP_LENGTH = 6;
const RESEND_SECONDS = 45;
const PHONE_PATTERN = /^[6-9]\d{9}$/;

const emptyOtp = () => Array(OTP_LENGTH).fill("");

const maskPhone = (phone) => `******${phone.slice(-4)}`;

const OtpInputs = ({ values, disabled, onChange, onComplete }) => {
  const inputRefs = useRef([]);

  const focusAt = (index) => {
    inputRefs.current[index]?.focus();
    inputRefs.current[index]?.select();
  };

  const emit = (next) => {
    onChange(next);
    if (next.every(Boolean)) {
      onComplete?.(next.join(""));
    }
  };

  const handleChange = (index, raw) => {
    const digit = raw.replace(/\D/g, "").slice(-1);
    const next = [...values];
    next[index] = digit;
    emit(next);
    if (digit && index < OTP_LENGTH - 1) {
      focusAt(index + 1);
    }
  };

  const handleKeyDown = (index, event) => {
    if (event.key === "Backspace" && !values[index] && index > 0) {
      event.preventDefault();
      const next = [...values];
      next[index - 1] = "";
      emit(next);
      focusAt(index - 1);
    }
  };

  const handlePaste = (event) => {
    event.preventDefault();
    const pasted = event.clipboardData.getData("text").replace(/\D/g, "").slice(0, OTP_LENGTH);
    if (!pasted) return;

    const next = emptyOtp();
    pasted.split("").forEach((digit, index) => {
      next[index] = digit;
    });
    emit(next);
    focusAt(Math.min(pasted.length, OTP_LENGTH - 1));
  };

  return (
    <div className="flex justify-center gap-2">
      {values.map((value, index) => (
        <input
          key={index}
          ref={(node) => {
            inputRefs.current[index] = node;
          }}
          type="text"
          inputMode="numeric"
          autoComplete={index === 0 ? "one-time-code" : "off"}
          maxLength={1}
          value={value}
          disabled={disabled}
          aria-label={`OTP digit ${index + 1}`}
          className="gz-input h-12 w-10 px-0 text-center text-lg font-semibold"
          onChange={(event) => handleChange(index, event.target.value)}
          onKeyDown={(event) => handleKeyDown(index, event)}
          onPaste={handlePaste}
        />
      ))}
    </div>
  );
};

const ForgotPasswordModal = ({ open, onClose }) => {
  const { showSnackbar } = useSnackbar();

  const [step, setStep] = useState(STEP.PHONE);
  const [phoneNumber, setPhoneNumber] = useState("");
  const [otp, setOtp] = useState(emptyOtp);
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [resetToken, setResetToken] = useState(null);
  const [loading, setLoading] = useState(false);
  const [resendIn, setResendIn] = useState(0);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const handleClose = () => {
    onClose();
  };

  useEffect(() => {
    if (open) return;

    setStep(STEP.PHONE);
    setPhoneNumber("");
    setOtp(emptyOtp());
    setNewPassword("");
    setConfirmPassword("");
    setResetToken(null);
    setLoading(false);
    setResendIn(0);
    setShowPassword(false);
    setShowConfirmPassword(false);
  }, [open]);

  useEffect(() => {
    if (!open) return undefined;

    const handleKeyDown = (event) => {
      if (event.key === "Escape") onClose();
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [open, onClose]);

  useEffect(() => {
    if (!open || resendIn <= 0) return undefined;

    const timer = window.setInterval(() => {
      setResendIn((seconds) => (seconds <= 1 ? 0 : seconds - 1));
    }, 1000);

    return () => window.clearInterval(timer);
  }, [open, resendIn]);

  if (!open) return null;

  const startResendTimer = () => setResendIn(RESEND_SECONDS);

  const handleSendOtp = async (event) => {
    event.preventDefault();

    if (!PHONE_PATTERN.test(phoneNumber)) {
      showSnackbar("Enter a valid 10-digit mobile number", "error");
      return;
    }

    try {
      setLoading(true);
      // const response = await forgotPassword({ phoneNumber });
      setOtp(emptyOtp());
      setStep(STEP.OTP);
      startResendTimer();
      showSnackbar( "OTP sent", "success");
    } catch (error) {
      console.error("Forgot Password Error:", error);
      showSnackbar(error.message || "Failed to send OTP.", "error");
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyOtp = async (event) => {
    event.preventDefault();
    const otpValue = otp.join("");

    if (otpValue.length !== OTP_LENGTH) {
      showSnackbar("Enter the 6-digit OTP", "error");
      return;
    }

    try {
      setLoading(true);
      // const response = await verifyResetOtp({ phoneNumber, otp: otpValue });
      setResetToken(resetToken);
      setStep(STEP.PASSWORD);
    } catch (error) {
      console.error("Verify OTP Error:", error);
      showSnackbar(error.message || "Invalid OTP.", "error");
    } finally {
      setLoading(false);
    }
  };

  const handleResendOtp = async () => {
    if (resendIn > 0 || loading) return;

    try {
      setLoading(true);
      const response = await resendResetOtp({ phoneNumber });
      setOtp(emptyOtp());
      startResendTimer();
      showSnackbar(response.message || "OTP resent", "success");
    } catch (error) {
      console.error("Resend OTP Error:", error);
      showSnackbar(error.message || "Failed to resend OTP.", "error");
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async (event) => {
    event.preventDefault();

    if (!newPassword || !confirmPassword) {
      showSnackbar("Both fields are required", "error");
      return;
    }

    if (newPassword !== confirmPassword) {
      showSnackbar("Passwords do not match", "error");
      return;
    }

    try {
      setLoading(true);
      // const response = await resetPassword({ resetToken, newPassword });
      setStep(STEP.SUCCESS);
      showSnackbar( "Password reset successful", "success");
    } catch (error) {
      console.error("Reset Password Error:", error);
      showSnackbar(error.message || "Failed to reset password.", "error");
    } finally {
      setLoading(false);
    }
  };

  const titles = {
    [STEP.PHONE]: { title: "Forgot Password", subtitle: "Enter your registered mobile number" },
    [STEP.OTP]: { title: "Verify OTP", subtitle: `OTP sent to ${maskPhone(phoneNumber)}` },
    [STEP.PASSWORD]: { title: "Reset Password", subtitle: "Choose a new password for your account" },
    [STEP.SUCCESS]: { title: "Success", subtitle: "Your password has been reset." },
  };

  return (
    <div className="gz-exit-overlay" onClick={handleClose} role="presentation">
      <div
        className="gz-auth-card relative"
        role="dialog"
        aria-modal="true"
        aria-labelledby="forgot-password-title"
        onClick={(event) => event.stopPropagation()}
      >
        <button
          type="button"
          className="gz-exit-modal__close"
          onClick={handleClose}
          aria-label="Close"
        >
          <LuX />
        </button>

        <div className="mb-4 text-center">
          {step === STEP.SUCCESS && (
            <span className="mx-auto mb-3 flex h-12 w-12 items-center justify-center rounded-full border border-gz-primary-cyan/50 text-gz-primary-cyan shadow-[0_0_16px_rgb(0_217_232_/_35%)]">
              <FaCheck />
            </span>
          )}
          <h2 id="forgot-password-title" className="text-2xl font-bold text-gz-text">
            {titles[step].title}
          </h2>
          <p className="mt-1 text-sm text-gz-text-secondary">{titles[step].subtitle}</p>
        </div>

        {step === STEP.PHONE && (
          <form onSubmit={handleSendOtp} className="space-y-3">
            <TextField
              id="forgot-phone"
              type="tel"
              inputMode="numeric"
              autoComplete="tel"
              maxLength={10}
              placeholder="Mobile number"
              value={phoneNumber}
              onChange={(event) => setPhoneNumber(event.target.value.replace(/\D/g, "").slice(0, 10))}
            />
            <Button type="submit" disabled={loading}>
              {loading ? "Sending..." : "Send OTP"}
            </Button>
          </form>
        )}

        {step === STEP.OTP && (
          <form onSubmit={handleVerifyOtp} className="space-y-4">
            <OtpInputs values={otp} disabled={loading} onChange={setOtp} />

            <div className="text-center text-sm">
              {resendIn > 0 ? (
                <p className="text-gz-text-secondary">Resend OTP in {resendIn} seconds</p>
              ) : (
                <button
                  type="button"
                  className="gz-link bg-transparent"
                  disabled={loading}
                  onClick={handleResendOtp}
                >
                  Resend OTP
                </button>
              )}
            </div>

            <Button type="submit" disabled={loading}>
              {loading ? "Verifying..." : "Verify"}
            </Button>
          </form>
        )}

        {step === STEP.PASSWORD && (
          <form onSubmit={handleResetPassword} className="space-y-3">
            <TextField
              id="reset-new-password"
              type={showPassword ? "text" : "password"}
              placeholder="New Password"
              value={newPassword}
              onChange={(event) => setNewPassword(event.target.value)}
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
            <TextField
              id="reset-confirm-password"
              type={showConfirmPassword ? "text" : "password"}
              placeholder="Confirm Password"
              value={confirmPassword}
              onChange={(event) => setConfirmPassword(event.target.value)}
              autoComplete="new-password"
              leftIcon={<FaLock size={16} />}
              rightSlot={
                <button
                  type="button"
                  aria-label={showConfirmPassword ? "Hide password" : "Show password"}
                  onClick={() => setShowConfirmPassword((prev) => !prev)}
                  className="cursor-pointer text-gz-icon transition hover:text-gz-primary-cyan"
                >
                  {showConfirmPassword ? <FaEye size={18} /> : <FaEyeSlash size={18} />}
                </button>
              }
            />
            <Button type="submit" disabled={loading}>
              {loading ? "Resetting..." : "Reset Password"}
            </Button>
          </form>
        )}

        {step === STEP.SUCCESS && (
          <div className="space-y-3 text-center">
            <p className="text-sm text-gz-text-secondary">
              You can now login with your new password.
            </p>
            <Button type="button" onClick={handleClose}>
              Continue
            </Button>
          </div>
        )}
      </div>
    </div>
  );
};

export default ForgotPasswordModal;
