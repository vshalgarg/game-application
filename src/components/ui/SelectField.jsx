const SelectField = ({
  id,
  value,
  onChange,
  options = [],
  placeholder,
  leftIcon,
  error = false,
  helperText,
  className = "",
  ...props
}) => {
  const hasError = Boolean(error);
  const helperTextId = helperText ? `${id}-helper-text` : undefined;

  return (
    <div className={className}>
      <div className="relative">
        {leftIcon && (
          <span className="pointer-events-none absolute top-1/2 left-3.5 -translate-y-1/2 text-gz-primary-cyan">
            {leftIcon}
          </span>
        )}

        <select
          id={id}
          value={value}
          onChange={onChange}
          aria-invalid={hasError || undefined}
          aria-describedby={helperTextId}
          className={`gz-select ${leftIcon ? "pl-11" : "pl-4"} ${
            hasError ? "border-red-500 focus:border-red-500 focus:ring-red-500/40" : ""
          }`}
          {...props}
        >
          {placeholder ? (
            <option value="" disabled>
              {placeholder}
            </option>
          ) : null}

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

      {helperText && (
        <p id={helperTextId} className="mt-1 text-xs text-red-400">
          {helperText}
        </p>
      )}
    </div>
  );
};

export default SelectField;
