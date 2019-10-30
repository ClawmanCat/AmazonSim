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
            case "shelf":           result = new Shelf(this, json);             break;
            default:                result = new Unknown(this, json);           break;
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

        if (this.mesh !== null) {
            this.mesh.position.x = position.x;
            this.mesh.position.y = position.y;
            this.mesh.position.z = position.z;
        }
    }

    setRotation(rotation) {
        this.rotation = rotation;

        if (this.mesh !== null) {
            this.mesh.rotation.x = rotation.x;
            this.mesh.rotation.y = rotation.y;
            this.mesh.rotation.z = rotation.z;
        }
    }

    update(json) {
        this.uuid = json.parameters.uuid;

        this.setPosition(new THREE.Vector3(json.parameters.x, json.parameters.y, json.parameters.z));
        this.setRotation(new THREE.Vector3(json.parameters.rotationX, json.parameters.rotationY, json.parameters.rotationZ));
    }

    getID()  {
        return this.uuid;
    }

    getMesh() {
        if (this.mesh === null) {
            this.mesh = this.makeMesh();

            this.setPosition(this.position);
            this.setRotation(this.rotation);
        }

        return this.mesh;
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


class Shelf extends IWorldObject {
    static Geometry  = new THREE.BoxGeometry(1, 2.3, 1);
    static Materials = [
        Utility.LoadTextureOrDefault("shelf_side"  ), //RIGHT
        Utility.LoadTextureOrDefault("shelf_side"  ), //LEFT
        Utility.LoadTextureOrDefault("shelf_top"   ), //TOP
        Utility.LoadTextureOrDefault("shelf_bottom"), //BOTTOM
        Utility.LoadTextureOrDefault("shelf_side"  ), //FRONT
        Utility.LoadTextureOrDefault("shelf_side"  )  //BACK
    ];

    makeMesh() {
        return new THREE.Mesh(Shelf.Geometry, Shelf.Materials);
    }
}


class AmbientLight extends IWorldObject {
    constructor (world, json) {
        super(world, json);
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
    static Materials = new THREE.MeshBasicMaterial({ color: 0x424242, side: THREE.DoubleSide });

    constructor (world, json) {
        super(world, json);
    }

    update(json) {
        this.w = json.parameters.w;
        this.h = json.parameters.h;
        this.texture = json.parameters.texture;

        super.update(json);
    }

    makeMesh() {
        let geometry = new THREE.PlaneGeometry(this.w, this.h, this.w, this.h);
        let materials = Utility.LoadTextureOrDefault(this.texture, Floor.Materials, true);

        return new THREE.Mesh(geometry, materials);
    }


}


class Unknown extends IWorldObject {
    static Geometry  = new THREE.BoxGeometry(1.0, 1.0, 1.0);
    static Materials = new THREE.MeshBasicMaterial({ map: new THREE.TextureLoader(THREE.DefaultLoadingManager).load("textures/unknown_item.png" ),  side: THREE.DoubleSide });

    constructor(world, json) {
        super(world, json);

        this.texture = json.parameters.texture;
    }

    makeMesh() {
        let materials = Utility.LoadTextureOrDefault(this.texture, Unknown.Materials);

        return new THREE.Mesh(Unknown.Geometry, materials);
    }
}