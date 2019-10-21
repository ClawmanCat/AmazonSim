package com.groep15.amazonsim.controllers;

import com.groep15.amazonsim.base.Command;
import com.groep15.amazonsim.models.Model;
import com.groep15.amazonsim.models.Object3D;
import com.groep15.amazonsim.views.View;

import java.beans.PropertyChangeEvent;

public class SimulationController extends Controller {
    public SimulationController(Model model) {
        super(model);
    }

    @Override
    public void run() {
        while (true) {
            this.getModel().update();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onViewAdded(final View view) {
        final Controller t = this;

        view.onViewClose(new Command(){
        
            @Override
            public void execute() {
                t.removeView(view);
            }
        });

        for (Object3D object : this.getModel().getWorldObjectsAsList()) {
            view.update(Model.UPDATE_COMMAND, object);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        for (View v : this.getViews()) if (v != null) v.update(evt.getPropertyName(), (Object3D) evt.getNewValue());
    }

}