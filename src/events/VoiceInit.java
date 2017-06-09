package events;

import db.Database;
import dk.i1.diameter.AVP;
import dk.i1.diameter.AVP_Grouped;
import dk.i1.diameter.AVP_Integer32;
import dk.i1.diameter.AVP_Time;
import dk.i1.diameter.AVP_UTF8String;
import dk.i1.diameter.AVP_Unsigned32;
import dk.i1.diameter.Message;
import dk.i1.diameter.MessageHeader;
import dk.i1.diameter.node.Node;
import dk.i1.diameter.node.SimpleSyncClient;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import sshroaming.SSHFunction;

public class VoiceInit
        extends Event2 {

    private String number;
    private String cell_info;
    private String dest;
    private String timezone;
    private String vlr;
    private String id;
    private String code;
    private VoiceTerminate.AREA area;
    private VoiceTerminate.VOICE_TYPE type;
    private VoiceTerminate.VOICE_CALL callType;

    public VoiceInit(SimpleSyncClient client, String number, String dest, String cell_info, String timezone, String vlr, VoiceTerminate.AREA area, VoiceTerminate.VOICE_TYPE type, VoiceTerminate.VOICE_CALL callType, String id, String code) {
        super(client);
        if (callType == VoiceTerminate.VOICE_CALL.MOC) {
            this.number = number;
            this.dest = dest;
        } else {
            this.number = dest;
            this.dest = number;
        }
        this.timezone = timezone;
        this.area = area;
        this.type = type;
        this.vlr = vlr;
        this.cell_info = cell_info;
        this.callType = callType;
        this.id = id;
        this.code = code;
    }

    protected void prepareRequest() {
        this.avps.hdr.command_code = 272;
        this.avps.hdr.application_id = 4;
        this.avps.hdr.setRequest(true);
        this.avps.hdr.setProxiable(true);

        this.avps.add(new AVP_UTF8String(263, "Automation;voice;" + number.substring(number.length() - 8) + getSession()));

        this.connection.node().addOurHostAndRealm(this.avps);

        this.avps.add(new AVP_Unsigned32(258, 4));

        this.avps.add(new AVP_UTF8String(461, "0"));
        this.avps.add(new AVP_Integer32(416, 1));
        this.avps.add(new AVP_Unsigned32(415, 0));

        this.avps.add(new AVP_Grouped(443, new AVP[]{new AVP_Integer32(450, 0), new AVP_UTF8String(444, this.callType == VoiceTerminate.VOICE_CALL.MOC ? this.number : this.dest)}));

        this.avps.add(new AVP_Grouped(456, new AVP[]{new AVP_Integer32(439, 1)}));

        AVP service_type = new AVP_Unsigned32(1052, this.callType == VoiceTerminate.VOICE_CALL.MOC ? 1 : 2);
        service_type.vendor_id = 2011;
        this.avps.add(service_type);

        AVP service_subtype = new AVP_Unsigned32(1053, this.type == VoiceTerminate.VOICE_TYPE.VIDEO_CALL ? 7 : this.type == VoiceTerminate.VOICE_TYPE.VOICE_ONLY ? 1 : 8);

        service_subtype.vendor_id = 2011;
        this.avps.add(service_subtype);

        AVP party_address = new AVP_UTF8String(831, this.number);
        party_address.vendor_id = 2011;
        this.avps.add(party_address);

        AVP_UTF8String cell_info_avp = new AVP_UTF8String(25041, this.cell_info);
        cell_info_avp.vendor_id = 2011;
        this.avps.add(cell_info_avp);

        AVP_UTF8String called_party_address = new AVP_UTF8String(832, this.dest);
        called_party_address.vendor_id = 2011;
        this.avps.add(called_party_address);

        this.avps.add(new AVP_Grouped(25020, new AVP[]{new AVP_UTF8String(20522, "1"), new AVP_UTF8String(25021, this.dest)}));

        this.avps.add(new AVP_UTF8String(1005, this.dest));

        this.avps.add(new AVP_Time(2041, (int) (System.currentTimeMillis() / 1000L)));
        if (this.area == VoiceTerminate.AREA.LOCAL) {
            this.avps.add(new AVP_UTF8String(23, this.timezone));
        } else if (this.area == VoiceTerminate.AREA.INTERNATIONAL) {
            this.avps.add(new AVP_UTF8String(25033, this.timezone));
        }
        AVP_UTF8String vlr_id = new AVP_UTF8String(25005, this.vlr);
        vlr_id.vendor_id = 2011;
        this.avps.add(vlr_id);
    }

    protected void successHandle() {

        setResponseData("### " + getSession() + (this.type == VoiceTerminate.VOICE_TYPE.VOICE_ONLY ? " ### Voice Init OK" : " ### Video Init OK"));
        System.out.println("### " + getSession() + (this.type == VoiceTerminate.VOICE_TYPE.VOICE_ONLY ? " ### Voice Init OK" : " ### Video Init OK"));

    }

    protected void noResponseHandle() {
        SSHFunction ssh = new SSHFunction();
        HashMap<String, String> result = ssh.getLastTransaction(getSession(), this.code, this.number);
        String xml = (String) result.get("xml");
        if (result.containsKey("grepErrorSession")) {
            Database db = new Database();
            try {
                ssh.deleteSession(db.getAccountRef(number));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GprsInit.class.getName()).log(Level.SEVERE, null, ex);
            }
            start();
        } else if (result.containsKey("xml") && !result.containsKey("grepError")) {
            System.out.println("No Respond no Error");
            setResponseData("### " + getSession() + (this.type == VoiceTerminate.VOICE_TYPE.VOICE_ONLY ? " ### Voice Init OK" : " ### Video Init OK"));
            System.out.println("### " + getSession() + (this.type == VoiceTerminate.VOICE_TYPE.VOICE_ONLY ? " ### Voice Init OK" : " ### Video Init OK"));
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

    protected void failHandle(int rc) {
        SSHFunction ssh = new SSHFunction();
        HashMap<String, String> result = ssh.getLastTransaction(getSession(), this.code, this.number);
        String xml = (String) result.get("xml");
        if (result.containsKey("grepErrorSession")) {
            Database db = new Database();
            try {
                ssh.deleteSession(db.getAccountRef(number));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GprsInit.class.getName()).log(Level.SEVERE, null, ex);
            }
            start();
        } else if (result.containsKey("xml") && !result.containsKey("grepError")) {
            setResponseData("### " + getSession() + (this.type == VoiceTerminate.VOICE_TYPE.VOICE_ONLY ? " ### Voice Init OK" : " ### Video Init OK"));
            System.out.println("### " + getSession() + (this.type == VoiceTerminate.VOICE_TYPE.VOICE_ONLY ? " ### Voice Init OK" : " ### Video Init OK"));
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
}
