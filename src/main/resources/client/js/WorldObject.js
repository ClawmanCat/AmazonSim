class WorldObjectFactory extends ISocketUpdatableFactory {
    constructor (world) {
        super();

        this.world = world;
    }

    create(json) {
        let result = undefined;

        switch (json.parameters.type) {
            case "robot":           result = new Robot(this, json);             break;
            case "floor":           result = new Floor(this, json);             break;
            case "wall":            result = new Wall(this, json);              break;
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
        Utility.LoadTextureOrDefault("robot_side"  ), //RIGHT
        Utility.LoadTextureOrDefault("robot_side"  ), //LEFT
        Utility.LoadTextureOrDefault("robot_top"   ), //TOP
        Utility.LoadTextureOrDefault("robot_bottom"), //BOTTOM
        Utility.LoadTextureOrDefault("robot_front" ), //FRONT
        Utility.LoadTextureOrDefault("robot_front" )  //BACK
    ];

    makeMesh() {
        return new THREE.Mesh(Robot.Geometry, Robot.Materials);
    }
}


class Shelf extends IWorldObject {
    static ShelfGeometry  = new THREE.BoxGeometry(0.99, 78.0 / 32.0, 0.99);
    static ShelfMaterials = [
        Utility.LoadTextureOrDefault("shelf_side",   null, false, true), //RIGHT
        Utility.LoadTextureOrDefault("shelf_side",   null, false, true), //LEFT
        Utility.LoadTextureOrDefault("shelf_top",    null, false, true), //TOP
        Utility.LoadTextureOrDefault("shelf_bottom", null, false, true), //BOTTOM
        Utility.LoadTextureOrDefault("shelf_side",   null, false, true), //FRONT
        Utility.LoadTextureOrDefault("shelf_side",   null, false, true)
    ];

    static FloorGeometry  = new THREE.PlaneGeometry(0.99, 0.99);
    static FloorMaterials = Utility.LoadTextureOrDefault("shelf_inside");

    makeMesh() {
        let group = new THREE.Group();

        group.add(new THREE.Mesh(Shelf.ShelfGeometry, Shelf.ShelfMaterials));

        for (let i = 0; i < 5; ++i) {
            let h = ((11.0 + (13.0 * i)) / 32.0) - (78.0 / 32.0 / 2.0);

            let floor = new THREE.Mesh(Shelf.FloorGeometry, Shelf.FloorMaterials);
            floor.position.set(0, h, 0);
            floor.rotation.set(Math.PI / 2.0, 0, 0);

            group.add(floor);
        }

        return group;
    }
}


class Wall extends IWorldObject {
    static Geometry  = new THREE.BoxGeometry(1, 4, 1);
    static Materials = [
        Utility.LoadTextureOrDefault("wall_side"  ), //RIGHT
        Utility.LoadTextureOrDefault("wall_side"  ), //LEFT
        Utility.LoadTextureOrDefault("wall_top"   ), //TOP
        Utility.LoadTextureOrDefault("wall_top"   ), //BOTTOM
        Utility.LoadTextureOrDefault("wall_side"  ), //FRONT
        Utility.LoadTextureOrDefault("wall_side"  )  //BACK
    ];
    
    makeMesh() {
        return new THREE.Mesh(Wall.Geometry, Wall.Materials);
    }
}


class Floor extends IWorldObject {
    static Materials = Utility.LoadTextureOrDefault("floor");

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
    static Materials = Utility.LoadTextureOrDefault("unknown_item");

    constructor(world, json) {
        super(world, json);

        this.texture = json.parameters.texture;
    }

    makeMesh() {
        let materials = Utility.LoadTextureOrDefault(this.texture, Unknown.Materials);

        return new THREE.Mesh(Unknown.Geometry, materials);
    }
}