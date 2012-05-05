/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utd.ns.sim.client.view;

import com.utd.ns.sim.client.helper.Flags;
import com.utd.ns.sim.client.helper.Functions;
import com.utd.ns.sim.client.helper.Messages;
import com.utd.ns.sim.packet.Packet;
import com.utd.ns.sim.packet.Serial;
import java.io.IOException;

/**
 *
 * @author avinash
 */
public class UserList extends javax.swing.JFrame {
    public String thisUser;

    /**
     * Creates new form UserList
     */
    public UserList() {
        initComponents();
        thisUser = Flags.sessionUserName;
        setUserNameLbl(Flags.sessionUserName);
        refreshUserList();
    }

    public final void refreshUserList() {
        try {
            String command = "list";
            /*
             * Crafting a packet to send to the server
             */
            Packet sendPacket = new Packet();
            long nonce = Functions.generateNonce();
            sendPacket.craftPacket(command, Long.toString(nonce), Flags.sessionUserName);
            //Sending packet
            Serial.writeObject(Flags.socketToServer, sendPacket);

            // Wait for reply from server
            Packet recvPacket = (Packet) Serial.readObject(Flags.socketToServer);
            if (Functions.checkNonce(recvPacket.getNonce(), nonce + 1)) {
                String data = recvPacket.getData();
                errorMsg.setVisible(false);

                if ("".equalsIgnoreCase(data)) {
                    showErrorMessage("No users online!");
                }
                if (data.contains(",")) {
                    Flags.loggedInUserList = data.split(",");
                } else {
                    Flags.loggedInUserList = new String[1];
                    Flags.loggedInUserList[0] = data;
                }
                userList.setListData(Flags.loggedInUserList);
            } else {
                showErrorMessage(command + " failed" + ": " + recvPacket.getData());
            }
        } catch (ClassNotFoundException ex) {
            showErrorMessage("OOps! Error: " + ex.getMessage());
        } catch (IOException ex) {
            showErrorMessage(ex.getMessage());
        }
    }

    public boolean validateChoice() {
        errorMsg.setVisible(false);

        if (this.userList.getSelectedIndex() < 0) {
            return false;
        }

        return true;
    }

    public void hideErrorMessage() {
        errorMsg.setVisible(false);
    }

    public String getSelectedUser() {
        if (validateChoice()) {
            int listIndex = userList.getSelectedIndex();
            return (Flags.loggedInUserList[listIndex]);
        } else {
            showErrorMessage(Messages.NOT_SELECTED);
            return null;
        }
    }

    public final void setUserNameLbl(String message) {
        uNameLbl.setVisible(true);
        uNameLbl.setText("");
        uNameLbl.setText(message);
    }

    public void showErrorMessage(String message) {
        errorMsg.setVisible(true);
        errorMsg.setText("");
        errorMsg.setText("<html>* " + message + "</html>");
    }

    public void logout() {
        try {
            String command = "logout";
            /*
             * Crafting a packet to send to the server
             */
            Packet sendPacket = new Packet();
            long nonce = Functions.generateNonce();
            sendPacket.craftPacket(command, Long.toString(nonce), Flags.sessionUserName);
            //Sending packet
            Serial.writeObject(Flags.socketToServer, sendPacket);

            // Wait for reply from server
            Packet recvPacket = (Packet) Serial.readObject(Flags.socketToServer);
            System.exit(0);
            if (Functions.checkNonce(recvPacket.getNonce(), nonce + 1)) {
                //Logout successful
            } else {
                showErrorMessage(command + " failed" + ": " + recvPacket.getData());
            }
        } catch (ClassNotFoundException ex) {
            showErrorMessage("OOps! Error: " + ex.getMessage());
        } catch (IOException ex) {
            showErrorMessage(ex.getMessage());
        }
    }

    /*
     * Initialize chat. Get user details from server
     */
    public void chatInit(String username) {
        try {

            String command = "talk";

            /*
             * Crafting a packet to send to the server
             */
            String dataToSend = thisUser + ":" + username;
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
                String[] split = data.split(":");
                //Connect to IP:port
                showErrorMessage(data + " - " + internalPacket.getData());
            } else {
                showErrorMessage(command + " failed" + ": " + recvPacket.getData());
            }
        } catch (ClassNotFoundException ex) {
            showErrorMessage("OOps! Error: " + ex.getMessage());
        } catch (IOException ex) {
            showErrorMessage(ex.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        userList = new javax.swing.JList();
        titleLbl = new javax.swing.JLabel();
        chat = new javax.swing.JButton();
        logout = new javax.swing.JButton();
        refresh = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        uNameLbl = new javax.swing.JLabel();
        errorMsg = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SIM: User List");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jScrollPane1.setViewportView(userList);

        titleLbl.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        titleLbl.setText("SIM: Logged In Users");

        chat.setText("Chat");
        chat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                chatMouseClicked(evt);
            }
        });

        logout.setText("Logout");
        logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutMouseClicked(evt);
            }
        });

        refresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/utd/ns/sim/client/view/images/Refresh.png"))); // NOI18N
        refresh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                refreshMouseClicked(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 2, 13)); // NOI18N
        jLabel1.setText("Howdy, ");

        uNameLbl.setFont(new java.awt.Font("Lucida Grande", 2, 13)); // NOI18N
        uNameLbl.setText(" ");

        errorMsg.setLocation(new java.awt.Point(-32756, -32100));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(errorMsg, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 213, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(chat)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(logout, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 76, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 219, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(layout.createSequentialGroup()
                                    .add(jLabel1)
                                    .add(2, 2, 2)
                                    .add(uNameLbl))
                                .add(layout.createSequentialGroup()
                                    .add(titleLbl)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(refresh))))
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(uNameLbl))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(titleLbl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(refresh))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 166, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(chat)
                    .add(logout))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(errorMsg, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void refreshMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_refreshMouseClicked
        refreshUserList();
    }//GEN-LAST:event_refreshMouseClicked

    private void logoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseClicked
        logout();
    }//GEN-LAST:event_logoutMouseClicked

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        logout();
    }//GEN-LAST:event_formWindowClosed

    private void chatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chatMouseClicked
        if (getSelectedUser() != null) {
            chatInit(getSelectedUser());
        }
    }//GEN-LAST:event_chatMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton chat;
    private javax.swing.JLabel errorMsg;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton logout;
    private javax.swing.JButton refresh;
    private javax.swing.JLabel titleLbl;
    private javax.swing.JLabel uNameLbl;
    private javax.swing.JList userList;
    // End of variables declaration//GEN-END:variables
}
