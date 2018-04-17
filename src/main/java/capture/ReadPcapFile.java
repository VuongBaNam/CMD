
package capture;

import java.io.*;
import java.net.Inet4Address;
import java.util.concurrent.TimeoutException;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapHandle.TimestampPrecision;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.IpNumber;
import org.pcap4j.packet.namednumber.TcpPort;
import org.pcap4j.util.ByteArrays;

@SuppressWarnings("javadoc")
public class ReadPcapFile {

    private static final int COUNT = 5;

    private static final String PCAP_FILE_KEY
            = ReadPcapFile.class.getName() + ".pcapFile";
    private static final String PCAP_FILE
            = System.getProperty(PCAP_FILE_KEY, "F:\\series_0.pcap");

    private ReadPcapFile() {
    }

    public static void main(String[] args) throws PcapNativeException, NotOpenException, IllegalRawDataException, IOException {
        PcapHandle handle;
        try {
            handle = Pcaps.openOffline(PCAP_FILE, TimestampPrecision.NANO);
        } catch (PcapNativeException e) {
            handle = Pcaps.openOffline(PCAP_FILE);
        }

        Packet packet ;

        FileWriter fw = new FileWriter("F:\\CaiDaICMP.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        int dem = 0;

        while ((packet = handle.getNextPacket()) != null) {
            dem ++;
            StringBuilder sb = new StringBuilder();
            sb.append("A packet captured at ")
                    .append(handle.getTimestamp())
                    .append(":");

            byte[] data = packet.getRawData();

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

                if (p != 1) {
                    srcPort = TcpPort.getInstance(ByteArrays.getShort(data, 0 + headerLength));
                    dstPort = TcpPort.getInstance(ByteArrays.getShort(data, 2 + headerLength));
                }

                double timeStamp = handle.getTimestamp().getTime() * 1.0 / 1000;
                String ipSrc = ipsrc.getHostAddress();
                String ipDst = ipdst.getHostAddress();
                int portSrc = srcPort.valueAsInt();
                int portDst = dstPort.valueAsInt();
                String pro = "ICMP";
                if (p == 6) {
                    pro = "TCP";
                } else if (p == 17) {
                    pro = "UDP";
                }
                String str = timeStamp + "\t" + ipSrc + "\t" + ipDst + "\t" + portSrc + "\t" + portDst + "\t" + pro;
                bw.write(str +"\n");
            }
        }
        bw.close();
        fw.close();
        handle.close();
    }
}