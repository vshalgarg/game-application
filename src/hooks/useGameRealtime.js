import { useEffect } from "react";
import { supabase } from "../utils/supabaseClient";

const useGameRealtime = ({ roomCode, onGameUpdate }) => {
  useEffect(() => {
    if (!roomCode) return;

    const channel = supabase
      .channel(`game-${roomCode}`)
      .on(
        "postgres_changes",
        {
          event: "*",
          schema: "public",
          table: "realtime_game_state",
          filter: `room_code=eq.${roomCode}`,
        },
        (payload) => {
          const updated = payload.new;

          console.log("Game Realtime:", updated);

          if (onGameUpdate) {
            onGameUpdate(updated);
          }
        }
      )
      .subscribe((status) => {
  console.log("Realtime status:", status);
});

    return () => {
      supabase.removeChannel(channel);
    };
  }, [roomCode]);
};

export default useGameRealtime;