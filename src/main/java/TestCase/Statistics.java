package TestCase;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Statistics{
    private List<Item> listFlow1;//luu cac goi tin dau tien cua cac flow trong 6s đầu
    private List<Double> listIAT1;//luu danh sach cac paket Inter-Arrival Time cua tung flow trong 6s đầu

    private List<Item> listFlow2;//luu cac goi tin dau tien cua cac flow trong 6s sau
    private List<Double> listIAT2;//luu danh sach cac paket Inter-Arrival Time cua tung flow trong 6s sau

    public Statistics(List<Item> listFlow1, List<Double> listIAT1, List<Item> listFlow2, List<Double> listIAT2) {
        this.listFlow1 = listFlow1;
        this.listIAT1 = listIAT1;
        this.listFlow2 = listFlow2;
        this.listIAT2 = listIAT2;
    }
    public void statisticICMP() {
        if(listFlow1.size() != 0) {

            int NUM_ICMP = 0;//số flow có 1 gói tin
            int PKT_IAT_02 = 0;// số packet có inter-arrival time < 0.2ms

            int numberFlow = listFlow1.size();

            //Flow nào có danh sách paket Inter-Arrival Time rỗng (kích thước = 0) thì flow đó có 1 gói tin
            for (Item item : listFlow1) {
                String pro = (String) item.getFieldValue(Flow.PROTOCOL.toString());
                if(pro.equals("ICMP")){
                    NUM_ICMP += 1;
                }
            }

            //ONE_PKT_FLOW là %số flow có 1 gói tin trên tổng số flow
            double RATE_ICMP = NUM_ICMP * 1.0 / numberFlow;

            long size_IAT = listIAT1.size();
            for (double timeStamp : listIAT1) {
                if (timeStamp < 0.0002) {
                    PKT_IAT_02 += 1;
                }
            }
            //PKT_IAT là %số gói tin có paket Inter-Arrival Time < 0.02
            double P_IAT = (PKT_IAT_02 * 1.0 + 1) / size_IAT;

            //Xóa thông tin của 6s đầu
            listFlow1.clear();
            listIAT1.clear();

            //Lưu thông tin của 6s sau
            listFlow1 = listFlow2;
            listIAT1 = listIAT2;

            //Xóa thông tin của 6s sau
            listFlow2.clear();
            listIAT2.clear();

            //Module chạy thuật toán và gửi số z cho Contrller
            //Chạy thuật toán fuzzy để tìm ra số z
            double Z = FIS(RATE_ICMP, P_IAT);
            Date date = new Date(System.currentTimeMillis());
            System.out.println(date + ": "+Z+"\t" + RATE_ICMP+"\t"+P_IAT);
        }
    }
    public void run() {
        if(listFlow1.size() != 0) {

            int ONE_PKT_ON_FLOW = 0;//số flow có 1 gói tin
            int PKT_IAT_02 = 0;// số packet có inter-arrival time < 0.02s

            int numberFlow = listFlow1.size();

            //Flow nào có danh sách paket Inter-Arrival Time rỗng (kích thước = 0) thì flow đó có 1 gói tin
            for (Item item : listFlow1) {
                int packetPerFlow = (Integer) item.getFieldValue(Flow.COUNT.toString());
                if(packetPerFlow == 1){
                    ONE_PKT_ON_FLOW += 1;
                }
            }

            //ONE_PKT_FLOW là %số flow có 1 gói tin trên tổng số flow
            double PPF = ONE_PKT_ON_FLOW * 1.0 / numberFlow;

            long size_IAT = listIAT1.size();
            for (double timeStamp : listIAT1) {
                if (timeStamp < 0.0002) {
                    PKT_IAT_02 += 1;
                }
            }
            //PKT_IAT là %số gói tin có paket Inter-Arrival Time < 0.02
            double P_IAT = (PKT_IAT_02 * 1.0 - 1) / size_IAT;

            //Xóa thông tin của 6s đầu
            listFlow1.clear();
            listIAT1.clear();

            //Lưu thông tin của 6s sau
            listFlow1 = listFlow2;
            listIAT1 = listIAT2;

            //Xóa thông tin của 6s sau
            listFlow2.clear();
            listIAT2.clear();

            //Module chạy thuật toán và gửi số z cho Contrller
            //Chạy thuật toán fuzzy để tìm ra số z
            double Z = FIS(PPF, P_IAT);
            Date date = new Date(System.currentTimeMillis());
            System.out.println(date + ": " + Z);
        }
    }
    public static double FIS(double A1, double A2) {
        double z = 0;
        double a = 0.0;
        double b = 0.0;
        double c = 0.07;
        double d = 0.9;
        double a1 = 0.07;
        double b1 = 0.9;
        double c1 = 1.0;
        double d1 = 1.0;
        double e = 0.0;
        double f = 0.0;
        double g = 0.05;
        double h = 0.8;
        double e1 = 0.05;
        double f1 = 0.8;
        double g1 = 1.0;
        double h1 = 1.0;
        double A,B,C,D;

        if (b - a == 0) A = 1000;
        else A = (A1 - a) / (b - a);
        if (b1 - a1 == 0) C = 1000;
        else C = (A1 - a1) / (b1 - a1);
        if (d - c == 0) B = 1000;
        else B = (d - A1) / (d - c);
        if (d1 - c1 == 0) D = 1000;
        else D = (d1 - A1) / (d1 - c1);

        double q1 = min(A, 1.0);
        double Z3 = min(q1, B);
        double Fl1 = max(Z3, 0.0);
        double q2 = min(C, 1.0);
        double Z4 = min(q2, D);
        double Fh1 = max(Z4, 0.0);
        double E;
        double F;
        double G;
        double H;
        if (f - e == 0) E = 1000;
        else  E = (A2 - e) / (f - e);
        if (f1 - e1 == 0) G = 1000;
        else G = (A2 - e1) / (f1 - e1);
        if (h - g == 0) F = 1000;
        else F = (h - A2) / (h - g);
        if (h1 - g1 == 0) H = 1000;
        else H = (h1 - A2) / (h1 - g1);
        double q3 = min(E, 1.0); double Z1 = min(q3, F);
        double Fl2 = max(Z1, 0.0); double q4 = min(G, 1.0);
        double Z2 = min(q4, H); double Fh2 = max(Z2, 0.0);
        double W1 = min(Fl1, Fl2); double W2 = min(Fl1, Fh2);
        double W3 = min(Fh1, Fl2); double W4 = min(Fh1, Fh2);

        if (((A1 >= 0.99) && (A1 <= 1.0)) || ((A2 >= 0.9) && (A2 <= 1.0))) {
            z = 1;
        } else if (((A1 >= 0.0) && (A1 <= 0.8)) && ((A2 >= 0.0) && (A2 <= 0.15)))
        {
            z = 0;
        } else {
            z = (W2 + W3 + W4) / (W1 + W2 + W3 + W4);
        }
        return z;
    }
    private static double max(double t1, double t2) {
        double t3 = 0;
        if (t1 < t2) {
            t3 = t2;
        } else {
            t3 = t1;
        }
        return t3;
    }
    private static double min(double t1, double t2) {
        double t3 = 0;
        if (t1 < t2) {
            t3 = t1;
        } else {
            t3 = t2;
        }
        return t3;
    }
}
