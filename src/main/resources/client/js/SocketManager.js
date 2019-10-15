export class ISocketUpdatable {
    getID()      { throw Error('Must implement abstract method getID.'); }
    update(json) { throw Error('Must implement abstract method update.'); }
}


export class SocketManager {
    constructor (listenerFactory) {
        this.listeners = new Map();
        this.listenerFactory = listenerFactory;

        // IntelliJ gebruikt verschillende ports voor Spring en HTTP -> hardcode de port tot er een betere oplossing is.
        this.socket = new WebSocket("ws://" + window.location.hostname + ":" + 8080 + "/connectToSimulation");

        this.socket.onmessage = function(event) {
            let json = JSON.parse(event.data);

            if (json.command === "object_update") {
                let listener = this.listeners.get(json.parameters.uuid);

                // Maak een nieuwe listener als deze nog niet bestaat, anders update de bestaande listener.
                if (listener === undefined) {
                    this.addListener(this.listenerFactory(json));
                } else {
                    listener.update(json);
                }
            }
        }
    }

    addListener(listener) {
        this.listeners.set(listener.getID(), listener);
    }

    removeListener(listener) {
        this.listeners.delete(listener.getID());
    }
}