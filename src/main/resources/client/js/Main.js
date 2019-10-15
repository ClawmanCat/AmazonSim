import {SocketManager} from "./SocketManager.js";
import {WorldObjectFactory} from "./WorldObject.js";
import {World} from "./World.js";

export class Main {
    static Main() {
        console.log("Initializing simulation...");

        let world  = new World();
        WorldObjectFactory.setTargetWorld(world);

        let socket = new SocketManager(WorldObjectFactory.Create);

        console.log("Initialization complete.");
    }
}

Main.Main();