package com.utd.ns.sim.client.helper;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

/*
 * This class will have all functions that might be necessary in the server
 * application
 */
/**
 *
 * @author Avinash Joshi <avinash.joshi@utdallas.edu>
 * @since April 23, 2012
 */
public class Functions {

    /**
     * Decrements long nonce by 1
     *
     * @param nonce nonce of type int
     * @return decremented nonce as String
     */
    public static String nonceFail(long nonce) {
        return (Long.toString(nonce - 1));
    }

    /**
     * Decrements String nonce by 1
     *
     * @param nonce nonce of type String
     * @return decremented nonce as String
     */
    public static String nonceFail(String nonce) {
        return (nonceFail(Long.parseLong(nonce)));
    }

    /**
     * Increments long nonce by 1
     *
     * @param nonce nonce of type int
     * @return incremented nonce as String
     */
    public static String nonceSuccess(long nonce) {
        return (Long.toString(nonce + 1));
    }

    /**
     * Increments String nonce by 1
     *
     * @param nonce nonce of type String
     * @return incremented nonce as String
     */
    public static String nonceSuccess(String nonce) {
        return (nonceSuccess(Long.parseLong(nonce)));
    }

    /**
     * Check if a string is incremented or decremented
     *
     * @param what returned nonce
     * @param toWhat nonce sent
     * @return true or false
     */
    public static boolean checkNonce(String what, long toWhat) {
        return checkNonce(Long.parseLong(what), toWhat);
    }

    /**
     * Check if a string is incremented or decremented
     *
     * @param what returned nonce
     * @param toWhat nonce sent
     * @return true or false
     */
    public static boolean checkNonce(long what, long toWhat) {
        if (what == toWhat) {
            return true;
        } else {
            return false;
        }
    }

    public static long generateNonce() {
        try {
            // Create a secure random number generator
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");

            // Get 1024 random bits
            byte[] bytes = new byte[1024 / 8];
            sr.nextBytes(bytes);

            // Create two secure number generators with the same seed
            int seedByteCount = 20;
            byte[] seed = sr.generateSeed(seedByteCount);

            sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(seed);
            SecureRandom sr2 = SecureRandom.getInstance("SHA1PRNG");
            sr2.setSeed(seed);
            return sr2.nextLong();
        } catch (NoSuchAlgorithmException e) {
        }
        //return NONCE;
        return Long.parseLong("0");
    }

    public static ArrayList<String> LoadCommands(String cmdString, String sep) {
        ArrayList<String> commands = new ArrayList<String>();
        String cmdList[] = cmdString.split(sep);
        int i = 0;
        while (i < cmdList.length) {
            commands.add(cmdList[i]);
            i++;
        }
        return commands;
    }
}
