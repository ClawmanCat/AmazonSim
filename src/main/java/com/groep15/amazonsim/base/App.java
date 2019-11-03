package com.groep15.amazonsim.base;

import com.groep15.amazonsim.controllers.Controller;
import com.groep15.amazonsim.controllers.SimulationController;
import com.groep15.amazonsim.models.WorldReader;
import com.groep15.amazonsim.views.DefaultWebSocketView;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.File;
import java.io.IOException;

@Configuration
@EnableAutoConfiguration
@EnableWebSocket
public class App extends SpringBootServletInitializer implements WebSocketConfigurer {
    public static final String WORLD_PATH     = new File("src/main/resources/layout/master.worlddef").getAbsolutePath();
    public static final Controller Controller = new SimulationController(WorldReader.ReadWorld(WORLD_PATH));

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    public App() {
        Controller.start();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(App.class);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new DefaultWebSocketHandler(), "/connectToSimulation");
    }

    private class DefaultWebSocketHandler extends TextWebSocketHandler {
        @Override
        public void afterConnectionEstablished(WebSocketSession session) {
            System.out.println("New client connected: " + session.getRemoteAddress().getAddress().toString());
            Controller.addView(new DefaultWebSocketView(session));
        }

        @Override
        public void handleTextMessage(WebSocketSession session, TextMessage message) {
            //Do something to handle user input here
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws IOException {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }
}