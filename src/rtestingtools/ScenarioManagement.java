package rtestingtools;

import WebServices.WSConnection;
import com.excel.ExcelContent;
import dk.i1.diameter.node.ConnectionKey;
import dk.i1.diameter.node.EmptyHostNameException;
import dk.i1.diameter.node.InvalidSettingException;
import dk.i1.diameter.node.Node;
import dk.i1.diameter.node.NodeSettings;
import dk.i1.diameter.node.Peer;
import dk.i1.diameter.node.SimpleSyncClient;
import dk.i1.diameter.node.UnsupportedTransportProtocolException;
import events.CheckBalance;
import events.CheckBalance.TYPE;
import events.DeletePI;
import events.DirectDebit;
import events.ContentRefund;
import events.Event2;
import events.Event;
import events.FirstLocationUpdate;
import events.GprsInit;
import events.GprsTerminate;
import events.GprsTerminate.GPRS_TERMINATE;
import events.Mms;
import events.Offer;
import events.RoamingChecker;
import events.SmsInit;
import events.SmsTerminate;
import events.SubscriberStatus;
import events.TalkMania;
import events.UpdateBalance;
import events.VoiceInit;
import events.VoiceTerminate;
import events.VoiceTerminate.AREA;
import events.VoiceTerminate.VOICE_CALL;
import events.VoiceTerminate.VOICE_TERMINATE;
import events.VoiceTerminate.VOICE_TYPE;
import events.GprsInitPayu;
import events.GprsIntermediatePayu;
import events.GprsTerminatePayu;
import events.LogChecker;
import events.SOAPservices;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import static rtestingtools.ConfigReader.WEBSERVICE_PARAMS;

