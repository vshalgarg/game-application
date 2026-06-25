const CenterHome = () => {
  return (
    <div className="relative w-full h-full">

      {/* Top Green Triangle */}
      <div
        className="
          absolute
          top-0
          left-0
          w-full
          h-full
          bg-green-500
        "
        style={{
          clipPath: "polygon(50% 50%, 0 0, 100% 0)",
        }}
      />

      {/* Right Yellow Triangle */}
      <div
        className="
          absolute
          top-0
          left-0
          w-full
          h-full
          bg-yellow-400
        "
        style={{
          clipPath: "polygon(50% 50%, 100% 0, 100% 100%)",
        }}
      />

      {/* Bottom Blue Triangle */}
      <div
        className="
          absolute
          top-0
          left-0
          w-full
          h-full
          bg-blue-500
        "
        style={{
          clipPath: "polygon(50% 50%, 0 100%, 100% 100%)",
        }}
      />

      {/* Left Red Triangle */}
      <div
        className="
          absolute
          top-0
          left-0
          w-full
          h-full
          bg-red-500
        "
        style={{
          clipPath: "polygon(50% 50%, 0 0, 0 100%)",
        }}
      />
    </div>
  );
};

export default CenterHome;