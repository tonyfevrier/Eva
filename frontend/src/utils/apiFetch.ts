const API_BASE_URL = "http://localhost:9000";
const REFRESH_PATH = "/auth/refresh";

let refreshPromise: Promise<boolean> | null = null;

type ApiFetchOptions = RequestInit & {
    skipAuthRefresh?: boolean;
};

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

function isRefreshRequest(url: string): boolean {
    return url.endsWith(REFRESH_PATH);
}

async function refreshAccessToken(): Promise<boolean> {
    if (!refreshPromise) {
        refreshPromise = (async () => {
            try {
                const response = await fetch(toAbsoluteUrl(REFRESH_PATH), {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json",
                    },
                    credentials: "include",
                });

                return response.ok;
            } catch {
                return false;
            }
        })();
    }

    const didRefreshSucceed = await refreshPromise;
    refreshPromise = null;

    return didRefreshSucceed;
}

export async function apiFetch(input: string, options: ApiFetchOptions = {}): Promise<Response> {
    const { skipAuthRefresh = false, credentials, ...requestInit } = options;
    const url = toAbsoluteUrl(input);
    const shouldSkipRefresh = skipAuthRefresh || isRefreshRequest(url);

    const response = await fetch(url, {
        ...requestInit,
        credentials: credentials ?? "include",
    });

    if (response.status !== 401 || shouldSkipRefresh) {
        return response;
    }

    const didRefreshSucceed = await refreshAccessToken();
    if (!didRefreshSucceed) {
        return response;
    }

    return fetch(url, {
        ...requestInit,
        credentials: credentials ?? "include",
    });
}
