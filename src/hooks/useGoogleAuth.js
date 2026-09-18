import { useCallback, useEffect, useRef, useState } from "react";

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;

export const useGoogleAuth = ({ onSuccess, onError }) => {
  const codeClientRef = useRef(null);
  const [isReady, setIsReady] = useState(false);
  const onSuccessRef = useRef(onSuccess);
  const onErrorRef = useRef(onError);

  useEffect(() => {
    onSuccessRef.current = onSuccess;
    onErrorRef.current = onError;
  }, [onSuccess, onError]);

  useEffect(() => {
    if (!GOOGLE_CLIENT_ID) {
      console.error("Google Client ID is not configured.");
      return undefined;
    }

    const initializeGoogle = () => {
      if (!window.google?.accounts?.oauth2?.initCodeClient) {
        return false;
      }

      codeClientRef.current = window.google.accounts.oauth2.initCodeClient({
        client_id: GOOGLE_CLIENT_ID,
        scope: "openid email profile",
        ux_mode: "popup",
        callback: (response) => {
          if (response.error) {
            onErrorRef.current?.(response);
            return;
          }

          if (!response.code) {
            onErrorRef.current?.({ error: "google_auth_code_missing" });
            return;
          }

          onSuccessRef.current?.("google", response.code);
        },
        error_callback: (error) => {
          if (error?.type === "popup_closed" || error?.type === "popup_failed_to_open") {
            onErrorRef.current?.({ error: "google_login_cancelled", errorDetail: error });
            return;
          }

          onErrorRef.current?.(error);
        },
      });

      setIsReady(true);
      return true;
    };

    if (initializeGoogle()) {
      return undefined;
    }

    const interval = setInterval(() => {
      if (initializeGoogle()) {
        clearInterval(interval);
      }
    }, 100);

    return () => clearInterval(interval);
  }, []);

  const loginWithGoogle = useCallback(() => {
    if (!codeClientRef.current) {
      onErrorRef.current?.({ error: "google_not_ready" });
      return;
    }

    codeClientRef.current.requestCode();
  }, []);

  return {
    loginWithGoogle,
    isReady,
  };
};
