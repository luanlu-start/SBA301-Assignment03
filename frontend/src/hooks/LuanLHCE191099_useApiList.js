import { useCallback, useEffect, useState } from 'react';
import { errorText } from '../api/LuanLHCE191099_axiosClient.js';

export default function useApiList(load, search) {
  const [result, setResult] = useState(null);
  const [version, setVersion] = useState(0);
  const reload = useCallback(() => setVersion((value) => value + 1), []);

  useEffect(() => {
    const controller = new AbortController();
    load({ search, signal: controller.signal })
      .then(({ data }) => {
        if (!controller.signal.aborted)
          setResult({ load, search, version, data, error: '' });
      })
      .catch((error) => {
        if (!controller.signal.aborted)
          setResult({ load, search, version, data: [], error: errorText(error) });
      });
    return () => controller.abort();
  }, [load, search, version]);

  const isCurrent = result?.load === load && result.search === search && result.version === version;
  return {
    data: isCurrent ? result.data : [],
    error: isCurrent ? result.error : '',
    loading: !isCurrent,
    reload,
  };
}
