/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utd.ns.sim.client.listener;

import com.utd.ns.sim.client.helper.Flags;
import com.utd.ns.sim.client.helper.Functions;
import com.utd.ns.sim.client.view.ChatWindow;
import com.utd.ns.sim.crypto.AES;
import com.utd.ns.sim.packet.Packet;
import com.utd.ns.sim.packet.Serial;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Avinash Joshi <avinash.joshi@utdallas.edu>
 */
class TCPConnect extends Thread {

    private final Socket sock;
    private final int clientNumber;
    private final String sessionUser;
    private Packet packet;
    private Packet sendPacket;
    private String command;
    private String nonce;
    private String data;
    private Packet internalPacket;
    private ChatWindow cWin;
    private String dataToSend;
    private String sessionKey;

    TCPConnect(Socket listenSock, int clientNumber) {
        sock = listenSock;
        this.clientNumber = clientNumber;
        sessionUser = null;
    }

    @Override
    public void run() {
        try {
            while (true) {
                packet = (Packet) Serial.readObject(sock);

                sendPacket = new Packet();  // Creating a new packet to send back
                command = packet.getCommand();  // Get the command
                nonce = packet.getNonce();      // Get the Nonce
                data = packet.getData();        // Get the data
                
                command = AES.doEncryptDecryptHMACToString(command, Flags.sessionAESKey, 'D');
                nonce = AES.doEncryptDecryptHMACToString(nonce, Flags.sessionAESKey, 'D');
                data = AES.doEncryptDecryptHMACToString(data, Flags.sessionAESKey, 'D');

                if (command.equals("")
                        || data.equals("")
                        || nonce.equals("")) {
                    /*
                     * Warn the client that an invalid packet was sent!
                     */
                    sendPacket.craftPacket("error.log", nonce + 1, "Invalid packet!");
                    Serial.writeObject(sock, sendPacket);
                    continue;
                } else if (command.equals("talkrequest")) {
                    long currTime = System.currentTimeMillis();
                    if (currTime - Long.parseLong(nonce) > 5000) {
                        //Received invalid packet... discarding
                        System.out.println("Time difference less than 5000");
                    } else {
                        internalPacket = packet.pkt;
                        if (internalPacket == null) {
                            //error = something went wrong!!!
                            //Hacked packet
                            continue;
                        }
                        String[] dataValue = data.split(":");
                        sessionKey = dataValue[1];
                        nonce = AES.doEncryptDecryptHMACToString(internalPacket.getNonce(), sessionKey, 'D');
                        if (Flags.chatSession.contains(dataValue[0])) {
                            //user already chatting
                            //Just a check - not necessary in normal conditions
                            System.out.println("Already chatting???");
                            continue;
                        }
                        int choice = JOptionPane.showOptionDialog(null,
                                "User " + dataValue[0] + " wants to talk to you!", "Chat Request for " + Flags.sessionUserName,
                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                        if (choice == 0) {
                            nonce = AES.doEncryptDecryptHMACToString(Functions.nonceSuccess(nonce), sessionKey, 'E');
                            dataToSend = AES.doEncryptDecryptHMACToString("YES", sessionKey, 'E');
                            sendPacket.craftPacket("talkresponse", nonce, dataToSend);
                            Serial.writeObject(sock, sendPacket);
                            //Open chatwindow
                            cWin = new ChatWindow(sock, dataValue[0], sessionKey);
                            sessionKey = null;
                            cWin.setVisible(true);
                            Flags.chatSession.add(dataValue[0]);
                            break;
                        } else {
                            nonce = AES.doEncryptDecryptHMACToString(Functions.nonceFail(nonce), sessionKey, 'E');
                            dataToSend = AES.doEncryptDecryptHMACToString("NOT", sessionKey, 'E');
                            sendPacket.craftPacket("talkresponse", nonce, dataToSend);
                            Serial.writeObject(sock, sendPacket);
                        }
                    }
                } else {
                    sendPacket.craftPacket("talkresponse", nonce, "fail");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(TCPConnect.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TCPConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
