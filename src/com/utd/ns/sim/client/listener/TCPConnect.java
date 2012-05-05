/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utd.ns.sim.client.listener;

import java.net.Socket;

/**
 *
 * @author Avinash Joshi <avinash.joshi@utdallas.edu>
 */
class TCPConnect extends Thread {
    private final Socket sock;
    private final int clientNumber;
    private final String sessionUser;

    TCPConnect(Socket listenSock, int clientNumber) {
        sock = listenSock;
        this.clientNumber = clientNumber;
        sessionUser = null;
    }

    @Override
    public void run() {
    }
}
