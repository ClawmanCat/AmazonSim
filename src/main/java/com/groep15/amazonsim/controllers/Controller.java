package com.groep15.amazonsim.controllers;

import com.groep15.amazonsim.controllers.wms.ShippingReceivingManager;
import com.groep15.amazonsim.models.Model;
import com.groep15.amazonsim.views.View;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public abstract class Controller implements Runnable, PropertyChangeListener {
    private List<View> views;
    private Model model;

    public Controller(Model model) {
        this(model, new ArrayList<View>());
    }

    public Controller(Model model, List<View> views) {
        this.model = model;
        this.model.addObserver(this);
        this.views = new ArrayList<>(views);
    }

    public void addView(View view) {
        this.views.add(view);
        this.onViewAdded(view);
    }

    public boolean hasViews() {
        return views.size() > 0;
    }

    protected abstract void onViewAdded(View view);

    /**
     * Returns the views list. Be advised that this is the internal list, for use by the controller only.
     * @return The internal list of views.
     */
    protected List<View> getViews() {
        return this.views;
    }

    protected void removeView(View view) {
        this.views.remove(view);
    }

    /**
     * Returns the internal model used by the controller.
     * @return The internal model.
     */
    protected Model getModel() {
        return this.model;
    }

    /**
     * Method to start the controller in a new thread.
     */
    public final void start() {
        new Thread(this).start();
    }

    public abstract void run();

    public abstract ShippingReceivingManager getSRManager();
}