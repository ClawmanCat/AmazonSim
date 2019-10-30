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


    static LoadTextureOrDefault(texture, defaultVal = null, flip = false) {
        if (texture === undefined || texture === null) return defaultVal;

        let textureData = new THREE.TextureLoader(THREE.DefaultLoadingManager).load("textures/" + texture + ".png");
        if (flip) textureData.flipY = false;
        textureData.minFilter = THREE.NearestFilter;
        textureData.magFilter = THREE.NearestFilter;

        return new THREE.MeshBasicMaterial({ map: textureData, side: THREE.DoubleSide, transparent: true });
    }
}