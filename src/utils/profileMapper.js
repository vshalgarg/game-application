export const emptyProfile = {
  playerId: "",
  firstName: "",
  lastName: "",
  displayName: "",
  dob: "",
  gender: "",
  country: "",
  avatarId: "",
  email: "",
  phone: "",
};

const firstValue = (...values) =>
  values.find((value) => value !== undefined && value !== null && value !== "") ?? "";

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
  return {
    playerId: String(firstValue(response.userId)),
    firstName: firstValue(response.firstName),
    lastName: firstValue(response.lastName),
    displayName: firstValue(response.displayName),
    dob: toDateInputValue(firstValue(response.dob)),
    gender: firstValue(response.gender),
    country: firstValue(response.countryCode),
    avatarId: firstValue(response.avatarId),
    email: firstValue(response.email),
    phone: String(firstValue(response.phoneNumber)),
  };
};

export const mapProfileToApi = (form) => ({
  firstName: form.firstName.trim(),
  lastName: form.lastName.trim(),
  displayName: form.displayName.trim(),
  dob: form.dob,
  gender: form.gender,
  countryCode: form.country,
  avatarId: form.avatarId,
  email: form.email.trim(),
  phoneNumber: form.phone.trim(),
});
