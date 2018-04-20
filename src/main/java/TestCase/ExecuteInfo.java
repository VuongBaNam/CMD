package TestCase;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static TestCase.Utils.IP_CONTROLLER;
import static TestCase.Utils.PORT;

public class ExecuteInfo implements Runnable{

    FileWriter fw = new FileWriter("F:\\New folder\\data_pcap\\result.txt");
    BufferedWriter bw = new BufferedWriter(fw);
    BlockingQueue<String> queue = null;
    protected List<Item> listFlow1;//luu cac goi tin dau tien cua cac flow trong 6s đầu
    protected List<Double> listIAT1;//luu danh sach cac paket Inter-Arrival Time cua tung flow trong 6s đầu
    private Socket socket;
    private ObjectOutputStream out ;

    public ExecuteInfo(BlockingQueue<String> queue) throws IOException {

//        socket = new Socket(IP_CONTROLLER,PORT);
//        out = new ObjectOutputStream(socket.getOutputStream());
        this.queue = queue;
        listFlow1 = new ArrayList<Item>();
        listIAT1 = new ArrayList<Double>();
    }

    public void run() {
        String line = "";

        try {
            line = queue.poll(12, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double start = Double.parseDouble(line.split("\\t")[0]);
        double oldTimeStamp = 0;
        int dem = 0;
        while (true) {
            try {
                line = queue.poll(12, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println(new Date(System.currentTimeMillis())+": "+line);
            if (line == null) continue;
            if (line .equals("q")) {
                try {
                    System.out.println(++dem);
                    Parameter parameter = new Statistics(listFlow1,listIAT1,socket).statisticICMP();
                    bw.write(parameter.getRATE_ICMP()+",");
                    bw.write(parameter.getP_IAT()+",");
                    bw.write(parameter.getPKT_SIZE_AVG()+",");
                    bw.write(parameter.getTOTAL_PKT()+"\n");
                    bw.close();
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return;
            }

            String[] a = line.trim().split("\\t");

            Item item = createItem(a);
            if (item != null) {

                double itemPacket = (Double) item.getFieldValue(Flow.TIME_STAMP.toString());
                long byteCount = (Long) item.getFieldValue(Flow.BYTE_COUNT.toString());
                listIAT1.add(itemPacket - oldTimeStamp);
                oldTimeStamp = itemPacket;
                if(itemPacket - start > 6){
                    try {
                        System.out.println(++dem);
                        Parameter parameter = new Statistics(listFlow1,listIAT1,socket).statisticICMP();
                        bw.write(parameter.getRATE_ICMP()+",");
                        bw.write(parameter.getP_IAT()+",");
                        bw.write(parameter.getPKT_SIZE_AVG()+",");
                        bw.write(parameter.getTOTAL_PKT()+"\n");
                        listFlow1.add(item);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    start = itemPacket;
                }else {
                    //first là gói tin đầu tiên của luồng ứng với gói tin vừa nhận được
                    Item first = getItem1(item);

                    if (first == null) {//first = null => gói tin vừa nhận được là gói tin đầu tiên của luồng
                        listFlow1.add(item);
                    } else {
                        int count = (Integer) first.getFieldValue(Flow.COUNT.toString());
                        long byte_count = (Long) first.getFieldValue(Flow.BYTE_COUNT.toString());
                        first.setAttribute(Flow.COUNT.toString(), count + 1);
                        first.setAttribute(Flow.BYTE_COUNT.toString(), byte_count + byteCount);
                    }
                }
            }
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
        item.setAttribute(Flow.COUNT.toString(),Integer.valueOf(1));
        return item;
    }
}