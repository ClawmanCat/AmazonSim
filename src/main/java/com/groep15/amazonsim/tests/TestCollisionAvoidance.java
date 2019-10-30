package com.groep15.amazonsim.tests;

import com.groep15.amazonsim.ai.ActionTransportObject;
import com.groep15.amazonsim.ai.IWorldActor;
import com.groep15.amazonsim.models.Object3D;
import com.groep15.amazonsim.models.Shelf;
import com.groep15.amazonsim.models.World;
import com.groep15.amazonsim.utility.Square2i;
import com.groep15.amazonsim.utility.Vec2i;
import com.groep15.amazonsim.utility.WorldReader;
import com.groep15.amazonsim.wms.ObjectTracker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestCollisionAvoidance implements ITest {
    private static final String WORLD_PATH = new File("src/main/resources/layout/layout_04.worlddef").getAbsolutePath();

    @Override
    public boolean run() {
        try {
            return runImpl();
        } catch (Exception e) {
            System.out.println("Test failed with error: " + e.getMessage());
            System.out.println("Stacktrace: ");
            e.printStackTrace();

            return false;
        }
    }

    public boolean runImpl() {
        World world = WorldReader.ReadWorld(WORLD_PATH);
        ObjectTracker tracker = world.getTracker();

        Square2i targetArea = GetWorldShippingArea(world);
        int destX = 0, destY = 0;

        List<Shelf> shelves = world.getWorldObjectsModifyiable().stream()
                .filter(x -> x instanceof Shelf)
                .map(x -> (Shelf) x)
                .collect(Collectors.toList());

        List<IWorldActor> free = world.getWorldObjectsModifyiable().stream()
                .filter(x -> x instanceof IWorldActor)
                .map(x -> (IWorldActor) x)
                .collect(Collectors.toList());

        List<IWorldActor> busy = new ArrayList<>();
        Map<IWorldActor, Shelf> targets = new HashMap<>();

        System.out.println("Shelves: " + shelves.size() + ", Actors: " + free.size());

        while (true) {
            // Set free robots to work.
            if (free.size() > 0 && shelves.size() > 0) {
                Shelf s       = shelves.remove(0);
                IWorldActor a = free.remove(0);

                a.setAction(new ActionTransportObject(a, s, new Vec2i(destX, destY)));
                Move(a, free, busy);

                targets.put(a, s);

                System.out.println(a.toString() + ": moving shelf " + s.toString() + " to " + new Vec2i(destX, destY).toString());

                ++destX;
                if (destX >= targetArea.b.x) {
                    destX = 0;
                    ++destY;
                }
            }

            // Add done robots back to the free queue.
            for (int i = busy.size() - 1; i >= 0; --i) {
                if (!busy.get(i).getAction().isDone()) continue;

                targets.remove(busy.get(i));
                Move(busy.get(i), busy, free);
            }

            // The actual test: tick the world and see if anything is colliding.
            world.update();
            if (world.getTickCount() % 1000 == 0) System.out.println("Took " + world.getTickCount() + " ticks...");

            for (Object3D o : world.getWorldObjectsModifyiable()) {
                if (o.getIsPassable() && !(o instanceof IWorldActor)) continue;

                Vec2i objpos = new Vec2i(o.getPosition().x, o.getPosition().z);

                for (IWorldActor a : Combine(free, busy)) {
                    if (targets.get(a) == o)    continue;   // The robot may move under its target.
                    if (a == o)                 continue;   // The robot does not collide with itself.

                    if (new Vec2i(a.getPosition().x, a.getPosition().z).equals(objpos)) {
                        System.out.println("Collision at t = " + world.getTickCount() + ": ");
                        System.out.println("Actor " + a.toString() + " collided with object " + o.toString() + " at " + objpos.toString());

                        return false;   // oh no
                    }
                }
            }

            // Test is complete when all shelves have been moved.
            if (shelves.size() == 0 && busy.size() == 0) break;
        }

        return true;
    }


    private static Square2i GetWorldShippingArea(World world) {
        double xmin = (Double) world.getSetting("shipping_area.begin.x");
        double ymin = (Double) world.getSetting("shipping_area.begin.y");
        double xmax = (Double) world.getSetting("shipping_area.end.x");
        double ymax = (Double) world.getSetting("shipping_area.end.y");

        return new Square2i(new Vec2i(xmin, ymin), new Vec2i(xmax, ymax));
    }

    private static void Move(IWorldActor a, List<IWorldActor> from, List<IWorldActor> to) {
        from.remove(a);
        to.add(a);
    }

    private static <T> List<T> Combine(List<T>... ls) {
        List<T> result = new ArrayList<>();
        for (List<T> l : ls) result.addAll(l);

        return result;
    }
}
