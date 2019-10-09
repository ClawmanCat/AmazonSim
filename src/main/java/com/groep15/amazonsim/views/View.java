package com.groep15.amazonsim.views;

import com.groep15.amazonsim.base.Command;
import com.groep15.amazonsim.models.Object3D;

/*
 * Deze interface is de beschrijving van een view binnen het systeem.
 * Ze de andere classes voor meer uitleg.
 */
public interface View {
    void update(String event, Object3D data);
    void onViewClose(Command command);
}