package TestCase;

public class ParameterTCP {
    private String ip;
    private double entropy_port_src;

    public ParameterTCP(String ip, double entropy_port_src) {
        this.ip = ip;
        this.entropy_port_src = entropy_port_src;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public double getEntropy_port_src() {
        return entropy_port_src;
    }

    public void setEntropy_port_src(double entropy_port_src) {
        this.entropy_port_src = entropy_port_src;
    }
}
