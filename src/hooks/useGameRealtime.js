import { useEffect } from "react";
import { supabase } from "../utils/supabaseClient";

const useGameRealtime = ({ roomCode, onGameUpdate }) => {
  useEffect(() => {
    if (!roomCode) return;

    // let channel;

    const initializeRealtime = async () => {
      // STEP 1: Fetch current game state
      const { data, error } = await supabase
        .from("realtime_game_state")
        .select("*")
        .eq("room_code", roomCode)
        .single();

      if (error) {
        console.error("Failed to fetch game state:", error);
      } else {
        console.log("Initial Game State:", data);

        if (onGameUpdate) {
          onGameUpdate(data);
        }
      }

      // STEP 2: Subscribe to realtime updates
      const channel = supabase
        .channel(`game-${roomCode}-${Date.now()}`) // useeffect runs this twice hence it shows duplicacy  
        // .channel(`game-${roomCode}`)  
        .on(
          "postgres_changes",
          {
            event: "*",
            schema: "public",
            table: "realtime_game_state",
            filter: `room_code=eq.${roomCode}`,
          },
          (payload) => {
            console.log("Realtime Update:", payload.new);

            if (onGameUpdate) {
              onGameUpdate(payload.new);
            }
          }
        )
        .subscribe((status) => {
          console.log("Realtime status:", status);
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