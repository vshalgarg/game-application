import { useEffect } from "react";
import { supabase } from "../utils/supabaseClient";

const useGameRealtime = ({ roomCode, onGameUpdate }) => {
  useEffect(() => {
    if (!roomCode) return;

    let channel;

    const initializeRealtime = async () => {
      // Step 1: Fetch current game state
      const { data, error } = await supabase
        .from("realtime_game_state")
        .select("*")
        .eq("room_code", roomCode)
        .single();

      if (error) {
        console.error("Failed to fetch game state:", error);
      } else {
        console.info("Initial Game State:", data);

        if (onGameUpdate) {
          onGameUpdate(data);
        }
      }

      // Step 2: Subscribe to realtime updates
      channel = supabase
      // useeffect duplicacy removal
        .channel(`game-${roomCode}-${Date.now()}`)  
        .on(
          "postgres_changes",
          {
            event: "*",
            schema: "public",
            table: "realtime_game_state",
            filter: `room_code=eq.${roomCode}`,
          },
          (payload) => {
            console.info("Realtime Update:", payload.new);

            if (onGameUpdate) {
              onGameUpdate(payload.new);
            }
          }
        )
        .subscribe((status) => {
          console.info("Realtime status:", status);
        });
    };

    initializeRealtime();

    return () => {
      if (channel) {
        supabase.removeChannel(channel);
      }
    };
  }, [roomCode]);
};

export default useGameRealtime;