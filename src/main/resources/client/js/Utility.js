class Utility {
    static Exists(f) {
        return typeof f !== "undefined";
    }


    static MakeWorldObjectJSON(uuid, position, rotation, additional = {}) {
        return {
            command: "object_update",
            parameters: {
                uuid: uuid,

                x: position[0],
                y: position[1],
                z: position[2],

                rotationX: rotation[0],
                rotationY: rotation[1],
                rotationZ: rotation[2],

                ...additional
            }
        };
    }
}