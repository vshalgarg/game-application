const HEXAGON_PATH =
  "M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z";

const HexBadge = ({ children, className = "" }) => {
  return (
    <span className={`relative inline-flex h-9 w-9 shrink-0 items-center justify-center ${className}`}>
      <svg
        className="absolute inset-0 h-full w-full"
        viewBox="0 0 24 24"
        fill="none"
        aria-hidden="true"
      >
        <path
          d={HEXAGON_PATH}
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
      </svg>
      <span className="relative z-10 flex items-center justify-center">{children}</span>
    </span>
  );
};

export default HexBadge;
