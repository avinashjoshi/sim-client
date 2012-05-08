/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utd.ns.sim.client.action;

import com.utd.ns.sim.client.helper.Flags;
import com.utd.ns.sim.client.helper.Functions;
import com.utd.ns.sim.client.helper.Messages;
import com.utd.ns.sim.client.listener.TCPListener;
import com.utd.ns.sim.client.view.LoginForm;
import com.utd.ns.sim.client.view.UserList;
import com.utd.ns.sim.crypto.AES;
import com.utd.ns.sim.crypto.RSA;
import com.utd.ns.sim.crypto.SHA;
import com.utd.ns.sim.packet.Packet;
import com.utd.ns.sim.packet.Serial;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi;

/**
 *
 * @author Avinash Joshi <avinash.joshi@utdallas.edu>
 */
public class LoginAction implements ActionListener, Runnable {

    public LoginForm loginForm;

    public LoginAction(LoginForm lgForm) {
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
            String command = "login";

            loginForm.connectToServer();
            /*
             * Crafting a packet to send to the server
             */
            Packet sendPacket = new Packet();
            Long nonce = Functions.generateNonce();
            
            String timeStamp = Long.toString(System.currentTimeMillis());
            
            /*
             * Key = H(usernameH(password))
             */
            Flags.sessionAESKey = SHA.SHA512String(loginForm.getUserName() + SHA.SHA256String(loginForm.getPassword()));
            
            ArrayList<String> nonceToSend = AES.doEncryptDecryptHMAC(Long.toString(nonce), Flags.sessionAESKey, 'E');
            String dataToSend = RSA.encrypt(loginForm.getUserName()
                    + ":" + loginForm.getPassword()
                    + ":" + timeStamp
                    , Flags.rsaKey);

            sendPacket.craftPacket(command, nonceToSend.get(1), dataToSend);

            //Sending packet
            Serial.writeObject(Flags.socketToServer, sendPacket);

            // Wait for reply from server
            Packet recvPacket = (Packet) Serial.readObject(Flags.socketToServer);
            //System.out.println(recvPacket.getNonce() + "-" + Long.toString(nonce+1));
            if (Functions.checkNonce(recvPacket.getNonce(), nonce + 1)) {
                loginForm.showErrorMessage(command + " success" + ": " + recvPacket.getData());
                Flags.sessionUserName = loginForm.getUserName();
                // Open a TCP Listner on random port
                TCPListener tcpListen = new TCPListener(recvPacket.getNonce());
                tcpListen.start();
                UserList uList = new UserList();
                this.loginForm.setVisible(false);
                uList.setVisible(true);
            } else {
                loginForm.showErrorMessage(command + " failed" + ": " + recvPacket.getData());
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(LoginAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            loginForm.showErrorMessage("OOps! Error: " + ex.getMessage());
        } catch (IOException ex) {
            loginForm.showErrorMessage(Messages.CONNECTION_REFUSED + loginForm.getHostName() + ":" + "31337");
        }

    }
}
