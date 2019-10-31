package com.groep15.amazonsim.controllers.wms;

import com.groep15.amazonsim.models.Robot;
import com.groep15.amazonsim.models.Shelf;
import com.groep15.amazonsim.models.World;
import com.groep15.amazonsim.models.ai.ActionCompound;
import com.groep15.amazonsim.models.ai.ActionRunCommand;
import com.groep15.amazonsim.models.ai.ActionTransportObject;
import com.groep15.amazonsim.utility.Square2i;
import com.groep15.amazonsim.utility.Utility;
import com.groep15.amazonsim.utility.Vec2i;
import org.javatuples.Pair;

import java.util.*;
import java.util.stream.Collectors;

// Generate item shipping and receiving requests for the robots to complete.
public class ShippingReceivingManager {
    public static final int SHELF_ITEM_CAPACITY     = 10;
    public static final int TIME_TO_TRANSFER_ITEM   = 30;

    private Random random = new Random();

    private World world;
    private Map<WarehouseItem, Shelf> storage;
    private List<WarehouseItem> items;
    private List<Robot> free, busy;
    private List<Shelf> standing, taken;

    private Square2i receiveArea, deliveryArea;
    private int receiveCount, deliveryCount;

    private List<List<WarehouseItem>> pendingReceives;
    private List<Pair<Shelf, List<WarehouseItem>>> pendingDeliveries;

    public ShippingReceivingManager(World world) {
        this.world = world;

        this.free = world.getActors().stream()
                .filter(x -> x instanceof Robot)
                .map(x -> (Robot) x)
                .collect(Collectors.toList());

        this.standing = world.getWorldObjectsModifyiable().stream()
                .filter(x -> x instanceof Shelf)
                .map(x -> (Shelf) x)
                .collect(Collectors.toList());


        this.storage  = new HashMap<>();
        this.busy     = new ArrayList<>();
        this.items    = new ArrayList<>();
        this.taken    = new ArrayList<>();

        this.receiveArea  = GetWorldArea(world, "receiving");
        this.deliveryArea = GetWorldArea(world, "shipping");
        this.receiveCount = this.deliveryCount = 0;

        this.pendingReceives   = new ArrayList<>();
        this.pendingDeliveries = new ArrayList<>();
    }


