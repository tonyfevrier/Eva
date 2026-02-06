import { useState, useEffect } from "react";

interface FetchOptions extends RequestInit {
    headers?: Record<string, string>;
    /*correspond à { [key:string]: string }; 
    RequestInit est un type représentant ce qu'on peut passer à fetch */
}

export function useFetch<T>(url: string, credentials: RequestCredentials = 'include', options: FetchOptions = {}) {
    const [loading, setLoading] = useState<boolean>(true);
    const [data, setData] = useState<T | null>(null);
    const [error, setError] = useState<Error | null>(null);

    useEffect(() => {
        setLoading(true);
        setError(null);

        fetch(url, {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                ...(options.headers || {})
            },
            credentials
        })
        .then(response => response.json())
        .then(data => setData(data)) 
        .catch(error => setError(error.message))
        .finally(() => setLoading(false));
    }, []);
    
    return { loading, data, error }
}

useFetch.displayName = 'useFetch';