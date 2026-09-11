const LabeledField = ({ id, label, required = false, children }) => {
  return (
    <div className="flex flex-col gap-1.5">
      <label htmlFor={id} className="pl-0.5 text-xs font-semibold tracking-wide text-gz-text-secondary">
        {label}
        {required ? <span className="text-gz-primary-cyan"> *</span> : null}
      </label>
      {children}
    </div>
  );
};

export default LabeledField;
