package com.groep15.amazonsim.models.ai;

import com.groep15.amazonsim.base.Command;
import com.groep15.amazonsim.utility.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Wrapper around command to run it as an action.
public class ActionRunCommand implements IWorldAction {
    private Command command;
    private int duration;

    public ActionRunCommand(Command command, int duration) {
        this.command = command;
        this.duration = duration;
    }

    public ActionRunCommand(Command command) {
        this(command, 1);
    }

    @Override
    public boolean progress(IWorldActor obj) {
        if (this.isDone()) return false;

        if (this.command != null) this.command.execute();
        this.command = null;

        --duration;

        return true;
    }

    @Override
    public boolean isDone() {
        return duration > 0;
    }

    @Override
    public List<Direction> getMovementFuture() {
        return this.isDone() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(Direction.NONE));
    }

    @Override
    public void onWorldChanged() { }
}
