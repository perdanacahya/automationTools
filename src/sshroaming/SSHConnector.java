package sshroaming;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SSHConnector
{
  private JSch jsch = null;
  private Session session = null;
  private String host;
  private String user;
  private String password;
  private int port;
  private String log_directory = null;
  
  public SSHConnector(String host, String username, String password)
  {
    this(host, username, password, 22);
  }
  
  public SSHConnector(String host, String username, String password, int port)
  {
    this.jsch = new JSch();
    
    this.host = host;
    this.user = username;
    this.password = password;
    this.port = port;
    
    this.log_directory = "~/var/tks/projs/olc/log";
  }
  
  public boolean createNewSession()
  {
    return createNewSession(this.host, this.user, this.password, this.port);
  }
  
  public boolean createNewSession(String host, String user, String password, int port)
  {
    boolean result = false;
    try
    {
      this.session = this.jsch.getSession(user, host, port);
      this.session.setPassword(password);
      this.session.setConfig("StrictHostKeyChecking", "no");
      this.session.connect();
      result = true;
    }
    catch (JSchException ex)
    {
      Logger.getLogger(SSHConnector.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }
  
  public void closeSession()
  {
    this.session.disconnect();
  }
  
  public String getLogDirectory()
  {
    return this.log_directory;
  }
  
  public String sendCommand(String command)
  {
    StringBuilder outputBuffer = new StringBuilder();
    try
    {
      Channel channel = this.session.openChannel("exec");
      ((ChannelExec)channel).setCommand(command);
      channel.connect();
      InputStream commandOutput = channel.getInputStream();
      int readByte = commandOutput.read();
      while (readByte != -1)
      {
        outputBuffer.append((char)readByte);
        readByte = commandOutput.read();
      }
      channel.disconnect();
    }
    catch (IOException ioX)
    {
      Logger.getLogger(SSHConnector.class.getName()).log(Level.SEVERE, null, ioX);
      return null;
    }
    catch (JSchException jschX)
    {
      Logger.getLogger(SSHConnector.class.getName()).log(Level.SEVERE, null, jschX);
      return null;
    }
    return outputBuffer.toString();
  }
}