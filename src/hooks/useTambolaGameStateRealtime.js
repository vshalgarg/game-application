import { useEffect, useState } from "react";
import { supabase } from "../utils/supabaseClient";

const useTambolaGameStateRealtime = (roomCode) => {
  const [gameState, setGameState] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!roomCode) {
      setGameState(null);
      setLoading(false);
      return;
    }

    let isMounted = true;

    const fetchInitialGameState = async () => {
      try {
        setLoading(true);

        const { data, error } = await supabase
          .from("realtime_tambola_game_state")
          .select("*")
          .eq("room_code", roomCode)
          .maybeSingle();

        if (error) {
          console.error("Error fetching initial Tambola game state:", error);

          if (isMounted) {
            setGameState(null);
          }

          return;
        }

        console.log("Initial Tambola Game State:", data);

        if (isMounted) {
          setGameState(data);
        }
      } catch (error) {
        console.error("Unexpected error fetching Tambola game state:", error);

        if (isMounted) {
          setGameState(null);
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };

    fetchInitialGameState();

    const channel = supabase
      .channel(`tambola-game-state-${roomCode}`)
      .on(
        "postgres_changes",
        {
          event: "*",
          schema: "public",
          table: "realtime_tambola_game_state",
          filter: `room_code=eq.${roomCode}`,
        },
        (payload) => {
          console.log("Tambola Game State Realtime Event:", payload);

          if (!isMounted) return;

          if (payload.eventType === "INSERT") {
            setGameState(payload.new);
          }

          if (payload.eventType === "UPDATE") {
            setGameState(payload.new);
          }

          if (payload.eventType === "DELETE") {
            setGameState(null);
          }
        }
      )
      .subscribe((status) => {
        console.log(`Tambola game state realtime status for room ${roomCode}:`, status);
      });

    return () => {
      isMounted = false;

      console.log(`Removing Tambola game state listener for room ${roomCode}`);

      supabase.removeChannel(channel);
    };
  }, [roomCode]);

  return {
    gameState,
    loading,
  };
};

export default useTambolaGameStateRealtime;