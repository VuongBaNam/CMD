package TestCase;

public class IPLink {
    String IP;
    String link;

    public IPLink(String IP, String link) {
        this.IP = IP;
        this.link = link;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
