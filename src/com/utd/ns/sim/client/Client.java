/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utd.ns.sim.client;

import com.utd.ns.sim.client.helper.UIHelper;
import com.utd.ns.sim.client.view.LoginForm;

/**
 *
 * @author avinash
 */
public class Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        UIHelper.setTitle("SIM");
        LoginForm loginForm = new LoginForm();
        loginForm.setVisible(true);
    }
}
