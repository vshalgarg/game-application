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
          <span className="pointer-events-none absolute inset-y-0 left-3.5 flex items-center text-gz-icon">
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
          <div className="absolute inset-y-0 right-3.5 flex items-center [&_button]:inline-flex [&_button]:items-center [&_button]:leading-none [&_svg]:block">
            {rightSlot}
          </div>
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
