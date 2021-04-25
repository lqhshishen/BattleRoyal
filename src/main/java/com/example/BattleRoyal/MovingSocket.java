package com.example.BattleRoyal;

import com.alibaba.fastjson.JSONObject;
import com.example.BattleRoyal.bean.Packet;
import com.example.BattleRoyal.bean.PlayerLocation;
import com.example.BattleRoyal.bean.User;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
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

    private boolean isReady = false;

    private static Map<Integer, User> USER_MAP = new HashMap<>();

    public boolean isUse = false;

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
        //System.out.println(session.getId() + "xxxxx");
        //    System.out.println(jsonObject.getString("message") + " " + message);
        switch (jsonObject.getString("message")) {
            case "login":
                //login
                onLogin(jsonObject.getObject("loginInfo", User.class).getUsername(), session);
                break;
            case "ready":
                System.out.println("ready " + message);
                //click ready return player position to every players
                onReady(jsonObject.getJSONObject("spwan").getObject("spwan", User.class), session);
                break;
            case "movement":
                onMovement(message);
                break;
            case "damage":
                onDamaged(message);
                break;
            default:
                break;
        }
    }

    public void onDamaged(String message) {
        System.out.println(message);
        for (MovingSocket socket : set) {
            socket.sendMsg(message);
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
                user.setUsername(username);
                user.setUserId(id);
                packet.loginInfo = user;
                packet.message = "login";
                socket.sendMsg(JSONObject.toJSONString(packet));
                socket.user = user;
            }
        }
    }

    public void onReady(User user, Session session) {
        System.out.println("user = " + user.toString());
        for (MovingSocket socket : set) {
            if (session.getId().equals(socket.session.getId())) {
                socket.isReady = true;
                socket.user.setUserModel(user.getUserModel());
                System.out.println("set user" + user.toString());
            }
        }
        USER_MAP.put(user.getUserId(), user);
        addReadyCount();
        System.out.println("ready count = " + READY_COUNT);
        //if player number == 1 start the game. todo it's for test, change it when the game finished
        if (READY_COUNT == 2) {
            onStart();
        }
    }

    int[][] positionssss = new int[][]{{410, 2, 405}, {410, 2, 400}};

    public void onStart() {
        // give every players a position
        int i = 0;
        for (MovingSocket movingSocket : set) {
            if (!movingSocket.isReady) {
                continue;
            }
            System.out.println();
            Packet packet = new Packet();
            packet.message = "start";
            packet.spwan = new PlayerLocation();
            packet.spwan.setSpwan(movingSocket.user);

            PlayerLocation.Angle angle = new PlayerLocation.Angle();
            angle.setX(0);
            angle.setY(0);
            angle.setZ(0);

            PlayerLocation.Position position = new PlayerLocation.Position();
            position.setX(positionssss[i][0]);
            position.setY(positionssss[i][1]);
            position.setZ(positionssss[i][2]);

            packet.spwan.setAngle(angle);
            packet.spwan.setPosition(position);
            for (MovingSocket move : set) {
                if (!move.isReady) {
                    continue;
                }
                System.out.println("send " + packet.spwan.getSpwan().toString() + " to " + move.user.toString());
                move.sendMsg(JSONObject.toJSONString(packet));
                i++;
            }
            i = 0;
        }
    }

    public void onCancelReady(User user) {
        USER_MAP.remove(user.getUserId());
        subReadyCount();
    }

    public void onMovement(String message) {
        for (MovingSocket socket : set) {
//            if (socket.user.getUsername().equals("b")) {
//                System.out.println(message);
//            }
            socket.sendMsg(message);
            socket.isUse = true;
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        print("something error");
        error.printStackTrace();
    }

    public void sendMsg(String msg) {
        //print("send " + msg);
        try {
            this.session.getBasicRemote().sendText(msg);
        } catch (IllegalStateException | IOException e) {
//            e.printStackTrace();
            System.out.println("wait return");
            sendMsg(msg);
        }
        // System.out.println(msg);
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
