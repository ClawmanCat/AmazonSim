package com.groep15.amazonsim.controllers;

import com.groep15.amazonsim.controllers.wms.ShippingReceivingManager;
import com.groep15.amazonsim.models.Model;
import com.groep15.amazonsim.models.Object3D;
import com.groep15.amazonsim.models.World;
import com.groep15.amazonsim.views.View;

import java.beans.PropertyChangeEvent;
import java.util.ConcurrentModificationException;

public class SimulationController extends Controller {
    private ShippingReceivingManager manager;

    public SimulationController(Model model) {
        super(model);

        World world = (World) model;
        this.manager = new ShippingReceivingManager(world);
    }

    @Override
    public void run() {
        while (true) {
            this.getModel().update();

            if (((World) this.getModel()).getTickCount() % 1000 == 0) {
                this.manager.receiveObjects(16);
            }

            this.manager.update();
            try {
                Thread.sleep(1000 / 30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onViewAdded(final View view) {
        view.onViewClose(() -> { this.removeView(view); });

        for (Object3D object : this.getModel().getWorldObjectsAsList()) {
            view.update(Model.UPDATE_COMMAND, object);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        try {
            for (View v : this.getViews()) if (v != null) v.update(evt.getPropertyName(), (Object3D) evt.getNewValue());
        } catch (ConcurrentModificationException e) {
            // Thrown due to some external access to one of the views when a view is being removed.
            // This issue was already present in the code on Blackboard.
            // No, the issue is not in onViewAdded / view.onViewClose (set in the same method), I already tried that.
            // The best we can do for now is just catch it and produce this complaint, that nobody will probably ever bother to investigate.
            System.out.println("View destruction caused unsupported concurrent access. This is not a good thing.");
        }
    }

}