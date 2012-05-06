package com.utd.ns.sim.client.listener;

import com.utd.ns.sim.client.helper.Flags;
import com.utd.ns.sim.packet.Packet;
import com.utd.ns.sim.packet.Serial;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Logger;

/*
 * TCPListner keeps listening for new TCP connections from various clients
 */
/**
 *
 * @author Avinash Joshi <avinash.joshi@utdallas.edu>
 * @since April 19, 2012
 */
public class TCPListener extends Thread {

    private TCPConnect tcpConnection;
    private String nonce;
    private Socket listenSock;

    public TCPListener(String nonce) {
        this.nonce = nonce;
    }

    @Override
    public void run() {
        try {
            /*
             * Creating ServerSocket() Note that I user Flags so that the socket
             * can be closed from main()
             */
            Flags.clientSocket = new ServerSocket(0);
            /*
             * Notifying server the TCPListner socket that is generated randomly
             * above
             */
            Packet sendPacket = new Packet();
            sendPacket.craftPacket("myport", Long.toString(Long.parseLong(nonce) + 10), Flags.sessionUserName 
                    + ":" + Flags.clientSocket.getLocalPort());
            System.out.println(Flags.clientSocket);
            Serial.writeObject(Flags.socketToServer, sendPacket);
            
            Flags.clientNumber = 0;
            
            while (Flags.endClient == false) {
                // Listening for incoming connections
                listenSock = Flags.clientSocket.accept();

                //Adding entry into all Sockets
                Flags.clientNumberWriteLock.lock();
                try {

                    Flags.allSocketList.put(Flags.clientNumber, listenSock);
                    Flags.clientNumber++;

                    //Starting a new thread for actual processing!
                    tcpConnection = new TCPConnect(listenSock, Flags.clientNumber);
                    tcpConnection.start();
                    System.out.println("Started TCPConnect");
                } finally {
                    Flags.clientNumberWriteLock.unlock();
                }
            }
        } catch (Exception e) {
        }
    }
}