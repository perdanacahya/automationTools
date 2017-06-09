package events;

import db.Database;
import dk.i1.diameter.node.SimpleSyncClient;
import java.util.logging.Level;
import java.util.logging.Logger;
import sshroaming.SSHFunction;

public class LogChecker
  extends Event2
{
  private String msisdn;
  private String session;
  private String code;
  private int ccnumber;
  
  public LogChecker(SimpleSyncClient client, String msisdn, String session, String code, int ccnumber)
  {
    super(client);
    
    this.msisdn = msisdn;
    this.session=session;
    this.code=code;
    this.ccnumber=ccnumber;
  }
  
  protected void prepareRequest()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  protected void successHandle()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void getLogFR()
  {
    SSHFunction ssh = new SSHFunction();
    String logFR = ssh.getFRLog(this.session, this.code, this.msisdn, this.ccnumber);
      setResponseData("### FR Log ### " + logFR);
    
  }
  public void getLogRB()
  {
    SSHFunction ssh = new SSHFunction();
    String logRB = ssh.getRBLog(this.session, this.code, this.msisdn, this.ccnumber);
      setResponseData("### RB Log ### " + logRB);
    
  }
  
  protected void noResponseHandle()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  protected void failHandle(int rc)
  {
      throw new UnsupportedOperationException("Not supported yet.");
  }
}