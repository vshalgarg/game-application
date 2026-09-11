import { useEffect, useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import { FaIdBadge } from "react-icons/fa";
import AuthLayout from "../components/auth/AuthLayout";
import GameZoneLogo from "../components/brand/GameZoneLogo";
import Button from "../components/ui/Button";
import AvatarPreview from "../components/profile/AvatarPreview";
import AvatarPicker from "../components/profile/AvatarPicker";
import ProfileForm from "../components/profile/ProfileForm";
import { validateProfile } from "../utils/profileValidation";
import { getAvatarById } from "../data/avatars";
import { getProfile, updateProfile } from "../services/profileService";
import { useAuth } from "../context/AuthContext";
import { useSnackbar } from "../context/SnackbarContext";
import { emptyProfile, mapProfileFromApi, mapProfileToApi } from "../utils/profileMapper";

const CompleteProfile = () => {
  const navigate = useNavigate();
  const { auth } = useAuth();
  const { showSnackbar } = useSnackbar();

  const [form, setForm] = useState(emptyProfile);
  const [pickerOpen, setPickerOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [loadError, setLoadError] = useState("");

  const selectedAvatar = getAvatarById(form.avatarId);

  const fetchProfile = async() => {
    setLoading(true);
    try {
      const response = await getProfile();
      const mapped = mapProfileFromApi(response);
      setForm(mapped);
    } catch (error) {
      console.error("Profile Fetch Error:", error);
      setLoadError(error.message || "Unable to load your profile.");
    } finally{
       setLoading(false);
    }
  }

  useEffect(() => {

     fetchProfile();

  }, []);

  const handleSubmit = async (event) => {
    event.preventDefault();
    const errorMessage = validateProfile(form);
    if (errorMessage) {
      showSnackbar(errorMessage, "error");
      return;
    }

    try {
      setSaving(true);
      const response = await updateProfile(mapProfileToApi(form));
      const mapped = mapProfileFromApi(response, { ...form, profileStatus: true });
      const nextProfile = { ...mapped, profileStatus: true };

      showSnackbar(response?.message || "Profile saved", "success");
      navigate("/", { replace: true });
    } catch (error) {
      console.error("Profile Save Error:", error);
      showSnackbar(error.message || "Unable to save profile.", "error");
    } finally {
      setSaving(false);
    }
  };

  if (auth?.profileStatus) {
    return <Navigate to="/" replace />;
  }

  return (
    <AuthLayout scrollable>
      <div className="gz-auth-card w-full max-w-2xl! md:max-w-2xl!">
        <div className="mb-4 flex flex-col items-center text-center">
          <GameZoneLogo className="mb-1.5 h-10 w-10 text-gz-primary-cyan" />
          <p className="text-xl font-bold tracking-wide text-gz-text">GameZone</p>
          <div className="gz-divider mt-3 w-full max-w-[220px] justify-center">
            <span className="text-sm text-gz-primary-cyan">Complete Profile</span>
          </div>
          <h1 className="mt-2 text-3xl font-bold text-gz-text">Set up your player</h1>
          <p className="mt-1 text-sm text-gz-text-secondary">
            Fill in the remaining details to start playing.
          </p>
        </div>

        {loading ? (
          <p className="py-6 text-center text-sm text-gz-text-secondary">Fetching your details...</p>
        ) : loadError ? (
          <div className="py-4 text-center">
            <p className="text-sm text-gz-text-secondary">{loadError}</p>
            <Button type="button" className="mt-5" onClick={getProfile}>
              Try again
            </Button>
          </div>
        ) : (
          <>
            <div className="mb-4 flex items-center gap-4 rounded-2xl border border-white/10 bg-gz-popup/45 p-3.5">
              <AvatarPreview
                avatarId={form.avatarId}
                avatarUrl={form.avatarUrl}
                name={selectedAvatar?.name || form.displayName}
                editable
                onClick={() => setPickerOpen(true)}
              />
              <div className="min-w-0 flex-1">
                <p className="truncate text-lg font-bold text-gz-text">
                  {form.displayName || "Unnamed player"}
                </p>
                <span className="mt-2 inline-flex max-w-full items-center gap-1.5 truncate rounded-full border border-gz-primary-cyan/40 bg-gz-popup-dark/80 px-2.5 py-1 text-xs font-medium text-gz-primary-cyan">
                  <FaIdBadge size={12} />
                  {form.playerId || "—"}
                </span>
                <p className="mt-2 text-xs text-gz-text-secondary">
                  {form.avatarId ? `Avatar: ${selectedAvatar?.name || "Custom"}` : "Avatar is required"}
                </p>
              </div>
            </div>

            <ProfileForm form={form} onPatch={(patch) => setForm((prev) => ({ ...prev, ...patch }))} onSubmit={handleSubmit}>
              <Button type="submit" disabled={saving}>
                {saving ? "Saving..." : "Save & Continue"}
              </Button>
            </ProfileForm>
          </>
        )}
      </div>

      <AvatarPicker
        open={pickerOpen}
        selectedId={form.avatarId}
        onSelect={(avatarId) => setForm((prev) => ({ ...prev, avatarId, avatarUrl: "" }))}
        onClose={() => setPickerOpen(false)}
      />
    </AuthLayout>
  );
};

export default CompleteProfile;
