/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utd.ns.sim.client.helper;

import com.utd.ns.sim.client.view.ChatWindow;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author Avinash Joshi <avinash.joshi@utdallas.edu>
 */
public class Flags {

    public static final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    public static Socket socketToServer = null;
    public static String sessionUserName = "";
    public static String[] loggedInUserList;
    public static ServerSocket clientSocket;
    public static final Lock clientNumberReadLock = readWriteLock.readLock();
    public static final Lock clientNumberWriteLock = readWriteLock.writeLock();
    public static boolean endClient;
    public static HashMap<Integer, Socket> allSocketList; //A hashmap of all sockets connected via TCP
    public static Integer clientNumber = 0;
    public static ArrayList<String> outgoingChatSession;
    public static ArrayList<String> chatSession;
    public static HashMap<String, ChatWindow> chatWindowList;
    public static HashMap<String, ChatWindow> chatWindowListFrom;
    public static HashMap<String, Socket> socketTo;
    public static HashMap<String, Socket> socketFrom;
    public static String rsaKey = "etc/server.pub";
    public static String sessionAESKey;

    public Flags() {
        allSocketList = new HashMap<Integer, Socket>();
        endClient = false;
        outgoingChatSession = new ArrayList<String>();
        chatSession = new ArrayList<String>();
        chatWindowList = new HashMap<String, ChatWindow>();
        chatWindowListFrom = new HashMap<String, ChatWindow>();
        socketFrom = new HashMap<String, Socket>();
        socketTo = new HashMap<String, Socket>();
    }
}
