package events;

import db.Database;
import dk.i1.diameter.node.SimpleSyncClient;
import java.util.logging.Level;
import java.util.logging.Logger;
import sshroaming.SSHFunction;

public class UpdateBalance
  extends Event2
{
  private String msisdn;
  private int amount;
  
  public UpdateBalance(SimpleSyncClient client, String msisdn, int amount)
  {
    super(client);
    
    this.msisdn = msisdn;
    this.amount = amount;
  }
  
  protected void prepareRequest()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  protected void successHandle()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void update()
  {
    Database db = new Database();
    SSHFunction ssh = new SSHFunction();
    try
    {
      setResponseData("### Update Balance " + this.msisdn + " " + (ssh.updateBalance(db.getAccountRef(this.msisdn), this.amount) ? "Rp " + this.amount + " OK" : " FAIL"));
    }
    catch (ClassNotFoundException ex)
    {
      Logger.getLogger(UpdateBalance.class.getName()).log(Level.SEVERE, null, ex);
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