package com.groep15.amazonsim.ai;

import com.groep15.amazonsim.base.Command;
import com.groep15.amazonsim.utility.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Wrapper around command to run it as an action.
public class ActionRunCommand implements IWorldAction {
    private Command command;

    public ActionRunCommand(Command command) {
        this.command = command;
    }

    @Override
    public boolean progress(IWorldActor obj) {
        if (this.isDone()) return false;

        this.command.execute();
        this.command = null;

        return true;
    }

    @Override
    public boolean isDone() {
        return command == null;
    }

    @Override
    public List<Direction> getMovementFuture() {
        return this.isDone() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(Direction.NONE));
    }

    @Override
    public void clearMovementFuture() { }

    @Override
    public void onWorldChanged() { }
}
