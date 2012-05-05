/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.utd.ns.sim.client.action;

import com.utd.ns.sim.client.view.UserList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Avinash Joshi <avinash.joshi@utdallas.edu>
 */
public class ChatAction implements ActionListener, Runnable {
    public UserList uListForm;

    public ChatAction(UserList uForm) {
        this.uListForm = uForm;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    

}
