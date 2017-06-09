package events;

import dk.i1.diameter.AVP;
import dk.i1.diameter.AVP_Address;
import dk.i1.diameter.AVP_Grouped;
import dk.i1.diameter.AVP_Integer32;
import dk.i1.diameter.AVP_OctetString;
import dk.i1.diameter.AVP_UTF8String;
import dk.i1.diameter.AVP_Unsigned32;
import dk.i1.diameter.AVP_Unsigned64;
import dk.i1.diameter.Message;
import dk.i1.diameter.MessageHeader;
import dk.i1.diameter.node.Node;
import dk.i1.diameter.node.SimpleSyncClient;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import sshroaming.SSHFunction;

public class GprsTerminatePayu
        extends Event2 {

    private String a_number;
    private String station_id;
    private String sgsn;
    private String id;
    private String cgi = null;
    private String session;
    private int rating_group;
    private int ccnumber;
    private long total_octets;

    protected void noResponseHandle() {
        SSHFunction ssh = new SSHFunction();
        HashMap<String, String> result = ssh.getLastTransaction(this.session, "53u", this.a_number);
        String xml = (String) result.get("xml");
        if (result.containsKey("xml") && result.containsKey("grepErrorSession")) {
            String basicValue = (String) result.get("basicValue");
            String afterAllowance = (String) result.get("afterAllowance");
            setResponseData("### " + this.session + " ### " + xml + " ### GPRS " + this.total_octets + " B OK" + ", pemakaian:  Charge Amount " + basicValue + ", After monetary allowance " + afterAllowance);
        } else if (result.containsKey("xml") && !result.containsKey("grepError")) {
            System.out.println("No Respond no Error");
            String basicValue = (String) result.get("basicValue");
            String afterAllowance = (String) result.get("afterAllowance");
            setResponseData("### " + this.session + " ### " + xml + " ### GPRS " + this.total_octets + " B OK" + ", pemakaian:  Charge Amount " + basicValue + ", After monetary allowance " + afterAllowance);
            System.out.println("### " + this.session + " ### " + xml + " ### GPRS " + this.total_octets + " B OK" + ", pemakaian:  Charge Amount " + basicValue + ", After monetary allowance " + afterAllowance);
        } else {
            System.out.println("\n" + id + " " + "### " + this.session + " ### " + xml + " ### " + result.get("errorcode") + " " + result.get("grepError"));
            System.out.println("No Respond Error");
            String input = "";
            do {
                System.out.println("\n y to rerun, n to continue run, x to close?(y/n/x)\n");
                Scanner scanner = new Scanner(System.in);
                input = scanner.next();
            } while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n") && !input.equalsIgnoreCase("x"));

            if (input.equalsIgnoreCase("y")) {
                start();
            } else if (input.equalsIgnoreCase("n")) {
                setResponseData("### " + this.session + " ### " + xml + " ### " + result.get("grepError"));
            } else if (input.equalsIgnoreCase("x")) {
                setCloseParameter(1);
                setResponseData("### " + this.session + " ### " + xml + " ### " + result.get("grepError"));
            }
        }
    }


    public GprsTerminatePayu(SimpleSyncClient test, String a_number, String session, int ccnumber, String station_id, String sgsn, int rating_group, long total_octets, String id) {
        super(test);
        this.a_number = a_number;
        this.station_id = station_id;
        this.rating_group = rating_group;
        this.ccnumber = ccnumber;
        this.sgsn = sgsn;
        this.total_octets = total_octets;
        this.id = id;
        this.session=session;
    }

    public GprsTerminatePayu(SimpleSyncClient test, String a_number, String session, int ccnumber, String station_id, String sgsn, int rating_group, long total_octets, String cgi, String id) {
        this(test, a_number, session, ccnumber, station_id, sgsn, rating_group, total_octets, id);
        this.cgi = cgi;
    }

    protected void prepareRequest() {
        this.avps.hdr.command_code = 272;
        this.avps.hdr.application_id = 4;
        this.avps.hdr.setRequest(true);
        this.avps.hdr.setProxiable(true);

        this.avps.add(new AVP_UTF8String(263, "Automation;gprs;" + a_number.substring(a_number.length() - 8) + ";" + this.session));

        this.connection.node().addOurHostAndRealm(this.avps);
        this.avps.add(new AVP_UTF8String(264, "8080808"));

        this.avps.add(new AVP_Unsigned32(258, 4));

        this.avps.add(new AVP_UTF8String(461, "123@DOX"));
        this.avps.add(new AVP_Integer32(416, 3));

        this.avps.add(new AVP_Unsigned32(415, this.ccnumber));

        this.avps.add(new AVP_Grouped(443, new AVP[]{new AVP_Integer32(450, 0), new AVP_UTF8String(444, this.a_number)}));

        this.avps.add(new AVP_Grouped(456, new AVP[]{new AVP_Integer32(432, this.rating_group), new AVP_Grouped(446, new AVP[]{new AVP_Unsigned64(421, this.total_octets), new AVP_Unsigned64(412, 0L), new AVP_Unsigned64(414, 0L)})}));

        AVP_Integer32 msi = new AVP_Integer32(455, 1);

        AVP_UTF8String trigpp_charge_id = new AVP_UTF8String(2, "1");
        trigpp_charge_id.vendor_id = 10415;
        this.avps.add(trigpp_charge_id);

        AVP_UTF8String trigpp_ggsn = new AVP_UTF8String(9, "2");
        this.avps.add(trigpp_ggsn);

        AVP_UTF8String trigpp_sggsn = new AVP_UTF8String(18, this.sgsn);
        trigpp_sggsn.vendor_id = 10415;
        this.avps.add(trigpp_sggsn);

        AVP_UTF8String called_stat_id = new AVP_UTF8String(30, this.station_id);
        this.avps.add(called_stat_id);

        AVP_UTF8String framed_ip_add = new AVP_UTF8String(8, "1");
        this.avps.add(framed_ip_add);
        if (this.cgi != null) {
            int len = 5;
            String[] cgi_temp = this.cgi.split("(?<=\\G.{" + len + "})");
            cgi_temp[0] = ("00" + cgi_temp[0].charAt(1) + cgi_temp[0].charAt(0) + "F" + cgi_temp[0].charAt(2) + cgi_temp[0].charAt(4) + cgi_temp[0].charAt(3));

            cgi_temp[1] = Integer.toHexString(Integer.parseInt(cgi_temp[1]));
            cgi_temp[1] = ("0000".substring(cgi_temp[1].length()) + cgi_temp[1]);

            cgi_temp[2] = Integer.toHexString(Integer.parseInt(cgi_temp[2]));
            cgi_temp[2] = ("0000".substring(cgi_temp[2].length()) + cgi_temp[2]);

            AVP_OctetString cgi_avp = new AVP_OctetString(22, DatatypeConverter.parseHexBinary(cgi_temp[0] + cgi_temp[1] + cgi_temp[2]));
            cgi_avp.vendor_id = 10415;
            this.avps.add(cgi_avp);
        }
        try {
            this.avps.add(new AVP_Address(6, 10415, InetAddress.getByName("0.0.0.0")));
        } catch (UnknownHostException ex) {
            Logger.getLogger(GprsTerminatePayu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void successHandle() {
        SSHFunction ssh = new SSHFunction();
        HashMap<String, String> result = ssh.getLastTransaction(this.session, "53u", this.a_number);
        String xml = (String) result.get("xml");
        if (result.containsKey("xml") && result.containsKey("grepErrorSession")) {
            String basicValue = (String) result.get("basicValue");
            String afterAllowance = (String) result.get("afterAllowance");
            setResponseData("### " + this.session + " ### " + xml + " ### GPRS " + this.total_octets + " B OK" + ", pemakaian:  Charge Amount " + basicValue + ", After monetary allowance " + afterAllowance);
        } else if (result.containsKey("xml") && !result.containsKey("grepError")) {
            String basicValue = (String) result.get("basicValue");
            String afterAllowance = (String) result.get("afterAllowance");
            setResponseData("### " + this.session + " ### " + xml + " ### GPRS " + this.total_octets + " B OK" + ", pemakaian:  Charge Amount " + basicValue + ", After monetary allowance " + afterAllowance);
            System.out.println("### " + this.session + " ### " + xml + " ### GPRS " + this.total_octets + " B OK" + ", pemakaian:  Charge Amount " + basicValue + ", After monetary allowance " + afterAllowance);
        } else {
            System.out.println("\n" + id + " " + "### " + this.session + " ### " + xml + " ### " + result.get("grepError"));
            String input = "";
            do {
                System.out.println("\n y to rerun, n to continue run, x to close?(y/n/x)\n");
                Scanner scanner = new Scanner(System.in);
                input = scanner.next();
            } while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n") && !input.equalsIgnoreCase("x"));

            if (input.equalsIgnoreCase("y")) {
                start();
            } else if (input.equalsIgnoreCase("n")) {
                setResponseData("### " + this.session + " ### " + xml + " ### " + result.get("grepError"));
            } else if (input.equalsIgnoreCase("x")) {
                setCloseParameter(1);
                setResponseData("### " + this.session + " ### " + xml + " ### " + result.get("grepError"));
            }
        }
    }
    protected void failHandle(int rc)
  {
      throw new UnsupportedOperationException("Please check the environtment daemon...");
  }
}
