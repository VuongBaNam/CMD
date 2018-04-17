package TestCase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ExecuteInfo implements Runnable{
    BlockingQueue<String> queue = null;
    protected List<Item> listFlow1;//luu cac goi tin dau tien cua cac flow trong 6s đầu
    protected List<Double> listIAT1;//luu danh sach cac paket Inter-Arrival Time cua tung flow trong 6s đầu

    protected List<Item> listFlow2;//luu cac goi tin dau tien cua cac flow trong 6s sau
    protected List<Double> listIAT2;//luu danh sach cac paket Inter-Arrival Time cua tung flow trong 6s sau

    public ExecuteInfo(BlockingQueue<String> queue) {
        this.queue = queue;
        listFlow1 = new ArrayList<Item>();
        listIAT1 = new ArrayList<Double>();
        listFlow2 = new ArrayList<Item>();
        listIAT2 = new ArrayList<Double>();
    }

    public void run() {
        String line = "";

        double oldTimeStamp = 0;
        long start = System.currentTimeMillis();
        while (true) {
            long current = System.currentTimeMillis();
            try {
                line = queue.poll(12, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println(new Date(System.currentTimeMillis())+": "+line);
            if (line == null) continue;

            String[] a = line.trim().split("\\t");

            Item item = createItem(a);
            if (item != null) {

                double itemPacket = (Double) item.getFieldValue(Flow.TIME_STAMP.toString());
                listIAT1.add(itemPacket - oldTimeStamp);
                oldTimeStamp = itemPacket;
                if(current - start > 12000){
                    new Statistics(listFlow1,listIAT1,listFlow2,listIAT2).statisticICMP();
                    start += 6000;
                }else {
                    //first là gói tin đầu tiên của luồng ứng với gói tin vừa nhận được
                    Item first = getItem1(item);

                    if (first == null) {//first = null => gói tin vừa nhận được là gói tin đầu tiên của luồng
                        listFlow1.add(item);
                    } else {
                        double fisrtPacket = (Double) first.getFieldValue(Flow.TIME_STAMP.toString());
                        double time = itemPacket - fisrtPacket;

                        //Kiểm tra xem gói tin1 vừa nhận được thuộc 6s đầu hay 6s sau
                        if (time / 6 < 1) {
                            int count = (Integer) first.getFieldValue(Flow.COUNT.toString());
                            first.setAttribute(Flow.COUNT.toString(), count + 1);//Tăng số gói tin của luồng lên 1
                        } else {// Gói tin vừa nhận được thuộc về 6s sau ta làm tương tự so với 6s đầu
                            Item f = getItem2(item);
                            if (f == null) {
                                listFlow2.add(item);
                            } else {
                                int count = (Integer) f.getFieldValue(Flow.COUNT.toString());
                                f.setAttribute(Flow.COUNT.toString(), count + 1);
                            }
                        }
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
    public Item getItem2(Item item){
        for(Item i : listFlow2){
            if(flowCompare(i,item)){
                return i;
            }
        }
        return null;
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
        item.setAttribute(Flow.COUNT.toString(),1);
        return item;
    }
}
