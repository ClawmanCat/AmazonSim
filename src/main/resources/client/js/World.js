class World {
    constructor () {
        // ThreeJS Renderer Setup
        this.scene = new THREE.Scene();

        this.renderer = new THREE.WebGLRenderer({ antialias: true });
        this.renderer.setPixelRatio(window.devicePixelRatio);
        this.renderer.setSize(window.innerWidth, window.innerHeight + 5);
        document.body.appendChild(this.renderer.domElement);

        this.camera = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 1, 1000);

        //this.controller = new THREE.PointerLockControls(this.camera, this.renderer.domElement);
        this.controller = new THREE.OrbitControls(this.camera, this.renderer.domElement);

        this.camera.position.z = 15;
        this.camera.position.y = 5;
        this.camera.position.x = 15;

        // Some camera controllers (e.g. PointerLockControls) don't have an update function,
        // so check if the update function exists before calling it.
        if (Utility.Exists(this.controller.update)) this.controller.update();

        let self = this;
        window.addEventListener(
            "resize", 
            () => {
                self.camera.aspect = window.innerWidth / window.innerHeight;
                self.camera.updateProjectionMatrix();
                self.renderer.setSize(window.innerWidth, window.innerHeight);
            }, 
            false
        );


        // Socket updater
        this.updater = new SocketManager(new WorldObjectFactory(this));


        // Basic world layout.
        // TODO: Load this from the server instead.
        let floor = new Floor(this, Utility.MakeWorldObjectJSON("fake-uuid-01", [ 15, 0, 15 ], [ Math.PI / 2.0, 0, 0 ]));
        let light = new AmbientLight(this, Utility.MakeWorldObjectJSON("fake-uuid-02", [ 0, 0, 0 ], [ 0, 0, 0 ], { intensity: 4, color: 0x404040 }));

        this.addObject(floor);
        this.addObject(light);


        // Render loop
        this.frameCount = 0;
        this.animate = () => {
            requestAnimationFrame(self.animate);
            if (Utility.Exists(self.controller.update)) self.controller.update();
            self.renderer.render(self.scene, self.camera);

            ++self.frameCount;
        };
    }

    addObject(object) {
        let mesh = object.mesh;
        mesh.name = object.getID();

        this.scene.add(mesh);
    }

    removeObject(object) {
        let mesh = this.scene.getObjectByName(object.getID());
        this.scene.remove(mesh);
    }

    getObjectMesh(id) {
        return this.scene.getObjectByName(id);
    }
}