import { boardLayout } from "../../data/boardLayout";
import LudoCell from "./LudoCell";
import CenterHome from "./CenterHome";
import HomeArea from "./HomeArea";

const LudoBoard = () => {
  return (
    <div className="relative w-[450px] h-[450px]">
    <div
      className="
        w-[450px]
        h-[450px]
        grid
        grid-cols-15
        grid-rows-15
      "
    >
      {boardLayout.flat().map((cell, index) => (
        <LudoCell
          key={index}
          type={cell}
        />
      ))}
    </div>

    {/* Center Triangle Area */}
      <div
        className="absolute"
        style={{
            left: "calc(40% + 1px)",
            top: "calc(40% + 1px)",
            width: "calc(20% - 2px)",
            height: "calc(20% - 2px)",
        }}
      >
        <CenterHome />


      </div>
              <div
  className="absolute"
  style={{
    left: "6.66%",
    top: "6.66%",
    width: "26.66%",
    height: "26.66%",
  }}
>
  <HomeArea color="#ef4444" />
</div>

<div
  className="absolute"
  style={{
    right: "6.66%",
    top: "6.66%",
    width: "26.66%",
    height: "26.66%",
  }}
>
  <HomeArea color="#22c55e" />
</div>

<div
  className="absolute"
  style={{
    left: "6.66%",
    bottom: "6.66%",
    width: "26.66%",
    height: "26.66%",
  }}
>
  <HomeArea color="#3b82f6" />
</div>

<div
  className="absolute"
  style={{
    right: "6.66%",
    bottom: "6.66%",
    width: "26.66%",
    height: "26.66%",
  }}
>
  <HomeArea color="#facc15" />
</div>

    </div>
  );
};

export default LudoBoard;