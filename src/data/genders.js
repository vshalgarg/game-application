export const GENDERS = [
  { value: "male", label: "Male" },
  { value: "female", label: "Female" },
  { value: "other", label: "Other" },
  { value: "prefer_not_to_say", label: "Prefer not to say" },
];

const GENDER_ALIASES = {
  m: "male",
  male: "male",
  f: "female",
  female: "female",
  other: "other",
  "non-binary": "other",
  nonbinary: "other",
  prefer_not_to_say: "prefer_not_to_say",
  "prefer not to say": "prefer_not_to_say",
};

export const normalizeGender = (value) => {
  if (!value) return "";
  return GENDER_ALIASES[String(value).trim().toLowerCase()] || "";
};

export const getGenderLabel = (value) =>
  GENDERS.find((gender) => gender.value === normalizeGender(value))?.label || value || "";
