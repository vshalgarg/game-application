const SelectField = ({
  id,
  value,
  onChange,
  options = [],
  leftIcon,
  className = "",
  ...props
}) => {
  return (
    <div className={`relative ${className}`}>
      {leftIcon && (
        <span className="pointer-events-none absolute top-1/2 left-3.5 -translate-y-1/2 text-gz-primary-cyan">
          {leftIcon}
        </span>
      )}

      <select
        id={id}
        value={value}
        onChange={onChange}
        className={`gz-select ${leftIcon ? "pl-11" : "pl-4"}`}
        {...props}
      >
        {options.map(({ label, value: optionValue }) => (
          <option key={optionValue || "default"} value={optionValue}>
            {label}
          </option>
        ))}
      </select>

      <span
        className="pointer-events-none absolute top-1/2 right-3.5 -translate-y-1/2 text-gz-primary-cyan"
        aria-hidden="true"
      >
        <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
          <path
            d="M4 6L8 10L12 6"
            stroke="currentColor"
            strokeWidth="1.8"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
        </svg>
      </span>
    </div>
  );
};

export default SelectField;
