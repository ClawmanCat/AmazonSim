class WorldObjectFactory extends ISocketUpdatableFactory {
    constructor (world) {
        super();

        this.world = world;
    }

    create(json) {
        let result = undefined;

        switch (json.parameters.type) {
            case "robot":           result = new Robot(this.world, json);             break;
            case "floor":           result = new Floor(this.world, json);             break;
            case "wall":            result = new Wall(this.world, json);              break;
            case "shelf":           result = new Shelf(this.world, json);             break;
            case "light":           result = new Lamp(this.world, json);              break;
            default:                result = new Unknown(this.world, json);           break;
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
        Utility.LoadTextureOrDefault("robot_side",    0.75), //RIGHT
        Utility.LoadTextureOrDefault("robot_side",    0.75), //LEFT
        Utility.LoadTextureOrDefault("robot_top",     0.75), //TOP
        Utility.LoadTextureOrDefault("robot_bottom",  0.75), //BOTTOM
        Utility.LoadTextureOrDefault("robot_front",   0.75), //FRONT
        Utility.LoadTextureOrDefault("robot_front",   0.75)  //BACK
    ];

    makeMesh() {
        return new THREE.Mesh(Robot.Geometry, Robot.Materials);
    }
}


class Shelf extends IWorldObject {
    static ShelfGeometry  = new THREE.BoxGeometry(0.99, 78.0 / 32.0, 0.99);
    static ShelfMaterials = [
        Utility.LoadTextureOrDefault("shelf_side",   0.80, null, false, true), //RIGHT
        Utility.LoadTextureOrDefault("shelf_side",   0.80, null, false, true), //LEFT
        Utility.LoadTextureOrDefault("shelf_top",    0.80, null, false, true), //TOP
        Utility.LoadTextureOrDefault("shelf_bottom", 0.80, null, false, true), //BOTTOM
        Utility.LoadTextureOrDefault("shelf_side",   0.80, null, false, true), //FRONT
        Utility.LoadTextureOrDefault("shelf_side",   0.80, null, false, true)
    ];

    static FloorGeometry  = new THREE.PlaneGeometry(0.99, 0.99);
    static FloorMaterials = Utility.LoadTextureOrDefault("shelf_inside");

    static BigBoxGeometry  = new THREE.BoxGeometry(15.0 / 32.0, 8.0 / 32.0, 15.0 / 32.0);
    static BigBoxMaterials = [
        Utility.LoadTextureOrDefault("big_box_side", 0.0),
        Utility.LoadTextureOrDefault("big_box_side", 0.0),
        Utility.LoadTextureOrDefault("big_box_top",  0.0),
        Utility.LoadTextureOrDefault("big_box_top",  0.0),
        Utility.LoadTextureOrDefault("big_box_side", 0.0),
        Utility.LoadTextureOrDefault("big_box_side", 0.0)
    ];

    static SmallBoxGeometry  = new THREE.BoxGeometry(11.0 / 32.0, 5.0 / 32.0, 11.0 / 32.0);
    static SmallBoxMaterials = [
        Utility.LoadTextureOrDefault("small_box_side", 0.0),
        Utility.LoadTextureOrDefault("small_box_side", 0.0),
        Utility.LoadTextureOrDefault("small_box_top",  0.0),
        Utility.LoadTextureOrDefault("small_box_top",  0.0),
        Utility.LoadTextureOrDefault("small_box_side", 0.0),
        Utility.LoadTextureOrDefault("small_box_side", 0.0)
    ];

    constructor(world, json) {
        super(world, json);

        this.count = json.parameters.item_count;
        this.items = json.parameters.items;
    }

    update(json) {
        super.update(json);

        this.count = json.parameters.item_count;
        this.items = json.parameters.items;

        // Use .copy?
        if (this.mesh !== null) {
            this.world.removeObject(this.mesh);

            this.mesh = this.makeMesh();
            this.setPosition(this.position);
            this.setRotation(this.rotation);

            this.world.addObject(this.mesh);
        }
    }

    makeMesh() {
        let group = new THREE.Group();

        group.add(new THREE.Mesh(Shelf.ShelfGeometry, Shelf.ShelfMaterials));

        for (let i = 0; i < 5; ++i) {
            let h = ((11.0 + (13.0 * i)) / 32.0) - (78.0 / 32.0 / 2.0);

            let floor = new THREE.Mesh(Shelf.FloorGeometry, Shelf.FloorMaterials);
            floor.position.set(0, h, 0);
            floor.rotation.set(Math.PI / 2.0, 0, 0);

            group.add(floor);

            if (Math.floor(this.count / 2) > i) {
                let boxes = new THREE.Group();
                boxes.rotation.set(0, (Math.PI / 2.0) * Utility.RandInt(3), 0);

                let wbig = (15.0 / 32.0) / 2.0 - 0.5;
                let wsml = (11.0 / 32.0) / 2.0 - 0.5;
                let hbig = (8.0  / 32.0) / 2.0 + 0.0001;
                let hsml = (5.0  / 32.0) / 2.0 + 0.0001;

                let box1 = new THREE.Mesh(Shelf.BigBoxGeometry, Shelf.BigBoxMaterials);
                box1.position.set(3.0 / 32.0 + wbig, h + hbig, 3.0 / 32.0 + wbig);
                boxes.add(box1);

                let box2 = new THREE.Mesh(Shelf.SmallBoxGeometry, Shelf.SmallBoxMaterials);
                box2.position.set(18.0 / 32.0 + wsml, h + hsml, 3.0 / 32.0 + wsml);
                boxes.add(box2);

                let box3 = new THREE.Mesh(Shelf.SmallBoxGeometry, Shelf.SmallBoxMaterials);
                box3.position.set(18.0 / 32.0 + wsml, h + hsml, 14.0 / 32.0 + wsml);
                boxes.add(box3);

                let box4 = new THREE.Mesh(Shelf.SmallBoxGeometry, Shelf.SmallBoxMaterials);
                box4.position.set(3.0 / 32.0 + wsml, h + hsml, 18.0 / 32.0 + wsml);
                boxes.add(box4);

                group.add(boxes);
            }
        }

        return group;
    }
}


class Wall extends IWorldObject {
    static Geometry  = new THREE.BoxGeometry(1, 4, 1);
    static Materials = [
        Utility.LoadTextureOrDefault("wall_side", 0.1), //RIGHT
        Utility.LoadTextureOrDefault("wall_side", 0.1), //LEFT
        Utility.LoadTextureOrDefault("wall_top",  0.1), //TOP
        Utility.LoadTextureOrDefault("wall_top",  0.1), //BOTTOM
        Utility.LoadTextureOrDefault("wall_side", 0.1), //FRONT
        Utility.LoadTextureOrDefault("wall_side", 0.1)  //BACK
    ];
    
    makeMesh() {
        return new THREE.Mesh(Wall.Geometry, Wall.Materials);
    }
}


class Floor extends IWorldObject {
    static Materials = Utility.LoadTextureOrDefault("floor", 0.5);

    constructor (world, json) {
        super(world, json);
    }

    update(json) {
        super.update(json);

        this.w = json.parameters.w;
        this.h = json.parameters.h;
        this.texture = json.parameters.texture;

        super.update(json);
    }

    makeMesh() {
        let geometry = new THREE.PlaneGeometry(this.w, this.h, this.w, this.h);
        let materials = Utility.LoadTextureOrDefault(this.texture, 0.5, Floor.Materials, true);

        return new THREE.Mesh(geometry, materials);
    }
}


class Lamp extends IWorldObject {
    static LampGeometry  = new THREE.BoxGeometry(1, 8.0 / 32.0, 1);
    static LampMaterials = [
        Utility.LoadTextureOrDefault("light_side",   0.8), //RIGHT
        Utility.LoadTextureOrDefault("light_side",   0.8), //LEFT
        Utility.LoadTextureOrDefault("light_top",    0.8), //TOP
        Utility.LoadTextureOrDefault("light_bottom", 0.8), //BOTTOM
        Utility.LoadTextureOrDefault("light_side",   0.8), //FRONT
        Utility.LoadTextureOrDefault("light_side",   0.8)  //BACK
    ];
    static LampEmissive = [
        Utility.LoadTextureOrDefault("light_side_emissive",   0.8), //RIGHT
        Utility.LoadTextureOrDefault("light_side_emissive",   0.8), //LEFT
        Utility.LoadTextureOrDefault("light_top_emissive",    0.8), //TOP
        Utility.LoadTextureOrDefault("light_bottom_emissive", 0.8), //BOTTOM
        Utility.LoadTextureOrDefault("light_side_emissive",   0.8), //FRONT
        Utility.LoadTextureOrDefault("light_side_emissive",   0.8)  //BACK
    ];

    static HandleGeometry  = new THREE.BoxGeometry(18.0 / 32.0, 4.0 / 32.0, 4.0 / 32.0);
    static HandleMaterials = [
        Utility.LoadTextureOrDefault("handle_top",    0.8), //RIGHT
        Utility.LoadTextureOrDefault("handle_top",    0.8), //LEFT
        Utility.LoadTextureOrDefault("handle_side",   0.8), //TOP
        Utility.LoadTextureOrDefault("handle_side",   0.8), //BOTTOM
        Utility.LoadTextureOrDefault("handle_side",   0.8), //FRONT
        Utility.LoadTextureOrDefault("handle_side",   0.8)  //BACK
    ];

    constructor(world, json) {
        super(world, json);
    }

    makeMesh() {
        let group = new THREE.Group();

        let cover = new THREE.Mesh(Lamp.LampGeometry, Lamp.LampMaterials);
        cover.position.set(0, 0.5, 0.1);

        group.add(cover);

        let light = new THREE.PointLight(0xFFFFAA, 1, 100);
        light.position.set(0, 0.6, 0);
        //light.target = this.world.down;
        light.castShadow = true;
        group.add(light);

        let handle = new THREE.Mesh(Lamp.HandleGeometry, Lamp.HandleMaterials);
        handle.position.set(0, 0.5 + (6.0 / 32.0), -0.2);
        handle.rotation.set(0, (Math.PI / 2.0), 0);
        group.add(handle);

        return group;
    }
}


class Unknown extends IWorldObject {
    static Geometry  = new THREE.BoxGeometry(1.0, 1.0, 1.0);
    static Materials = Utility.LoadTextureOrDefault("unknown_item", 0.0);

    constructor(world, json) {
        super(world, json);

        this.texture = json.parameters.texture;
    }

    makeMesh() {
        let materials = Utility.LoadTextureOrDefault(this.texture, Unknown.Materials);

        return new THREE.Mesh(Unknown.Geometry, materials);
    }
}