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
            case "truck":           result = new Truck(this.world, json);             break;
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
        Utility.LoadTextureOrDefault("robot_side",    70), //RIGHT
        Utility.LoadTextureOrDefault("robot_side",    70), //LEFT
        Utility.LoadTextureOrDefault("robot_top",     70), //TOP
        Utility.LoadTextureOrDefault("robot_bottom",  70), //BOTTOM
        Utility.LoadTextureOrDefault("robot_front",   70), //FRONT
        Utility.LoadTextureOrDefault("robot_front",   70)  //BACK
    ];

    makeMesh() {
        return new THREE.Mesh(Robot.Geometry, Robot.Materials);
    }
}


class Shelf extends IWorldObject {
    static ShelfGeometry  = new THREE.BoxGeometry(0.99, 78.0 / 32.0, 0.99);
    static ShelfMaterials = [
        Utility.LoadTextureOrDefault("shelf_side",   90, null, false, true), //RIGHT
        Utility.LoadTextureOrDefault("shelf_side",   90, null, false, true), //LEFT
        Utility.LoadTextureOrDefault("shelf_top",    90, null, false, true), //TOP
        Utility.LoadTextureOrDefault("shelf_bottom", 90, null, false, true), //BOTTOM
        Utility.LoadTextureOrDefault("shelf_side",   90, null, false, true), //FRONT
        Utility.LoadTextureOrDefault("shelf_side",   90, null, false, true)
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
        this.boxes = [null, null, null, null, null];
        this.boxesAdded = [false, false, false, false, false];
    }

    update(json) {
        super.update(json);

        this.count = json.parameters.item_count;
        if (this.boxes === null || this.boxes === undefined) return;

        for (let i = 0; i < 5; ++i) {
            if (this.count >  i && !this.boxesAdded[i]) this.mesh.add(this.boxes[i]);
            if (this.count <= i &&  this.boxesAdded[i]) this.mesh.remove(this.boxes[i]);
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

            if (this.count > i) {
                group.add(boxes);
                this.boxesAdded[i] = true;
            }

            this.boxes[i] = boxes;
        }

        return group;
    }
}


class Wall extends IWorldObject {
    static Geometry  = new THREE.BoxGeometry(1, 4, 1);
    static Materials = [
        Utility.LoadTextureOrDefault("wall_side", 10), //RIGHT
        Utility.LoadTextureOrDefault("wall_side", 10), //LEFT
        Utility.LoadTextureOrDefault("wall_top",  10), //TOP
        Utility.LoadTextureOrDefault("wall_top",  10), //BOTTOM
        Utility.LoadTextureOrDefault("wall_side", 10), //FRONT
        Utility.LoadTextureOrDefault("wall_side", 10)  //BACK
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
        Utility.LoadTextureOrDefault("light_side",   90), //RIGHT
        Utility.LoadTextureOrDefault("light_side",   90), //LEFT
        Utility.LoadTextureOrDefault("light_top",    90), //TOP
        Utility.LoadTextureOrDefault("light_bottom", 90), //BOTTOM
        Utility.LoadTextureOrDefault("light_side",   90), //FRONT
        Utility.LoadTextureOrDefault("light_side",   90)  //BACK
    ];
    static LampEmissive = [
        Utility.LoadTextureOrDefault("light_side_emissive",   90), //RIGHT
        Utility.LoadTextureOrDefault("light_side_emissive",   90), //LEFT
        Utility.LoadTextureOrDefault("light_top_emissive",    90), //TOP
        Utility.LoadTextureOrDefault("light_bottom_emissive", 90), //BOTTOM
        Utility.LoadTextureOrDefault("light_side_emissive",   90), //FRONT
        Utility.LoadTextureOrDefault("light_side_emissive",   90)  //BACK
    ];