    public void update() {
        // Since we only process one order per tick, process deliveries on even ticks and receives on uneven ones.
        if ((world.getTickCount() & 1) == 0) {
            if (!free.isEmpty() && Utility.Contains(pendingDeliveries, x -> standing.contains(x.getValue0()))) {
                // Get next point where we can deliver.
                Vec2i where = GetNextShelfPoint(deliveryArea, deliveryCount);
                if (where == null) return; else ++deliveryCount;

                Pair<Shelf, List<WarehouseItem>> delivery = Utility.Find(pendingDeliveries, x -> standing.contains(x.getValue0()));
                Robot robot = free.remove(0);

                Vec2i current = new Vec2i(delivery.getValue0().getPosition().x, delivery.getValue0().getPosition().y);
                robot.setAction(new ActionCompound(
                        new ActionTransportObject(robot, delivery.getValue0(), current, where),
                        new ActionRunCommand(
                                () -> {
                                    for (WarehouseItem i : delivery.getValue1()) {
                                        this.storage.remove(i);
                                        delivery.getValue0().removeItem(i);
                                    }
                                },
                                TIME_TO_TRANSFER_ITEM * delivery.getValue1().size()
                        ),
                        new ActionTransportObject(robot, delivery.getValue0(), where, current),
                        new ActionRunCommand(() -> {
                            Utility.Move(delivery.getValue0(), taken, standing);
                            Utility.Move(robot, busy, free);
                        })
                ));

                Utility.Move(delivery.getValue0(), standing, taken);
                this.busy.add(robot);
                this.pendingDeliveries.remove(delivery);
            }
        } else {
            if (!free.isEmpty() && !pendingReceives.isEmpty() && Utility.Contains(standing, x -> x.getItemCount() < SHELF_ITEM_CAPACITY)) {
                // Get next point where we can receive.
                Vec2i where = GetNextShelfPoint(receiveArea, receiveCount);
                if (where == null) return; else ++receiveCount;

                List<WarehouseItem> receive = pendingReceives.get(0);
                Robot robot  = free.remove(0);
                Shelf shelf  = Utility.Find(standing, x -> x.getItemCount() < SHELF_ITEM_CAPACITY);
                int movecnt  = Math.min(receive.size(), SHELF_ITEM_CAPACITY - shelf.getItemCount());
                boolean done = movecnt == receive.size();
                Vec2i old    = new Vec2i(shelf.getPosition().x, shelf.getPosition().z);

                robot.setAction(new ActionCompound(
                        new ActionTransportObject(robot, shelf, old, where),
                        new ActionRunCommand(
                                () -> {
                                    for (int i = 0; i < movecnt; ++i) {
                                        WarehouseItem item = receive.remove(0);

                                        this.storage.put(item, shelf);
                                        this.items.add(item);
                                        shelf.addItem(item);
                                    }
                                },
                                TIME_TO_TRANSFER_ITEM * movecnt
                        ),
                        new ActionTransportObject(robot, shelf, where, old),
                        new ActionRunCommand(() -> {
                            Utility.Move(robot, busy, free);
                            Utility.Move(shelf, taken, standing);
                        })
                ));

                Utility.Move(shelf, standing, taken);
                this.busy.add(robot);
                if (done) pendingReceives.remove(0);
            }
        }
    }


    // Receive new objects into the warehouse.
    public void receiveObjects(int count) {
        List<WarehouseItem> items = WarehouseItemFactory.instance.create(count);
        pendingReceives.add(items);
    }


    // Ship existing objects out of the warehouse.
    public void shipObjects(int count) {
        Map<Shelf, List<WarehouseItem>> items = new HashMap<>();

        for (int i = 0; i < count; ++i) {
            WarehouseItem item = this.items.get(random.nextInt(this.items.size()));
            Shelf shelf = storage.get(item);

            if (items.containsKey(shelf)) items.get(shelf).add(item);
            else items.put(shelf, new ArrayList<>(Arrays.asList(item)));
        }

        for (Map.Entry<Shelf, List<WarehouseItem>> shelfitems : items.entrySet()) {
            if (standing.contains(shelfitems.getKey())) {
                // Shelf is standing at its normal position => if there are other orders for items from this shelf,
                // we can merge them.
                Pair<Shelf, List<WarehouseItem>> order = Utility.Find(pendingDeliveries, x -> x.getValue0() == shelfitems.getKey());

                if (order == null) pendingDeliveries.add(new Pair<>(shelfitems.getKey(), shelfitems.getValue()));
                else order.getValue1().addAll(shelfitems.getValue());
            } else {
                // Shelf is moving somewhere => make a new order.
                pendingDeliveries.add(new Pair<>(shelfitems.getKey(), shelfitems.getValue()));
            }
        }

        for (List<WarehouseItem> i : items.values()) this.items.removeAll(i);
    }


    private static Square2i GetWorldArea(World world, String area) {
        double xmin = (Double) world.getSetting(area + "_area.begin.x");
        double ymin = (Double) world.getSetting(area + "_area.begin.y");
        double xmax = (Double) world.getSetting(area + "_area.end.x");
        double ymax = (Double) world.getSetting(area + "_area.end.y");

        return new Square2i(new Vec2i(xmin, ymin), new Vec2i(xmax, ymax));
    }


    private static Vec2i GetNextShelfPoint(Square2i area, int current) {
        int distance = area.b.x - area.a.x;
        if (current >= distance) return null;

        return new Vec2i(area.a.x + current, area.b.y - 1);
    }
}
