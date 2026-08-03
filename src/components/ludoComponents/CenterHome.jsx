const clipPaths = {
  top: "polygon(50% 50%, 0 0, 100% 0)",
  right: "polygon(50% 50%, 100% 0, 100% 100%)",
  bottom: "polygon(50% 50%, 0 100%, 100% 100%)",
  left: "polygon(50% 50%, 0 0, 0 100%)",
};

const CenterHome = ({ centerArea, colors }) => {
  return (
    <div className="relative w-full h-full">
      {centerArea.triangles.map((triangle, index) => (
        <div
          key={index}
          // className="absolute -z-10"
          className="absolute inset-0"
          style={{
            backgroundColor: colors[triangle.colorIndex],
            clipPath: clipPaths[triangle.clip],
          }}
        />
      ))}
    </div>
  );
};

export default CenterHome;