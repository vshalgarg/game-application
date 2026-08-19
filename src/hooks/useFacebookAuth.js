import { useCallback, useEffect, useRef, useState } from "react";

const FACEBOOK_APP_ID = import.meta.env.VITE_FACEBOOK_APP_ID;
const FACEBOOK_GRAPH_VERSION = import.meta.env.VITE_FACEBOOK_GRAPH_VERSION;

export const useFacebookAuth = ({ onSuccess, onError }) => {
  const [isReady, setIsReady] = useState(false);

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

    const initializeFacebook = () => {
      if (!window.FB) {
        return;
      }

      window.FB.init({
        appId: FACEBOOK_APP_ID,
        cookie: true,
        xfbml: true,
        version: FACEBOOK_GRAPH_VERSION,
      });

      setIsReady(true);
    };

    if (window.FB) {
      initializeFacebook();
      return;
    }

    window.fbAsyncInit = initializeFacebook;

    const scriptId = "facebook-jssdk";

    if (!document.getElementById(scriptId)) {
      const script = document.createElement("script");

      script.id = scriptId;
      script.async = true;
      script.defer = true;
      script.crossOrigin = "anonymous";
      script.src = "https://connect.facebook.net/en_US/sdk.js";

      document.body.appendChild(script);
    }

    return () => {
      if (window.fbAsyncInit === initializeFacebook) {
        window.fbAsyncInit = undefined;
      }
    };
  }, []);

  const loginWithFacebook = useCallback(
    (provider) => {
      if (!window.FB || !isReady) {
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
    },
    [isReady],
  );

  return {
    loginWithFacebook,
    isReady,
  };
};
