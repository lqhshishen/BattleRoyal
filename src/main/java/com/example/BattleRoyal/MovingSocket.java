package com.example.BattleRoyal;

import com.alibaba.fastjson.JSONObject;
import com.example.BattleRoyal.bean.Packet;
import com.example.BattleRoyal.bean.PlayerLocation;
import com.example.BattleRoyal.bean.User;

import javax.ejb.Schedule;
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

    public boolean isStart = false;

    public String room = "";


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
        System.out.println("open success " + session.getId());
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
            case "dead":
                onDamaged(message);
                break;
            case "pickup":
                onPickup(message);
                break;
            case "start":
                startTimer();
            default:
                break;
        }
    }

    public void onPickup(String message) {
        for (MovingSocket socket : set) {
            socket.sendMsg(message);
        }
    }

    public void onDamaged(String message) {
        System.out.println(message);
        for (MovingSocket socket : set) {
            if (socket.room.equals(room)) {
                socket.sendMsg(message);
            }
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

    public void startTimer() {
        for (MovingSocket socket : set) {
            if (socket.room.equals(room) && socket.isStart) {
                isStart = true;
                return;
            }
        }
        isStart = true;
        onStart();
        System.out.println("start");
    }

    public void onReady(User user, Session session) {
        room = session.getId();
        for (MovingSocket socket : set) {
            if (socket.isReady && !socket.isStart) {
                this.room = socket.room;
                break;
            }
        }
        System.out.println("user = " + user.toString());
        for (MovingSocket socket : set) {
            if (session.getId().equals(socket.session.getId())) {
                socket.isReady = true;
                socket.user.setUserModel(user.getUserModel());
                System.out.println("set user" + user.toString());
            }
            Packet packet = new Packet();
            packet.message = "ready";
            socket.sendMsg(JSONObject.toJSONString(packet));
        }
        USER_MAP.put(user.getUserId(), user);
        addReadyCount();
    }

    int[][] positionssss = new int[][]{{300, 0, 355}, {350, 0, 355}, {400, 0, 355}, {455, 0, 355}, {520, 0, 355}
            , {580, 0, 420}, {520, 0, 420}, {455, 0, 420}, {400, 0, 400}, {330, 0, 400}
            , {330, 0, 450}, {400, 0, 450}, {390, 0, 450}, {420, 0, 480}, {460, 0, 480}
            , {530, 0, 480}, {570, 0, 480}, {570, 0, 520}, {460, 0, 520}, {400, 0, 520}
            , {570, 0, 520}, {350, 0, 580}, {350, 0, 580}, {400, 0, 580}, {450, 0, 580}
            , {350, 0, 580}, {520, 0, 580}, {580, 0, 500}, {420, 0, 610}, {500, 0, 610}
    };

    public void onStart() {
        // give every players a position
        for (MovingSocket movingSocket : set) {
            if (!movingSocket.session.getId().equals(session.getId()) && movingSocket.isStart) {
                break;
            }
        }
        int i = 0;
        System.out.println("start the game");
        for (MovingSocket movingSocket : set) {
            if (!movingSocket.isReady) {
                continue;
            }
            if (!movingSocket.room.equals(room)) {
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
                if (!movingSocket.room.equals(room)) {
                    continue;
                }
                System.out.println("send " + packet.spwan.getSpwan().toString() + " to " + move.user.toString() + " with position " + packet.spwan.getPosition().toString());
                move.sendMsg(JSONObject.toJSONString(packet));
            }
            i++;
        }
        for (; i < positionssss.length; i++) {
            Packet packet = new Packet();
            packet.message = "start";
            packet.spwan = new PlayerLocation();

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
            for (MovingSocket movingSocket : set) {
                if (!movingSocket.room.equals(room)) {
                    continue;
                }
                movingSocket.sendMsg(JSONObject.toJSONString(packet));
            }
        }
    }

    public void onCancelReady(User user) {
        USER_MAP.remove(user.getUserId());
        subReadyCount();
    }

    public void onMovement(String message) {
        for (MovingSocket socket : set) {
            socket.sendMsg(message);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        print(session.getId());
        print("something error");
        error.printStackTrace();
    }

    public void sendMsg(String msg) {
        try {
            this.session.getBasicRemote().sendText(msg);
        } catch (IllegalStateException | IOException e) {
            System.out.println("wait return");
            sendMsg(msg);
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
