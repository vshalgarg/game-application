// const LudoCell = ({ type, color, arrowDirection, arrowColor }) => {
//   const renderSymbol = () => {
//     switch (type) {
//       case "SC":
//         return (
//           <span className="text-gray-600 text-xs font-bold">
//             ★
//           </span>
//         );

//       case "SS":
//         return (
//           <span className="text-gray-600 text-xs font-bold">
//             ★
//           </span>
//         );

//       case "GP":
//         return 

//       case "GE":
//         return (
//           <span className="text-xl leading-none select-none"
//             style={{ color: arrowColor }}
//           >
//             {{
//               up: "↑",
//               down: "↓",
//               left: "←",
//               right: "→",
//             }[arrowDirection]}
//           </span>
//         );

//       case "G":
//         return 

//       case "S":
//         return null

//       case "N":
//         return null;

//       case null:
//       default:
//         return null;
//     }
//   };

//   return (
//     <div className="border border-gray-400 flex items-center justify-center box-border w-full h-full"
//       style={{backgroundColor: color,}}>
//       {renderSymbol()}
//     </div>
//   );
// };

// export default LudoCell;


const LudoCell = ({ type, color, arrowDirection, arrowColor, hasToken = false }) => {
  const renderSymbol = () => {
    switch (type) {
      case "SC":
      case "SS":
        return hasToken ? null : (
          <span className="text-gray-600 text-xs font-bold">
            ★
          </span>
        );

      case "GP":
        return 

      case "GE":
        return  hasToken ? null : (
          <span className="text-xl leading-none select-none"
            style={{ color: arrowColor }}
          >
            {{
              up: "↑",
              down: "↓",
              left: "←",
              right: "→",
            }[arrowDirection]}
          </span>
        );

      case "G":
        return 

      case "S":
        return null

      case "N":
        return null;

      case null:
      default:
        return null;
    }
  };

  return (
    <div className="border border-gray-400 flex items-center justify-center box-border w-full h-full"
      style={{backgroundColor: color,}}>
      {renderSymbol()}
    </div>
  );
};

export default LudoCell;

