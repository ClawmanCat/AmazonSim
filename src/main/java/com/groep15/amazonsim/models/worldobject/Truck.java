package com.groep15.amazonsim.models.worldobject;

import com.groep15.amazonsim.base.App;
import com.groep15.amazonsim.controllers.wms.ShippingReceivingManager;
import com.groep15.amazonsim.models.World;
import com.groep15.amazonsim.models.ai.*;
import com.groep15.amazonsim.utility.Vec2i;
import org.json.simple.JSONObject;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Truck extends Object3D implements IWorldActor {
    public static final int DOOR_OPEN_TIME      = 60;
    public static final int TRUCK_ITEM_CAPACITY = 64;

    private enum Task { SHIPPING, RECEIVING }

    private Random random = new Random();

    private Task task;
    private int distance, interval;
    private double doorAngle;
    private boolean visible;
    private IWorldAction action;

    public Truck(World world) {
        super(world);

        this.task      = Task.SHIPPING;
        this.distance  = 30;
        this.interval  = 5000;
        this.doorAngle = 0;
        this.visible   = true;
        this.action    = null;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject o = super.toJSON();

        o.put("door_angle", this.doorAngle);
        o.put("visible", this.visible);

        return o;
    }

    @Override
    public void fromJSON(JSONObject o) {
        super.fromJSON(o);

        if (o.get("properties") != null) {
            JSONObject params = (JSONObject) o.get("properties");

            this.distance = (int) Math.round((params.get("distance") == null) ? this.distance : (Double) params.get("distance"));
            this.interval = (int) Math.round((params.get("interval") == null) ? this.interval : (Double) params.get("interval"));

            this.task = (params.get("task") == null) ? this.task : (params.get("task").equals("shipping") ? Task.SHIPPING : Task.RECEIVING);
        }
    }

    @Override
    public boolean update() {
        if (this.action == null) this.updateAction();
        return this.action.progress(this);
    }

    @Override
    public double getSpeed() {
        return 0.2;
    }

    @Override
    public void setAction(IWorldAction action) {
        this.action = action;
    }

    @Override
    public IWorldAction getAction() {
        return action;
    }

    @Override
    public void grab(Object3D object) {
        throw new UnsupportedOperationException("This action is not supported.");
    }

    @Override
    public void release() {
        throw new UnsupportedOperationException("This action is not supported.");
    }

    @Override
    public Object3D getHeldObject() {
        throw new UnsupportedOperationException("This action is not supported.");
    }

    private void updateAction() {
        Vec2i currpos = new Vec2i(this.getPosition().x, this.getPosition().z);
        Vec2i destpos = new Vec2i(currpos.x, currpos.y + distance);

        AtomicInteger i = new AtomicInteger(0);
        AtomicReference<ShippingReceivingManager.IToken> token = new AtomicReference<>();

        this.action = new ActionLoop(() -> new ActionCompound(
                // Go to shipping or receiving point.
                new ActionRunCommand(() -> { this.visible = true; }),
                new ActionGotoUnrestricted(currpos, destpos),
                // Open doors.
                new ActionLoop(
                        () -> new ActionRunCommand(() -> {
                            this.doorAngle += (Math.PI / 2.0) / DOOR_OPEN_TIME;
                            i.set(i.get() + 1);
                        }),
                        () -> i.get() >= DOOR_OPEN_TIME
                ),
                // Generate requests & await completion.
                new ActionRunCommand(() -> {
                    ShippingReceivingManager sr = App.Controller.getSRManager();

                    token.set((this.task == Task.RECEIVING)
                            ? sr.receiveObjects(random.nextInt(TRUCK_ITEM_CAPACITY))
                            : sr.shipObjects(random.nextInt(Math.min(sr.getItemCount(), TRUCK_ITEM_CAPACITY)))
                    );
                }),
                new ActionLoop(
                        () -> new ActionRunCommand(() -> { }),
                        () -> token.get().completed()
                ),
                // Close doors.
                new ActionLoop(
                        () -> new ActionRunCommand(() -> {
                            this.doorAngle -= (Math.PI / 2.0) / DOOR_OPEN_TIME;
                            i.set(i.get() - 1);
                        }),
                        () -> i.get() == 0
                ),
                // Go back.
                new ActionGotoUnrestricted(destpos, currpos),
                new ActionRunCommand(() -> { this.visible = false; }),
                // Wait.
                new ActionRunCommand(() -> {}, this.interval)
        ));

        // Make the timings of shipping and receiving trucks be offset from each other.
        if (this.task.equals(Task.SHIPPING)) {
            this.action = new ActionCompound(
                    new ActionRunCommand(() -> {}, this.interval / 2),
                    this.action
            );
        }
    }
}
