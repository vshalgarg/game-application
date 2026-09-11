import { COUNTRIES } from "../data/countries";
import { normalizeGender } from "../data/genders";

export const emptyProfile = {
  playerId: "",
  firstName: "",
  lastName: "",
  displayName: "",
  dob: "",
  gender: "",
  country: "",
  avatarId: "",
  avatarUrl: "",
  email: "",
  phone: ""
};

const firstValue = (...values) => values.find((value) => value !== undefined && value !== null && value !== "") ?? "";


const normalizeCountry = (value) => {
  if (!value) return "";
  const text = String(value).trim();
  const byCode = COUNTRIES.find((country) => country.value.toLowerCase() === text.toLowerCase());
  if (byCode) return byCode.value;
  const byLabel = COUNTRIES.find((country) => country.label.toLowerCase() === text.toLowerCase());
  return byLabel?.value || text;
};

const firstNonEmail = (...values) => {
  const value = firstValue(...values);
  return String(value).includes("@") ? "" : value;
};

const toDateInputValue = (value) => {
  if (!value) return "";
  const text = String(value);
  if (/^\d{4}-\d{2}-\d{2}/.test(text)) return text.slice(0, 10);

  const parsed = new Date(text);
  if (Number.isNaN(parsed.getTime())) return "";

  const year = parsed.getFullYear();
  const month = String(parsed.getMonth() + 1).padStart(2, "0");
  const day = String(parsed.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

export const mapProfileFromApi = (response) => {
  const avatarValue = firstValue(response.avatarId);
  const isAvatarUrl = typeof avatarValue === "string" && /^(https?:)?\/\//.test(avatarValue);

  return {
    playerId: String(firstValue(response.playerId)),
    firstName: firstValue(response.firstName),
    lastName: firstValue(response.lastName),
    displayName: firstValue(response.displayName),
    dob: toDateInputValue(firstValue(response.dob)),
    gender: normalizeGender(firstValue(response.gender)),
    country: normalizeCountry(firstValue(response.country)),
    avatarId: isAvatarUrl ? "" : avatarValue,
    avatarUrl: isAvatarUrl ? avatarValue : firstValue(response.avatarUrl),
    email: firstValue(response.email),
    phone: String(firstValue(response.phoneNumber))
  };
};

export const mapProfileToApi = (form) => ({
  firstName: form.firstName.trim(),
  lastName: form.lastName.trim(),
  displayName: form.displayName.trim(),
  dob: form.dob,
  gender: form.gender,
  country: form.country,
  avatarId: form.avatarId,
  email: form.email.trim(),
  phone: form.phone.trim(),
});
