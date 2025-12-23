
abstract class Fetch {
    protected credentials : 'include'|'omit';
    protected method: string;

    constructor(credentials: 'include'|'omit', method: string){
        this.credentials = credentials;
        this.method = method;
    }

    abstract sendRequest(url: string): Promise<Response|Error>;
}

export class FetchWithBody extends Fetch {
    private body: string;

    constructor(method: string, credentials: 'include'|'omit', body: string){
        super(credentials, method);
        this.body = body;
    }

    async sendRequest(url: string): Promise<Response|Error> {
        return fetch(url, {
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

    constructor(method: string, credentials: 'include'|'omit'){
        super(credentials, method);
    }

    async sendRequest(url: string): Promise<Response|Error> {
        return fetch(url, {
            method: this.method,
            headers: {
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