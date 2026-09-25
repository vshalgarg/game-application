import { useEffect, useState } from "react";
import { supabase } from "../utils/supabaseClient";

const useTambolaTicketRealtime = (roomCode, playerId) => {
  const [ticket, setTicket] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!roomCode || !playerId) {
      setTicket(null);
      setLoading(false);
      return;
    }

    let channel;

    const fetchTicket = async () => {
      try {
        setLoading(true);
        setError(null);

        const { data, error } = await supabase
          .from("tambola_tickets")
          .select("*")
          .eq("room_code", roomCode)
          .eq("player_id", playerId)
          .maybeSingle();

        if (error) {
          console.error("Error fetching Tambola ticket:", error);
          setError(error);
          setTicket(null);
          return;
        }

        setTicket(data);
      } catch (err) {
        console.error("Unexpected error fetching ticket:", err);
        setError(err);
        setTicket(null);
      } finally {
        setLoading(false);
      }
    };

    fetchTicket();

    channel = supabase
      .channel(`tambola-ticket-${roomCode}-${playerId}`)
      .on(
        "postgres_changes",
        {
          event: "*",
          schema: "public",
          table: "tambola_tickets",
          filter: `room_code=eq.${roomCode}`,
        },
        (payload) => {
          console.log("Ticket Payload:", payload);

          const newRow = payload.new;
          const oldRow = payload.old;

          // insert or update
          if ((payload.eventType === "INSERT" || payload.eventType === "UPDATE") && newRow?.player_id === playerId) {
            setTicket(newRow);
          }

          // delete
          if (payload.eventType === "DELETE" && oldRow?.player_id === playerId) {
            setTicket(null);
          }
        }
      )
      .subscribe((status) => {
        console.log("Ticket Realtime Status:", status);
      });

    return () => {
      if (channel) {
        supabase.removeChannel(channel);
      }
    };
  }, [roomCode, playerId]);

  return {
    ticket,
    loading,
    error,
  };
};

export default useTambolaTicketRealtime;