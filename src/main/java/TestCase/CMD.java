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

    BlockingQueue<String> queue;
    protected List<Item> listFlow1;//luu cac goi tin dau tien cua cac flow trong 6s đầu
    protected List<Double> listIAT1;//luu danh sach cac paket Inter-Arrival Time cua tung flow trong 6s đầu

    public CMD(){
        queue = new LinkedBlockingQueue<String>();
        listFlow1 = new ArrayList<Item>();
        listIAT1 = new ArrayList<Double>();
    }

    private int executeCommand( ) throws IOException {
        try {
            FileReader fr = new FileReader("F:\\CaiDaICMP.txt");
            BufferedReader reader = new BufferedReader(fr);
            ExecuteInfo info = new ExecuteInfo(queue);
            new Thread(info).start();
            String line = "";
            double start = Double.parseDouble(reader.readLine().split("\\t")[0]);

            int dem = 0;
            while ((line = reader.readLine())!=null) {
                queue.put(line);
            }
            queue.put("q");

        } catch (Exception e) {
            System.out.println(e.getMessage().toString());
        }
        return 0;
    }
    public static void main(String[] args) throws InterruptedException {
        try {
            new CMD().executeCommand();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean flowCompare(Item i,Item item) {
        if (i.getFieldValue(Flow.IP_SRC.toString()).equals(item.getFieldValue(Flow.IP_SRC.toString()))
                && i.getFieldValue(Flow.IP_DST.toString()).equals(item.getFieldValue(Flow.IP_DST.toString()))
                && i.getFieldValue(Flow.PORT_SRC.toString()).equals(item.getFieldValue(Flow.PORT_SRC.toString()))
                && i.getFieldValue(Flow.PORT_DST.toString()).equals(item.getFieldValue(Flow.PORT_DST.toString())))
            return true;

        return false;
    }

    public Item getItem1(Item item){
        for(Item i : listFlow1){
            if(flowCompare(i,item)){
                return i;
            }
        }
        return null;
    }

    public Item createItem(String a[]){
        Item item = new Item();
        item.setAttribute(Flow.TIME_STAMP.toString(), Double.parseDouble(a[0]));
        item.setAttribute(Flow.IP_SRC.toString(),a[1]);
        item.setAttribute(Flow.IP_DST.toString(),a[2]);
        item.setAttribute(Flow.PORT_SRC.toString(), a[3]);
        item.setAttribute(Flow.PORT_DST.toString(), a[4]);
        item.setAttribute(Flow.PROTOCOL.toString(),a[5]);
        item.setAttribute(Flow.BYTE_COUNT.toString(),Long.parseLong(a[6]));
        item.setAttribute(Flow.COUNT.toString(),1);
        return item;
    }
}
