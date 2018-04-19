package TestCase;

import com.google.gson.Gson;

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nam on 20/04/2017.
 */
public class CMD {
    // câu lệnh lấy thông tin packet sử dụng tshark

    public static final String command = "C:\\Program Files\\Wireshark\\tshark.exe -i \"Wireless Network Connection\" -T fields -e frame.time_relative -e ip.src -e ip.dst -e tcp.srcport -e tcp.dstport";
    //Địa chỉ ip của controller
    public static final String IP_CONTROLLER = "192.168.163.1";

    private int executeCommand(String command) {
        Process p;
        BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
//        ExecuteInfo executeInfo = new ExecuteInfo(queue);
//        new Thread(executeInfo).start();
        try {
            // Module thực hiện câu lệnh
            p = Runtime.getRuntime().exec(command);
//            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            //Module tính toán 2 thông số là % số flow có 1 gói tin và % paket inter-arrival time < 0.02s
            String line = "";
            long start = System.currentTimeMillis();// Thời điểm bắt đầ

            while (true){
                line = reader.readLine();
                System.out.println(new Date(System.currentTimeMillis()) +": "+line);
            }

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (true){
//                        try {
//                            queue.put(reader.readLine());
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
////                        try {
////                            Thread.sleep(1);
////                        } catch (InterruptedException e) {
////                            e.printStackTrace();
////                        }
//                    }
//                }
//            }).start();
        } catch (Exception e) {
            System.out.println(e.getMessage().toString());
        }
        return 0;
    }
    public static void main(String[] args) throws InterruptedException {
        Item item = new Item();

        item.setAttribute(Parameter.RATE_ICMP.toString(),0.002345);
        item.setAttribute(Parameter.P_IAT.toString(),0.12345);
        item.setAttribute(Parameter.PKT_SIZE_AVG.toString(),64.09090);
        item.setAttribute(Parameter.TOTAL_PKT.toString(),305000);

        Gson gson = new Gson();
        String str = gson.toJson(item);
        System.out.println(str);
    }

}
