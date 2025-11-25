import { useState, useEffect } from "react";

interface FetchOptions extends RequestInit {
    headers?: Record<string, string>;
    /*correspond à { [key:string]: string }; 
    RequestInit est un type représentant ce qu'on peut passer à fetch */
}

export function useFetch<T>(url: string, options: FetchOptions = {}) {
    const [loading, setLoading] = useState<boolean>(true);
    const [data, setData] = useState<T | null>(null);
    const [error, setError] = useState<Error | null>(null);

    useEffect(() => {
        setLoading(true);
        setError(null);
        
        fetch(url, {
            ...options,
            headers: {
                'Accept': 'application/json, charset=UTF-8',
                ...(options.headers || {})
            }
        })
        .then(response => response.json())
        .then(data => setData(data)) 
        .catch(error => setError(error.message))
        .finally(() => setLoading(false));
    }, [url]);
    
    return { loading, data, error }
}

useFetch.displayName = 'useFetch';