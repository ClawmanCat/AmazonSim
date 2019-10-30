package com.groep15.amazonsim.wms;

import com.groep15.amazonsim.ai.ActionTransportObject;
import com.groep15.amazonsim.models.Robot;
import com.groep15.amazonsim.models.Shelf;
import com.groep15.amazonsim.models.World;
import com.groep15.amazonsim.utility.Square2i;
import com.groep15.amazonsim.utility.Vec2i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// Generate item shipping and receiving requests for the robots to complete.
public class ShippingReceivingManager {
    private static class SRRequest {
        enum Type { SHIPPING, RECEIVING }

        public Type type;
        public Shelf shelf;
        public List<WarehouseItem> items;

        public SRRequest(Type type, Shelf shelf, List<WarehouseItem> items) {
            this.type = type;
            this.shelf = shelf;
            this.items = items;
        }
    }

    private ObjectTracker tracker;
    private WarehouseItemFactory factory;
    private List<Robot> idle, busy;
    private List<SRRequest> requests;
    private Square2i shipping, receiving;
    private World world;

    public ShippingReceivingManager(World world, ObjectTracker tracker, WarehouseItemFactory factory, List<Robot> robots, Square2i shippingArea, Square2i receivingArea) {
        this.world = world;
        this.tracker = tracker;
        this.factory = factory;
        this.idle = robots;
        this.busy = new ArrayList<>();
        this.shipping = shippingArea;
        this.receiving = receivingArea;
        this.requests = new ArrayList<>();

        free = new ArrayList<>();
        for (int x = shipping.a.x; x < shipping.b.x; ++x) {
            for (int y = shipping.a.y; y < shipping.b.y; ++y) {
                free.add(new Vec2i(x, y));
            }
        }

        banned = new ArrayList<>();
    }

    private List<Vec2i> free;
    private List<Shelf> banned;

    public void update() {
        long shelfs = world.getWorldObjectsModifyiable().stream()
                .filter(x -> x instanceof Shelf)
                .count();

        if (!idle.isEmpty() && !free.isEmpty() && banned.size() < shelfs) {
            Robot r = idle.remove(0);

            Shelf s;
            do {
                s = tracker.randomShelf();
            } while (banned.contains(s));
            banned.add(s);

            r.setAction(new ActionTransportObject(r, s, free.remove(0)));
            busy.add(r);
        }

        idle = idle.stream().filter(x -> !busy.contains(x)).collect(Collectors.toList());
        for (Robot r : busy) if (r.getAction().isDone()) idle.add(r);
        busy = busy.stream().filter(x -> !idle.contains(x)).collect(Collectors.toList());

        // Find tasks for idle robots.
        /*List<Robot> toIdle = new ArrayList<>(), toBusy = new ArrayList<>();
        for (Robot r : idle) {
            if (requests.size() > 0) {
                SRRequest req = requests.remove(0);
                Vec2i oldpos = new Vec2i(req.shelf.getPosition().x, req.shelf.getPosition().z);

                if (req.type == SRRequest.Type.RECEIVING) {
                    // Go to random point in receiving zone, add items to shelf and go back.
                    List<IWorldAction> actions = new ArrayList<>();
                    actions.add(new ActionTransportShelf(req.shelf, receiving.randomPointWithin()));
                    actions.add(new ActionDummy(() -> {
                        for (WarehouseItem i : req.items) tracker.addItem(i, req.shelf);
                    }, req.items.size() * TIME_TO_ADD_ITEM));
                    actions.add(new ActionTransportShelf(req.shelf, oldpos));

                    r.setAction(new ActionCompound(actions));
                } else {
                    // Go to random point in shipping zone, remove items from shelf and go back.
                    List<IWorldAction> actions = new ArrayList<>();
                    actions.add(new ActionTransportShelf(req.shelf, shipping.randomPointWithin()));
                    actions.add(new ActionDummy(() -> {
                        for (WarehouseItem i : req.items) tracker.removeItem(i);
                    }, req.items.size() * TIME_TO_ADD_ITEM));
                    actions.add(new ActionTransportShelf(req.shelf, oldpos));

                    r.setAction(new ActionCompound(actions));
                }

                toBusy.add(r);
            }
        }

        // Move robots that are finished back to the idle queue.
        for (Robot r : busy) {
            if (r.getAction().isDone()) {
                toIdle.add(r);
            }
        }

        for (Robot r : toIdle) { idle.add(r); busy.remove(r); }
        for (Robot r : toBusy) { busy.add(r); idle.remove(r); }*/
    }

    // Generate items that are to be stored in the warehouse.
    public void generateReceiveRequests(int count) {
        while (count > 0) {
            Shelf shelf = this.tracker.findShelfWithSpace();
            int items = Math.min(count, ObjectTracker.ShelfCapacity - shelf.getItemCount());
            count -= items;

            List<WarehouseItem> list = factory.create(tracker, items, false);

            addOrMerge(new SRRequest(
                    SRRequest.Type.RECEIVING,
                    shelf,
                    list
            ));
        }
    }

    // Generate requests for items to be taken from the warehouse.
    public void generateDeliveryRequests(int count) {
        if (count > getStoredItemCount())
            throw new IllegalArgumentException("Can't generate requests for " + count + " items when there are only " + getStoredItemCount() + " items in the warehouse.");

        List<WarehouseItem> items = new ArrayList<>();
        for (int i = 0; i < count; ++i) items.add(this.tracker.randomItem());

        for (WarehouseItem i : items) {
            addOrMerge(new SRRequest(
                    SRRequest.Type.SHIPPING,
                    this.tracker.findItem(i),
                    Arrays.asList(i)
            ));
        }
}

    public int getStoredItemCount() {
        return this.tracker.storedItemCount();
    }

    private void addOrMerge(SRRequest request) {
        // TODO: Replace with map.
        for (SRRequest r : this.requests) {
            if (r.shelf == request.shelf && r.type == request.type && r.items.size() + request.items.size() <= ObjectTracker.ShelfCapacity) {
                r.items.addAll(request.items);
                return;
            }
        }

        this.requests.add(request);
    }
}
