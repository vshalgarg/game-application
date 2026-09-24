import { useEffect, useState } from "react";
import { supabase } from "../utils/supabaseClient";

const useTambolaRulesRealtime = (roomCode) => {
  const [rules, setRules] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!roomCode) {
      setRules([]);
      setLoading(false);
      return;
    }

    setLoading(true);

    const channel = supabase
      .channel(`tambola-rules-${roomCode}`)
      .on(
        "postgres_changes",
        {
          event: "*",
          schema: "public",
          table: "realtime_tambola_rules",
          filter: `room_code=eq.${roomCode}`,
        },
        (payload) => {
          console.log("Tambola Rules Realtime Event:", payload);

          if (payload.eventType === "INSERT") {
            setRules((prev) => [...prev, payload.new]);
          }

          if (payload.eventType === "UPDATE") {
            setRules((prev) =>
              prev.map((rule) =>
                rule.id === payload.new.id ? payload.new : rule
              )
            );
          }

          if (payload.eventType === "DELETE") {
            setRules((prev) =>
              prev.filter((rule) => rule.id !== payload.old.id)
            );
          }
        }
      )
      .subscribe((status) => {
        console.log(
          `Tambola rules realtime status for room ${roomCode}:`,
          status
        );

        if (status === "SUBSCRIBED") {
            console.log(
      `[Tambola Rules] Successfully subscribed for room: ${roomCode}`
    );
          setLoading(false);
        }
      });

    return () => {
      console.log(`Removing Tambola rules listener for room ${roomCode}`);

      supabase.removeChannel(channel);
    };
  }, [roomCode]);

  return {
    rules,
    loading,
  };
};

export default useTambolaRulesRealtime;