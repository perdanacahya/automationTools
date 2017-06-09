package sshroaming;

import db.Database;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import rtestingtools.ConfigReader;

public class SSHFunction {

    private String host;
    private String username;
    private String pwd;
    private String dsn;
    private String FR;
    private boolean trace_xml;
    private static final Logger log = Logger.getLogger(SSHFunction.class.getName());

    public SSHFunction() {
        this.host = ConfigReader.SSH_SERVER_CONFIGURATION[0];
        this.username = ConfigReader.SSH_SERVER_CONFIGURATION[1];
        this.pwd = ConfigReader.SSH_SERVER_CONFIGURATION[2];
        this.dsn = ConfigReader.SSH_SERVER_CONFIGURATION[3];
        this.trace_xml = ConfigReader.TRACE_XML;
        String[] destination = new String[ConfigReader.DESTINATIONS.size()];
        destination = (String[]) ConfigReader.DESTINATIONS.toArray(destination);
        if (destination[0].toString().endsWith("7") && destination[1].toString().endsWith("9")) {
            this.FR = "FR2";
        } else {
            this.FR = "FR1";
        }
        if ((log.getHandlers() == null) || (log.getHandlers().length < 1)) {
            log.addHandler(ConfigReader.LOG_FILEHANDLER);
            log.setUseParentHandlers(false);
        }
    }

