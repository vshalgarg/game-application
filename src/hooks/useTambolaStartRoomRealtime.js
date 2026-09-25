import { useEffect } from "react";
import { supabase } from "../utils/supabaseClient";

const useTambolaRoomRealtime = ({
  roomCode,
  onStartGame,
  onRoomUpdate,
}) => {
  useEffect(() => {
    if (!roomCode) return;

    const channel = supabase
      .channel(`tambola-game-room-${roomCode}`)
      .on(
        "postgres_changes",
        {
          event: "*",
          schema: "public",
          table: "realtime_tambola_game_state",
          filter: `room_code=eq.${roomCode}`,
        },
        (payload) => {
          console.info("TAMBOLA REALTIME PAYLOAD:", payload);

          const gameState = payload.new;

          console.info("TAMBOLA GAME STATE:", gameState);

          if (onRoomUpdate) {
            onRoomUpdate(gameState);
          }

          onStartGame?.(gameState);
        }
      )
      .subscribe((status) => {
        console.info(`TAMBOLA ROOM SUBSCRIBE STATUS for ${roomCode}:`,status);
      });

    return () => {
      console.info(`Removing Tambola room realtime listener for ${roomCode}`);

      supabase.removeChannel(channel);
    };
  }, [roomCode, onStartGame, onRoomUpdate]);
};

export default useTambolaRoomRealtime;