package com.groep15.amazonsim.models;

import com.groep15.amazonsim.models.ai.IWorldActor;
import com.groep15.amazonsim.models.worldobject.Object3D;
import com.groep15.amazonsim.utility.Direction;
import com.groep15.amazonsim.utility.Utility;
import com.groep15.amazonsim.utility.Vec2i;

import java.util.*;

public class WorldGraph {
    private boolean[][] map;
    private World world;
    private List<IWorldActor> actors;


    public WorldGraph(World world) {
        this.world = world;
        this.map = null;
    }

    public void update() {
        this.actors = world.getActors();

        boolean[][] old = map;
        this.map = new boolean[world.getSize().x][world.getSize().y];
        for (boolean[] arr : this.map) Arrays.fill(arr, true);

        boolean changed = false;
        for (Object3D o : world.getWorldObjectsModifyiable()) {
            Vec2i pos = new Vec2i(Math.round(o.getPosition().x), Math.round(o.getPosition().z));

            if (o.getIsPassable())                                          continue;
            if (pos.x >= world.getSize().x || pos.y >= world.getSize().y)   continue;
            if (pos.x < 0 || pos.y < 0)                                     continue;

            // Don't count objects that are being moved by an actor.
            boolean carried = false;
            for (IWorldActor actor : actors) if (actor.getHeldObject() == o) { carried = true; break; }
            if (carried) continue;

            // Update map
            map[pos.x][pos.y] = false;
            if (old != null && old[pos.x][pos.y] != map[pos.x][pos.y]) changed = true;
        }

        //if (changed) for (IWorldActor actor : actors) actor.getAction().onWorldChanged(new ArrayList<>());
    }


    // Find the shortest path through BFS.
    // Setting avoid actors does not do proper collision avoidance, it just generates a path without collisions,
    // and if it can't do so, it will return null, rather than modifying the path with wait points.
    public List<Direction> calculatePath(IWorldActor actor, Vec2i from, Vec2i to) {
        if (map == null) update();

        // Mark other paths as occupied.
        boolean[][] tmpmap = Arrays.stream(map).map(boolean[]::clone).toArray(boolean[][]::new);
        for (IWorldActor a : actors) {
            if (a == actor) continue;

            List<Vec2i> path = Utility.DirectionsToPositions(
                    a.getAction().getMovementFuture(),
                    new Vec2i(a.getPosition().x, a.getPosition().z)
            );

            for (Vec2i v : path) tmpmap[v.x][v.y] = false;
        }

        Map<Vec2i, Direction> parents = new HashMap<>();
        Queue<Vec2i> queue = new LinkedList<>();

        parents.put(from, Direction.NONE);
        queue.add(from);

        while (queue.size() > 0) {
            Vec2i pos = queue.remove();

            if (pos.equals(to)) {
                return RetracePath(pos, from, parents);
            }

            for (Direction d : Direction.values()) {
                Vec2i next = new Vec2i(pos.x + d.movement.x, pos.y + d.movement.y);

                if (next.x < 0 || next.x >= map.length)              continue;       // Check X-coord inside bounds.
                if (next.y < 0 || next.y >= map[next.x].length)      continue;       // Check Y-coord inside bounds.
                if (!tmpmap[next.x][next.y] && !next.equals(to))     continue;       // Check space not occupied. (can always move to dest.)
                if (parents.containsKey(next))                       continue;       // Check node already discovered.

                parents.put(next, d.invert());
                queue.add(next);
            }
        }

        return null;    // No path exists.
    }


    public boolean collides(IWorldActor actor, Vec2i start, List<Direction> path) {
        Map<Vec2i, List<Integer>> occupations = new HashMap<>();

        for (IWorldActor a : this.actors) {
            if (a == actor) continue;

            Vec2i apos = new Vec2i(a.getPosition().x, a.getPosition().z);
            List<Direction> apath = a.getAction().getMovementFuture();

            List<Vec2i> pathpos = Utility.DirectionsToPositions(apath, apos);

            for (int i = 0; i < pathpos.size(); ++i) {
                Vec2i pos = pathpos.get(i);

                for (int j = -1; j <= 1; ++j) {
                    if (i + j < 0) continue;

                    // Occupy for 3 ticks to prevent semi-collisions
                    if (occupations.containsKey(pos)) occupations.get(pos).add(i + j);
                    else occupations.put(pos, new ArrayList<>(Arrays.asList(i + j)));
                }
            }
        }

        List<Vec2i> pathpos = Utility.DirectionsToPositions(path, start);

        for (int i = 0; i < pathpos.size(); ++i) {
            Vec2i pos = pathpos.get(i);

            if (occupations.containsKey(pos) && occupations.get(pos).contains(i)) return true;
        }

        return false;
    }


    private static List<Direction> RetracePath(Vec2i pos, Vec2i from, Map<Vec2i, Direction> parents) {
        List<Direction> path = new ArrayList<>();

        Vec2i next = pos;
        while (!next.equals(from)) {
            Direction d = parents.get(next);

            path.add(d.invert());
            next = new Vec2i(next.x + d.movement.x, next.y + d.movement.y);
        }

        Collections.reverse(path);

        return path;
    }
}
