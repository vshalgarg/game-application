const CHIEF_DROP_URL = "https://chief-drop.vercel.app/";

const ChiefDropFrame = () => {
  return (
    <iframe
      title="Chief Drop"
      src={CHIEF_DROP_URL}
      className="block h-full w-full border-0 bg-black"
    />
  );
};

export default ChiefDropFrame;
