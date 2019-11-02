class ISocketUpdatable {
    update(json) { throw Error("Must implement abstract method update."); }
    getID()      { throw Error("Must implement abstract method getID." ); }
}

class ISocketUpdatableFactory {
    create(json) { throw Error("Must implement abstract method create."); }
}


// Class that manages creating and updating objects through a socket connection.
// Objects are identified by their UUID. If a command is received to update an object that does not exist,
// the provided factory object is used to construct a new one.
// Factory object must extend ISocketUpdatableFactory, the objects it constructs must extend ISocketUpdatable.
class SocketManager {
    static CONNECTION_ADDRESS = "ws://" + window.location.hostname + ":" + 8080 + "/connectToSimulation";

    constructor(factory) {
        this.factory = factory;
        this.updatables = new Map();
        this.socket = new WebSocket(SocketManager.CONNECTION_ADDRESS);

        let self = this;
        this.decode = (json) => {
            if (json.command === "object_update") {
                let target = self.updatables.get(json.parameters.uuid);

                if (target === undefined) {
                    // Object does not yet exist => make a new one.
                    target = self.factory.create(json);

                    if (target !== null) {
                        self.addObject(target);
                        console.log("Created new object with ID " + target.getID());
                    }
                } else {
                    // Object already exists => update it.
                    target.update(json);
                    console.log("Updated object with ID " + target.getID());
                }
            } else if (json.command === "compound_update") {
                json.parameters.forEach(self.decode);
            } else {
                console.log("Unknown command: " + json.command);
            }
        };

        this.socket.onmessage = (event) => {
            console.log("Received message from server: " + event.data);

            let json = JSON.parse(event.data);
            self.decode(json);
        };
    }

    addObject(object) {
        this.updatables.set(object.getID(), object);
    }

    removeObject(id) {
        this.updatables.delete(id);
    }
}