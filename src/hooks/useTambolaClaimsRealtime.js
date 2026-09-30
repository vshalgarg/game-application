import { useEffect, useState } from "react";
import { supabase } from "../utils/supabaseClient";

const useTambolaClaimsRealtime = (roomCode, playerId) => {
  const [claims, setClaims] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!roomCode) {
      setClaims([]);
      setLoading(false);
      return;
    }

    let channel;

    const fetchClaims = async () => {
      try {
        setLoading(true);
        setError(null);

        const { data, error } = await supabase
          .from("tambola_claims")
          .select("*")
          .eq("room_code", roomCode)
          .order("claim_id", { ascending: true });

        if (error) {
          console.error("Error fetching Tambola claims:", error);
          setError(error);
          setClaims([]);
          return;
        }

        setClaims(data || []);
      } catch (err) {
        console.error("Unexpected error fetching Tambola claims:", err);
        setError(err);
        setClaims([]);
      } finally {
        setLoading(false);
      }
    };

    fetchClaims();

    channel = supabase
      .channel(`tambola-claims-${roomCode}`)
      .on(
        "postgres_changes",
        {
          event: "*",
          schema: "public",
          table: "tambola_claims",
          filter: `room_code=eq.${roomCode}`,
        },
        (payload) => {
          console.log("Tambola claim realtime update:", payload);

          const newClaim = payload.new;
          const oldClaim = payload.old;

          if (payload.eventType === "INSERT" && newClaim) {
            setClaims((prev) => {
              const alreadyExists = prev.some(
                (claim) => claim.claim_id === newClaim.claim_id
              );

              if (alreadyExists) {
                return prev;
              }
              return [...prev, newClaim];
            });
          }

          if (payload.eventType === "UPDATE" && newClaim) {
            setClaims((prev) =>
              prev.map((claim) =>
                claim.claim_id === newClaim.claim_id
                  ? newClaim
                  : claim
              )
            );
          }

          if (payload.eventType === "DELETE" && oldClaim) {
            setClaims((prev) =>
              prev.filter(
                (claim) => claim.claim_id !== oldClaim.claim_id
              )
            );
          }
        }
      )
      .subscribe((status) => {
        console.log("Tambola claims realtime status:", status);
      });

    return () => {
      if (channel) {
        supabase.removeChannel(channel);
      }
    };
  }, [roomCode]);

  // Claims belonging to the current player
  const myClaims = playerId
    ? claims.filter(
        (claim) => String(claim.player_id) === String(playerId)
      )
    : [];

  return {
    claims,
    myClaims,
    loading,
    error,
  };
};

export default useTambolaClaimsRealtime;