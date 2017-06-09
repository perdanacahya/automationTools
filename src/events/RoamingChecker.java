package events;

import dk.i1.diameter.node.SimpleSyncClient;
import java.util.HashMap;
import sshroaming.SSHFunction;

public class RoamingChecker
  extends Event2
{
  private String host;
  private String username;
  private String pwd;
  private String number;
  private String code;
  
  public RoamingChecker(SimpleSyncClient client, String host, String username, String pwd, String number, String code)
  {
    super(client);
    
    this.host = host;
    this.username = username;
    this.pwd = pwd;
    this.number = number;
    this.code = code;
  }
  
  protected void prepareRequest()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  protected void successHandle()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void getLastTransaction()
  {
    SSHFunction ssh = new SSHFunction();
    HashMap<String, String> result = ssh.getLastTransaction(getSession(), this.code, this.number);
    String xml = (String)result.get("xml");
    String basicValue = (String)result.get("basicValue");
    String afterAllowance = (String)result.get("afterAllowance");
    
    setResponseData("### " + getSession() + " ### " + xml + " ### roaming, Pemakaian terakhir (" + this.number + "): Charge Amount " + basicValue + ", After monetary allowance " + afterAllowance);
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