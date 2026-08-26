import { useEffect } from "react";
import { supabase } from "../utils/supabaseClient";

const useRoomRealtime = ({ roomCode, onStartGame, onRoomUpdate }) => {
  useEffect(() => {
    if (!roomCode) return;

    const channel = supabase
      .channel(`game-room-${roomCode}`)
      .on(
        "postgres_changes",
        {
          event: "*",
          schema: "public", 
          table: "realtime_game_state",
          filter: `room_code=eq.${roomCode}`,
        },
        (payload) => {
          console.info("REALTIME PAYLOAD:", payload);
          const gameState = payload.new;
          console.info("GAME STATE:", gameState);
          if (onRoomUpdate) {
            onRoomUpdate(gameState);
          }
          onStartGame?.(gameState);
}
      )
      .subscribe((status) => {
        console.info("SUBSCRIBE STATUS:", status);
});

    return () => {
      supabase.removeChannel(channel);
    };
  }, [roomCode]);
};

export default useRoomRealtime;