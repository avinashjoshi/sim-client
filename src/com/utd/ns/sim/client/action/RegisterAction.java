/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utd.ns.sim.client.action;

import com.utd.ns.sim.client.helper.Flags;
import com.utd.ns.sim.client.helper.Functions;
import com.utd.ns.sim.client.helper.Messages;
import com.utd.ns.sim.client.view.LoginForm;
import com.utd.ns.sim.packet.Packet;
import com.utd.ns.sim.packet.Serial;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 *
 * @author Avinash Joshi <avinash.joshi@utdallas.edu>
 */
public class RegisterAction implements ActionListener, Runnable {

    public LoginForm loginForm;

    public RegisterAction(LoginForm lgForm) {
        this.loginForm = lgForm;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (loginForm.validateInput()) {
            new Thread(this).start();
        } else {
            loginForm.showErrorMessage(Messages.LOGIN_CREDENTIALS_NEEDED);
        }
    }

    @Override
    public void run() {
        try {

            String command = "register";
            loginForm.connectToServer();

            /*
             * Crafting a packet to send to the server
             */
            Packet sendPacket = new Packet();
            Long nonce = Functions.generateNonce();
            sendPacket.craftPacket(command, Long.toString(nonce), loginForm.getUserName() + ":" + loginForm.getPassword());

            //Sending packet
            Serial.writeObject(Flags.socketToServer, sendPacket);

            // Wait for reply from server
            Packet recvPacket = (Packet) Serial.readObject(Flags.socketToServer);
            if (Functions.checkNonce(recvPacket.getNonce(), nonce + 1)) {
                loginForm.showErrorMessage(command + " success" + ": " + recvPacket.getData());
            } else {
                loginForm.showErrorMessage(command + " failed" + ": " + recvPacket.getData());
            }

        } catch (ClassNotFoundException ex) {
            loginForm.showErrorMessage("OOps! Error: " + ex.getMessage());
        } catch (IOException ex) {
            loginForm.showErrorMessage(Messages.CONNECTION_REFUSED + loginForm.getHostName() + ":" + "31337");
        }
    }
}
