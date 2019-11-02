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


    static LoadTextureOrDefault(texture, shininess, defaultVal = null, flip = false, transparent = false, side = THREE.DoubleSide) {
        if (texture === undefined || texture === null) return defaultVal;

        let textureData = new THREE.TextureLoader(THREE.DefaultLoadingManager).load("textures/" + texture + ".png");
        if (flip) textureData.flipY = false;
        textureData.minFilter = THREE.NearestFilter;
        textureData.magFilter = THREE.NearestFilter;

        return new THREE.MeshPhongMaterial({ map: textureData, side: side, transparent: transparent, shininess: shininess });
    }


    static RandInt(max) {
        return Math.floor(Math.random() * max);
    }
}