package com.yunfeng.anysdk.tool;

public class Log {

    public static void d(Object message) {
        if (null == message) {
            System.out.println("log.d with null object");
        } else {
            System.out.println(message.toString());
        }
    }

    public static void e(Object message) {
        if (null == message) {
            System.err.println("log.e with null object");
        } else {
            System.err.println(message.toString());
        }
    }
}
