import { useLayoutEffect } from "react";

const useBackExitGuard = (onBack) => {
  useLayoutEffect(() => {
    window.history.pushState({ gzExitGuard: true }, "", window.location.href);

    const onPopState = () => {
      window.history.pushState({ gzExitGuard: true }, "", window.location.href);
      onBack();
    };

    window.addEventListener("popstate", onPopState);
    return () => window.removeEventListener("popstate", onPopState);
  }, [onBack]);
};

export default useBackExitGuard;
