package events;

import dk.i1.diameter.AVP;
import dk.i1.diameter.AVP_Grouped;
import dk.i1.diameter.AVP_Integer32;
import dk.i1.diameter.AVP_UTF8String;
import dk.i1.diameter.AVP_Unsigned32;
import dk.i1.diameter.AVP_Unsigned64;
import dk.i1.diameter.Message;
import dk.i1.diameter.MessageHeader;
import dk.i1.diameter.node.Node;
import dk.i1.diameter.node.SimpleSyncClient;
import java.util.HashMap;
import java.util.Scanner;
import sshroaming.SSHFunction;

public class Mms
        extends Event2 {

    private String a_number;
    private String modifier;
    private String code;
    private long cc_service_unit;
    private String id;

    public Mms(SimpleSyncClient test, String a_number, long cc_service_unit, String modifier, String code, String id) {
        super(test);
        this.a_number = a_number;
        this.cc_service_unit = cc_service_unit;
        this.modifier = modifier;
        this.code = code;
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
        this.avps.add(new AVP_Unsigned32(415, 0));

        this.avps.add(new AVP_Grouped(443, new AVP[]{new AVP_Integer32(450, 0), new AVP_UTF8String(444, this.a_number)}));

        this.avps.add(new AVP_Unsigned32(455, 1));

        this.avps.add(new AVP_Grouped(456, new AVP[]{new AVP_Unsigned32(432, 88), new AVP_Grouped(437, new AVP[]{new AVP_Unsigned64(417, this.cc_service_unit)}), new AVP_Grouped(1004107, 4329, new AVP[]{new AVP_UTF8String(1004109, 4329, "M"), new AVP_UTF8String(1004110, 4329, "1"), new AVP_UTF8String(1004111, 4329, "1"), new AVP_UTF8String(1004112, 4329, "1"), new AVP_UTF8String(1004113, 4329, "1"), new AVP_UTF8String(1004114, 4329, "1"), new AVP_UTF8String(1004115, 4329, this.modifier)})}));

        AVP service_type = new AVP_Unsigned32(1052, 1);
        service_type.vendor_id = 2011;
        this.avps.add(service_type);

        this.avps.add(new AVP_Unsigned32(436, 0));
    }

    protected void successHandle() {
        SSHFunction ssh = new SSHFunction();
        HashMap<String, String> result = ssh.getLastTransaction(getSession(), this.code, this.a_number);
        String xml = (String) result.get("xml");
        if (result.containsKey("xml") && !result.containsKey("grepError")) {
            String basicValue = (String) result.get("basicValue");
            String afterAllowance = (String) result.get("afterAllowance");
            setResponseData("### " + getSession() + " ### " + xml + " ### MMS (" + this.a_number + ") TERKIRIM ke (" + this.modifier + ")" + ", pemakaian:  Charge Amount " + basicValue + ", After monetary allowance " + afterAllowance);
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
        HashMap<String, String> result = ssh.getLastTransaction(getSession(), this.code, this.a_number);
        String xml = (String) result.get("xml");
        if (result.containsKey("xml") && !result.containsKey("grepError")) {

            String basicValue = (String) result.get("basicValue");
            String afterAllowance = (String) result.get("afterAllowance");
            setResponseData("### " + getSession() + " ### " + xml + " ### MMS (" + this.a_number + ") TERKIRIM ke (" + this.modifier + ")" + ", pemakaian:  Charge Amount " + basicValue + ", After monetary allowance " + afterAllowance);
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
