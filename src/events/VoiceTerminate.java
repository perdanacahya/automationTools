package events;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import sshroaming.SSHFunction;

public class VoiceTerminate
        extends Event2 {

    private String number;
    private String cell_info;
    private String dest;
    private String timezone;
    private int duration;
    private int ccnumber;
    private String vlr;
    private AREA area;
    private VOICE_TYPE type;
    private VOICE_CALL callType;
    private VOICE_TERMINATE voiceTerminate;
    private String code;

    protected void noResponseHandle() {
        SSHFunction ssh = new SSHFunction();
        HashMap<String, String> result = ssh.getLastTransaction(getSession(), this.code, this.number);
        String xml = (String) result.get("xml");
        if (result.containsKey("xml") && result.containsKey("grepErrorSession")) {
            String basicValue = (String) result.get("basicValue");
            String afterAllowance = (String) result.get("afterAllowance");
            if (this.voiceTerminate == VOICE_TERMINATE.TERMINATE) {
                setResponseData("### " + getSession() + " ### " + xml + " ### Pulsa yang terpakai " + "A#(" + this.number + ") ke B#(" + this.dest + ") selama " + this.duration + "s: Charge Amount " + basicValue + ", After monetary allowance " + afterAllowance);
            } else {
                setResponseData("### " + getSession() + (this.type == VOICE_TYPE.VOICE_ONLY ? " ### Voice Intermediate " + this.duration + "s OK" : new StringBuilder().append(" ### Video Intermediate ").append(this.duration).append("s OK").toString()));
                System.out.println("### " + getSession() + (this.type == VOICE_TYPE.VOICE_ONLY ? " ### Voice Intermediate " + this.duration + "s OK" : new StringBuilder().append(" ### Video Intermediate ").append(this.duration).append("s OK").toString()));
            }
        } else if (result.containsKey("xml") && !result.containsKey("grepError")) {
            String basicValue = (String) result.get("basicValue");
            String afterAllowance = (String) result.get("afterAllowance");
            if (this.voiceTerminate == VOICE_TERMINATE.TERMINATE) {
                setResponseData("### " + getSession() + " ### " + xml + " ### Pulsa yang terpakai " + "A#(" + this.number + ") ke B#(" + this.dest + ") selama " + this.duration + "s: Charge Amount " + basicValue + ", After monetary allowance " + afterAllowance);
            } else {
                setResponseData("### " + getSession() + (this.type == VOICE_TYPE.VOICE_ONLY ? " ### Voice Intermediate " + this.duration + "s OK" : new StringBuilder().append(" ### Video Intermediate ").append(this.duration).append("s OK").toString()));
                System.out.println("### " + getSession() + (this.type == VOICE_TYPE.VOICE_ONLY ? " ### Voice Intermediate " + this.duration + "s OK" : new StringBuilder().append(" ### Video Intermediate ").append(this.duration).append("s OK").toString()));
            }
        } else if (result.containsKey("xml")) {
            setResponseData("### " + getSession() + " ### " + xml + " ### " + result.get("grepError"));
        } else {
            start();
        }
    }

    public static enum AREA {
        LOCAL, INTERNATIONAL;

        private AREA() {
        }
    }

    public static enum VOICE_TYPE {
        VOICE_ONLY, VIDEO_CALL, UCB;

        private VOICE_TYPE() {
        }
    }

    public static enum VOICE_CALL {
        MOC, MTC;

        private VOICE_CALL() {
        }
    }

    public static enum VOICE_TERMINATE {
        INTERMEDIATE, TERMINATE;

        private VOICE_TERMINATE() {
        }
    }

    public VoiceTerminate(SimpleSyncClient client, String number, String dest, String cell_info, String timezone, String vlr, int ccnumber, int duration, AREA area, VOICE_TYPE type, VOICE_CALL callType, VOICE_TERMINATE voiceTerminate, String code) {
        super(client);
        if (callType == VOICE_CALL.MOC) {
            this.number = number;
            this.dest = dest;
        } else {
            this.number = dest;
            this.dest = number;
        }
        this.voiceTerminate = voiceTerminate;
        this.cell_info = cell_info;
        this.duration = duration;
        this.timezone = timezone;
        this.area = area;
        this.type = type;
        this.vlr = vlr;
        this.callType = callType;
        this.ccnumber = ccnumber;
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
        this.avps.add(new AVP_Integer32(416, this.voiceTerminate == VOICE_TERMINATE.TERMINATE ? 3 : 2));

        this.avps.add(new AVP_Unsigned32(415, this.ccnumber));

        this.avps.add(new AVP_Grouped(443, new AVP[]{new AVP_Integer32(450, 0), new AVP_UTF8String(444, this.callType == VOICE_CALL.MOC ? this.number : this.dest)}));

        this.avps.add(new AVP_Grouped(456, new AVP[]{new AVP_Integer32(439, 1), new AVP_Grouped(446, new AVP[]{new AVP_Unsigned32(420, 2011, this.duration)})}));

        AVP service_type = new AVP_Unsigned32(1052, this.callType == VOICE_CALL.MOC ? 1 : 2);
        service_type.vendor_id = 2011;
        this.avps.add(service_type);

        AVP service_subtype = new AVP_Unsigned32(1053, this.type == VOICE_TYPE.VIDEO_CALL ? 7 : this.type == VOICE_TYPE.VOICE_ONLY ? 1 : 8);

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

        this.avps.add(new AVP_Time(2041, (int) getTimeStamp()));
        if (this.area == AREA.LOCAL) {
            this.avps.add(new AVP_UTF8String(23, this.timezone));
        } else if (this.area == AREA.INTERNATIONAL) {
            this.avps.add(new AVP_UTF8String(25033, this.timezone));
        }
        AVP_UTF8String vlr_id = new AVP_UTF8String(25005, this.vlr);
        vlr_id.vendor_id = 2011;
        this.avps.add(vlr_id);
    }

    protected void successHandle() {
        SSHFunction ssh = new SSHFunction();
        HashMap<String, String> result = ssh.getLastTransaction(getSession(), this.code, this.number);
        String xml = (String) result.get("xml");
        if (result.containsKey("xml")) {
            String basicValue = (String) result.get("basicValue");
            String afterAllowance = (String) result.get("afterAllowance");
            if (this.voiceTerminate == VOICE_TERMINATE.TERMINATE) {
                setResponseData("### " + getSession() + " ### " + xml + " ### Pulsa yang terpakai " + "A#(" + this.number + ") ke B#(" + this.dest + ") selama " + this.duration + "s: Charge Amount " + basicValue + ", After monetary allowance " + afterAllowance);
            } else {
                setResponseData("### " + getSession() + (this.type == VOICE_TYPE.VOICE_ONLY ? " ### Voice Intermediate " + this.duration + "s OK" : new StringBuilder().append(" ### Video Intermediate ").append(this.duration).append("s OK").toString()));
                System.out.println("### " + getSession() + (this.type == VOICE_TYPE.VOICE_ONLY ? " ### Voice Intermediate " + this.duration + "s OK" : new StringBuilder().append(" ### Video Intermediate ").append(this.duration).append("s OK").toString()));
            }
        }
    }
    protected void failHandle(int rc)
  {
      SSHFunction ssh = new SSHFunction();
        HashMap<String, String> result = ssh.getLastTransaction(getSession(), this.code, this.number);
        String xml = (String) result.get("xml");
        if (result.containsKey("xml") && result.containsKey("grepErrorSession")) {
            String basicValue = (String) result.get("basicValue");
            String afterAllowance = (String) result.get("afterAllowance");
            if (this.voiceTerminate == VOICE_TERMINATE.TERMINATE) {
                setResponseData("### " + getSession() + " ### " + xml + " ### Pulsa yang terpakai " + "A#(" + this.number + ") ke B#(" + this.dest + ") selama " + this.duration + "s: Charge Amount " + basicValue + ", After monetary allowance " + afterAllowance);
            } else {
                setResponseData("### " + getSession() + (this.type == VOICE_TYPE.VOICE_ONLY ? " ### Voice Intermediate " + this.duration + "s OK" : new StringBuilder().append(" ### Video Intermediate ").append(this.duration).append("s OK").toString()));
                System.out.println("### " + getSession() + (this.type == VOICE_TYPE.VOICE_ONLY ? " ### Voice Intermediate " + this.duration + "s OK" : new StringBuilder().append(" ### Video Intermediate ").append(this.duration).append("s OK").toString()));
            }
        } else if (result.containsKey("xml") && !result.containsKey("grepError")) {
            String basicValue = (String) result.get("basicValue");
            String afterAllowance = (String) result.get("afterAllowance");
            if (this.voiceTerminate == VOICE_TERMINATE.TERMINATE) {
                setResponseData("### " + getSession() + " ### " + xml + " ### Pulsa yang terpakai " + "A#(" + this.number + ") ke B#(" + this.dest + ") selama " + this.duration + "s: Charge Amount " + basicValue + ", After monetary allowance " + afterAllowance);
            } else {
                setResponseData("### " + getSession() + (this.type == VOICE_TYPE.VOICE_ONLY ? " ### Voice Intermediate " + this.duration + "s OK" : new StringBuilder().append(" ### Video Intermediate ").append(this.duration).append("s OK").toString()));
                System.out.println("### " + getSession() + (this.type == VOICE_TYPE.VOICE_ONLY ? " ### Voice Intermediate " + this.duration + "s OK" : new StringBuilder().append(" ### Video Intermediate ").append(this.duration).append("s OK").toString()));
            }
        } else {
            setResponseData("### " + getSession() + " ### " + xml + " ### " + result.get("grepError"));
        }
  }
}
