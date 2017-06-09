/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sshroaming;

/**
 *
 * @author SONA
 */
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author SONA
 */
public class SSHConnector2 {

    private Session session;
    private ChannelShell channel;
    private String username;
    private String password;
    private String hostname;

    public SSHConnector2(String host, String username, String password) {
        this.hostname = host;
        this.username = username;
        this.password = password;

    }

    private Session getSession() {
        if (session == null || !session.isConnected()) {
            session = connect(this.hostname, this.username, this.password);
        }
        return session;
    }

    private Channel getChannel() {
        if (channel == null || !channel.isConnected()) {
            try {
                channel = (ChannelShell) getSession().openChannel("shell");
                channel.connect();

            } catch (Exception e) {
                System.out.println("Error while opening channel: " + e);
            }
        }
        return channel;
    }

    private Session connect(String hostname, String username, String password) {

        JSch jSch = new JSch();

        try {

            session = jSch.getSession(username, hostname, 22);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(password);

            //System.out.println("Connecting SSH to " + hostname + " - Please wait for few seconds... ");
            session.connect();
            //System.out.println("Connected!");
        } catch (Exception e) {
            System.out.println("An error occurred while connecting to " + hostname + ": " + e);
        }

        return session;

    }

    private String executeCommands(List<String> commands) {
        String result="";
        try {
            Channel channel = getChannel();

            //System.out.println("Sending commands...");
            sendCommands(channel, commands);

            result=readChannelOutput(channel);
            //System.out.println("Finished sending commands!");

        } catch (Exception e) {
            System.out.println("An error ocurred during executeCommands: " + e);
        }
        return result;
    }

    private void sendCommands(Channel channel, List<String> commands) {

        try {
            PrintStream out = new PrintStream(channel.getOutputStream());

            out.println("#!/bin/bash");
            for (String command : commands) {
                out.println(command);
            }
            out.println("exit");

            out.flush();
        } catch (Exception e) {
            System.out.println("Error while sending commands: " + e);
        }

    }

    private String readChannelOutput(Channel channel) {

        byte[] buffer = new byte[1024];
        String result = "";
        try {
            InputStream in = channel.getInputStream();
            String line = "";
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(buffer, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    line = new String(buffer, 0, i);
                    //System.out.println(line);
                    result = result + line + "\n";
                }

                if (line.contains("logout")) {
                    break;
                }

                if (channel.isClosed()) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error while reading channel output: " + e);
        }
        return result;
    }

    public void close() {
        channel.disconnect();
        session.disconnect();
        //System.out.println("Disconnected channel and session");
    }

    public String run(List<String> commands) {
        //List<String> commands = new ArrayList<String>();
        //commands.add("cd ~/J2EEServer/config/ABP-FULL/ABPServer/scripts && pingServer");

        String result=executeCommands(commands);
        close();
        return result;
    }

    /*public static void main(String[] args){
    List<String> commands = new ArrayList<String>();
    commands.add("cd ~/J2EEServer/config/ABP-FULL/ABPServer/scripts && pingServer");

    executeCommands(commands);
    close();
}*/
}