    public HashMap<String, String> getLastTransaction(String session, String code, String msisdn) {
        HashMap<String, String> result = new HashMap(6);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ex) {
            Logger.getLogger(SSHFunction.class.getName()).log(Level.SEVERE, null, ex);
        }
        SSHConnector ssh = new SSHConnector(this.host, this.username, this.pwd);
        if (ssh.createNewSession()) {
            log.info("SSH Login to " + this.username + "@" + this.host + " success");
            try {
                String tmp = ssh.sendCommand("cd " + ssh.getLogDirectory() + " && ls -tr event_file_" + this.FR + "*");
                System.out.print(tmp);
                StringTokenizer token = new StringTokenizer(tmp, "\n");
                do {
                    tmp = token.nextToken();
                } while (token.hasMoreTokens());
                System.out.println("\n\n" + tmp);

                String cmd = "cd " + ssh.getLogDirectory() + " && cat " + tmp + " | grep -F '" + session + "' | grep '" + code + "' | grep '" + msisdn + "'";
                tmp = ssh.sendCommand(cmd);

                token = new StringTokenizer(tmp, "\n");
                do {
                    tmp = token.nextToken();
                } while (token.hasMoreTokens());
                String tmp4 = "0";
                try {
                    if (code.equals("51u")) {
                        tmp4 = tmp.split(";")[58];
                        System.out.println(tmp4);
                    } else {
                        tmp4 = tmp.split(";")[55];
                        System.out.println(tmp4);
                    }
                } catch (Exception e) {

                }
                String tmp3 = tmp.split(";")[7];
                if (!tmp4.equals("0") && tmp3.equals("10017")) {
                    result.put("errorcode", tmp4);
                } else {
                    //result.put("errorcode", "");
                }

                tmp = tmp.split(";")[0];

                if (tmp3.equals("16001") || tmp3.equals("16003") || tmp3.equals("50") || tmp3.equals("10") || tmp3.equals("21140") || tmp3.equals("21100") || tmp3.equals("32003") || tmp3.equals("21010")) {
                    String grepError2 = ssh.sendCommand("cd " + ssh.getLogDirectory() + " && grep " + tmp + " apperror_file_FR*").trim();
                    String[] grepError = grepError2.split(";");
                    result.put("grepErrorSession", grepError[grepError.length - 1]);
                }
                if (!tmp3.equals("0")) {
                    String grepError2 = ssh.sendCommand("cd " + ssh.getLogDirectory() + " && grep " + tmp + " apperror_file_FR*").trim();
                    String[] grepError = grepError2.split(";");
                    result.put("grepError", grepError[grepError.length - 1]);
                }
                if (trace_xml == true) {
                    String xml = ssh.sendCommand("cd " + ssh.getLogDirectory() + " && ls Event" + tmp + "*").trim();
                    result.put("xml", xml);
                    String basicValue = ssh.sendCommand("cd " + ssh.getLogDirectory() + " && cat Event" + tmp + "* | grep '<Attribute name=\"Charge amount\" basicType=\"Numeric\">' | awk -F '\"' '{print $6}'");

                    String afterAllowance = ssh.sendCommand("cd " + ssh.getLogDirectory() + " && cat Event" + tmp + "* | grep '<Attribute name=\"Charge amount after monetary allowance\" basicType=\"Numeric\">' | awk -F '\"' '{print $6}'");

                    token = new StringTokenizer(basicValue, "\n");
                    do {
                        basicValue = token.nextToken();
                    } while (token.hasMoreTokens());
                    token = new StringTokenizer(afterAllowance, "\n");
                    do {
                        afterAllowance = token.nextToken();
                    } while (token.hasMoreTokens());
                    //System.out.println("test");
                    result.put("basicValue", basicValue);
                    result.put("afterAllowance", afterAllowance);
                    //result.put("xml", xml);
                    //result.put("grepError", "test");
                    //grepError[grepError.length-1]
                    //System.out.println("ssh "+result.get("grepError"));
                    log.info(xml + ": " + msisdn + "|" + code + "| BasicValue: " + basicValue + ", AfterAllowance: " + afterAllowance);
                } else {
                    String xml = tmp;
                    result.put("xml", xml);
                    String cmdd = "cd " + ssh.getLogDirectory() + " && cat event_file_RB* | grep " + tmp;
                    tmp = ssh.sendCommand(cmdd);

                    token = new StringTokenizer(tmp, "\n");
                    do {
                        tmp = token.nextToken();
                    } while (token.hasMoreTokens());
                    //String type = tmp.split(";")[6];
                    /*int number;
                    switch (type) {
                        case "91u":
                            number = 19;
                        case "92u":
                            number = 19;
                        case "93u":
                            number = 19;
                        case "49u":
                            number = 19;
                        case "50u":
                            number = 19;
                        case "65u":
                            number = 19;
                        case "66u":
                            number = 19;
                        case "67u":
                            number = 19;
                        case "63u":
                            number = 19;
                        case "64u":
                            number = 19;
                        case "51u":
                            number = 19;
                        case "52u":
                            number = 19;
                        case "53u":
                            number = 19;
                        case "54u":
                            number = 19;
                        case "55u":
                            number = 19;
                        case "80u":
                            number = 19;
                        case "81u":
                            number = 19;
                        case "82u":
                            number = 19;
                        case "83u":
                            number = 19;
                        default:
                            number = 0;
                    }*/
                    String substype = "PRE";
                    try {
                        Database a = new Database();
                        substype = a.getSubscriberType(msisdn);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(SSHFunction.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    String basicValue, afterAllowance;
                    if (substype.equals("POST")) {
                        //System.out.println("POST");
                        //System.out.println(tmp.split(";")[19].trim());
                        float charge = Float.parseFloat(tmp.split(";")[19].trim());
                        charge = (float) (charge / 1.1);
                        //System.out.println("POST2");
                        basicValue = tmp.split(";")[19];
                        afterAllowance = charge + "";
                    } else {
                        //System.out.println("PRE");
                        basicValue = tmp.split(";")[19];
                        afterAllowance = tmp.split(";")[19];
                    }
                    
                    token = new StringTokenizer(basicValue, "\n");
                    do {
                        basicValue = token.nextToken();
                    } while (token.hasMoreTokens());
                    token = new StringTokenizer(afterAllowance, "\n");
                    do {
                        afterAllowance = token.nextToken();
                    } while (token.hasMoreTokens());
                    //System.out.println("test");
                    result.put("basicValue", basicValue);
                    result.put("afterAllowance", afterAllowance);
                    //result.put("xml", xml);
                    //result.put("grepError", "test");
                    //grepError[grepError.length-1]
                    //System.out.println("ssh "+result.get("grepError"));
                    log.info(xml + ": " + msisdn + "|" + code + "| BasicValue: " + basicValue + ", AfterAllowance: " + afterAllowance);
                }
            } catch (NoSuchElementException ex) {
            }
            ssh.closeSession();
        }
        return result;
    }

    public boolean updateBalance(String account_ref, int amount)
            throws ClassNotFoundException {
        boolean success = false;

        String bin = "type /ttvl01/TimesTen/tt705_64/bin/ttIsql";

        SSHConnector ssh = new SSHConnector(ConfigReader.SSH_SERVER_CONFIGURATION[0], ConfigReader.SSH_SERVER_CONFIGURATION[1], ConfigReader.SSH_SERVER_CONFIGURATION[2]);
        if (ssh.createNewSession()) {
            String output = ssh.sendCommand(bin);

            System.out.println(output);
            if (output.toLowerCase().contains("not found")) {
                bin = "/ttvl000/TimesTen/tt705_64/bin/ttisql -e ";
            } else {
                bin = "/ttvl01/TimesTen/tt705_64/bin/ttIsql -e ";
            }
            ssh.closeSession();
            log.info("SSH Try command " + bin + " success");
        }
        String cmd = bin + "\"connect DSN=" + this.dsn + ";" + "UPDATE OLC1_ACC_BALANCE set BALANCE_AMOUNT =" + amount * 100 + ",TOTAL_RESERVED_AMOUNT=0" + " where account_ref=" + account_ref + ";" + "  EXIT;\"";

        ssh = new SSHConnector(ConfigReader.SSH_SERVER_CONFIGURATION[0], ConfigReader.SSH_SERVER_CONFIGURATION[1], ConfigReader.SSH_SERVER_CONFIGURATION[2]);
        if (ssh.createNewSession()) {
            log.info("SSH Login to " + this.username + "@" + this.host + " success");

            cmd = ssh.sendCommand(cmd);

            System.out.println(cmd);
            if (cmd.toLowerCase().contains("1 row updated.")) {
                success = true;

                log.info(account_ref + " Balance updated to " + amount);
            }
            ssh.closeSession();
        }
        return success;
    }

    public boolean deletePI(String customer_id)
            throws ClassNotFoundException {
        boolean success = false;

        String bin = "type /ttvl01/TimesTen/tt705_64/bin/ttIsql";

        SSHConnector ssh = new SSHConnector(ConfigReader.SSH_SERVER_CONFIGURATION[0], ConfigReader.SSH_SERVER_CONFIGURATION[1], ConfigReader.SSH_SERVER_CONFIGURATION[2]);
        if (ssh.createNewSession()) {
            String output = ssh.sendCommand(bin);

            System.out.println(output);
            if (output.toLowerCase().contains("not found")) {
                bin = "/ttvl000/TimesTen/tt705_64/bin/ttisql -e ";
            } else {
                bin = "/ttvl01/TimesTen/tt705_64/bin/ttIsql -e ";
            }
            ssh.closeSession();
            log.info("SSH Try command " + bin + " success");
        }
        String cmd = bin + "\"connect DSN=" + this.dsn + ";" + "delete from performance_ind" + " where customer_id=" + customer_id + ";" + "  EXIT;\"";

        ssh = new SSHConnector(ConfigReader.SSH_SERVER_CONFIGURATION[0], ConfigReader.SSH_SERVER_CONFIGURATION[1], ConfigReader.SSH_SERVER_CONFIGURATION[2]);
        if (ssh.createNewSession()) {
            log.info("SSH Login to " + this.username + "@" + this.host + " success");

            cmd = ssh.sendCommand(cmd);

            System.out.println(cmd);
            if (cmd.toLowerCase().contains("deleted")) {
                success = true;

                log.info(customer_id + " PI has been deleted");
            }
            ssh.closeSession();
        }
        return success;
    }

    public boolean deleteSession(String account_ref)
            throws ClassNotFoundException {
        boolean success = false;

        String bin = "type /ttvl01/TimesTen/tt705_64/bin/ttIsql";

        SSHConnector ssh = new SSHConnector(ConfigReader.SSH_SERVER_CONFIGURATION[0], ConfigReader.SSH_SERVER_CONFIGURATION[1], ConfigReader.SSH_SERVER_CONFIGURATION[2]);
        if (ssh.createNewSession()) {
            String output = ssh.sendCommand(bin);

            System.out.println(output);
            if (output.toLowerCase().contains("not found")) {
                bin = "/ttvl000/TimesTen/tt705_64/bin/ttisql -e ";
            } else {
                bin = "/ttvl01/TimesTen/tt705_64/bin/ttIsql -e ";
            }
            ssh.closeSession();
            log.info("SSH Try command " + bin + " success");
        }
        String cmd = bin + "\"connect DSN=" + this.dsn + ";" + "delete from OLC1_SESSION" + " where ACCOUNT_REF=" + account_ref + ";" + "  EXIT;\"";

        ssh = new SSHConnector(ConfigReader.SSH_SERVER_CONFIGURATION[0], ConfigReader.SSH_SERVER_CONFIGURATION[1], ConfigReader.SSH_SERVER_CONFIGURATION[2]);
        if (ssh.createNewSession()) {
            log.info("SSH Login to " + this.username + "@" + this.host + " success");

            cmd = ssh.sendCommand(cmd);

            System.out.println(cmd);
            if (cmd.toLowerCase().contains("deleted")) {
                success = true;

                log.info(account_ref + " Session has been deleted");
            }
            ssh.closeSession();
        }
        return success;
    }

    public HashMap<String, String> getLastTransactionPayu(String session, String code, String msisdn) {
        HashMap<String, String> result = new HashMap(6);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SSHFunction.class.getName()).log(Level.SEVERE, null, ex);
        }
        SSHConnector ssh = new SSHConnector(this.host, this.username, this.pwd);
        if (ssh.createNewSession()) {
            log.info("SSH Login to " + this.username + "@" + this.host + " success");
            try {
                String tmp = ssh.sendCommand("cd " + ssh.getLogDirectory() + " && ls -tr event_file_" + this.FR + "*");
                System.out.print(tmp);
                StringTokenizer token = new StringTokenizer(tmp, "\n");
                do {
                    tmp = token.nextToken();
                } while (token.hasMoreTokens());
                //System.out.println("\n\n" + tmp);

                String cmd = "cd " + ssh.getLogDirectory() + " && cat " + tmp + " | grep -F '" + session + "' | grep '" + code + "' | grep '" + msisdn + "'";
                tmp = ssh.sendCommand(cmd);

                token = new StringTokenizer(tmp, "\n");
                do {
                    tmp = token.nextToken();
                } while (token.hasMoreTokens());
                String tmp3 = tmp.split(";")[7];
                String tmp4 = "0";
                if (code.equals("51u")) {
                    tmp4 = tmp.split(";")[58];
                    System.out.println(tmp4);
                } else {
                    tmp4 = tmp.split(";")[55];
                    System.out.println(tmp4);
                }
                if (!tmp4.equals("0")) {
                    result.put("errorcode", tmp4);
                } else {
                    result.put("errorcode", "");
                }
                tmp = tmp.split(";")[0];
                /*if (tmp3.equals("16001") || tmp3.equals("16003") || tmp3.equals("50") || tmp3.equals("10")) {
                    String grepError2 = ssh.sendCommand("cd " + ssh.getLogDirectory() + " && grep " + tmp + " apperror_file_FR*").trim();
                    String[] grepError = grepError2.split(";");
                    result.put("grepErrorSession", grepError[grepError.length - 1]);
                }*/
                if (!tmp3.equals("0")) {
                    String grepError2 = ssh.sendCommand("cd " + ssh.getLogDirectory() + " && grep " + tmp + " apperror_file_FR*").trim();
                    String[] grepError = grepError2.split(";");
                    result.put("grepError", grepError[grepError.length - 1]);
                }
                String xml = null;
                if (trace_xml == true) {
                    xml = ssh.sendCommand("cd " + ssh.getLogDirectory() + " && ls Event" + tmp + "*").trim();

                } else {
                    xml = tmp;
                }
                result.put("xml", xml);
            } catch (NoSuchElementException ex) {
            }
            ssh.closeSession();
        }
        return result;
    }

    public String getFRLog(String session, String code, String msisdn, int ccnumber) {
        SSHConnector ssh = new SSHConnector(this.host, this.username, this.pwd);
        String tmp2 = "FR Log not Found";
        if (ssh.createNewSession()) {
            log.info("SSH Login to " + this.username + "@" + this.host + " success");
            try {
                String tmp = ssh.sendCommand("cd " + ssh.getLogDirectory() + " && ls -tr event_file_" + this.FR + "*");
                System.out.print(tmp);
                StringTokenizer token = new StringTokenizer(tmp, "\n");
                do {
                    tmp = token.nextToken();
                } while (token.hasMoreTokens());
                //System.out.println("\n\n" + tmp);

                String cmd = "cd " + ssh.getLogDirectory() + " && cat " + tmp + " | grep -F '" + session + "' | grep '" + code + "' | grep '" + msisdn + "'";
                tmp = ssh.sendCommand(cmd);

                token = new StringTokenizer(tmp, "\n");
                String reversetoken = null;
                do {
                    reversetoken = token.nextToken() + "\n" + reversetoken;

                } while (token.hasMoreTokens());
                token = new StringTokenizer(reversetoken, "\n");
                ccnumber = ccnumber + 1;
                String ccnumber2 = ccnumber + "";
                do {
                    tmp2 = token.nextToken();
                    if (tmp2.split(";")[11].equals(ccnumber2)) {
                        break;
                    }
                } while (token.hasMoreTokens());

            } catch (NoSuchElementException ex) {
            }
            ssh.closeSession();
        }
        return tmp2;
    }

    public String getRBLog(String session, String code, String msisdn, int ccnumber) {
        SSHConnector ssh = new SSHConnector(this.host, this.username, this.pwd);
        String tmp2 = "RB Log not Found";
        String substype = "PRE";
        String RB = "RB21";
        try {
            Database a = new Database();
            substype = a.getSubscriberType(msisdn);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SSHFunction.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (substype.equals("POST")) {
            RB = "RB11";
        }
        if (ssh.createNewSession()) {
            log.info("SSH Login to " + this.username + "@" + this.host + " success");
            try {
                String tmp = ssh.sendCommand("cd " + ssh.getLogDirectory() + " && ls -tr event_file_" + RB + "*");
                System.out.print(tmp);
                StringTokenizer token = new StringTokenizer(tmp, "\n");
                do {
                    tmp = token.nextToken();
                } while (token.hasMoreTokens());
                System.out.println("\n\n" + tmp);

                String cmd = "cd " + ssh.getLogDirectory() + " && cat " + tmp + " | grep -F '" + session + "' | grep '" + code + "' | grep '" + msisdn + "'";
                tmp = ssh.sendCommand(cmd);

                token = new StringTokenizer(tmp, "\n");
                String reversetoken = null;
                do {
                    reversetoken = token.nextToken() + "\n" + reversetoken;

                } while (token.hasMoreTokens());
                token = new StringTokenizer(reversetoken, "\n");
                ccnumber = ccnumber + 1;
                String ccnumber2 = ccnumber + "";
                do {
                    tmp2 = token.nextToken();
                    if (tmp2.split(";")[11].equals(ccnumber2)) {
                        break;
                    }
                } while (token.hasMoreTokens());
                System.out.println();
            } catch (NoSuchElementException ex) {
            }
            ssh.closeSession();
        }
        return tmp2;
    }

    public String getXML(String session, String code, String msisdn) {
        SSHConnector ssh = new SSHConnector(this.host, this.username, this.pwd);
        String xml="Error";
        if (ssh.createNewSession()) {
            log.info("SSH Login to " + this.username + "@" + this.host + " success");
            try {
                String tmp = ssh.sendCommand("cd " + ssh.getLogDirectory() + " && ls -tr event_file_" + this.FR + "*");
                //System.out.print(tmp);
                StringTokenizer token = new StringTokenizer(tmp, "\n");
                do {
                    tmp = token.nextToken();
                } while (token.hasMoreTokens());
                //System.out.println("\n\n" + tmp);

                String cmd = "cd " + ssh.getLogDirectory() + " && cat " + tmp + " | grep -F '" + session + "' | grep '" + code + "' | grep '" + msisdn + "'";
                //System.out.println(cmd);
                tmp = ssh.sendCommand(cmd);

                token = new StringTokenizer(tmp, "\n");
                do {
                    tmp = token.nextToken();
                } while (token.hasMoreTokens());
                //String tmp4 = "0";
                /*try {
                    if (code.equals("51u")) {
                        tmp4 = tmp.split(";")[58];
                        System.out.println(tmp4);
                    } else {
                        tmp4 = tmp.split(";")[55];
                        System.out.println(tmp4);
                    }
                } catch (Exception e) {

                }
                String tmp3 = tmp.split(";")[7];
                if (!tmp4.equals("0") && tmp3.equals("10017")) {
                    result.put("errorcode", tmp4);
                } else {
                    //result.put("errorcode", "");
                }

                tmp = tmp.split(";")[0];

                if (tmp3.equals("16001") || tmp3.equals("16003") || tmp3.equals("50") || tmp3.equals("10") || tmp3.equals("21140") || tmp3.equals("21100") || tmp3.equals("32003") || tmp3.equals("21010")) {
                    String grepError2 = ssh.sendCommand("cd " + ssh.getLogDirectory() + " && grep " + tmp + " apperror_file_FR*").trim();
                    String[] grepError = grepError2.split(";");
                    result.put("grepErrorSession", grepError[grepError.length - 1]);
                }
                if (!tmp3.equals("0")) {
                    String grepError2 = ssh.sendCommand("cd " + ssh.getLogDirectory() + " && grep " + tmp + " apperror_file_FR*").trim();
                    String[] grepError = grepError2.split(";");
                    result.put("grepError", grepError[grepError.length - 1]);
                }*/
                tmp = tmp.split(";")[0];
                if (trace_xml == true) {
                    String comm="cd " + ssh.getLogDirectory() + " && ls Event" + tmp + "*";
                    xml = ssh.sendCommand(comm).trim();
                    //return xml;
                    
                    //log.info(xml + ": " + msisdn + "|" + code + "| BasicValue: " + basicValue + ", AfterAllowance: " + afterAllowance);
                } else {
                    xml = tmp;
                    //System.out.println(xml);
                    //return xml;
                    
                }
            } catch (NoSuchElementException ex) {
            }
            ssh.closeSession();
        }
        return xml;
    }

}
