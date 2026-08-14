const TextField = ({
  id,
  type = "text",
  value,
  onChange,
  placeholder,
  leftIcon,
  rightSlot,
  autoComplete,
  className = "",
  ...props
}) => {
  return (
    <div className={`relative ${className}`}>
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
        className={`gz-input ${leftIcon ? "pl-11" : "px-4"} ${rightSlot ? "pr-11" : "pr-4"}`}
        {...props}
      />
      {rightSlot && (
        <div className="absolute top-1/2 right-3.5 -translate-y-1/2">{rightSlot}</div>
      )}
    </div>
  );
};

export default TextField;
