package capture;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import TestCase.ExecuteInfo;
import org.pcap4j.core.*;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.IpNumber;
import org.pcap4j.packet.namednumber.IpVersion;
import org.pcap4j.packet.namednumber.TcpPort;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.NifSelector;

public class PackageCapture {

    public static BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
    public static final int max = Integer.MAX_VALUE;

    public static void main(String [] args) throws PcapNativeException, IOException, NotOpenException, InterruptedException {
//        if (args == null) {
//            System.out.println("Plesase enter arguments IP....");
//            return;
//        }
        ExecuteInfo executeInfo = new ExecuteInfo(queue);
        new Thread(executeInfo).start();
        String filter = null;
        if (args.length != 0) {
            filter = args[0];
        }

//        InetAddress addr = InetAddress.getByName("192.168.10.100");
//        PcapNetworkInterface nif = Pcaps.getDevByAddress(addr);
        PcapNetworkInterface nif = new NifSelector().selectNetworkInterface();
        if (nif == null) {
            System.exit(1);
        }

        final PcapHandle handle = nif.openLive(65536, PromiscuousMode.PROMISCUOUS, 10);

        if (filter != null && filter.length() != 0) {
            handle.setFilter(filter, BpfCompileMode.OPTIMIZE);
        }

        PacketListener listener = new PacketListener() {
            public void gotPacket(Packet packet) {
                try {
                    printPacket(packet, handle);
                } catch (IllegalRawDataException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        while (true)
            handle.loop(1, listener);
    }

    private static void printPacket(Packet packet, PcapHandle ph) throws IllegalRawDataException, InterruptedException {

        int packetLength = packet.length();

        EthernetPacket ethernetPacket = EthernetPacket.newPacket(packet.getRawData(), 0, packet.length());

        Packet ipV4packet = ethernetPacket.getPayload();
        byte[] data = ipV4packet.getRawData();

        IpNumber protocol = IpNumber.getInstance(ByteArrays.getByte(data, 9 ));

        byte p = protocol.value();
        if(p == 6 || p == 17 || p == 1) {
            Inet4Address ipsrc = ByteArrays.getInet4Address(data, 12);
            Inet4Address ipdst = ByteArrays.getInet4Address(data, 16);
            byte versionAndIhl = ByteArrays.getByte(data, 0);
            byte ihl = (byte) (versionAndIhl & 15);
            int headerLength = (255 & ihl) * 4;

            TcpPort srcPort = TcpPort.ECHO;
            TcpPort dstPort = TcpPort.ECHO;

            if(p != 1) {
                srcPort = TcpPort.getInstance(ByteArrays.getShort(data, headerLength));
                dstPort = TcpPort.getInstance(ByteArrays.getShort(data, 2 + headerLength));
            }

            double timeStamp = ph.getTimestamp().getTime()*1.0/1000;
            String ipSrc = ipsrc.getHostAddress();
            String ipDst = ipdst.getHostAddress();
            int portSrc = srcPort.valueAsInt();
            int portDst = dstPort.valueAsInt();
            String pro = "ICMP";
            if(p == 6){
                pro = "TCP";
            }else if(p == 17){
                pro = "UDP";
            }
            String str = timeStamp +"\t"+ipSrc+"\t"+ipDst+"\t"+portSrc+"\t"+portDst+"\t"+pro+"\t"+packetLength;

//            System.out.println(str);

            queue.put(str);

            //System.out.println(ipsrc.getHostAddress() + "  " + ipdst.getHostAddress() + " " + srcPort.valueAsString() + " " + dstPort.valueAsString());
        }
    }
}

