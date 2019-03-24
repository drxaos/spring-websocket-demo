package com.example.demotable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    Map<String, List<String>> userSessions = new HashMap<>();

    public void auth(String topic) {
        String place = getPlace(topic);

        List<String> sessions = userSessions.computeIfAbsent(place, k -> new ArrayList<>());
        sessions.add(topic);
    }

    private String getPlace(String topic) {
        UriTemplate template = new UriTemplate("/topic/{place}/{token}");
        if (!template.matches(topic)) {
            throw new IllegalArgumentException("wrong destination");
        }
        Map<String, String> params = template.match(topic);
        String place = params.get("place");
        String token = params.get("token");

        return place;
    }

    public void disconnected(String topic) {
        String place = getPlace(topic);

        List<String> sessions = userSessions.computeIfAbsent(place, k -> new ArrayList<>());
        sessions.remove(topic);

        System.out.println("disconnected " + place + " : " + topic);
    }

    public void afterAuth(String topic) {
        messagingTemplate.convertAndSend(topic, Map.of(
                "afterAuth", "1",
                "sessionId", topic
        ));

    }

    public void send(Map<String, String> chatMessage, String topic) {
        String place = getPlace(topic);
        for (String t : userSessions.getOrDefault(place, new ArrayList<>())) {
            messagingTemplate.convertAndSend(t, chatMessage);
        }
    }
}
