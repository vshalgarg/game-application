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
  const errors = {};

  if (!form.avatarId && !form.avatarUrl) {
    errors.avatar = "Please select an avatar.";
  }

  const displayName = form.displayName.trim();

  if (!displayName) {
    errors.displayName = "Display name is required.";
  } else if (!DISPLAY_NAME_PATTERN.test(displayName)) {
    errors.displayName =
      "Display name must be 2-24 characters using letters, numbers, spaces, or _ . -";
  }

  const firstName = form.firstName.trim();

  if (firstName && !NAME_PATTERN.test(firstName)) {
    errors.firstName = "Enter a valid first name.";
  }

  const lastName = form.lastName.trim();

  if (lastName && !NAME_PATTERN.test(lastName)) {
    errors.lastName = "Enter a valid last name.";
  }

  if (!form.dob) {
    errors.dob = "Date of birth is required.";
  } else {
    const age = getAge(form.dob);

    if (Number.isNaN(age) || age > 120) {
      errors.dob = "Enter a valid date of birth.";
    } else if (age < 13) {
      errors.dob = "You must be at least 13 years old.";
    }
  }

  if (!form.gender) {
    errors.gender = "Gender is required.";
  }

  if (!form.country) {
    errors.country = "Country is required.";
  }

  const email = form.email.trim();

  if (!email) {
    errors.email = "Email is required.";
  } else if (!EMAIL_PATTERN.test(email)) {
    errors.email = "Enter a valid email address.";
  }

  const phone = form.phone.replace(/\D/g, "");

  if (!phone) {
    errors.phone = "Phone number is required.";
  } else if (form.country === "IN" && !PHONE_IN_PATTERN.test(phone)) {
    errors.phone = "Enter a valid 10-digit Indian mobile number.";
  } else if (form.country !== "IN" && (phone.length < 7 || phone.length > 15)) {
    errors.phone = "Enter a valid phone number.";
  }

  return errors;
};
