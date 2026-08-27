import { useCallback, useEffect, useRef, useState } from "react";

const FACEBOOK_APP_ID = import.meta.env.VITE_FACEBOOK_APP_ID;
const FACEBOOK_GRAPH_VERSION = import.meta.env.VITE_FACEBOOK_GRAPH_VERSION;
const FACEBOOK_SCRIPT_ID = "facebook-jssdk";

export const useFacebookAuth = ({ onSuccess, onError }) => {
  const [isReady, setIsReady] = useState(false);
  const isReadyRef = useRef(false);

  const onSuccessRef = useRef(onSuccess);
  const onErrorRef = useRef(onError);

  useEffect(() => {
    onSuccessRef.current = onSuccess;
    onErrorRef.current = onError;
  }, [onSuccess, onError]);

  useEffect(() => {
    if (!FACEBOOK_APP_ID) {
      console.error("Facebook App ID is not configured.");
      return;
    }

    if (!FACEBOOK_GRAPH_VERSION) {
      console.error("Facebook Graph API version is not configured.");
      return;
    }

    const markReady = () => {
      if (isReadyRef.current) {
        return;
      }

      isReadyRef.current = true;
      setIsReady(true);
    };

    const initializeFacebook = () => {
      if (!window.FB) {
        return false;
      }

      try {
        if (!window.__fbInitialized) {
          window.FB.init({
            appId: FACEBOOK_APP_ID,
            cookie: true,
            xfbml: true,
            version: FACEBOOK_GRAPH_VERSION,
          });
          window.__fbInitialized = true;
        }

        markReady();
        return true;
      } catch (error) {
        onErrorRef.current?.({
          error: "facebook_init_failed",
          details: error,
        });
        return false;
      }
    };

    window.fbAsyncInit = initializeFacebook;

    if (!document.getElementById("fb-root")) {
      const fbRoot = document.createElement("div");
      fbRoot.id = "fb-root";
      document.body.prepend(fbRoot);
    }

    if (!document.getElementById(FACEBOOK_SCRIPT_ID)) {
      const script = document.createElement("script");
      script.id = FACEBOOK_SCRIPT_ID;
      script.async = true;
      script.defer = true;
      script.src = "https://connect.facebook.net/en_US/sdk.js";
      script.onerror = () => {
        onErrorRef.current?.({
          error: "facebook_sdk_load_failed",
        });
      };
      document.body.appendChild(script);
    }

    if (initializeFacebook()) {
      return;
    }

    const interval = setInterval(() => {
      if (initializeFacebook()) {
        clearInterval(interval);
      }
    }, 100);

    return () => {
      clearInterval(interval);
    };
  }, []);

  const loginWithFacebook = useCallback((provider) => {
    if (!window.FB || !isReadyRef.current) {
      onErrorRef.current?.({
        error: "facebook_not_ready",
      });
      return;
    }

    window.FB.login(
      (response) => {
        if (response.status !== "connected") {
          onErrorRef.current?.({
            error: "facebook_login_cancelled",
            response,
          });
          return;
        }

        const accessToken = response.authResponse?.accessToken;

        if (!accessToken) {
          onErrorRef.current?.({
            error: "facebook_access_token_missing",
            response,
          });
          return;
        }

        onSuccessRef.current?.(provider, accessToken);
      },
      {
        scope: "public_profile,email",
      },
    );
  }, []);

  return {
    loginWithFacebook,
    isReady,
  };
};
