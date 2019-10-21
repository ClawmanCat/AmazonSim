package com.groep15.amazonsim.views;

import com.groep15.amazonsim.base.Command;
import com.groep15.amazonsim.models.Object3D;
import org.json.simple.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class DefaultWebSocketView implements View {
    private WebSocketSession session;
    private Command onClose;

    public DefaultWebSocketView(WebSocketSession session) {
        this.session = session;
    }

    @Override
    public void update(String event, Object3D data) {
        try {
            if(this.session.isOpen()) {
                JSONObject result = new JSONObject();
                result.put("command", event);
                result.put("parameters", data.toJSON());

                this.session.sendMessage(new TextMessage(result.toString()));
            }
            else {
                this.onClose.execute();
            }
            
        } catch (IOException e) {
            this.onClose.execute();
        }
    }

    @Override
    public void onViewClose(Command command) {
        onClose = command;
    }
}