import {
  FaCalendarAlt,
  FaEnvelope,
  FaGlobe,
  FaIdBadge,
  FaPhoneAlt,
  FaUser,
  FaVenusMars,
} from "react-icons/fa";
import TextField from "../ui/TextField";
import SelectField from "../ui/SelectField";
import LabeledField from "./LabeledField";
import { GENDERS } from "../../data/genders";
import { minDobIso, todayIso } from "../../utils/profileValidation";

const ProfileForm = ({
  id = "profile-form",
  form,
  errors,
  onClearError,
  onPatch,
  onSubmit,
  countries = [],
  countriesLoading = false,
  children,
}) => {
  const updateField = (field) => (event) => {
    onPatch({ [field]: event.target.value });
    onClearError(field);
  };

  const handlePhoneChange = (event) => {
    const maxLength = form.country === "IN" ? 10 : 15;
    onPatch({ phone: event.target.value.replace(/\D/g, "").slice(0, maxLength) });
    onClearError("phone");
  };

  const handleCountryChange = (event) => {
    const country = event.target.value;
    onPatch({
      country,
      phone: country === "IN" ? form.phone.slice(0, 10) : form.phone,
    });
    onClearError("country");
    onClearError("phone");
  };

  return (
    <form id={id} onSubmit={onSubmit} className="flex flex-col gap-3.5">
      <LabeledField id="player-id" label="Player ID">
        <TextField
          id="player-id"
          value={form.playerId}
          readOnly
          disabled
          leftIcon={<FaIdBadge size={15} />}
        />
      </LabeledField>

      <div className="grid gap-3.5 sm:grid-cols-2">
        <LabeledField id="first-name" label="First Name">
          <TextField
            id="first-name"
            value={form.firstName}
            onChange={updateField("firstName")}
            placeholder="First name"
            autoComplete="given-name"
            leftIcon={<FaUser size={14} />}
            error={errors.firstName}
            helperText={errors.firstName}
          />
        </LabeledField>
        <LabeledField id="last-name" label="Last Name">
          <TextField
            id="last-name"
            value={form.lastName}
            onChange={updateField("lastName")}
            placeholder="Last name"
            autoComplete="family-name"
            leftIcon={<FaUser size={14} />}
            error={errors.lastName}
            helperText={errors.lastName}
          />
        </LabeledField>
      </div>

      <div className="grid gap-3.5 sm:grid-cols-2">
        <LabeledField id="email" label="Email" required>
          <TextField
            id="email"
            value={form.email}
            readOnly
            disabled
            leftIcon={<FaEnvelope size={14} />}
          />
        </LabeledField>
        <LabeledField id="display-name" label="Display Name" required>
          <TextField
            id="display-name"
            value={form.displayName}
            onChange={updateField("displayName")}
            placeholder="Shown on leaderboards"
            autoComplete="nickname"
            leftIcon={<FaUser size={14} />}
            error={errors.displayName}
            helperText={errors.displayName}
          />
        </LabeledField>
      </div>

      <div className="grid gap-3.5 sm:grid-cols-2">
        <LabeledField id="dob" label="Date of Birth" required>
          <TextField
            id="dob"
            type="date"
            value={form.dob}
            onChange={updateField("dob")}
            min={minDobIso}
            max={todayIso}
            leftIcon={<FaCalendarAlt size={14} />}
            error={errors.dob}
            helperText={errors.dob}
          />
        </LabeledField>
        <LabeledField id="gender" label="Gender" required>
          <SelectField
            id="gender"
            value={form.gender}
            onChange={updateField("gender")}
            placeholder="Select gender"
            options={GENDERS}
            leftIcon={<FaVenusMars size={15} />}
            error={errors.gender}
            helperText={errors.gender}
          />
        </LabeledField>
      </div>

      <div className="grid gap-3.5 sm:grid-cols-2">
        <LabeledField id="country" label="Country" required>
          <SelectField
            id="country"
            value={form.country}
            onChange={handleCountryChange}
            placeholder={countriesLoading ? "Loading countries..." : "Select country"}
            options={countries}
            leftIcon={<FaGlobe size={14} />}
            error={errors.country}
            helperText={errors.country}
            disabled={countriesLoading || countries.length === 0}
          />
        </LabeledField>
        <LabeledField id="phone" label="Phone" required>
          <TextField
            id="phone"
            type="tel"
            value={form.phone}
            onChange={handlePhoneChange}
            placeholder={form.country === "IN" ? "10-digit mobile number" : "Phone number"}
            autoComplete="tel"
            inputMode="numeric"
            leftIcon={<FaPhoneAlt size={14} />}
            error={errors.phone}
            helperText={errors.phone}
          />
        </LabeledField>
      </div>

      {children}
    </form>
  );
};

export default ProfileForm;
