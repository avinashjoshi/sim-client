/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utd.ns.sim.client.action;

import com.utd.ns.sim.client.view.ChatWindow;
import com.utd.ns.sim.crypto.AES;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Avinash Joshi <avinash.joshi@utdallas.edu>
 */
public class ChattingAction implements ActionListener, Runnable {

    private ChatWindow chatWindow;
    private BufferedReader in;
    private PrintWriter out;
    private String stringToSend;

    public ChattingAction(ChatWindow aThis) {
        chatWindow = aThis;

        //this.run();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (!chatWindow.hasChatStarted()) {
            System.out.println("started new thread...");
            new Thread(this).start();
            chatWindow.chatStarted();
        } 
        if (chatWindow.getChatLine().equals("")) {
            return;
        }
        stringToSend = AES.doEncryptDecryptHMACToString(chatWindow.getChatLine(), chatWindow.getSessionKey(), 'E');
        stringToSend = stringToSend.replaceAll("\n", "");
        stringToSend = stringToSend.replaceAll("\r", "");
        out.println(stringToSend);
        chatWindow.sendClicked();
        out.flush();
    }

    @Override
    public void run() {
        try {
            this.in = new BufferedReader(new InputStreamReader(
                    chatWindow.getSocket().getInputStream()));
            this.out = new PrintWriter(chatWindow.getSocket().getOutputStream(), true);
            

            // Process all messages from server, according to the protocol.
            while (true) {
                String line = this.in.readLine();
                if (line != null && !line.equals("")) {
                    line = AES.doEncryptDecryptHMACToString(line, chatWindow.getSessionKey(), 'D');
                    chatWindow.updateChatArea(line);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ChattingAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
