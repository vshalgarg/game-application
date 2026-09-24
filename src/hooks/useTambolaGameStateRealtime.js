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

    setLoading(true);

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
          console.log(
            "Tambola Game State Realtime Event:",
            payload
          );

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
        console.log(
          `Tambola game state realtime status for room ${roomCode}:`,
          status
        );

        if (status === "SUBSCRIBED") {
          setLoading(false);
        }
      });

    return () => {
      console.log(
        `Removing Tambola game state listener for room ${roomCode}`
      );

      supabase.removeChannel(channel);
    };
  }, [roomCode]);

  return {
    gameState,
    loading,
  };
};

export default useTambolaGameStateRealtime;