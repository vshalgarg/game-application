import { useEffect, useState } from "react";
import { FaCopy, FaIdBadge, FaPen } from "react-icons/fa";
import PageShell from "../components/layout/PageShell";
import Button from "../components/ui/Button";
import AvatarPreview from "../components/profile/AvatarPreview";
import AvatarPicker from "../components/profile/AvatarPicker";
import ProfileForm from "../components/profile/ProfileForm";
import { validateProfile } from "../utils/profileValidation";
import { getCountryLabel } from "../data/countries";
import { getGenderLabel } from "../data/genders";
import { getAvatarById } from "../data/avatars";
import { getProfile, updateProfile } from "../services/profileService";
import { useAuth } from "../context/AuthContext";
import { useSnackbar } from "../context/SnackbarContext";
import { emptyProfile, mapProfileFromApi, mapProfileToApi } from "../utils/profileMapper";

const formatDob = (value) => {
  if (!value) return "—";
  const date = new Date(`${value}T00:00:00`);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleDateString(undefined, {
    day: "numeric",
    month: "short",
    year: "numeric",
  });
};

const Profile = () => {
  const { auth, updateCurrentUser } = useAuth();
  const { showSnackbar } = useSnackbar();

  const [form, setForm] = useState(emptyProfile);
  const [savedProfile, setSavedProfile] = useState(emptyProfile);
  const [editing, setEditing] = useState(false);
  const [pickerOpen, setPickerOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [loadError, setLoadError] = useState("");

  const selectedAvatar = getAvatarById(form.avatarId);

  const fetchProfile = async() => {
    setLoading(true);
    try {
      const response = await getProfile();
      const mapped = mapProfileFromApi(response);
      setSavedProfile(mapped);
      setForm(mapped);
      setEditing(false);
    } catch (error) {
      console.error("Profile Fetch Error:", error);
      setLoadError(error.message || "Unable to load your profile.");
      setLoading(false);
    }
  }


  useEffect(() => {

    fetchProfile();

  }, []);


  const handleCancel = () => {
    setForm(savedProfile);
    setEditing(false);
    setPickerOpen(false);
  };

  const handleCopyPlayerId = async () => {
    if (!form.playerId) return;
    try {
      await navigator.clipboard.writeText(form.playerId);
      showSnackbar("Player ID copied", "success");
    } catch (error) {
      console.error("Copy Player ID Error:", error);
      showSnackbar("Unable to copy Player ID", "error");
    }
  };

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

      setSavedProfile(nextProfile);
      setForm(nextProfile);
      setEditing(false);
      updateCurrentUser({ profileStatus: true, userProfile: nextProfile });
      showSnackbar(response?.message || "Profile saved", "success");
    } catch (error) {
      console.error("Profile Save Error:", error);
      showSnackbar(error.message || "Unable to save profile.", "error");
    } finally {
      setSaving(false);
    }
  };

  const fullName = [form.firstName, form.lastName].filter(Boolean).join(" ") || "—";
  const infoItems = [
    { id: "name", label: "Name", value: fullName },
    { id: "dob", label: "Date of Birth", value: formatDob(form.dob) },
    { id: "gender", label: "Gender", value: getGenderLabel(form.gender) || "—" },
    { id: "country", label: "Country", value: getCountryLabel(form.country) || "—" },
    { id: "email", label: "Email", value: form.email || "—" },
    { id: "phone", label: "Phone", value: form.phone || "—" },
  ];

  return (
    <PageShell scrollable className="pt-5 pb-7">
      <section className="relative z-10 mx-auto w-full max-w-3xl rounded-gz-card border border-gz-primary-cyan/40 bg-gz-popup-dark/90 px-4 py-5 backdrop-blur-xl sm:px-6 sm:py-6 shadow-[0_20px_60px_rgba(0,0,0,0.45),0_0_32px_rgb(0_217_232/0.16),0_0_24px_rgb(139_92_246/0.12)]">
        {loading ? (
          <div className="py-10 text-center">
            <p className="text-sm font-semibold tracking-[0.2em] text-gz-primary-cyan">LOADING</p>
            <h1 className="mt-2 text-2xl font-bold text-gz-text">Player Profile</h1>
            <p className="mt-2 text-sm text-gz-text-secondary">Fetching your details...</p>
          </div>
        ) : loadError ? (
          <div className="py-8 text-center">
            <h1 className="text-2xl font-bold text-gz-text">Player Profile</h1>
            <p className="mt-2 text-sm text-gz-text-secondary">{loadError}</p>
            <Button type="button" className="mt-5" onClick={fetchProfile}>
              Try again
            </Button>
          </div>
        ) : (
          <>
            <header className="mb-5 flex flex-col gap-3 sm:mb-6 sm:flex-row sm:items-start sm:justify-between">
              <div>
                <p className="gz-about__kicker">PLAYER PROFILE</p>
                <h1 className="mt-1 text-2xl font-bold text-gz-text sm:text-3xl">
                  {form.displayName || "Your Profile"}
                </h1>
                <p className="mt-1 text-sm text-gz-text-secondary">
                  {editing
                    ? "Update your player details and submit to save."
                    : "This is how you appear across GameZone."}
                </p>
              </div>

              {editing ? (
                <div className="flex items-center gap-2 self-start">
                  <Button
                    type="button"
                    variant="secondary"
                    className="w-auto! px-5"
                    onClick={handleCancel}
                    disabled={saving}
                  >
                    Cancel
                  </Button>
                  <Button type="submit" form="profile-form" variant="nav" disabled={saving}>
                    {saving ? "Saving..." : "Submit"}
                  </Button>
                </div>
              ) : (
                <Button
                  type="button"
                  variant="nav"
                  className="inline-flex items-center justify-center gap-2 self-start"
                  onClick={() => setEditing(true)}
                >
                  <FaPen size={12} />
                  Edit
                </Button>
              )}
            </header>

            <div className="mb-5 flex items-center gap-4 rounded-2xl border border-white/10 bg-gz-popup/45 p-3.5 sm:gap-5 sm:p-4">
              <AvatarPreview
                avatarId={form.avatarId}
                avatarUrl={form.avatarUrl}
                name={selectedAvatar?.name || form.displayName}
                editable={editing}
                onClick={() => setPickerOpen(true)}
              />

              <div className="min-w-0 flex-1">
                <p className="truncate text-lg font-bold text-gz-text">
                  {form.displayName || "Unnamed player"}
                </p>
                <div className="mt-2 flex flex-wrap items-center gap-2">
                  <span className="inline-flex max-w-full items-center gap-1.5 truncate rounded-full border border-gz-primary-cyan/40 bg-gz-popup-dark/80 px-2.5 py-1 text-xs font-medium text-gz-primary-cyan">
                    <FaIdBadge size={12} />
                    {form.playerId || "—"}
                  </span>
                  {form.playerId ? (
                    <button
                      type="button"
                      className="inline-flex cursor-pointer items-center gap-1 rounded-full border border-white/15 bg-transparent px-2 py-1 text-[11px] font-medium text-gz-text-secondary transition hover:border-gz-primary-cyan/50 hover:text-gz-primary-cyan"
                      onClick={handleCopyPlayerId}
                      aria-label="Copy Player ID"
                    >
                      <FaCopy size={11} />
                      Copy
                    </button>
                  ) : null}
                </div>
              </div>
            </div>

            {editing ? (
              <ProfileForm
                form={form}
                onPatch={(patch) => setForm((prev) => ({ ...prev, ...patch }))}
                onSubmit={handleSubmit}
              />
            ) : (
              <div className="grid gap-2.5 sm:grid-cols-2">
                {infoItems.map((item) => (
                  <article key={item.id} className="rounded-xl border border-white/10 bg-gz-popup/40 px-3.5 py-3">
                    <p className="text-[11px] font-semibold tracking-[0.14em] text-gz-text-secondary uppercase">
                      {item.label}
                    </p>
                    <p className="mt-1 truncate text-sm font-semibold text-gz-text">{item.value}</p>
                  </article>
                ))}
              </div>
            )}
          </>
        )}
      </section>

      <AvatarPicker
        open={pickerOpen}
        selectedId={form.avatarId}
        onSelect={(avatarId) => setForm((prev) => ({ ...prev, avatarId, avatarUrl: "" }))}
        onClose={() => setPickerOpen(false)}
      />
    </PageShell>
  );
};

export default Profile;