public class ScenarioManagement
        implements Runnable {

    private String filename;
    private String fileoutput;
    private String SecureTicket1;
    private SimpleSyncClient[] client;
    private BufferedReader in = null;
    private BufferedWriter out = null;
    private String[] servers = null;
    private NodeSettings node_settings = null;
    private static Logger log = Logger.getLogger(ScenarioManagement.class.getName());
    private static ScenarioManagement scenarioMgt = null;
    private WSConnection ws;
    ExcelContent cn;
    private String SecureTicket;

    public ScenarioManagement(String[] servers, NodeSettings node_settings, String filename) {
        try {
            this.client = new SimpleSyncClient[servers.length];
            this.servers = servers;
            this.node_settings = node_settings;
            this.filename = filename;
            this.ws=new WSConnection(WEBSERVICE_PARAMS[0], WEBSERVICE_PARAMS[1], WEBSERVICE_PARAMS[2], WEBSERVICE_PARAMS[3]);
            this.fileoutput = ("LOG" + File.separator + filename + "_output_" + new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date()));
            this.SecureTicket=ws.getSecureTicket();
            
            this.in = new BufferedReader(new FileReader("SCENARIO" + File.separator + filename + ".txt"));
            this.out = new BufferedWriter(new FileWriter(this.fileoutput + ".txt"));
            scenarioMgt = this;

            ConfigReader.LOG_FILEHANDLER = new FileHandler(this.fileoutput + ".log");
            ConfigReader.LOG_FILEHANDLER.setFormatter(new SimpleFormatter());
            log.addHandler(ConfigReader.LOG_FILEHANDLER);
            log.setUseParentHandlers(false);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ScenarioManagement.class.getName()).log(Level.SEVERE, "Scenario: " + filename + " not found", ex);
        } catch (IOException ex) {
            Logger.getLogger(ScenarioManagement.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ScenarioManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static SimpleSyncClient resetConnection(SimpleSyncClient client) {
        int index;
        for (index = 0; index < scenarioMgt.client.length; index++) {
            if (scenarioMgt.client[index].equals(client)) {
                break;
            }
        }
        scenarioMgt.closeConnection();
        scenarioMgt.setupConnection();

        return scenarioMgt.client[index];
    }

    private void setupConnection() {
        int i = 0;
        for (String destination : this.servers) {
            try {
                StringTokenizer token = new StringTokenizer(destination, ":");
                String dest_host = token.nextToken();
                int dest_port = Integer.parseInt(token.nextToken());
                NodeSettings setting = new NodeSettings(this.node_settings.hostId(), this.node_settings.realm(), this.node_settings.vendorId(), this.node_settings.capabilities(), (int) Math.random(), this.node_settings.productName(), this.node_settings.firmwareRevision());

                this.client[i] = new SimpleSyncClient(setting, new Peer[]{new Peer(dest_host, dest_port)});

                this.client[i].start();
                this.client[i].waitForConnection();

                ConnectionKey k = this.client[i].node().findConnection(new Peer(ConfigReader.SERVER_HOST_ID, dest_port));
                this.client[i].node().connectionKey2Peer(k).host(dest_host);
                i++;

                log.info("Diameter connection to " + dest_host + ":" + dest_port + " initiated");
            } catch (InvalidSettingException ex) {
                Logger.getLogger(ScenarioManagement.class.getName()).log(Level.SEVERE, null, ex);
            } catch (EmptyHostNameException ex) {
                Logger.getLogger(ScenarioManagement.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ScenarioManagement.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(ScenarioManagement.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedTransportProtocolException ex) {
                Logger.getLogger(ScenarioManagement.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void closeConnection() {
        for (int i = 0; i < this.client.length; i++) {
            this.client[i].stop();

            log.info("Diameter connection to " + this.client[i].toString() + " closed");
        }
    }

    public void run() {
        boolean isFirstProcedureFailed = false;
        try {
            String line;
            while ((line = this.in.readLine()) != null) {
                if ((!line.startsWith("#")) && (!line.isEmpty())) {
                    if (line.toLowerCase().startsWith("space")) {
                        this.out.write("\n");
                        this.out.flush();
                    } else {
                        log.info("Executing line: \"" + line + "\"");

                        setupConnection();

                        StringTokenizer space = new StringTokenizer(line, " ");
                        String id = space.nextToken();
                        String user_session = null;
                        if (id.startsWith("%")) {
                            user_session = id.substring(1);
                        }
                        String command = space.nextToken();
                        String param = space.nextToken();
                        while (space.hasMoreTokens()) {
                            param = param + " " + space.nextToken();
                        }
                        String[] params = param.split(",");
                        System.out.println(command);
                        Event2 evt = null;
                        Event evt2 = null;
                        if (command.equalsIgnoreCase("888")) {
                            evt2 = new CheckBalance(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], Integer.parseInt(params[3]), CheckBalance.TYPE.BALANCE, 1);

                            evt2.setTimeStamp(params.length <= 4 ? null : params[4]);
                        } else if (command.equalsIgnoreCase("887")) {
                            evt2 = new CheckBalance(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], Integer.parseInt(params[3]), CheckBalance.TYPE.LAST_TRANSACTION, 2);

                            evt2.setTimeStamp(params.length <= 4 ? null : params[4]);
                        } else if (command.equalsIgnoreCase("889")) {
                            evt2 = new CheckBalance(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], Integer.parseInt(params[3]), CheckBalance.TYPE.BONUS, 3);

                            evt2.setTimeStamp(params.length <= 4 ? null : params[4]);
                        } else if (command.equalsIgnoreCase("voice")) {
                            evt = new VoiceInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], "0", VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.VOICE_ONLY, VoiceTerminate.VOICE_CALL.MOC, id, "91u");

                            evt.setTimeStamp(params.length <= 6 ? null : params[6]);
                            if (user_session != null) {
                                evt.setSession(user_session);
                            }
                            evt.start();
                            if (evt.getResponseData().endsWith("Voice Init OK")) {
                                String voice_session = evt.getSession();
                                evt = new VoiceTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], "0", 1, Integer.parseInt(params[5]), VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.VOICE_ONLY, VoiceTerminate.VOICE_CALL.MOC, VoiceTerminate.VOICE_TERMINATE.TERMINATE, "93u");

                                evt.setTimeStamp(params.length <= 6 ? null : params[6]);
                                evt.setSession(voice_session);
                            } else {
                                isFirstProcedureFailed = true;
                            }
                        } else if (command.equalsIgnoreCase("voice_int")) {
                            evt = new VoiceInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], "0", VoiceTerminate.AREA.INTERNATIONAL, VoiceTerminate.VOICE_TYPE.VOICE_ONLY, VoiceTerminate.VOICE_CALL.MOC, id, "91u");

                            evt.setTimeStamp(params.length <= 6 ? null : params[6]);
                            if (user_session != null) {
                                evt.setSession(user_session);
                            }
                            evt.start();
                            if (evt.getResponseData().endsWith("Voice Init OK")) {
                                String voice_session = evt.getSession();
                                evt = new VoiceTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], "0", 1, Integer.parseInt(params[5]), VoiceTerminate.AREA.INTERNATIONAL, VoiceTerminate.VOICE_TYPE.VOICE_ONLY, VoiceTerminate.VOICE_CALL.MOC, VoiceTerminate.VOICE_TERMINATE.TERMINATE, "93u");

                                evt.setTimeStamp(params.length <= 6 ? null : params[6]);
                                evt.setSession(voice_session);
                            } else {
                                isFirstProcedureFailed = true;
                            }
                        } else if (command.equalsIgnoreCase("voice_roaming")) {
                            evt = new VoiceInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.VOICE_ONLY, VoiceTerminate.VOICE_CALL.MOC, id, "91u");

                            evt.setTimeStamp(params.length <= 7 ? null : params[7]);
                            if (user_session != null) {
                                evt.setSession(user_session);
                            }
                            evt.start();
                            if (evt.getResponseData().endsWith("Voice Init OK")) {
                                String voice_session = evt.getSession();
                                evt = new VoiceTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], 1, Integer.parseInt(params[6]), VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.VOICE_ONLY, VoiceTerminate.VOICE_CALL.MOC, VoiceTerminate.VOICE_TERMINATE.TERMINATE, "93u");

                                evt.setTimeStamp(params.length <= 7 ? null : params[7]);
                                evt.setSession(voice_session);
                            } else {
                                isFirstProcedureFailed = true;
                            }
                        } else if (command.equalsIgnoreCase("voice_mtc")) {
                            evt = new VoiceInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.VOICE_ONLY, VoiceTerminate.VOICE_CALL.MTC, id, "65u");

                            evt.setTimeStamp(params.length <= 7 ? null : params[7]);
                            if (user_session != null) {
                                evt.setSession(user_session);
                            }
                            evt.start();
                            if (evt.getResponseData().endsWith("Voice Init OK")) {
                                String voice_session = evt.getSession();
                                evt = new VoiceTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], 1, Integer.parseInt(params[6]), VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.VOICE_ONLY, VoiceTerminate.VOICE_CALL.MTC, VoiceTerminate.VOICE_TERMINATE.TERMINATE, "67u");

                                evt.setTimeStamp(params.length <= 7 ? null : params[7]);
                                evt.setSession(voice_session);
                            } else {
                                isFirstProcedureFailed = true;
                            }
                        } else if (command.equalsIgnoreCase("voice_ucb")) {
                            evt = new VoiceInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.UCB, VoiceTerminate.VOICE_CALL.MOC, id, "91u");

                            evt.setTimeStamp(params.length <= 7 ? null : params[7]);
                            if (user_session != null) {
                                evt.setSession(user_session);
                            }
                            evt.start();
                            if (evt.getResponseData().endsWith("Video Init OK")) {
                                String voice_session = evt.getSession();
                                evt = new VoiceTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], 1, Integer.parseInt(params[6]), VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.UCB, VoiceTerminate.VOICE_CALL.MOC, VoiceTerminate.VOICE_TERMINATE.TERMINATE, "93u");

                                evt.setTimeStamp(params.length <= 7 ? null : params[7]);
                                evt.setSession(voice_session);
                            } else {
                                isFirstProcedureFailed = true;
                            }
                        } else if (command.equalsIgnoreCase("voice_init")) {
                            evt = new VoiceInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], "0", VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.VOICE_ONLY, VoiceTerminate.VOICE_CALL.MOC, id, "91u");

                            evt.setTimeStamp(params.length <= 5 ? null : params[5]);
                        } else if (command.equalsIgnoreCase("voice_int_init")) {
                            evt = new VoiceInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], "0", VoiceTerminate.AREA.INTERNATIONAL, VoiceTerminate.VOICE_TYPE.VOICE_ONLY, VoiceTerminate.VOICE_CALL.MOC, id, "91u");

                            evt.setTimeStamp(params.length <= 5 ? null : params[5]);
                        } else if (command.equalsIgnoreCase("voice_roaming_init")) {
                            evt = new VoiceInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.VOICE_ONLY, VoiceTerminate.VOICE_CALL.MOC, id, "91u");

                            evt.setTimeStamp(params.length <= 6 ? null : params[6]);
                        } else if (command.equalsIgnoreCase("voice_mtc_init")) {
                            evt = new VoiceInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.VOICE_ONLY, VoiceTerminate.VOICE_CALL.MTC, id, "65u");

                            evt.setTimeStamp(params.length <= 6 ? null : params[6]);
                        } else if (command.equalsIgnoreCase("voice_ucb_init")) {
                            evt = new VoiceInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.UCB, VoiceTerminate.VOICE_CALL.MOC, id, "91u");

                            evt.setTimeStamp(params.length <= 6 ? null : params[6]);
                        } else if (command.equalsIgnoreCase("voice_intermediate")) {
                            evt = new VoiceTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], "0", Integer.parseInt(params[5]), Integer.parseInt(params[6]), VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.VOICE_ONLY, VoiceTerminate.VOICE_CALL.MOC, VoiceTerminate.VOICE_TERMINATE.INTERMEDIATE, "93u");

                            evt.setTimeStamp(params.length <= 7 ? null : params[7]);
                        } else if (command.equalsIgnoreCase("voice_int_intermediate")) {
                            evt = new VoiceTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], "0", Integer.parseInt(params[5]), Integer.parseInt(params[6]), VoiceTerminate.AREA.INTERNATIONAL, VoiceTerminate.VOICE_TYPE.VOICE_ONLY, VoiceTerminate.VOICE_CALL.MOC, VoiceTerminate.VOICE_TERMINATE.INTERMEDIATE, "93u");

                            evt.setTimeStamp(params.length <= 7 ? null : params[7]);
                        } else if (command.equalsIgnoreCase("voice_roaming_intermediate")) {
                            evt = new VoiceTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], Integer.parseInt(params[6]), Integer.parseInt(params[7]), VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.VOICE_ONLY, VoiceTerminate.VOICE_CALL.MOC, VoiceTerminate.VOICE_TERMINATE.INTERMEDIATE, "93u");

                            evt.setTimeStamp(params.length <= 8 ? null : params[8]);
                        } else if (command.equalsIgnoreCase("voice_terminate")) {
                            evt = new VoiceTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], "0", 1, Integer.parseInt(params[5]), VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.VOICE_ONLY, VoiceTerminate.VOICE_CALL.MOC, VoiceTerminate.VOICE_TERMINATE.TERMINATE, "93u");

                            evt.setTimeStamp(params.length <= 6 ? null : params[6]);
                        } else if (command.equalsIgnoreCase("voice_int_terminate")) {
                            evt = new VoiceTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], "0", 1, Integer.parseInt(params[5]), VoiceTerminate.AREA.INTERNATIONAL, VoiceTerminate.VOICE_TYPE.VOICE_ONLY, VoiceTerminate.VOICE_CALL.MOC, VoiceTerminate.VOICE_TERMINATE.TERMINATE, "93u");

                            evt.setTimeStamp(params.length <= 6 ? null : params[6]);
                        } else if (command.equalsIgnoreCase("voice_roaming_terminate")) {
                            evt = new VoiceTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], Integer.parseInt(params[6]), Integer.parseInt(params[7]), VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.VOICE_ONLY, VoiceTerminate.VOICE_CALL.MOC, VoiceTerminate.VOICE_TERMINATE.TERMINATE, "93u");

                            evt.setTimeStamp(params.length <= 8 ? null : params[8]);
                        } else if (command.equalsIgnoreCase("voice_mtc_terminate")) {
                            evt = new VoiceTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], Integer.parseInt(params[6]), Integer.parseInt(params[7]), VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.VOICE_ONLY, VoiceTerminate.VOICE_CALL.MTC, VoiceTerminate.VOICE_TERMINATE.TERMINATE, "67u");

                            evt.setTimeStamp(params.length <= 8 ? null : params[8]);
                        } else if (command.equalsIgnoreCase("voice_ucb_terminate")) {
                            evt = new VoiceTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], Integer.parseInt(params[6]), Integer.parseInt(params[7]), VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.UCB, VoiceTerminate.VOICE_CALL.MOC, VoiceTerminate.VOICE_TERMINATE.TERMINATE, "93u");

                            evt.setTimeStamp(params.length <= 8 ? null : params[8]);
                        } else if (command.equalsIgnoreCase("video")) {
                            evt = new VoiceInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.VIDEO_CALL, VoiceTerminate.VOICE_CALL.MOC, id, "91u");

                            evt.setTimeStamp(params.length <= 7 ? null : params[7]);
                            if (user_session != null) {
                                evt.setSession(user_session);
                            }
                            evt.start();
                            if (evt.getResponseData().endsWith("Video Init OK")) {
                                String voice_session = evt.getSession();
                                evt = new VoiceTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], 1, Integer.parseInt(params[6]), VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.VIDEO_CALL, VoiceTerminate.VOICE_CALL.MOC, VoiceTerminate.VOICE_TERMINATE.TERMINATE, "93u");

                                evt.setTimeStamp(params.length <= 7 ? null : params[7]);
                                evt.setSession(voice_session);
                            } else {
                                isFirstProcedureFailed = true;
                            }
                        } else if (command.equalsIgnoreCase("video_int")) {
                            evt = new VoiceInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], VoiceTerminate.AREA.INTERNATIONAL, VoiceTerminate.VOICE_TYPE.VIDEO_CALL, VoiceTerminate.VOICE_CALL.MOC, id, "91u");

                            evt.setTimeStamp(params.length <= 7 ? null : params[7]);
                            if (user_session != null) {
                                evt.setSession(user_session);
                            }
                            evt.start();
                            if (evt.getResponseData().endsWith("Video Init OK")) {
                                String voice_session = evt.getSession();
                                evt = new VoiceTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], 1, Integer.parseInt(params[6]), VoiceTerminate.AREA.INTERNATIONAL, VoiceTerminate.VOICE_TYPE.VIDEO_CALL, VoiceTerminate.VOICE_CALL.MOC, VoiceTerminate.VOICE_TERMINATE.TERMINATE, "93u");

                                evt.setTimeStamp(params.length <= 7 ? null : params[7]);
                                evt.setSession(voice_session);
                            } else {
                                isFirstProcedureFailed = true;
                            }
                        } else if (command.equalsIgnoreCase("video_init")) {
                            evt = new VoiceInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.VIDEO_CALL, VoiceTerminate.VOICE_CALL.MOC, id, "91u");

                            evt.setTimeStamp(params.length <= 6 ? null : params[6]);
                        } else if (command.equalsIgnoreCase("video_int_init")) {
                            evt = new VoiceInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], VoiceTerminate.AREA.INTERNATIONAL, VoiceTerminate.VOICE_TYPE.VIDEO_CALL, VoiceTerminate.VOICE_CALL.MOC, id, "91u");

                            evt.setTimeStamp(params.length <= 6 ? null : params[6]);
                        } else if (command.equalsIgnoreCase("video_terminate")) {
                            evt = new VoiceTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], 1, Integer.parseInt(params[6]), VoiceTerminate.AREA.LOCAL, VoiceTerminate.VOICE_TYPE.VIDEO_CALL, VoiceTerminate.VOICE_CALL.MOC, VoiceTerminate.VOICE_TERMINATE.TERMINATE, "93u");

                            evt.setTimeStamp(params.length <= 7 ? null : params[7]);
                        } else if (command.equalsIgnoreCase("video_int_terminate")) {
                            evt = new VoiceTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[4], params[5], 1, Integer.parseInt(params[6]), VoiceTerminate.AREA.INTERNATIONAL, VoiceTerminate.VOICE_TYPE.VIDEO_CALL, VoiceTerminate.VOICE_CALL.MOC, VoiceTerminate.VOICE_TERMINATE.TERMINATE, "93u");

                            evt.setTimeStamp(params.length <= 7 ? null : params[7]);
                        } else if (command.equalsIgnoreCase("sms")) {
                            evt = new SmsInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], "0", id);

                            evt.setTimeStamp(params.length <= 5 ? null : params[5]);
                            if (user_session != null) {
                                evt.setSession(user_session);
                            }
                            evt.start();
                            if (evt.getResponseData().endsWith("SMS Init OK")) {
                                String sms_session = evt.getSession();
                                evt = new SmsTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], Integer.parseInt(params[4]), "50u", "0");

                                evt.setTimeStamp(params.length <= 5 ? null : params[5]);
                                evt.setSession(sms_session);
                            } else {
                                isFirstProcedureFailed = true;
                            }
                        } else if (command.equalsIgnoreCase("sms_roaming")) {
                            evt = new SmsInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], params[5], id);

                            evt.setTimeStamp(params.length <= 6 ? null : params[6]);
                            if (user_session != null) {
                                evt.setSession(user_session);
                            }
                            evt.start();
                            if (evt.getResponseData().endsWith("SMS Init OK")) {
                                String sms_session = evt.getSession();
                                evt = new SmsTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], Integer.parseInt(params[4]), "50u", params[5]);

                                evt.setTimeStamp(params.length <= 6 ? null : params[6]);
                                evt.setSession(sms_session);
                            } else {
                                isFirstProcedureFailed = true;
                            }
                        } else if (command.equalsIgnoreCase("sms_init")) {
                            evt = new SmsInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], "0", id);

                            evt.setTimeStamp(params.length <= 4 ? null : params[4]);
                        } else if (command.equalsIgnoreCase("sms_terminate")) {
                            evt = new SmsTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], Integer.parseInt(params[4]), "50u", "0");

                            evt.setTimeStamp(params.length <= 5 ? null : params[5]);
                        } else if (command.equalsIgnoreCase("mms")) {
                            evt = new Mms(this.client[(Integer.parseInt(params[0]) - 1)], params[1], Long.parseLong(params[2]), params[3], "54u", id);

                            evt.setTimeStamp(params.length <= 4 ? null : params[4]);
                        } else if (command.equalsIgnoreCase("gprs")) {
                            evt = new GprsInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], Integer.parseInt(params[4]), id);

                            evt.setTimeStamp(params.length <= 6 ? null : params[6]);
                            if (user_session != null) {
                                evt.setSession(user_session);
                            }
                            evt.start();
                            if (evt.getResponseData().endsWith("GPRS Init OK")) {
                                String gprs_session = evt.getSession();
                                evt = new GprsTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], 1, Integer.parseInt(params[4]), Long.parseLong(params[5]), GprsTerminate.GPRS_TERMINATE.TERMINATE, "53u", id);

                                evt.setTimeStamp(params.length <= 6 ? null : params[6]);
                                evt.setSession(gprs_session);
                            } else {
                                isFirstProcedureFailed = true;
                            }
                        } else if (command.equalsIgnoreCase("gprs_location")) {
                            evt = new GprsInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], Integer.parseInt(params[4]), params[5], id);

                            evt.setTimeStamp(params.length <= 7 ? null : params[7]);
                            if (user_session != null) {
                                evt.setSession(user_session);
                            }
                            evt.start();
                            if (evt.getResponseData().endsWith("GPRS Init OK")) {
                                String gprs_session = evt.getSession();
                                evt = new GprsTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], 1, Integer.parseInt(params[4]), params[5], Long.parseLong(params[6]), GprsTerminate.GPRS_TERMINATE.TERMINATE, "53u", id);

                                evt.setTimeStamp(params.length <= 7 ? null : params[7]);
                                evt.setSession(gprs_session);
                            } else {
                                isFirstProcedureFailed = true;
                            }
                        } else if (command.equalsIgnoreCase("gprs_terminate")) {
                            evt = new GprsTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], 1, Integer.parseInt(params[4]), Long.parseLong(params[5]), GprsTerminate.GPRS_TERMINATE.TERMINATE, "53u", id);

                            evt.setTimeStamp(params.length <= 6 ? null : params[6]);
                        } else if (command.equalsIgnoreCase("gprs_intermediate")) {
                            evt = new GprsTerminate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], Integer.parseInt(params[4]), Integer.parseInt(params[5]), Long.parseLong(params[6]), GprsTerminate.GPRS_TERMINATE.INTERMEDIATE, "53u", id);

                            evt.setTimeStamp(params.length <= 7 ? null : params[7]);
                        } else if (command.equalsIgnoreCase("gprs_init")) {
                            evt = new GprsInit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], params[3], Integer.parseInt(params[4]), id);

                            evt.setTimeStamp(params.length <= 5 ? null : params[5]);
                        } else if (command.equalsIgnoreCase("direct_debit")) {
                            evt = new DirectDebit(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], "80u", id);
                            evt.setTimeStamp(params.length <= 3 ? null : params[3]);
                        } else if (command.equalsIgnoreCase("content_refund")) {
                            evt = new ContentRefund(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], "81u", id);
                            evt.setTimeStamp(params.length <= 3 ? null : params[3]);
                        } else if (command.equalsIgnoreCase("talkmania")) {
                            evt = new TalkMania(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], id);
                            evt.setTimeStamp(params.length <= 3 ? null : params[3]);
                        } else if (command.equalsIgnoreCase("flu")) {
                            evt = new FirstLocationUpdate(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2]);
                            evt.setTimeStamp(params.length <= 3 ? null : params[3]);
                        } else if (command.equalsIgnoreCase("cek_status")) {
                            evt = new SubscriberStatus(this.client[0], params[0]);
                            ((SubscriberStatus) evt).waitForStatusChanged();
                            if ((evt.getResponseData() == null) || (!evt.getResponseData().startsWith("#"))) {
                                this.out.write(id + " ### " + (evt.getResponseData() == null ? "Database connection failed" : new StringBuilder().append(params[3]).append(" INACTIVE(").append(evt.getResponseData()).append("). You must recharge this number").toString()) + "\n");

                                this.out.flush();
                                closeConnection();
                                break;
                            }
                            isFirstProcedureFailed = true;
                        } else if (command.equalsIgnoreCase("offline_transaction")) {
                            evt = new RoamingChecker(this.client[0], params[0], params[1], params[2], params[3], params[4]);
                            if (user_session != null) {
                                evt.setSession(user_session);
                            }
                            ((RoamingChecker) evt).getLastTransaction();

                            isFirstProcedureFailed = true;
                        } else if (command.equalsIgnoreCase("update_balance")) {
                            evt = new UpdateBalance(this.client[0], params[0], Integer.parseInt(params[1]));
                            ((UpdateBalance) evt).update();
                            isFirstProcedureFailed = true;
                        } else if (command.equalsIgnoreCase("delete_pi")) {
                            evt = new DeletePI(this.client[0], params[0]);
                            ((DeletePI) evt).delete();
                            isFirstProcedureFailed = true;
                        } else if (command.equalsIgnoreCase("addoffer")) {
                            evt= new SOAPservices(this.client[0], this.ws, this.SecureTicket);
                            ((SOAPservices)evt).attachOffer(params[0], params[1]);
                            isFirstProcedureFailed = true;
                        } else if (command.equalsIgnoreCase("addofferwithparameter")) {
                            evt= new SOAPservices(this.client[0], this.ws, this.SecureTicket);
                            try {
                                ((SOAPservices)evt).addofferwithparameter(params[0], params[1], params[2], params[3]);
                            } catch (Exception ex) {
                                Logger.getLogger(ScenarioManagement.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            isFirstProcedureFailed = true;
                        }else if (command.equalsIgnoreCase("getbonusinfo")) {
                            evt= new SOAPservices(this.client[0], this.ws, this.SecureTicket);
                            try {
                                ((SOAPservices)evt).getBonusInfo(params[0], params[1]);
                            } catch (Exception ex) {
                                Logger.getLogger(ScenarioManagement.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            isFirstProcedureFailed = true;
            //creatCUG                
                        }else if (command.equalsIgnoreCase("createCUG")) {
                            evt= new SOAPservices(this.client[0], this.ws, this.SecureTicket);
                            try {
                                ((SOAPservices)evt).createCUG(params[0]);
                            } catch (Exception ex) {
                                Logger.getLogger(ScenarioManagement.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            isFirstProcedureFailed = true;
            //Recharge
                            }else if (command.equalsIgnoreCase("ISORecharge")) {
                            evt= new SOAPservices(this.client[0], this.ws, this.SecureTicket);
                            try {
                                ((SOAPservices)evt).getISORecharge(params[0], params[1]);
                            } catch (Exception ex) {
                                Logger.getLogger(ScenarioManagement.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            isFirstProcedureFailed = true;
            //addGroupMember                
                        }else if (command.equalsIgnoreCase("addGroupMember")) {
                            evt= new SOAPservices(this.client[0], this.ws, this.SecureTicket);
                            try {
                                ((SOAPservices)evt).getaddGroupMember(params[0], params[1], params[2]);
                            } catch (Exception ex) {
                                Logger.getLogger(ScenarioManagement.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            isFirstProcedureFailed = true;
                        }else if (command.equalsIgnoreCase("removeoffer")) {
                            evt = new SOAPservices(this.client[0], this.ws, this.SecureTicket);
                            ((SOAPservices)evt).removeOffer(params[0], params[1]);
                            isFirstProcedureFailed = true;
                            
                        } else if (command.equalsIgnoreCase("gprs_init_payu")) {
                            evt = new GprsInitPayu(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], Integer.parseInt(params[3]), params[4], params[5], Integer.parseInt(params[6]), id);

                            evt.setTimeStamp(params.length <= 7 ? null : params[7]);
                        } else if (command.equalsIgnoreCase("gprs_intermediate_payu")) {
                            evt = new GprsIntermediatePayu(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], Integer.parseInt(params[3]), params[4], params[5], Integer.parseInt(params[6]), Long.parseLong(params[7]), id);

                            evt.setTimeStamp(params.length <= 8 ? null : params[8]);
                        } else if (command.equalsIgnoreCase("gprs_terminate_payu")) {
                            evt = new GprsTerminatePayu(this.client[(Integer.parseInt(params[0]) - 1)], params[1], params[2], Integer.parseInt(params[3]), params[4], params[5], Integer.parseInt(params[6]), Long.parseLong(params[7]), id);

                            evt.setTimeStamp(params.length <= 8 ? null : params[8]);
                        } else if (command.equalsIgnoreCase("log_fr")) {
                            evt = new LogChecker(this.client[0], params[0], params[1], params[2], Integer.parseInt(params[3]));
                            ((LogChecker) evt).getLogFR();
                            isFirstProcedureFailed = true;
                        } else if (command.equalsIgnoreCase("log_rb")) {
                            evt = new LogChecker(this.client[0], params[0], params[1], params[2], Integer.parseInt(params[3]));
                            ((LogChecker) evt).getLogRB();
                            isFirstProcedureFailed = true;
                        }
                        if (user_session != null) {
                            try {
                                evt.setSession(user_session);
                            } catch (Exception e) {
                                evt2.setSession(user_session);
                            }
                        }
                        if (!isFirstProcedureFailed) {
                            try {
                                evt.start();
                            } catch (Exception e) {
                                evt2.start();
                            }
                        }
                        try {
                            this.out.write(id + " " + evt.getResponseData() + "\n");
                        } catch (Exception e) {
                            this.out.write(id + " " + evt2.getResponseData() + "\n");
                        }

                        this.out.flush();
                        try {
                            if (evt.getCloseParameter() == 1) {
                                createExcel();
                                System.exit(5);
                            }
                        } catch (Exception e) {
                            if (evt2.getCloseParameter() == 1) {
                                createExcel();
                                System.exit(5);
                            }
                        }

                        isFirstProcedureFailed = false;

                        closeConnection();
                        if (ConfigReader.DELAY_TIME > 0L) {
                            try {
                                Thread.sleep(ConfigReader.DELAY_TIME);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        log.info("Closing line execution: \"" + line + "\"");
                    }
                }
            }

            return;
        } catch (IOException ex) {
            Logger.getLogger(ScenarioManagement.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ScenarioManagement.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            createExcel();
            System.exit(0);
        }
    }

    public void createExcel() {
        try {
            this.in.close();
            this.out.close();
            if (ConfigReader.REPORT.containsKey(this.filename)) {
                cn = new ExcelContent();
                System.out.println("Excel output " + cn.copyTo(((Integer) ConfigReader.REPORT.get(this.filename)).intValue(), new StringBuilder().append(this.fileoutput).append(".txt").toString(), "template.xlsx", new StringBuilder().append(this.fileoutput).append(".xlsx").toString()));
            }
            ConfigReader.LOG_FILEHANDLER.flush();
            ConfigReader.LOG_FILEHANDLER.close();
        } catch (IOException ex) {
            Logger.getLogger(ScenarioManagement.class.getName()).log(Level.SEVERE, "Scenario: " + this.filename + " cannot read file", ex);
        }
    }
}
