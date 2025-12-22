
export abstract class Fetch {
    protected url: string;
    protected credentials : 'include'|'omit';
    protected method: string;

    constructor(url:string, credentials: 'include'|'omit', method: string){
        this.url = url;
        this.credentials = credentials;
        this.method = method;
    }

    abstract sendRequest(): Promise<Response|Error>;
}

export class FetchWithBody extends Fetch {
    private body: string;

    constructor(url: string, method: string, credentials: 'include'|'omit', body: string){
        super(url, credentials, method);
        this.body = body;
    }

    async sendRequest(): Promise<Response|Error> {
        return fetch(this.url, {
            method: this.method,
            headers:{
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            credentials: this.credentials,
            body: this.body })
            .then(response => {
                return response;
            })
            .catch(error => {
                return error;
        });
    }
}

export class FetchWithoutBody extends Fetch {

    constructor(url: string, method: string, credentials: 'include'|'omit'){
        super(url, credentials, method);
    }

    async sendRequest(): Promise<Response|Error> {
        return fetch(this.url, {
            method: this.method,
            headers:{
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            credentials: this.credentials
            })
            .then(response => {
                return response;
            })
            .catch(error => {
                return error;
        });
    }
} 