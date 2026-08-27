import { useCallback, useEffect, useRef, useState } from "react";

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;

export const useGoogleAuth = ({ onSuccess, onError }) => {
  const clientRef = useRef(null);
  const buttonHostRef = useRef(null);
  const [isReady, setIsReady] = useState(false);

  useEffect(() => {
    if (!GOOGLE_CLIENT_ID) {
      console.error("Google Client ID is not configured.");
      return;
    }

    const initializeGoogle = () => {
      if (!window.google?.accounts?.id) {
        return;
      }

      window.google.accounts.id.initialize({
        client_id: GOOGLE_CLIENT_ID,
        scope: "openid email profile",
        ux_mode: "popup",
        callback: (response) => {
          if (response.error) {
            onError?.(response);
            return;
          }

          onSuccess?.("google", response.credential);
        },
      });

      if (!buttonHostRef.current) {
        const host = document.createElement("div");
        host.style.position = "fixed";
        host.style.left = "-9999px";
        host.style.top = "0";
        host.setAttribute("aria-hidden", "true");
        document.body.appendChild(host);
        buttonHostRef.current = host;
      }

      buttonHostRef.current.innerHTML = "";
      window.google.accounts.id.renderButton(buttonHostRef.current, {
        type: "standard",
        size: "large",
      });

      clientRef.current = window.google.accounts.id;
      setIsReady(true);
    };

    if (window.google?.accounts?.id) {
      initializeGoogle();
      return;
    }

    const interval = setInterval(() => {
      if (window.google?.accounts?.id) {
        clearInterval(interval);
        initializeGoogle();
      }
    }, 100);

    return () => clearInterval(interval);
  }, [onSuccess, onError]);

  const loginWithGoogle = useCallback(
    () => {
      if (!clientRef.current) {
        onError?.({
          error: "google_not_ready",
        });
        return;
      }

      const googleButton =
        buttonHostRef.current?.querySelector("div[role='button']");

      if (googleButton) {
        googleButton.click();
        return;
      }

      clientRef.current.prompt();
    },
    [onError],
  );

  return {
    loginWithGoogle,
    isReady,
  };
};
