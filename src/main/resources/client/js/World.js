import * as Three from './lib/three.min.js';
import * as PointerLockControls from './lib/PointerLockControls.min.js';
import {AmbientLight, Floor} from './WorldObject.js';


export class World {
    constructor() {
        this.objects = [];

        // Scene & renderer setup
        this.scene = new Three.Scene();

        this.renderer = new Three.WebGLRenderer({ antialias: true });
        this.renderer.setPixelRatio(window.devicePixelRatio);
        this.renderer.setSize(window.innerWidth, window.innerHeight + 5);

        document.getElementById("RendererTarget").replaceWith(this.renderer.domElement);

        // Camera setup
        this.camera = new Three.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 1, 1000);
        this.cameraController = new PointerLockControls(this.camera, this.renderer.domElement);

        this.camera.position = new Three.Vector3(15, 5, 15);

        // Update camera on window resize.
        window.addEventListener(
            'resize',
            function(){
                this.camera.aspect = window.innerWidth / window.innerHeight;
                this.camera.updateProjectionMatrix();
                this.renderer.setSize(window.innerWidth, window.innerHeight);
            },
            false
        );

        // Basic world layout
        // TODO: Load this from server instead of creating it here. (And get *actual* UUIDs)
        this.addObject(new Floor({ uuid: "fake-uuid-0001", position: new Three.Vector3(15, 0, 15), rotation: new Three.Vector3(Math.PI / 2.0, 0, 0) }));
        this.addObject(new AmbientLight({ uuid: "fake-uuid-0002", position: new Three.Vector3(0, 0, 0), rotation: new Three.Vector3(0, 0, 0) }));

        this.render();
    }


    addObject(obj) {
        let mesh = obj.makeMesh();

        this.scene.add(mesh);
        this.objects.push([obj, mesh.uuid]);
    }


    removeObject(obj) {
        let elem = this.objects.filter(x => x[0] === obj)[0];

        this.scene.remove(this.scene.getObjectByProperty("uuid", elem[1]));
        this.objects = this.objects.filter(x => x !== elem);
    }


    getObject(uuid) {
        let results = this.objects.filter(x => x[0].uuid === uuid);
        return (results.length > 0) ? results[0][0] : undefined;
    }


    render() {
        requestAnimationFrame(this.render);
        this.renderer.render(this.scene, this.camera);
    }
}