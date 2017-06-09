package events;

import dk.i1.diameter.AVP;
import dk.i1.diameter.AVP_Grouped;
import dk.i1.diameter.AVP_Time;
import dk.i1.diameter.AVP_Unsigned32;
import dk.i1.diameter.InvalidAVPLengthException;
import dk.i1.diameter.Message;
import dk.i1.diameter.Utils;
import dk.i1.diameter.node.Node;
import dk.i1.diameter.node.SimpleSyncClient;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import rtestingtools.ConfigReader;
import rtestingtools.ScenarioManagement;

public abstract class Event
{
  protected SimpleSyncClient connection;
  protected Message avps;
  private String responseData = "";
  private String session_id;
  private long timestamp = -1L;
  private static final Logger log = Logger.getLogger(Event.class.getName());
  private int loop=0;
  private int CloseParameter=0;
  
  public Event(SimpleSyncClient client)
  {
    if ((log.getHandlers() == null) || (log.getHandlers().length < 1))
    {
      log.addHandler(ConfigReader.LOG_FILEHANDLER);
      log.setUseParentHandlers(false);
    }
    this.connection = client;
    String[] session = client.node().makeNewSessionId().split(";");
    
    this.session_id =session[1];
  }
  
  private void processAnswer()
  {
    if (this.avps == null)
    {
      log.warning("Diameter not responding, reset diameter connection...");
      
      System.out.println("No response");
      setResponseData("### No response");
      this.connection = ScenarioManagement.resetConnection(this.connection);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Event.class.getName()).log(Level.SEVERE, null, ex);
        }
      noResponseHandle();
      return;
    }
    AVP result_code = this.avps.find(268);
    
    if (result_code == null)
    {
      System.out.println("No result code");
      setResponseData("### No result code");
      return;
    }
    try
    {
      AVP_Unsigned32 result_code_u32 = new AVP_Unsigned32(result_code);
      int rc = result_code_u32.queryValue();
      switch (rc)
      {
      case 2001: 
        System.out.println("success");
        successHandle();
        break;
      case 4010: 
        System.out.println("End user service denied");
        setResponseData("### End user service denied");
        break;
      case 4011: 
        System.out.println("Credit-control not applicable");
        setResponseData("### Credit-control not applicable");
        break;
      case 4012: 
        System.out.println("Credit-limit reached");
        setResponseData("### Credit-limit reached");
        break;
      case 5030: 
        System.out.println("User unknown");
        setResponseData("### A# unrecognized");
        break;
      case 5031: 
        System.out.println("Rating failed");
        setResponseData("### Rating failed");
        break;
      default: 
        if ((rc >= 1000) && (rc < 1999))
        {
          System.out.println("Informational: " + rc);
          setResponseData("### Informational: " + rc);
        }
        else if ((rc >= 2000) && (rc < 2999))
        {
          System.out.println("Success: " + rc);
          setResponseData("Success: " + rc);
        }
        else if ((rc >= 3000) && (rc < 3999))
        {
          System.out.println("Protocol error: " + rc);
          setResponseData("### Protocol error: " + rc);
        }
        else if ((rc >= 4000) && (rc < 4999))
        {
          System.out.println("Transient failure: " + rc);
          setResponseData("### Transient failure: " + rc);
        }
        else if ((rc >= 5000) && (rc < 5999))
        {
          if(loop<3){
            loop++;
            System.out.println("Permanent failure: " + rc);
            setResponseData("### Permanent failure: " + rc);
            log.warning("Connection Permanent Failure : "+rc+", reset diameter connection...");
            this.connection = ScenarioManagement.resetConnection(this.connection);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Event.class.getName()).log(Level.SEVERE, null, ex);
            }
             noResponseHandle();
             return;
          }else{
              loop=0;
              System.out.println("Permanent failure: " + rc);
              setResponseData("### Permanent failure: " + rc);
          }
        }
        else
        {
          System.out.println("(unknown error class): " + rc);
          setResponseData("### (unknown error class): " + rc);
        }
        break;
      }
      if (rc != 2001) {
        log.warning("Diameter negative response code: " + rc);
      }
    }
    catch (InvalidAVPLengthException ex)
    {
      System.out.println("result-code was illformed");
      setResponseData("### result-code was illformed");
      return;
    }
  }
  
  protected abstract void prepareRequest();
  
  protected abstract void successHandle();
  
  protected abstract void noResponseHandle();
  
  protected void setResponseData(String responseData)
  {
    this.responseData = responseData;
  }
  
  protected void addResponseData(String responseData)
  {
    this.responseData += responseData;
  }
  
  public void setTimeStamp(String time)
  {
    if ((time != null) && (!time.isEmpty()))
    {
      StringTokenizer token = new StringTokenizer(time, "-");
      if (token.countTokens() == 6) {
        this.timestamp = new Date(Integer.parseInt(token.nextToken()) - 1900, Integer.parseInt(token.nextToken()) - 1, Integer.parseInt(token.nextToken()), Integer.parseInt(token.nextToken()), Integer.parseInt(token.nextToken()), Integer.parseInt(token.nextToken())).getTime();
      }
      if (token.countTokens() == 4)
      {
        Calendar cal = Calendar.getInstance();
        cal.add(5, Integer.parseInt(token.nextToken().split("\\+")[1]));
        Date tmp = cal.getTime();
        tmp.setHours(Integer.parseInt(token.nextToken()));
        tmp.setMinutes(Integer.parseInt(token.nextToken()));
        tmp.setSeconds(Integer.parseInt(token.nextToken()));
        String tgl = tmp.toString();
        this.timestamp = tmp.getTime();
      }
      if (token.countTokens() == 3) {
        this.timestamp = new Date(Integer.parseInt(token.nextToken()) - 1900, Integer.parseInt(token.nextToken()) - 1, Integer.parseInt(token.nextToken())).getTime();
      }
      log.info("Timestamp is set manually: " + time);
    }
  }
  
  protected long getTimeStamp()
  {
    return (this.timestamp == -1L ? System.currentTimeMillis() : this.timestamp) / 1000L;
  }
  
  public void setSession(String session_id)
  {
    this.session_id = session_id;
  }
  
  public String getSession()
  {
    return this.session_id;
  }
  
  public void start()
  {
    this.avps = new Message();
    prepareRequest();
    
    log.info("Diameter avp preparation done");
    

    this.avps.add(new AVP_Time(55, (int)getTimeStamp()));
    

    Utils.setMandatory_RFC3588(this.avps);
    Utils.setMandatory_RFC4006(this.avps);
    try
    {
      this.connection.waitForConnection();
      this.avps = this.connection.sendRequest(this.avps, 2000L);
      
      processAnswer();
    }
    catch (InterruptedException ex)
    {
      Logger.getLogger(Event.class.getName()).log(Level.SEVERE, null, ex);
      

      log.severe("Diameter connection interrupted");
    }
  }
  
  public String getResponseData()
  {
    return this.responseData;
  }
  public void setCloseParameter(int closeParameter)
  {
    this.CloseParameter=closeParameter;
  }
  public int getCloseParameter()
  {
    return this.CloseParameter;
  }
}