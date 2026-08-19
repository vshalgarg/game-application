import { useCallback, useEffect, useRef, useState } from "react";

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;

export const useGoogleAuth = ({ onSuccess, onError }) => {
  const clientRef = useRef(null);
  const providerRef = useRef(null);
  const [isReady, setIsReady] = useState(false);

  useEffect(() => {
    if (!GOOGLE_CLIENT_ID) {
      console.error("Google Client ID is not configured.");
      return;
    }

    const initializeGoogle = () => {
      if (!window.google?.accounts?.oauth2) {
        return;
      }

      clientRef.current = window.google.accounts.oauth2.initCodeClient({
        client_id: GOOGLE_CLIENT_ID,
        scope: "openid email profile",
        ux_mode: "popup",

        callback: (response) => {
          if (response.error) {
            onError?.(response);
            return;
          }

          onSuccess?.(providerRef.current, response.code);
        },
      });

      setIsReady(true);
    };

    if (window.google?.accounts?.oauth2) {
      initializeGoogle();
      return;
    }

    const interval = setInterval(() => {
      if (window.google?.accounts?.oauth2) {
        clearInterval(interval);
        initializeGoogle();
      }
    }, 100);

    return () => clearInterval(interval);
  }, [onSuccess, onError]);

  const loginWithGoogle = useCallback(
    (provider) => {
      if (!clientRef.current) {
        onError?.({
          error: "google_not_ready",
        });
        return;
      }
      providerRef.current = provider;
      clientRef.current.requestCode();
    },
    [onError],
  );

  return {
    loginWithGoogle,
    isReady,
  };
};
