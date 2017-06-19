package com.red.innopolis;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by _red_ on 16.06.17.
 */
public class Resource {
    private static volatile long sum = 0;
    private final static Lock lock = new ReentrantLock();
    
    public static synchronized void printSum () {
        System.out.println("Sum - " + sum);
    }
    
    public static synchronized void superIncrementSum (long number) {
        sum += number;
    }
    
    public static void lockLeakSum () {
        lock.lock();
        try {
            // доступ к совместно используемому ресурсу
            System.out.println("Sum - " + sum);
        } catch (Exception e) {
        } finally {
            lock.unlock();
        }
    }
    
    public static void lockLeakIncrementSum (long number) {
        lock.lock();
        try {
            // доступ к совместно используемому ресурсу
            sum += number;
        } catch (Exception e) {
        } finally {
            lock.unlock();
    
        }
    }
}