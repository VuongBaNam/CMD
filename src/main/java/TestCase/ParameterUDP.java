package TestCase;

public class ParameterUDP {
    double entropy_ipsrc;
    double entropy_portsec;
    double entropy_portdst;
    double entropy_protocol;

    public ParameterUDP(double entropy_ipsrc, double entropy_portsec, double entropy_portdst, double entropy_protocol) {
        this.entropy_ipsrc = entropy_ipsrc;
        this.entropy_portsec = entropy_portsec;
        this.entropy_portdst = entropy_portdst;
        this.entropy_protocol = entropy_protocol;
    }

    public double getEntropy_ipsrc() {
        return entropy_ipsrc;
    }

    public void setEntropy_ipsrc(double entropy_ipsrc) {
        this.entropy_ipsrc = entropy_ipsrc;
    }

    public double getEntropy_portsec() {
        return entropy_portsec;
    }

    public void setEntropy_portsec(double entropy_portsec) {
        this.entropy_portsec = entropy_portsec;
    }

    public double getEntropy_portdst() {
        return entropy_portdst;
    }

    public void setEntropy_portdst(double entropy_portdst) {
        this.entropy_portdst = entropy_portdst;
    }

    public double getEntropy_protocol() {
        return entropy_protocol;
    }

    public void setEntropy_protocol(double entropy_protocol) {
        this.entropy_protocol = entropy_protocol;
    }
}
