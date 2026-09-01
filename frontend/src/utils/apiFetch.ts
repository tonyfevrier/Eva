/* Permet de renvoyer toutes les requêtes vers /api qui est soit renvoyé vers 
localhost en développement soit vers le nom de domaine en production suivant la configuration
du proxy (vite.config.js en dév) */

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "/api";

/* Endpoints d'authentification : ne jamais tenter de les rejouer après un refresh */
const NO_RETRY_PATHS = ["/auth/login", "/auth/register", "/auth/refresh", "/auth/logout"];

export const SESSION_EXPIRED_EVENT = "auth:sessionExpired";

/* Une seule requête de refresh à la fois, même si plusieurs appels échouent simultanément */
let ongoingRefresh: Promise<boolean> | null = null;

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

function isRetryable(input: string, options: RequestInit): boolean {
  if (NO_RETRY_PATHS.some(path => input.includes(path))) {
    return false;
  }
  // Un body de type flux ne peut pas être relu lors du rejeu de la requête
  return !(options.body instanceof ReadableStream);
}

async function refreshAccessToken(): Promise<boolean> {
  const response = await fetch(toAbsoluteUrl("/auth/refresh"), {
    headers: { Accept: "application/json" },
    credentials: "include",
  });

  if (!response.ok) {
    localStorage.setItem("isAuthenticated", "false");
    localStorage.setItem("expirationTime", "0");
    window.dispatchEvent(new Event(SESSION_EXPIRED_EVENT));
    return false;
  }

  const data = await response.json();
  localStorage.setItem("expirationTime", String(Date.now() + data.accessExpiresIn));
  return true;
}

function refreshOnce(): Promise<boolean> {
  if (!ongoingRefresh) {
    ongoingRefresh = refreshAccessToken()
      .catch(() => false)
      .finally(() => { ongoingRefresh = null; });
  }
  return ongoingRefresh;
}

export async function apiFetch(input: string, options: RequestInit = {}): Promise<Response> {
  const { credentials, ...requestInit } = options;
  const url = toAbsoluteUrl(input);
  const init: RequestInit = { ...requestInit, credentials: credentials ?? "include" };

  const response = await fetch(url, init);

  /* Token d'accès expiré (page restée inactive) : on le renouvelle puis on rejoue
     la requête, de façon transparente pour l'appelant. */
  if (response.status === 401 && isRetryable(input, init)) {
    const refreshed = await refreshOnce();
    if (refreshed) {
      return fetch(url, init);
    }
  }

  return response;
}


