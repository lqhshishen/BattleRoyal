package com.example.BattleRoyal;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Owner
 */
@ServerEndpoint(value = "/websocket")
public class MovingSocket {

    private static int COUNT = 0;

    private static CopyOnWriteArraySet<MovingSocket> set = new CopyOnWriteArraySet<>();

    private Session session;

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
        try {
            this.sendMsg("connection ok");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        set.remove(this);
        subOnlineCount();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        print("message from client" + message);
        for (MovingSocket socket : set) {
            try {
                socket.sendMsg("your message is " + message);
                print(socket.hashCode() + "");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        print("something error");
        error.printStackTrace();
    }

    public void sendMsg(String msg) throws IOException {
        this.session.getBasicRemote().sendText(msg);
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
