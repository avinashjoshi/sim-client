/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utd.ns.sim.client.action;

import com.utd.ns.sim.client.view.ChatWindow;
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

    private final ChatWindow chatWindow;
    public static BufferedReader in;
    public static PrintWriter out;
    private boolean bufferNotSet = true;

    public ChattingAction(ChatWindow aThis) {
        chatWindow = aThis;

        //this.run();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (!chatWindow.hasChatStarted()) {
            new Thread(this).start();
            chatWindow.chatStarted();
        } 
        while (bufferNotSet) {
            
        }
        if (chatWindow.getChatLine().equals("")) {
            return;
        }
        out.println(chatWindow.getChatLine());
        chatWindow.sendClicked();
        out.flush();
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(
                    chatWindow.getSocket().getInputStream()));
            out = new PrintWriter(chatWindow.getSocket().getOutputStream(), true);
            
            bufferNotSet = false;

            // Process all messages from server, according to the protocol.
            while (true) {
                String line = in.readLine();
                if (line != null && !line.equals("")) {
                    chatWindow.updateChatArea(line);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ChattingAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
