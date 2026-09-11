const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PHONE_IN_PATTERN = /^[6-9]\d{9}$/;
const DISPLAY_NAME_PATTERN = /^[a-zA-Z0-9 _.-]{2,24}$/;
const NAME_PATTERN = /^[a-zA-Z][a-zA-Z .'-]{0,39}$/;

const toIsoDate = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

export const todayIso = toIsoDate(new Date());
export const minDobIso = toIsoDate(new Date(new Date().getFullYear() - 120, 0, 1));

const getAge = (dob) => {
  const birth = new Date(`${dob}T00:00:00`);
  if (Number.isNaN(birth.getTime())) return NaN;

  const today = new Date();
  let age = today.getFullYear() - birth.getFullYear();
  const monthDelta = today.getMonth() - birth.getMonth();
  if (monthDelta < 0 || (monthDelta === 0 && today.getDate() < birth.getDate())) {
    age -= 1;
  }
  return age;
};

export const validateProfile = (form) => {
  if (!form.avatarId && !form.avatarUrl) return "Please select an avatar.";
  if (!form.displayName.trim()) return "Display name is required.";
  if (!DISPLAY_NAME_PATTERN.test(form.displayName.trim())) {
    return "Display name must be 2-24 characters using letters, numbers, spaces, or _ . -";
  }
  if (form.firstName.trim() && !NAME_PATTERN.test(form.firstName.trim())) {
    return "Enter a valid first name.";
  }
  if (form.lastName.trim() && !NAME_PATTERN.test(form.lastName.trim())) {
    return "Enter a valid last name.";
  }
  if (!form.dob) return "Date of birth is required.";
  const age = getAge(form.dob);
  if (Number.isNaN(age) || age > 120) return "Enter a valid date of birth.";
  if (age < 13) return "You must be at least 13 years old.";
  if (!form.gender) return "Gender is required.";
  if (!form.country) return "Country is required.";
  if (!form.email.trim()) return "Email is required.";
  if (!EMAIL_PATTERN.test(form.email.trim())) return "Enter a valid email address.";

  const phone = form.phone.replace(/\D/g, "");
  if (!phone) return "Phone number is required.";
  if (form.country === "IN" && !PHONE_IN_PATTERN.test(phone)) {
    return "Enter a valid 10-digit Indian mobile number.";
  }
  if (form.country !== "IN" && (phone.length < 7 || phone.length > 15)) {
    return "Enter a valid phone number.";
  }
  return "";
};
