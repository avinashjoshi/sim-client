/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.utd.ns.sim.client.helper;

import java.net.ServerSocket;
import java.net.Socket;
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
    
    public Flags() {
        
    }
}
