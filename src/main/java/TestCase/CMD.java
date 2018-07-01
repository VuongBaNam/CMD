package TestCase;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nam on 20/04/2017.
 */
public class CMD {
    public static void main(String[] args) throws InterruptedException {
        Set<String> set = new HashSet<>();
        set.add("10.0.0.1");
        set.add("10.0.0.1");
        System.out.println(set.size());
    }
}
