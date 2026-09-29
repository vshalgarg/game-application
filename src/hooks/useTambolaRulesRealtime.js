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

    let channel;
    let isMounted = true;

    const fetchRules = async () => {
      try {
        const { data, error } = await supabase
          .from("realtime_tambola_rules")
          .select("*")
          .eq("room_code", roomCode)
          .order("rule_order", { ascending: true });

        if (error) {
          console.error("[Tambola Rules] Fetch failed:", error);
          return;
        }

        console.log("ALL RULES FROM SUPABASE:", data);

        if (isMounted) {
          setRules(data || []);
        }
      } catch (error) {
        console.error("[Tambola Rules] Unexpected fetch error:", error);
      }
    };

    channel = supabase
      .channel(`tambola-rules-${roomCode}`)
      .on(
        "postgres_changes",
        {
          event: "*",
          schema: "public",
          table: "realtime_tambola_rules",
          filter: `room_code=eq.${roomCode}`,
        },
        async (payload) => {
          console.log("[Tambola Rules] Realtime event:", payload);

          await fetchRules();
        }
      )
      .subscribe(async (status) => {
        console.log(`[Tambola Rules] Subscription status for ${roomCode}:`, status);

        if (status === "SUBSCRIBED") {
          await fetchRules();

          if (isMounted) {
            setLoading(false);
          }
        }
      });

    return () => {
      isMounted = false;

      if (channel) {
        supabase.removeChannel(channel);
      }
    };
  }, [roomCode]);

  return {
    rules,
    loading,
  };
};

export default useTambolaRulesRealtime;