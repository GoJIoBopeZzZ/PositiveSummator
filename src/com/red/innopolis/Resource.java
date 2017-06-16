package com.red.innopolis;

/**
 * Created by _red_ on 16.06.17.
 */
public class Resource {
    private static int sum = 0;
    
    public static synchronized void incrementSum() {
        sum++;
    }
    
    public static synchronized void printSum() {
        System.out.println("Sum - " + sum);
    }
}
