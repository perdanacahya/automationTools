package events;

import dk.i1.diameter.AVP;
import dk.i1.diameter.AVP_Grouped;
import dk.i1.diameter.AVP_Integer32;
import dk.i1.diameter.AVP_Integer64;
import dk.i1.diameter.AVP_UTF8String;
import dk.i1.diameter.AVP_Unsigned32;
import dk.i1.diameter.Message;
import dk.i1.diameter.MessageHeader;
import dk.i1.diameter.node.Node;
import dk.i1.diameter.node.SimpleSyncClient;
import java.util.HashMap;
import java.util.Scanner;
import sshroaming.SSHFunction;

public class TalkMania
        extends Event2 {

    private String a_number;
    private String cost_code;
    private String id;

    public TalkMania(SimpleSyncClient test, String a_number, String cost_code, String id) {
        super(test);
        this.a_number = a_number;
        this.cost_code = cost_code;
        this.id = id;
    }

    protected void prepareRequest() {
        this.avps.hdr.command_code = 272;
        this.avps.hdr.application_id = 4;
        this.avps.hdr.setRequest(true);
        this.avps.hdr.setProxiable(true);

        this.avps.add(new AVP_UTF8String(263, getSession()));

        this.connection.node().addOurHostAndRealm(this.avps);

        this.avps.add(new AVP_Unsigned32(258, 4));

        this.avps.add(new AVP_UTF8String(461, "123@DOX"));
        this.avps.add(new AVP_Integer32(416, 4));
        this.avps.add(new AVP_Unsigned32(415, 1));

        this.avps.add(new AVP_Grouped(443, new AVP[]{new AVP_Integer32(450, 0), new AVP_UTF8String(444, this.a_number)}));

        this.avps.add(new AVP_Unsigned32(455, 1));

        this.avps.add(new AVP_Grouped(456, new AVP[]{new AVP_Unsigned32(432, 91), new AVP_Grouped(437, new AVP[]{new AVP_Grouped(413, new AVP[]{new AVP_Grouped(445, new AVP[]{new AVP_Integer64(447, 1L)})})}), new AVP_Grouped(1004107, 4329, new AVP[]{new AVP_UTF8String(1004109, 4329, "ChargingGW"), new AVP_UTF8String(1004110, 4329, this.cost_code)})}));

        this.avps.add(new AVP_Unsigned32(436, 1));
    }

    protected void successHandle() {
        SSHFunction ssh = new SSHFunction();
        HashMap<String, String> result = ssh.getLastTransaction(getSession(), "83u", this.a_number);
        String xml = (String) result.get("xml");
        if (result.containsKey("xml") && !result.containsKey("grepError")) {
            setResponseData("### " + getSession() + " ### " + xml + " ### TalkMania " + this.cost_code + " (" + this.a_number + ") ACTIVATED");
        } else {
            System.out.println("\n" + id + " " + "### " + getSession() + " ### " + xml + " ### " + result.get("grepError"));
            String input = "";
            do {
                System.out.println("\n y to rerun, n to continue run, x to close?(y/n/x)\n");
                Scanner scanner = new Scanner(System.in);
                input = scanner.next();
            } while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n") && !input.equalsIgnoreCase("x"));

            if (input.equalsIgnoreCase("y")) {
                start();
            } else if (input.equalsIgnoreCase("n")) {
                setResponseData("### " + getSession() + " ### " + xml + " ### " + result.get("grepError"));
            } else if (input.equalsIgnoreCase("x")) {
                setCloseParameter(1);
                setResponseData("### " + getSession() + " ### " + xml + " ### " + result.get("grepError"));
            }
        }
    }

    protected void noResponseHandle() {
        SSHFunction ssh = new SSHFunction();
        HashMap<String, String> result = ssh.getLastTransaction(getSession(), "83u", this.a_number);
        String xml = (String) result.get("xml");
        if (result.containsKey("xml") && !result.containsKey("grepError")) {
            setResponseData("### " + getSession() + " ### " + xml + " ### TalkMania " + this.cost_code + " (" + this.a_number + ") ACTIVATED");
        } else if (result.containsKey("xml")) {
            System.out.println("\n" + id + " " + "### " + getSession() + " ### " + xml + " ### " + result.get("grepError"));
            System.out.println("No Respond Error");
            String input = "";
            do {
                System.out.println("\n y to rerun, n to continue run, x to close?(y/n/x)?(y/n/x)\n");
                Scanner scanner = new Scanner(System.in);
                input = scanner.next();
            } while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n") && !input.equalsIgnoreCase("x"));

            if (input.equalsIgnoreCase("y")) {
                start();
            } else if (input.equalsIgnoreCase("n")) {
                setResponseData("### " + getSession() + " ### " + xml + " ### " + result.get("grepError"));
            } else if (input.equalsIgnoreCase("x")) {
                setCloseParameter(1);
                setResponseData("### " + getSession() + " ### " + xml + " ### " + result.get("grepError"));
            }
        } else {
            start();
        }
    }
    protected void failHandle(int rc)
  {
      throw new UnsupportedOperationException("Please check the environtment daemon...");
  }
}
