import GameZoneLogo from "./GameZoneLogo";

const CircuitMark = ({ className = "" }) => {
  return (
    <div className={`relative mx-auto ${className}`} aria-hidden="true">
      <svg className="absolute inset-0 h-full w-full" viewBox="0 0 120 120" fill="none">
        <path
          d="M18 60H4M116 60H102M60 18V4M60 116V102M28 28L16 16M92 28L104 16M28 92L16 104M92 92L104 104"
          stroke="#00D9E8"
          strokeWidth="1.4"
          strokeLinecap="round"
        />
        <path
          d="M22 48H10M110 72H98M48 22V10M72 110V98"
          stroke="#8B5CF6"
          strokeWidth="1.4"
          strokeLinecap="round"
        />
        <circle cx="16" cy="16" r="2" fill="#00D9E8" />
        <circle cx="104" cy="16" r="2" fill="#8B5CF6" />
        <circle cx="16" cy="104" r="2" fill="#8B5CF6" />
        <circle cx="104" cy="104" r="2" fill="#00D9E8" />
      </svg>
      <GameZoneLogo className="absolute top-1/2 left-1/2 h-[58%] w-[58%] -translate-x-1/2 -translate-y-1/2" />
    </div>
  );
};

export default CircuitMark;
