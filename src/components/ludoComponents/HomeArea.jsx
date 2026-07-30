import Token from "./Token";

const HomeArea = ({ color, tokens = [], selectedToken, setSelectedToken, handleTokenClick,}) => {
  return ( 
    <div
      className="
        w-full
        h-full
        bg-white
        rounded-lg
        border
        border-gray-500
        grid
        grid-cols-2
        grid-rows-2
        place-items-center
      "
    >
      {tokens.map((token) => (
        <div
          key={token.tokenId}
          className="w-full h-full flex items-center justify-center"
        >
          {token.state === "BASE" && (
            <Token
              color={color}
              selected={selectedToken === token.tokenId}
              onHandleClick={() => {
               console.log("Base token clicked");
                setSelectedToken(token.tokenId);
                handleTokenClick(token.tokenId);
  }}
/>
          )}
        </div>
      ))}
    </div>
  );
};

export default HomeArea;