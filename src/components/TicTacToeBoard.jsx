// import Square from "./Square";

// const TicTacToeBoard = ({ board = [], handleClick }) => {

//   return (

//     <div className="grid grid-cols-3 gap-4">

//       {(board).map((square, index) => (

//         <Square
//           key={index}
//           value={square}
//           onClick={() => handleClick(index)}
//         />

//       ))}

//     </div>

//   );
// };

// export default TicTacToeBoard;

import Square from "./Square";

const TicTacToeBoard = ({ board = [], handleClick }) => {
  const flattenedBoard = board.flat();

  return (
    <div className="grid grid-cols-3 gap-4">
      {flattenedBoard.map((square, index) => (
        <Square
          key={index}
          value={square}
          onClick={() => handleClick(index)}
        />
      ))}
    </div>
  );
};

export default TicTacToeBoard;