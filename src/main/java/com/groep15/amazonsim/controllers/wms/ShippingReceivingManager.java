package com.groep15.amazonsim.controllers.wms;

import com.groep15.amazonsim.models.World;
import com.groep15.amazonsim.models.ai.ActionCompound;
import com.groep15.amazonsim.models.ai.ActionRunCommand;
import com.groep15.amazonsim.models.ai.ActionTransportObject;
import com.groep15.amazonsim.models.worldobject.Robot;
import com.groep15.amazonsim.models.worldobject.Shelf;
import com.groep15.amazonsim.utility.Square2i;
import com.groep15.amazonsim.utility.Utility;
import com.groep15.amazonsim.utility.Vec2i;
import org.javatuples.Pair;

import java.util.*;
import java.util.stream.Collectors;

// Generate item shipping and receiving requests for the robots to complete.
public class ShippingReceivingManager {
    // Token can be used to check if a requested action has completed.
    public static interface IToken {
        boolean completed();
    }

    public static class ShippingToken implements IToken {
        private ShippingReceivingManager manager;
        private List<Pair<Shelf, List<WarehouseItem>>> tasks;

        public ShippingToken(ShippingReceivingManager manager, List<Pair<Shelf, List<WarehouseItem>>> tasks) {
            this.manager = manager;
            this.tasks = tasks;
        }

        @Override
        public boolean completed() {
            for (Pair<Shelf, List<WarehouseItem>> task : tasks) if (!manager.isCompleted(task)) return false;
            return true;
        }
    }

    public static class ReceivingToken implements IToken {
        private ShippingReceivingManager manager;
        private List<List<WarehouseItem>> tasks;

        public ReceivingToken(ShippingReceivingManager manager, List<List<WarehouseItem>> tasks) {
            this.manager = manager;
            this.tasks = tasks;
        }

        @Override
        public boolean completed() {
            for (List<WarehouseItem> task : tasks) if (!manager.isCompleted(task)) return false;
            return true;
        }
    }




    public static final int SHELF_ITEM_CAPACITY     = 10;
    public static final int TIME_TO_TRANSFER_ITEM   = 10;

    private Random random = new Random();

    private World world;
    private Map<WarehouseItem, Shelf> storage;
    private List<WarehouseItem> items;
    private List<Robot> free, busy;
    private List<Shelf> standing, taken;

    private Square2i receiveArea, deliveryArea;
    private List<Vec2i> receivePoints, deliveryPoints;

    private List<List<WarehouseItem>> pendingReceives, workingReceives;
    private List<Pair<Shelf, List<WarehouseItem>>> pendingDeliveries, workingDeliveries;

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

        this.receiveArea    = GetWorldArea(world, "receiving");
        this.deliveryArea   = GetWorldArea(world, "shipping");
        this.receivePoints  = GetValidShelfPoints(this.receiveArea);
        this.deliveryPoints = GetValidShelfPoints(this.deliveryArea);

        this.pendingReceives   = new ArrayList<>();
        this.pendingDeliveries = new ArrayList<>();

