(function(global){
    const root = global || window;
    const ns = (root.goog = root.goog || {});
    const appengine = (ns.appengine = ns.appengine || {});

    if(appengine.Channel) return;

    function backoff(attempt){
        return Math.min(2000, 100 + attempt * 200);
    }

    class ChannelSocket {
        constructor(token, config){
            this.token = token;
            this.config = Object.assign({}, config);
            this.closed = false;
            this.attempt = 0;
            if(typeof this.config.onopen === 'function'){
                setTimeout(() => { if(!this.closed) this.config.onopen(); }, 0);
            }
            this.poll();
        }

        poll(){
            if(this.closed) return;
            const url = `/channel?token=${encodeURIComponent(this.token)}`;
            fetch(url, { cache: 'no-store', credentials: 'same-origin' })
                .then(resp => {
                    if(this.closed) return;
                    if(resp.status === 200){
                        this.attempt = 0;
                        return resp.text().then(body => {
                            if(typeof this.config.onmessage === 'function'){
                                this.config.onmessage({ data: body });
                            }
                            this.poll();
                        });
                    }
                    if(resp.status === 204){
                        this.attempt = Math.max(0, this.attempt - 1);
                        setTimeout(() => this.poll(), backoff(this.attempt));
                        return;
                    }
                    throw new Error(`Unexpected status ${resp.status}`);
                })
                .catch(() => {
                    if(this.closed) return;
                    this.attempt += 1;
                    setTimeout(() => this.poll(), backoff(this.attempt));
                });
        }

        close(){
            this.closed = true;
        }
    }

    appengine.Channel = class {
        constructor(token){
            this.token = token;
        }
        open(config){
            return new ChannelSocket(this.token, config || {});
        }
    };
})(typeof window !== 'undefined' ? window : this);
