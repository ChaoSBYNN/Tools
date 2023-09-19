package com.example.testIP;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2021/10/14 13:41
 */
public class TestIP {

    public static void main(String[] args) {
        try {
            boolean result = InetAddress.getByName("172.24.185.113").isReachable(10);
            System.out.println(result);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(true);
    }

}
