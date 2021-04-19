package com.example.BattleRoyal;

import com.alibaba.fastjson.JSONObject;
import com.example.BattleRoyal.bean.Packet;
import com.example.BattleRoyal.bean.PlayerLocation;
import com.example.BattleRoyal.bean.User;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.http.WebSocket;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author Owner
 */
@ServerEndpoint(value = "/websocket")
public class MovingSocket {

    private static int COUNT = 0;

    private static CopyOnWriteArraySet<MovingSocket> set = new CopyOnWriteArraySet<>();

    private Session session;

    private User user;

    private static int READY_COUNT = 0;


    private static Map<Integer, User> USER_MAP = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MovingSocket that = (MovingSocket) o;
        return Objects.equals(session, that.session);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session);
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        set.add(this);
        System.out.println("open success" + session.getId());
        addOnlineCount();
    }

    @OnClose
    public void onClose(Session session) {
        set.remove(this);
        subOnlineCount();
        subReadyCount();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        System.out.println(session.getId() + "xxxxx");
        System.out.println(jsonObject.getString("message") + " " + message);
        switch (jsonObject.getString("message")) {
            case "login":
                //login
                onLogin(jsonObject.getObject("loginInfo", User.class).getUserName(), session);
                break;
            case "ready":
                //click ready return player position to every players
                onReady(jsonObject.getObject("spwan", User.class), session);
                break;
            case "movement":
                onMovement(message);
                break;
            default:
                break;
        }
    }

    public void onLogin(String username, Session session) {
        int id = (int) (Math.random() * 30);
        while (USER_MAP.get(id) != null) {
            id = (int) (Math.random() * 30);
        }
        User user = new User();
        for (MovingSocket socket : set) {
            if (session.getId().equals(socket.session.getId())) {
                Packet packet = new Packet();
                user.setUserName(username);
                user.setUserId(id);
                packet.loginInfo = user;
                packet.message = "login";
                socket.sendMsg(JSONObject.toJSONString(packet));
                socket.user = user;
            }
        }
    }

    public void onReady(User user, Session session) {
        USER_MAP.put(user.getUserId(), user);
        addReadyCount();
        //if player number == 1 start the game. todo it's for test change it when the game finished
        if (READY_COUNT == 1) {
            onStart();
        }
    }


    public void onStart() {
        // give every players a position
        for (MovingSocket movingSocket : set) {
            Packet packet = new Packet();
            packet.message = "start";
            packet.spwan = new PlayerLocation();
            packet.spwan.setSpwan(movingSocket.user);

            PlayerLocation.Angle angle = new PlayerLocation.Angle();
            angle.setX(0);
            angle.setY(0);
            angle.setZ(0);

            PlayerLocation.Position position = new PlayerLocation.Position();
            position.setX(410);
            position.setY(2);
            position.setZ(405);

            packet.spwan.setAngle(angle);
            packet.spwan.setPosition(position);

            movingSocket.sendMsg(JSONObject.toJSONString(packet));
        }
    }

    public void onCancelReady(User user) {
        USER_MAP.remove(user.getUserId());
        subReadyCount();
    }

    public void onMovement(String message) {
        for (MovingSocket webSocket : set) {
            for (MovingSocket socket : set) {
                socket.sendMsg(message);
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        print("something error");
        error.printStackTrace();
    }

    public void sendMsg(String msg) {
        print("send " + msg);
        try {
            this.session.getBasicRemote().sendText(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addReadyCount() {
        READY_COUNT++;
    }

    public static void subReadyCount() {
        READY_COUNT--;
    }

    public static int getReadyCount() {
        return READY_COUNT;
    }

    public static int getOnlineCount() {
        return COUNT;
    }

    public static void addOnlineCount() {
        COUNT++;
    }

    public static void subOnlineCount() {
        COUNT--;
    }

    private void print(String msg) {
        System.out.println(msg);
    }
}
