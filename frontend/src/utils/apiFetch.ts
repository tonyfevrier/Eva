/* Permet de renvoyer toutes les requêtes vers /api qui est soit renvoyé vers 
localhost en développement soit vers le nom de domaine en production suivant la configuration
du proxy (vite.config.js en dév) */

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "/api";

function toAbsoluteUrl(input: string): string {
    if (input.startsWith("http://") || input.startsWith("https://")) {
        return input;
    }

    const normalizedBase = API_BASE_URL.endsWith("/")
        ? API_BASE_URL.slice(0, -1)
        : API_BASE_URL;
    const normalizedPath = input.startsWith("/") ? input : `/${input}`;

    return `${normalizedBase}${normalizedPath}`;
}


export async function apiFetch(input: string, options: RequestInit = {}): Promise<Response> {
    const { credentials, ...requestInit } = options;
    const url = toAbsoluteUrl(input);

    return fetch(url, {
        ...requestInit,
        credentials: credentials ?? "include",
    });
}
