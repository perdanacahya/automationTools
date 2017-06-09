package rtestingtools;

import dk.i1.diameter.AVP;
import dk.i1.diameter.AVP_Grouped;
import dk.i1.diameter.AVP_Integer32;
import dk.i1.diameter.AVP_Integer64;
import dk.i1.diameter.AVP_UTF8String;
import dk.i1.diameter.AVP_Unsigned32;
import dk.i1.diameter.InvalidAVPLengthException;
import dk.i1.diameter.Message;
import dk.i1.diameter.MessageHeader;
import dk.i1.diameter.Utils;
import dk.i1.diameter.node.Capability;
import dk.i1.diameter.node.ConnectionKey;
import dk.i1.diameter.node.InvalidSettingException;
import dk.i1.diameter.node.Node;
import dk.i1.diameter.node.NodeManager;
import dk.i1.diameter.node.NodeSettings;
import dk.i1.diameter.node.NotAnAnswerException;
import dk.i1.diameter.node.Peer;
import java.io.InputStream;
import java.io.PrintStream;

public class Cc_test_server
        extends NodeManager {

    Cc_test_server(NodeSettings node_settings) {
        super(node_settings);
    }

    public static final void main(String[] args)
            throws Exception {
        String host_id = "127.0.0.1";
        String realm = "inode.com";
        int port = 11216;

        Capability capability = new Capability();
        capability.addAuthApp(4);
        NodeSettings node_settings;
        try {
            node_settings = new NodeSettings(host_id, realm, 99999, capability, port, "cc_test_server", 16777216);
        } catch (InvalidSettingException e) {
            System.out.println(e.toString());
            return;
        }
        Cc_test_server tss = new Cc_test_server(node_settings);
        tss.start();

        System.out.println("Hit enter to terminate server");
        System.in.read();

        tss.stop(50L);
    }

    protected void handleRequest(Message request, ConnectionKey connkey, Peer peer) {
        Message answer = new Message();
        answer.prepareResponse(request);

        AVP avp = request.find(263);
        if (avp != null) {
            answer.add(avp);
        }
        node().addOurHostAndRealm(answer);
        avp = request.find(416);
        if (avp == null) {
            answerError(answer, connkey, 5005, new AVP[]{new AVP_Grouped(279, new AVP[]{new AVP(416, new byte[0])})});

            return;
        }
        int cc_request_type = -1;
        try {
            cc_request_type = new AVP_Unsigned32(avp).queryValue();
        } catch (InvalidAVPLengthException ex) {
        }
        if ((cc_request_type != 1) && (cc_request_type != 2) && (cc_request_type != 3) && (cc_request_type != 4)) {
            answerError(answer, connkey, 5004, new AVP[]{new AVP_Grouped(279, new AVP[]{avp})});

            return;
        }
        //avp = request.find(456);

        avp = request.find(455);
        if (avp != null) {
            int indicator = -1;
            try {
                indicator = new AVP_Unsigned32(avp).queryValue();
            } catch (InvalidAVPLengthException ex) {
            }
            if (indicator != 0) {
                answerError(answer, connkey, 5004, new AVP[]{new AVP_Grouped(279, new AVP[]{avp})});

                return;
            }
        }
        answer.add(new AVP_Unsigned32(268, 2001));
        avp = request.find(258);
        if (avp != null) {
            answer.add(avp);
        }
        avp = request.find(416);
        if (avp != null) {
            answer.add(avp);
        }
        avp = request.find(415);
        if (avp != null) {
            answer.add(avp);
        }
        switch (cc_request_type) {
            case 1:
            case 2:
            case 3:
                avp = request.find(437);
                if (avp != null) {
                    AVP g = new AVP(avp);
                    g.code = 431;
                    answer.add(avp);
                }
                break;
            case 4:
                avp = request.find(436);
                if (avp == null) {
                    answerError(answer, connkey, 5005, new AVP[]{new AVP_Grouped(279, new AVP[]{new AVP(436, new byte[0])})});

                    return;
                }
                int requested_action = -1;
                try {
                    requested_action = new AVP_Unsigned32(avp).queryValue();
                } catch (InvalidAVPLengthException ex) {
                }
                switch (requested_action) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        answer.add(new AVP_Unsigned32(422, 0));
                        break;
                    case 3:
                        answer.add(new AVP_Grouped(423, new AVP[]{new AVP_Grouped(445, new AVP[]{new AVP_Integer64(447, 4217L), new AVP_Integer32(429, -2)}),
                            new AVP_Unsigned32(425, 208), new AVP_UTF8String(424, "kanelsnegl")}));

                        break;
                    default:
                        answerError(answer, connkey, 5004, new AVP[]{new AVP_Grouped(279, new AVP[]{avp})});
                        return;
                }
                break;
        }
        Utils.setMandatory_RFC3588(answer);
        try {
            answer(answer, connkey);
        } catch (NotAnAnswerException ex) {
        }
    }

    void answerError(Message answer, ConnectionKey connkey, int result_code, AVP[] error_avp) {
        answer.hdr.setError(true);
        answer.add(new AVP_Unsigned32(268, result_code));
        for (AVP avp : error_avp) {
            answer.add(avp);
        }
        try {
            answer(answer, connkey);
        } catch (NotAnAnswerException ex) {
        }
    }
}
