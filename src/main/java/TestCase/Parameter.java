package TestCase;

public class Parameter {
    double RATE_ICMP;
    double P_IAT;
    double PKT_SIZE_AVG;
    long TOTAL_PKT;

    public Parameter(double RATE_ICMP, double p_IAT, double PKT_SIZE_AVG, long TOTAL_PKT) {
        this.RATE_ICMP = RATE_ICMP;
        P_IAT = p_IAT;
        this.PKT_SIZE_AVG = PKT_SIZE_AVG;
        this.TOTAL_PKT = TOTAL_PKT;
    }

    public double getRATE_ICMP() {
        return RATE_ICMP;
    }

    public void setRATE_ICMP(double RATE_ICMP) {
        this.RATE_ICMP = RATE_ICMP;
    }

    public double getP_IAT() {
        return P_IAT;
    }

    public void setP_IAT(double p_IAT) {
        P_IAT = p_IAT;
    }

    public double getPKT_SIZE_AVG() {
        return PKT_SIZE_AVG;
    }

    public void setPKT_SIZE_AVG(double PKT_SIZE_AVG) {
        this.PKT_SIZE_AVG = PKT_SIZE_AVG;
    }

    public long getTOTAL_PKT() {
        return TOTAL_PKT;
    }

    public void setTOTAL_PKT(long TOTAL_PKT) {
        this.TOTAL_PKT = TOTAL_PKT;
    }
}
