import { useEffect } from "react";
import { supabase } from "../utils/supabaseClient";

const useRoomRealtime = ({ roomCode, onStartGame, onRoomUpdate }) => {
  useEffect(() => {
    console.log("ROOM REALTIME HOOK RUNNING");
    console.log("ROOM CODE:", roomCode);
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
          console.log("REALTIME PAYLOAD:", payload);
          const gameState = payload.new;
          console.log("GAME STATE:", gameState);
          if (onRoomUpdate) {
            onRoomUpdate(gameState);
          }
          console.log("CALLING NAVIGATION");

          onStartGame?.(gameState);
}
      )
      .subscribe((status) => {
        console.log("SUBSCRIBE STATUS:", status);
});

    return () => {
      supabase.removeChannel(channel);
    };
  }, [roomCode]);
};

export default useRoomRealtime;