const TextField = ({
  id,
  type = "text",
  value,
  onChange,
  placeholder,
  leftIcon,
  rightSlot,
  autoComplete,
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
          <span className="pointer-events-none absolute top-1/2 left-3.5 -translate-y-1/2 text-gz-icon">
            {leftIcon}
          </span>
        )}

        <input
          id={id}
          type={type}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          autoComplete={autoComplete}
          aria-invalid={hasError || undefined}
          aria-describedby={helperTextId}
          className={`gz-input ${leftIcon ? "pl-11" : "px-4"} ${rightSlot ? "pr-11" : "pr-4"} ${
            hasError ? "border-red-500 focus:border-red-500 focus:ring-red-500/40" : ""
          }`}
          {...props}
        />

        {rightSlot && (
          <div className="absolute top-1/2 right-3.5 -translate-y-1/2">{rightSlot}</div>
        )}
      </div>

      {helperText && (
        <p id={helperTextId} className="mt-1 text-xs text-red-400">
          {helperText}
        </p>
      )}
    </div>
  );
};

export default TextField;