    static HandleGeometry  = new THREE.BoxGeometry(18.0 / 32.0, 4.0 / 32.0, 4.0 / 32.0);
    static HandleMaterials = [
        Utility.LoadTextureOrDefault("handle_top",    90), //RIGHT
        Utility.LoadTextureOrDefault("handle_top",    90), //LEFT
        Utility.LoadTextureOrDefault("handle_side",   90), //TOP
        Utility.LoadTextureOrDefault("handle_side",   90), //BOTTOM
        Utility.LoadTextureOrDefault("handle_side",   90), //FRONT
        Utility.LoadTextureOrDefault("handle_side",   90)  //BACK
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


class Truck extends IWorldObject {
    static BodyGeometry  = new THREE.BoxGeometry(128.0 / 32.0, 135.0 / 32.0, 288.0 / 32.0);
    static BodyMaterials = [
        Utility.LoadTextureOrDefault("truck_side",        90, null, false, true), //RIGHT
        Utility.LoadTextureOrDefault("truck_side_flip",   90, null, false, true), //LEFT
        Utility.LoadTextureOrDefault("truck_top",         90, null, false, false), //TOP
        Utility.LoadTextureOrDefault("truck_bottom",      90, null, false, true), //BOTTOM
        Utility.LoadTextureOrDefault("truck_front",       90, null, false, true), //FRONT
        Utility.LoadTextureOrDefault("truck_inside_back", 90, null, false, true)  //BACK
    ];

    static InnerGeometry  = new THREE.BoxGeometry(128.0 / 32.0 * 0.999, 124.0 / 32.0 * 0.999, 288.0 / 32.0 * 0.999);
    static InnerMaterials = [
        Utility.LoadTextureOrDefault("truck_top",         90, null, false, false), //RIGHT
        Utility.LoadTextureOrDefault("truck_top",         90, null, false, false), //LEFT
        Utility.LoadTextureOrDefault("truck_top",         90, null, false, false), //TOP
        Utility.LoadTextureOrDefault("truck_top",         90, null, false, false), //BOTTOM
        Utility.LoadTextureOrDefault("truck_inside_back", 90, null, false, true),  //FRONT
        Utility.LoadTextureOrDefault("truck_inside_back", 90, null, false, true)   //BACK
    ];

    static FenderGeometry  = new THREE.PlaneGeometry(10.0 / 32.0, 128.0 / 32.0);
    static FenderMaterials = Utility.LoadTextureOrDefault("truck_back_bottom");

    static DoorGeometry   = new THREE.PlaneGeometry(64.0 / 32.0, 123.0 / 32.0);
    static LeftMaterials  = Utility.LoadTextureOrDefault("truck_back_left");
    static RightMaterials = Utility.LoadTextureOrDefault("truck_back_right");

    static WheelGeometry  = new THREE.CylinderGeometry(1, 1, 12.0 / 32.0, 64);
    static WheelMaterials = [
        Utility.LoadTextureOrDefault("truck_wheel_side", 0),
        Utility.LoadTextureOrDefault("truck_wheel", 0),
        Utility.LoadTextureOrDefault("truck_wheel", 0)
    ];

    static UndercarriageGeometry  = new THREE.BoxGeometry(128.0 / 32.0 * 0.9995, 1 * 0.9995, 160.0 / 32.0 * 0.9995);
    static UndercarriageMaterials = [
        Utility.LoadTextureOrDefault("truck_bottom_side",    90), //RIGHT
        Utility.LoadTextureOrDefault("truck_bottom_side",    90), //LEFT
        Utility.LoadTextureOrDefault("truck_bottom_bottom",  90), //TOP
        Utility.LoadTextureOrDefault("truck_bottom_bottom",  90), //BOTTOM
        Utility.LoadTextureOrDefault("truck_bottom_front",   90), //FRONT
        Utility.LoadTextureOrDefault("truck_bottom_front",   90)  //BACK
    ];

    static AxleGeometry  = new THREE.BoxGeometry(128.0 / 32.0, 16.0 / 32.0, 16.0 / 32.0);
    static AxleMaterials = Utility.LoadTextureOrDefault("truck_axle", 90);

    static LCockpitGeometry  = new THREE.BoxGeometry(128.0 / 32.0, 71.0 / 32.0, 128.0 / 32.0);
    static LCockpitMaterials = [
        Utility.LoadTextureOrDefault("truck_cockpit_side_flip",90, null, false, true), //RIGHT
        Utility.LoadTextureOrDefault("truck_cockpit_side",     90, null, false, true), //LEFT
        Utility.LoadTextureOrDefault("truck_cockpit_hood",     90), //TOP
        Utility.LoadTextureOrDefault("truck_cockpit_bottom",   90, null, false, true), //BOTTOM
        Utility.LoadTextureOrDefault("truck_cockpit_front",    90, null, false, true), //FRONT
        Utility.LoadTextureOrDefault("truck_cockpit_back",     90, null, false, true)  //BACK
    ];

    static WindshieldGeometry  = new PrismGeometry([new THREE.Vector2(48.0 / 32.0, 0), new THREE.Vector2(75.0 / 32.0, 0), new THREE.Vector2(48.0 / 32.0, 64.0 / 32.0)], 128.0 / 32.0);
    static WindshieldMaterials = Utility.LoadTextureOrDefault("truck_windshield", 100);

    static UCockpitGeometry  = new THREE.BoxGeometry(128.0 / 32.0, 64.0 / 32.0, 48.0 / 32.0);
    static UCockpitMaterials = [
        Utility.LoadTextureOrDefault("truck_cockpit_side_window", 90), //RIGHT
        Utility.LoadTextureOrDefault("truck_cockpit_side_window", 90), //LEFT
        Utility.LoadTextureOrDefault("truck_cockpit_roof",        90), //TOP
        Utility.LoadTextureOrDefault("truck_cockpit_roof",        90), //BOTTOM
        Utility.LoadTextureOrDefault("truck_cockpit_back_window", 90), //FRONT
        Utility.LoadTextureOrDefault("truck_cockpit_back_window", 90)  //BACK
    ];

    static FrontUndercarriageGeometry  = new THREE.BoxGeometry(128.0 / 32.0 * 0.999, 1, 96.0 / 32.0 * 0.999);
    static FrontUndercarriageMaterials = [
        Utility.LoadTextureOrDefault("truck_cockpit_bottom_side",   90), //RIGHT
        Utility.LoadTextureOrDefault("truck_cockpit_bottom_side",   90), //LEFT
        Utility.LoadTextureOrDefault("truck_cockpit_bottom_bottom", 90), //TOP
        Utility.LoadTextureOrDefault("truck_cockpit_bottom_bottom", 90), //BOTTOM
        Utility.LoadTextureOrDefault("truck_bottom_front",          90), //FRONT
        Utility.LoadTextureOrDefault("truck_bottom_front",          90)  //BACK
    ];

    constructor(world, json) {
        super(world, json);

        this.opencount = json.parameters.opencount;

        this.left = null;
        this.right = null;
        this.wheels = [];
    }

    update(json) {
        super.update(json);

        //this.opencount = json.parameters.opencount;

        if (this.wheels === undefined) return;
        for (let i = 0; i < this.wheels.length; ++i) {
            this.wheels[i].rotation.set(2 * json.parameters.z, 0, Math.PI / 2.0);
        }

        this.left.rotation.set(0, Math.PI, 0);
    }

    makeMesh() {
        let group = new THREE.Group();

        let outer = new THREE.Mesh(Truck.BodyGeometry, Truck.BodyMaterials);
        group.add(outer);

        let inner = new THREE.Mesh(Truck.InnerGeometry, Truck.InnerMaterials);
        inner.position.set(0, 11.0 / 32.0 / 2.0, 0);
        group.add(inner);

        let fender = new THREE.Mesh(Truck.FenderGeometry, Truck.FenderMaterials);
        fender.position.set(0, -123.0 / 32.0 / 2.0, -288.0 / 32.0 / 2.0);
        fender.rotation.set(0, 0, Math.PI / 2.0);
        group.add(fender);

        let leftDoor = new THREE.Mesh(Truck.DoorGeometry, Truck.LeftMaterials);
        leftDoor.position.set(64.0 / 32.0 / 2.0, 10.0 / 32.0 / 2.0, -288.0 / 32.0 / 2.0);
        leftDoor.rotation.set(0, Math.PI, 0);
        group.add(leftDoor);
        this.left = leftDoor;

        let rightDoor = new THREE.Mesh(Truck.DoorGeometry, Truck.RightMaterials);
        rightDoor.position.set(-64.0 / 32.0 / 2.0, 10.0 / 32.0 / 2.0, -288.0 / 32.0 / 2.0);
        rightDoor.rotation.set(0, Math.PI, 0);
        group.add(rightDoor);
        this.right = rightDoor;

        let dx = 70.0 / 32.0; let dy = -2.65; let dz = -0.2; let ddz = -2.3, ddz2 = 7.75;
        let wheelpos = [
            new THREE.Vector3(-dx, dy, dz), new THREE.Vector3(-dx, dy, dz + ddz),
            new THREE.Vector3( dx, dy, dz), new THREE.Vector3( dx, dy, dz + ddz),
            new THREE.Vector3(-dx, dy, dz + ddz2), new THREE.Vector3(dx, dy, dz +ddz2)];
        for (let i = 0; i < 6; ++i) {
            let wheel = new THREE.Mesh(Truck.WheelGeometry, Truck.WheelMaterials);
            wheel.rotation.set(0, 0, Math.PI / 2.0);
            wheel.position.set(wheelpos[i].x, wheelpos[i].y, wheelpos[i].z);

            group.add(wheel);
            this.wheels.push(wheel);
        }

        let undercarriage = new THREE.Mesh(Truck.UndercarriageGeometry, Truck.UndercarriageMaterials);
        undercarriage.position.set(0, -2, -1.4);
        group.add(undercarriage);

        let axle1 = new THREE.Mesh(Truck.AxleGeometry, Truck.AxleMaterials);
        axle1.position.set(0, dy, dz);
        group.add(axle1);

        let axle2 = new THREE.Mesh(Truck.AxleGeometry, Truck.AxleMaterials);
        axle2.position.set(0, dy, dz + ddz);
        group.add(axle2);

        let axle3 = new THREE.Mesh(Truck.AxleGeometry, Truck.AxleMaterials);
        axle3.position.set(0, dy, dz + ddz2);
        group.add(axle3);

        let lowerCockpit = new THREE.Mesh(Truck.LCockpitGeometry, Truck.LCockpitMaterials);
        lowerCockpit.position.set(0, -1, 6.9);
        group.add(lowerCockpit);

        let windshield = new THREE.Mesh(Truck.WindshieldGeometry, Truck.WindshieldMaterials);
        windshield.rotation.set(0, Math.PI * 1.5, 0);
        windshield.position.set(128.0 / 32.0 / 2, 0.11, 4.9);
        group.add(windshield);

        let upperCockpit = new THREE.Mesh(Truck.UCockpitGeometry, Truck.UCockpitMaterials);
        upperCockpit.position.set(0, 1.11, 5.65);
        group.add(upperCockpit);

        let frontUndercarriage = new THREE.Mesh(Truck.FrontUndercarriageGeometry, Truck.FrontUndercarriageMaterials);
        frontUndercarriage.position.set(0, -2, 7);
        group.add(frontUndercarriage);

        let connector = new THREE.Mesh(Lamp.HandleGeometry, Lamp.HandleMaterials);
        connector.rotation.set(0, Math.PI / 2.0, 0);
        connector.position.set(0, -1.7, 4.8);
        connector.scale.set(2, 2, 2);
        group.add(connector);

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