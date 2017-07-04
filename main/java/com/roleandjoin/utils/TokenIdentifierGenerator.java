/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.roleandjoin.utils;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 *
 * @author alejandro
 */
public class TokenIdentifierGenerator {

    private static SecureRandom random = new SecureRandom();

    public static String nextSessionId() {
        return new BigInteger(130, random).toString(32);
    }
}
