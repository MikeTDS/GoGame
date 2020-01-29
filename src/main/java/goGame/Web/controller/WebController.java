package com.example.websocketdemo.controller;

import goGame.GameLogic.Game;
import goGame.GameLogic.IPlayer;
import goGame.GameLogic.WebPlayer;
import com.example.websocketdemo.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;

@Controller
public class WebController {
    String color = "Black";
    ArrayList<IPlayer> players = new ArrayList<IPlayer>();
    ArrayList<Game> games = new ArrayList<Game>();
    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload Message chatMessage) {
        int gameNumber = chatMessage.getGameNumber();
        int playerNumber = chatMessage.getPlayerNumber();
        IPlayer player = players.get(playerNumber);
        int field = Integer.parseInt(chatMessage.getContent());
        if(((WebPlayer)player).processMoveCommand(field))
            return chatMessage;
        else
            return new Message();
    }

    @MessageMapping("/chat.addUser")
    public Message addUser(@Payload Message chatMessage,
                           SimpMessageHeaderAccessor headerAccessor) {

        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", color);
        if(players.size()%2==0) {
            games.add(new Game(9));
        }
        players.add(games.get(games.size()-1).createPlayer(color, this));
        preSet();
        return chatMessage;
    }

    private void swapColor(){
        if(color == "Black"){
            color = "White";
        } else {
            color = "Black";
        }
    }

    public void preSet() {
        Message msg = new Message();
        msg.setColor(color);
        msg.setGameNumber(games.size()-1);
        msg.setPlayerNumber(players.size()-1);
        template.convertAndSend("/topic/preSet", msg);
        swapColor();
    }

    public void sendKill(String field) {
        template.convertAndSend("/topic/kill", field);
    }
}
