package com.groep15.amazonsim.utility;

import com.groep15.amazonsim.models.Object3D;
import com.groep15.amazonsim.models.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WorldGraph {
    private boolean[][] passable;
    private World world;

    public WorldGraph(World world) {
        this.world = world;
        this.update();
    }

    public void update() {
        this.passable = new boolean[world.getSize().x][world.getSize().y];
        for (boolean[] arr : this.passable) Arrays.fill(arr, true);

        for (Object3D o : world.getWorldObjectsAsList()) {
            Vec2i pos = new Vec2i(o.getPosition().x, o.getPosition().z);
            if (pos.x >= world.getSize().x || pos.y >= world.getSize().y) continue;

            this.passable[pos.x][pos.y] &= o.getIsPassable();
        }
    }

    public List<Direction> calculatePath(Vec2i from, Vec2i to) {
        Pair<Integer, Direction>[][] costs = new Pair[this.passable.length][this.passable[0].length];
        for (Pair<Integer, Direction>[] arr : costs) Arrays.fill(arr, new Pair<>(Integer.MAX_VALUE, Direction.NONE));

        System.out.println("Calculating paths from " + from.toString());
        calculatePathsRecursive(costs, from, 0, Direction.NONE);

        System.out.println("Retrace: " + to.toString() + " --> " + from.toString());
        List<Direction> path = retracePathRecursive(new ArrayList<Direction>(), costs, to, from);
        Collections.reverse(path);

        return path;
    }

    private void calculatePathsRecursive(Pair<Integer, Direction>[][] costs, Vec2i at, int cost, Direction from) {
        System.out.println("@ " + at.toString() + ", cost = " + cost + " reached from " + from.toString());

        costs[at.x][at.y] = new Pair<>(cost, from);

        for (Direction d : Direction.values()) {
            Vec2i next = new Vec2i(at.x + d.movement.x, at.y + d.movement.y);

            if (next.x >= costs.length || next.x < 0)       continue;       // Bounds check x-coordinate
            if (next.y >= costs[0].length || next.y < 0)    continue;       // Bounds check y-coordinate
            if (!this.passable[next.x][next.y])             continue;       // Passability check
            if (costs[next.x][next.y].getKey() <= cost + 1) continue;       // Cost check

            calculatePathsRecursive(costs, next, cost + 1, d);
        }
    }

    private List<Direction> retracePathRecursive(List<Direction> list, Pair<Integer, Direction>[][] costs, Vec2i at, Vec2i dest) {
        System.out.println("@ " + at.toString() + " reached from " + costs[at.x][at.y].getValue().toString());

        if (at.equals(dest)) return list;

        list.add(costs[at.x][at.y].getValue());

        Direction nextDir = costs[at.x][at.y].getValue().invert();
        Vec2i next = new Vec2i(at.x +nextDir.movement.x, at.y + nextDir.movement.y);

        return retracePathRecursive(list, costs, next, dest);
    }
}
