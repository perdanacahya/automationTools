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
import sshroaming.SSHFunction;

public class SmsTerminate
        extends Event2 {

    private String a_number;
    private String b_number;
    private String cell_info;
    private String code;
    private String vlr;
    private int quantities;

    public SmsTerminate(SimpleSyncClient test, String a_number, String b_number, String cell_info, int quantities, String code, String vlr) {
        super(test);
        this.a_number = a_number;
        this.b_number = b_number;
        this.cell_info = cell_info;
        this.quantities = quantities;
        this.code = code;
        this.vlr = vlr;
    }

    protected void prepareRequest() {
        this.avps.hdr.command_code = 272;
        this.avps.hdr.application_id = 4;
        this.avps.hdr.setRequest(true);
        this.avps.hdr.setProxiable(true);

        this.avps.add(new AVP_UTF8String(263, "Automation;sms;" + a_number.substring(a_number.length() - 8) + getSession()));

        this.connection.node().addOurHostAndRealm(this.avps);

        this.avps.add(new AVP_Unsigned32(258, 4));

        this.avps.add(new AVP_UTF8String(461, "123@DOX"));
        this.avps.add(new AVP_Integer32(416, 3));
        this.avps.add(new AVP_Unsigned32(415, 1));

        this.avps.add(new AVP_Grouped(443, new AVP[]{new AVP_Integer32(450, 0), new AVP_UTF8String(444, this.a_number)}));

        this.avps.add(new AVP_Grouped(456, new AVP[]{new AVP_Integer32(439, 2), new AVP_Grouped(446, new AVP[]{new AVP_Unsigned64(417, 2011, this.quantities)})}));

        AVP service_type = new AVP_Unsigned32(1052, 1);
        service_type.vendor_id = 2011;
        this.avps.add(service_type);

        AVP party_address = new AVP_UTF8String(831, this.a_number);
        party_address.vendor_id = 2011;
        this.avps.add(party_address);

        AVP_UTF8String cell_info_avp = new AVP_UTF8String(25041, this.cell_info);
        cell_info_avp.vendor_id = 2011;
        this.avps.add(cell_info_avp);

        AVP_UTF8String called_party_address = new AVP_UTF8String(832, this.b_number);
        called_party_address.vendor_id = 2011;
        this.avps.add(called_party_address);

        AVP_UTF8String vlr_id = new AVP_UTF8String(25005, this.vlr);
        vlr_id.vendor_id = 2011;
        this.avps.add(vlr_id);

        AVP_UTF8String sms_rn = new AVP_UTF8String(26007, "0");
        sms_rn.vendor_id = 2011;
        this.avps.add(sms_rn);
    }

    protected void successHandle() {
        SSHFunction ssh = new SSHFunction();
        HashMap<String, String> result = ssh.getLastTransaction(getSession(), this.code, this.a_number);
        String xml = (String) result.get("xml");
        if (result.containsKey("xml")) {
            String basicValue = (String) result.get("basicValue");
            String afterAllowance = (String) result.get("afterAllowance");
            setResponseData("### " + getSession() + " ### " + xml + " ### SMS Terminate OK" + ", Sisa pulsa:  Charge Amount " + basicValue + ", After monetary allowance " + afterAllowance);
        }
    }

    protected void noResponseHandle() {
        SSHFunction ssh = new SSHFunction();
        HashMap<String, String> result = ssh.getLastTransaction(getSession(), this.code, this.a_number);
        String xml = (String) result.get("xml");
        if (result.containsKey("xml") && result.containsKey("grepErrorSession")) {
            String basicValue = (String) result.get("basicValue");
            String afterAllowance = (String) result.get("afterAllowance");
            setResponseData("### " + getSession() + " ### " + xml + " ### SMS Terminate OK" + ", Sisa pulsa:  Charge Amount " + basicValue + ", After monetary allowance " + afterAllowance);
        } else if (result.containsKey("xml") && !result.containsKey("grepError")) {
            String basicValue = (String) result.get("basicValue");
            String afterAllowance = (String) result.get("afterAllowance");
            setResponseData("### " + getSession() + " ### " + xml + " ### SMS Terminate OK" + ", Sisa pulsa:  Charge Amount " + basicValue + ", After monetary allowance " + afterAllowance);

        } else if (result.containsKey("xml")) {
            setResponseData("### " + getSession() + " ### " + xml + " ### " + result.get("grepError"));
        } else {
            start();
        }

    }
    protected void failHandle(int rc)
  {
      SSHFunction ssh = new SSHFunction();
        HashMap<String, String> result = ssh.getLastTransaction(getSession(), this.code, this.a_number);
        String xml = (String) result.get("xml");
        if (result.containsKey("xml") && result.containsKey("grepErrorSession")) {
            String basicValue = (String) result.get("basicValue");
            String afterAllowance = (String) result.get("afterAllowance");
            setResponseData("### " + getSession() + " ### " + xml + " ### SMS Terminate OK" + ", Sisa pulsa:  Charge Amount " + basicValue + ", After monetary allowance " + afterAllowance);
        } else if (result.containsKey("xml") && !result.containsKey("grepError")) {
            String basicValue = (String) result.get("basicValue");
            String afterAllowance = (String) result.get("afterAllowance");
            setResponseData("### " + getSession() + " ### " + xml + " ### SMS Terminate OK" + ", Sisa pulsa:  Charge Amount " + basicValue + ", After monetary allowance " + afterAllowance);

        } else {
            setResponseData("### " + getSession() + " ### " + xml + " ### " + result.get("grepError"));
        }
  }
}
