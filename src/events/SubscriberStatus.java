package events;

import db.Database;
import dk.i1.diameter.node.SimpleSyncClient;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubscriberStatus
  extends Event2
{
  private Database db;
  private String subscriber;
  
  public SubscriberStatus(SimpleSyncClient client, String subscriber)
  {
    super(client);
    
    this.db = new Database();
    this.subscriber = subscriber;
  }
  
  protected void prepareRequest()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  protected void successHandle()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void waitForStatusChanged()
  {
    String status = null;
    do
    {
      try
      {
        Thread.sleep(1000L);
        status = this.db.getSubscriberStatus(this.subscriber);
      }
      catch (ClassNotFoundException ex)
      {
        Logger.getLogger(SubscriberStatus.class.getName()).log(Level.SEVERE, null, ex);
      }
      catch (InterruptedException ex)
      {
        Logger.getLogger(SubscriberStatus.class.getName()).log(Level.SEVERE, null, ex);
      }
    } while ((status != null) && (status.equalsIgnoreCase("P")));
    setResponseData((status != null) && (status.equalsIgnoreCase("A")) ? "### " + this.subscriber + " status ACTIVE" : status);
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