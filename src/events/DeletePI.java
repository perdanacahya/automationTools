package events;

import db.Database;
import dk.i1.diameter.node.SimpleSyncClient;
import java.util.logging.Level;
import java.util.logging.Logger;
import sshroaming.SSHFunction;

public class DeletePI
  extends Event2
{
  private String msisdn;
  
  public DeletePI(SimpleSyncClient client, String msisdn)
  {
    super(client);
    
    this.msisdn = msisdn;
  }
  
  protected void prepareRequest()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  protected void successHandle()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void delete()
  {
    Database db = new Database();
    SSHFunction ssh = new SSHFunction();
    try
    {
      setResponseData("### Delete PI " + this.msisdn + (ssh.deletePI(db.getCustomerID(this.msisdn)) ? " OK" : " FAIL"));
    }
    catch (ClassNotFoundException ex)
    {
      Logger.getLogger(DeletePI.class.getName()).log(Level.SEVERE, null, ex);
    }
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