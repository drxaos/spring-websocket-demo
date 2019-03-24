package com.example.demotable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class AppSendController {

    @Autowired
    UserService userService;

    @MessageMapping("/send")
    public void send(
            @Payload Map<String, String> chatMessage,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        System.out.println("send");
        String topic = (String) headerAccessor.getSessionAttributes().get("topic");

        userService.send(chatMessage, topic);
    }

}
