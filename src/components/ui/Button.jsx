const Button = ({
  children,
  type = "button",
  variant = "primary",
  className = "",
  disabled = false,
  ...props
}) => {
  const variants = {
    primary: "gz-btn-primary",
    social: "gz-btn-social",
    nav: "gz-btn-nav",
  };

  return (
    <button
      type={type}
      disabled={disabled}
      className={`${variants[variant] || variants.primary} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
};

export default Button;