        this.workingReceives   = new ArrayList<>();
        this.workingDeliveries = new ArrayList<>();
    }


    public boolean isCompleted(List<WarehouseItem> receiveRequest) {
        return  !this.pendingReceives.contains(receiveRequest) &&
                !this.workingReceives.contains(receiveRequest);
    }


    public boolean isCompleted(Pair<Shelf, List<WarehouseItem>> deliveryRequest) {
        return  !this.pendingDeliveries.contains(deliveryRequest) &&
                !this.workingDeliveries.contains(deliveryRequest);
    }


    public int getItemCount() {
        return this.items.size();
    }


    public void update() {
        // Since we only process one order per tick, process deliveries on even ticks and receives on uneven ones.
        if (world.getTickCount() % 2 == 0) {
            if (!free.isEmpty() && Utility.Contains(pendingDeliveries, x -> standing.contains(x.getValue0()))) {
                // Get next point where we can deliver.
                if (deliveryPoints.isEmpty()) return;
                Vec2i where = deliveryPoints.remove(0);

                Pair<Shelf, List<WarehouseItem>> delivery = Utility.Find(pendingDeliveries, x -> standing.contains(x.getValue0()));
                Utility.Move(delivery, pendingDeliveries, workingDeliveries);

                Robot robot = GetRemoveClosestRobot(free, new Vec2i(delivery.getValue0().getPosition().x, delivery.getValue0().getPosition().z));

                for (WarehouseItem i : delivery.getValue1()) this.storage.remove(i);

                Vec2i current = new Vec2i(robot.getPosition().x, robot.getPosition().z);
                robot.setAction(new ActionCompound(
                        new ActionTransportObject(robot, delivery.getValue0(), current, where),
                        new ActionRunCommand(
                                () -> {
                                    workingDeliveries.remove(delivery);

                                    for (WarehouseItem i : delivery.getValue1()) {
                                        delivery.getValue0().removeItem(i);
                                    }
                                },
                                TIME_TO_TRANSFER_ITEM * delivery.getValue1().size()
                        ),
                        new ActionTransportObject(robot, delivery.getValue0(), where, where, current),
                        new ActionRunCommand(() -> {
                            Utility.Move(delivery.getValue0(), taken, standing);
                            Utility.Move(robot, busy, free);

                            this.deliveryPoints.add(where);
                        })
                ));

                Utility.Move(delivery.getValue0(), standing, taken);
                this.busy.add(robot);
            }
        } else {
            if (!free.isEmpty() && !pendingReceives.isEmpty() && Utility.Contains(standing, x -> x.getItemCount() < SHELF_ITEM_CAPACITY)) {
                // Get next point where we can receive.
                if (receivePoints.isEmpty()) return;
                Vec2i where = receivePoints.remove(0);

                List<WarehouseItem> receive = pendingReceives.get(0);
                Shelf shelf    = Utility.FindRandom(standing, x -> x.getItemCount() < SHELF_ITEM_CAPACITY);
                Robot robot    = GetRemoveClosestRobot(free, new Vec2i(shelf.getPosition().x, shelf.getPosition().z));
                int movecnt    = Math.min(receive.size(), SHELF_ITEM_CAPACITY - shelf.getItemCount());
                Vec2i oldpos   = new Vec2i(robot.getPosition().x, robot.getPosition().z);
                Vec2i shelfpos = new Vec2i(shelf.getPosition().x, shelf.getPosition().z);

                Utility.Move(receive, pendingReceives, workingReceives);

                robot.setAction(new ActionCompound(
                        new ActionTransportObject(robot, shelf, oldpos, where),
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
                        new ActionTransportObject(robot, shelf, where, where, shelfpos),
                        new ActionRunCommand(() -> {
                            Utility.Move(robot, busy, free);
                            Utility.Move(shelf, taken, standing);

                            this.receivePoints.add(where);

                            if (receive.size() > 0) {
                                Utility.Move(receive, workingReceives, pendingReceives);
                            } else {
                                workingReceives.remove(receive);
                            }
                        })
                ));

                Utility.Move(shelf, standing, taken);
                this.busy.add(robot);
            }
        }
    }


    // Receive new objects into the warehouse.
    public ReceivingToken receiveObjects(int count) {
        List<WarehouseItem> items = WarehouseItemFactory.instance.create(count);
        List<List<WarehouseItem>> tokenContents = new ArrayList<>();

        for (int i = 0; i < items.size(); i+= SHELF_ITEM_CAPACITY) {
            List<WarehouseItem> task = new ArrayList<>(items.subList(i, i + Math.min(SHELF_ITEM_CAPACITY, items.size() - i)));

            pendingReceives.add(task);
            tokenContents.add(task);
        }

        return new ReceivingToken(this, tokenContents);
    }


    // Ship existing objects out of the warehouse.
    public ShippingToken shipObjects(int count) {
        Map<Shelf, List<WarehouseItem>> items = new HashMap<>();
        List<Pair<Shelf, List<WarehouseItem>>> tokenContents = new ArrayList<>();

        for (int i = 0; i < count; ++i) {
            WarehouseItem item = this.items.remove(random.nextInt(this.items.size()));
            Shelf shelf = storage.get(item);

            if (items.containsKey(shelf)) items.get(shelf).add(item);
            else items.put(shelf, new ArrayList<>(Arrays.asList(item)));
        }


        for (Map.Entry<Shelf, List<WarehouseItem>> shelfitems : items.entrySet()) {
            Pair<Shelf, List<WarehouseItem>> p = new Pair<>(shelfitems.getKey(), shelfitems.getValue());

            if (standing.contains(shelfitems.getKey())) {
                // Shelf is standing at its normal position => if there are other orders for items from this shelf,
                // we can merge them.
                Pair<Shelf, List<WarehouseItem>> order = Utility.Find(pendingDeliveries, x -> x.getValue0() == shelfitems.getKey());

                if (order == null) {
                    pendingDeliveries.add(p);
                    tokenContents.add(p);
                } else {
                    order.getValue1().addAll(shelfitems.getValue());
                    tokenContents.add(order);
                }
            } else {
                // Shelf is moving somewhere => make a new order.
                pendingDeliveries.add(p);
                tokenContents.add(p);
            }
        }

        return new ShippingToken(this, tokenContents);
    }


    private static Square2i GetWorldArea(World world, String area) {
        double xmin = (Double) world.getSetting(area + "_area.begin.x");
        double ymin = (Double) world.getSetting(area + "_area.begin.y");
        double xmax = (Double) world.getSetting(area + "_area.end.x");
        double ymax = (Double) world.getSetting(area + "_area.end.y");

        return new Square2i(new Vec2i(xmin, ymin), new Vec2i(xmax, ymax));
    }


    private List<Vec2i> GetValidShelfPoints(Square2i area) {
        List<Vec2i> result = new ArrayList<>();
        for (int x = area.a.x; x < area.b.x; ++x) result.add(new Vec2i(x, area.b.y - 1));

        return result;
    }


    private static Robot GetRemoveClosestRobot(List<Robot> robots, Vec2i where) {
        Robot current = null;
        double currentDist = Double.POSITIVE_INFINITY;

        for (Robot r : robots) {
            double dx = Math.abs(r.getPosition().x - where.x);
            double dy = Math.abs(r.getPosition().z - where.y);

            double d = Math.sqrt((dx * dx) + (dy * dy));

            if (d < currentDist) {
                currentDist = d;
                current = r;
            }
        }

        robots.remove(current);
        return current;
    }
}
