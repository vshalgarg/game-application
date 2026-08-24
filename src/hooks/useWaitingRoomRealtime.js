import { useEffect, useState } from "react";
import { supabase } from "../utils/supabaseClient";

const useWaitingRoomRealtime = (roomCode) => {
  const [players, setPlayers] = useState([]);

  useEffect(() => {
    if (!roomCode) return;

    //  Fetch existing players initially
    const fetchPlayers = async () => {
    const { data, error } = await supabase
      .from("realtime_room_lobby")
      .select("*")    
      .eq("room_code", roomCode);

  if (error) {
    console.error("Fetch players error:", error);
    return;
  }

  console.info("Fetched Data:", data);

  setPlayers(data[0]?.players || []);
};

    fetchPlayers();

    //  Subscribe to realtime changes
    const channel = supabase
      .channel(`waiting-room-${roomCode}`)

      // Player joined
      .on(
        "postgres_changes",
        {
          event: "UPDATE",
          schema: "public",
          table: "realtime_room_lobby",
          filter: `room_code=eq.${roomCode}`,
        },
        (payload) => {
          console.info("Player Joined:", payload.new);

          setPlayers(payload.new.players || []);
        }
      )

      // Player left 
      .on(
        "postgres_changes",
        {
          event: "DELETE",
          schema: "public",
          table: "realtime_room_lobby",
          filter: `room_code=eq.${roomCode}`,
        },
        (payload) => {
          console.info("Player Left:", payload.old);

          setPlayers((prev) =>
            prev.filter(
              (player) => player.user_id !== payload.old.user_id
            )
          );
        }
      )

      .subscribe((status) => {
  console.info("Subscription Status:", status);
});

    return () => {
      supabase.removeChannel(channel);
    };
  }, [roomCode]);

  return { players };
};

export default useWaitingRoomRealtime;