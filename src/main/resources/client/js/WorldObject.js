class WorldObjectFactory extends ISocketUpdatableFactory {
    constructor (world) {
        super();

        this.world = world;
    }

    create(json) {
        let result = undefined;

        switch (json.parameters.type) {
            case "robot":           result = new Robot(this, json);             break;
            case "ambient_light":   result = new AmbientLight(this, json);      break;
            case "floor":           result = new Floor(this, json);             break;
        }

        this.world.addObject(result);
        return result;
    }
}


class IWorldObject extends ISocketUpdatable {
    constructor (world, json) {
        super();

        this.world    = world;
        this.uuid     = "UUID NOT SET!";
        this.position = new THREE.Vector3(0, 0, 0);
        this.rotation = new THREE.Vector3(0, 0, 0);
        this.mesh     = null;

        this.update(json);
    }

    makeMesh() { throw Error("Must implement abstract method makeMesh."); }

    setPosition(position) {
        this.position = position;

        // TODO: Calculate offset from ground instead of hardcoding it in server message.
        this.mesh.position.x = position.x;
        this.mesh.position.y = position.y;
        this.mesh.position.z = position.z;
    }

    setRotation(rotation) {
        this.rotation = rotation;

        this.mesh.rotation.x = rotation.x;
        this.mesh.rotation.y = rotation.y
        this.mesh.rotation.z = rotation.z;
    }

    update(json) {
        this.uuid = json.parameters.uuid;
        if (this.mesh === null) this.mesh = this.makeMesh();

        this.setPosition(new THREE.Vector3(json.parameters.x, json.parameters.y, json.parameters.z));
        this.setRotation(new THREE.Vector3(json.parameters.rotationX, json.parameters.rotationY, json.parameters.rotationZ));
    }

    getID()  {
        return this.uuid;
    }
}


class Robot extends IWorldObject {
    static Geometry  = new THREE.BoxGeometry(0.9, 0.3, 0.9);
    static Materials = [
        new THREE.MeshBasicMaterial({ map: new THREE.TextureLoader(THREE.DefaultLoadingManager).load("textures/robot_side.png" ),  side: THREE.DoubleSide }), //RIGHT
        new THREE.MeshBasicMaterial({ map: new THREE.TextureLoader(THREE.DefaultLoadingManager).load("textures/robot_side.png" ),  side: THREE.DoubleSide }), //LEFT
        new THREE.MeshBasicMaterial({ map: new THREE.TextureLoader(THREE.DefaultLoadingManager).load("textures/robot_top.png"  ),  side: THREE.DoubleSide }), //TOP
        new THREE.MeshBasicMaterial({ map: new THREE.TextureLoader(THREE.DefaultLoadingManager).load("textures/robot_bottom.png"), side: THREE.DoubleSide }), //BOTTOM
        new THREE.MeshBasicMaterial({ map: new THREE.TextureLoader(THREE.DefaultLoadingManager).load("textures/robot_front.png" ), side: THREE.DoubleSide }), //FRONT
        new THREE.MeshBasicMaterial({ map: new THREE.TextureLoader(THREE.DefaultLoadingManager).load("textures/robot_front.png" ), side: THREE.DoubleSide })  //BACK
    ];

    makeMesh() {
        return new THREE.Mesh(Robot.Geometry, Robot.Materials);
    }
}


class AmbientLight extends IWorldObject {
    constructor (world, json) {
        super(world, json);

        this.intensity = 1;
        this.color = 0xFFFFFF;
    }

    update(json) {
        this.intensity = json.parameters.intensity;
        this.color = json.parameters.color;

        super.update(json);
    }

    makeMesh() {
        let light = new THREE.AmbientLight(this.color);
        light.intensity = this.intensity;

        return light;
    }

    setPosition(position) {}
    setRotation(rotation) {}
}


class Floor extends IWorldObject {
    // TODO: Split floor into 1x1 tiles and allow multiple textures.
    static Geometry  = new THREE.PlaneGeometry(30, 30, 32);
    static Materials = new THREE.MeshBasicMaterial({ color: 0xFAFAFA, side: THREE.DoubleSide });

    makeMesh() {
        return new THREE.Mesh(Floor.Geometry, Floor.Materials);
    }
}