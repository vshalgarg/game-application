import { useEffect, useState } from "react";
import { getCountries } from "../services/authService"

const useCountries = () => {
  const [countries, setCountries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchCountries = async () => {
    setLoading(true);
    setError("");

    try {
      const list = await getCountries();
      setCountries(list);
    } catch (err) {
      setCountries([]);
      setError(err.message || "Unable to load countries.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCountries();
  }, []);

  return { countries, loading, error, refetch: fetchCountries };
};

export default useCountries;
