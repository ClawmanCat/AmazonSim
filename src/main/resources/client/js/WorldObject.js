import * as Three from './lib/three.min.js';
import {ISocketUpdatable} from "./SocketManager.js";


export class WorldObjectFactory {
    static target;

    static setTargetWorld(world) { WorldObjectFactory.target = world; }

    static Create(json) {
        let result = null;
        switch (json.parameters.type) {
            case "robot":         result = new Robot(json);             break;
            case "shelf":         result = new Shelf(json);             break;
            case "wall":          result = new Wall(json);              break;
            case 'floor':         result = new Floor(json);             break;
            case 'ambient_light': result = new AmbientLight(json);      break;
            default:              result = undefined;                   break;
        }

        WorldObjectFactory.target.addObject(result);
        return result;
    }
}


export class WorldObject extends ISocketUpdatable {
    constructor (json) {
        super();

        this.uuid = "";
        this.position = new Three.Vector3(0, 0, 0);
        this.rotation = new Three.Vector3(0, 0, 0);

        this.mesh = this.makeMesh();
        this.setPosition(this.position);
        this.setRotation(this.rotation);
    }

    makeMesh() {
        throw Error('Must implement abstract method makeMesh.');
    }

    update(json) {
        this.uuid = json.parameters.uuid;
        this.setPosition(new Three.Vector3(json.parameters.x, json.parameters.y, json.parameters.z));
        this.setRotation(new Three.Vector3(json.parameters.rotationX, json.parameters.rotationY, json.parameters.rotationZ));
    }

    setPosition(position) {
        this.position = position;
        this.mesh.position = position;
    }

    setRotation(rotation) {
        let r = new Three.Euler(rotation.x, rotation.y, rotation.z);

        this.rotation = rotation;
        this.mesh.rotation = r;
    }
}


export class Robot extends WorldObject {
    static Geometry  = new Three.BoxGeometry(0.9, 0.3, 0.9);
    static Materials = [
        new Three.MeshBasicMaterial({ map: new Three.TextureLoader(Three.DefaultLoadingManager).load("textures/robot_side.png" ),  side: Three.DoubleSide }), //RIGHT
        new Three.MeshBasicMaterial({ map: new Three.TextureLoader(Three.DefaultLoadingManager).load("textures/robot_side.png" ),  side: Three.DoubleSide }), //LEFT
        new Three.MeshBasicMaterial({ map: new Three.TextureLoader(Three.DefaultLoadingManager).load("textures/robot_top.png"  ),  side: Three.DoubleSide }), //TOP
        new Three.MeshBasicMaterial({ map: new Three.TextureLoader(Three.DefaultLoadingManager).load("textures/robot_bottom.png"), side: Three.DoubleSide }), //BOTTOM
        new Three.MeshBasicMaterial({ map: new Three.TextureLoader(Three.DefaultLoadingManager).load("textures/robot_front.png" ), side: Three.DoubleSide }), //FRONT
        new Three.MeshBasicMaterial({ map: new Three.TextureLoader(Three.DefaultLoadingManager).load("textures/robot_front.png" ), side: Three.DoubleSide }), //BACK
    ];

    makeMesh() {
        return new Three.Mesh(Robot.Geometry, Robot.Materials);
    }
}


export class Shelf extends WorldObject {
    makeMesh() {
        return undefined;
    }
}


export class Wall extends WorldObject {
    makeMesh() {
        return undefined;
    }
}


export class Floor extends WorldObject {
    static Geometry  = new Three.PlaneGeometry(30, 30, 32);
    static Materials = new Three.MeshBasicMaterial({ color: 0xfafafa, side: Three.DoubleSide });

    makeMesh() {
        return new Three.Mesh(Floor.Geometry, Floor.Materials);
    }
}


// TODO: Refactor WorldObject so this doesn't have to have a position / rotation.
export class AmbientLight extends WorldObject {
    makeMesh() {
        let light = new Three.AmbientLight(0x404040);
        light.intensity = 4;

        return light;
    }

    setPosition(position) {}
    setRotation(rotation) {}
}