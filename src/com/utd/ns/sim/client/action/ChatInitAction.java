/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utd.ns.sim.client.action;

import com.utd.ns.sim.client.helper.Flags;
import com.utd.ns.sim.client.helper.Functions;
import com.utd.ns.sim.client.helper.Messages;
import com.utd.ns.sim.client.view.ChatWindow;
import com.utd.ns.sim.client.view.UserList;
import com.utd.ns.sim.packet.Packet;
import com.utd.ns.sim.packet.Serial;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Avinash Joshi <avinash.joshi@utdallas.edu>
 */
public class ChatInitAction implements ActionListener, Runnable {

    public UserList uListForm;
    public String userToChat;
    private Socket socketToUser;

    public ChatInitAction(UserList uForm) {
        this.uListForm = uForm;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (uListForm.getSelectedUser() != null) {
            userToChat = uListForm.getSelectedUser();
            if (Flags.chatSession.contains(userToChat)) {
                uListForm.showErrorMessage("You are already chatting with " + userToChat);
            } else {
                new Thread(this).start();
            }
        }
    }

    public boolean connectToServer(String host, String port) {
        return connectToServer(host, Integer.parseInt(port));
    }

    public boolean connectToServer(String host, int port) {

        try {
            // Connect to socket
            socketToUser = new Socket(host, port);
            uListForm.showErrorMessage("Connected to " + host + "...");
            return true;
        } catch (UnknownHostException ex) {
            uListForm.showErrorMessage(Messages.HOST_NOT_FOUND + host);
            return false;
        } catch (IOException ex) {
            uListForm.showErrorMessage(Messages.CONNECTION_REFUSED + host + ":" + port);
            return false;
        }

    }

    @Override
    public void run() {
        try {

            String command = "talk";

            /*
             * Crafting a packet to send to the server
             */
            String dataToSend = Flags.sessionUserName + ":" + userToChat;
            System.out.println(dataToSend);
            Packet sendPacket = new Packet();
            long nonce = Functions.generateNonce();
            sendPacket.craftPacket(command, Long.toString(nonce), dataToSend);
            //Sending packet
            System.out.println("Sending command talk " + Flags.socketToServer);
            Serial.writeObject(Flags.socketToServer, sendPacket);
            System.out.println("Out of write");

            // Wait for reply from server
            Packet recvPacket = (Packet) Serial.readObject(Flags.socketToServer);
            System.out.println("In here");
            String commandReceived = recvPacket.getCommand();
            String data = recvPacket.getData();
            Packet internalPacket = recvPacket.pkt;
            if (Functions.checkNonce(recvPacket.getNonce(), nonce + 1)) {
                //Received correct packet

                //Connect to IP:port
                System.out.println("Packet inside packet");
                uListForm.showErrorMessage(data + " - " + internalPacket.getData());
                /*
                 * Try contacting that client's ip:port and check for response
                 */
                String[] ipPort = data.split(":");
                //userToChat = ipPort[0];
                String key = ipPort[1];
                if (connectToServer(ipPort[2], ipPort[3])) {
                    internalPacket.pkt = new Packet();
                    long timeStamp = System.currentTimeMillis();
                    internalPacket.pkt.setNonce(Long.toString(timeStamp));
                    Serial.writeObject(socketToUser, internalPacket);
                    System.out.println("Sent ticket to client");
                    recvPacket = (Packet) Serial.readObject(socketToUser);
                    System.out.println("Received packet from client");
                    if (Functions.checkNonce(recvPacket.getNonce(), timeStamp + 1)) {
                        //Done!
                        //open ChatWindow
                        ChatWindow cWin = new ChatWindow(socketToUser, userToChat);
                        cWin.setVisible(true);
                        Flags.chatSession.add(this.userToChat);
                        System.out.println("Chatting!!!!");
                    }
                    uListForm.showErrorMessage(recvPacket.getData());
                }
            } else {
                System.out.println("Just packet");
                uListForm.showErrorMessage(command + " failed" + ": " + recvPacket.getData());
            }
        } catch (ClassNotFoundException ex) {
            uListForm.showErrorMessage("OOps! Error: " + ex.getMessage());
        } catch (IOException ex) {
            uListForm.showErrorMessage(ex.getMessage());
        }
    }
}
