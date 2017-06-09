package rtestingtools;

import WebServices.WSConnection;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigReader
{
  public static LinkedList<String> DESTINATIONS = new LinkedList();
  public static String SERVER_HOST_ID = null;
  public static String LOCAL_HOST = null;
  public static LinkedList<String> SCENARIOS = new LinkedList();
  public static HashMap<String, Integer> REPORT = new HashMap();
  public static long DELAY_TIME = 0L;
  public static String[] SSH_SERVER_CONFIGURATION = new String[4];
  public static String[] DB_SERVER_CONFIGURATION = new String[3];
  public static String[] WEBSERVICE_PARAMS = new String[4];
  public static FileHandler LOG_FILEHANDLER = null;
  public static boolean TRACE_XML=false;
  //public static String SECURE_TICKET = null;
  
  public static boolean read() throws Exception
  {
    boolean result = true;
    try
    {
      BufferedReader in = new BufferedReader(new FileReader("config.txt"));
      String line;
      while ((line = in.readLine()) != null)
      {
        StringTokenizer token = new StringTokenizer(line, " ");
        if ((!line.isEmpty()) && (!line.startsWith("#")) && (token.hasMoreTokens()))
        {
          String param = token.nextToken();
          if (param.equalsIgnoreCase("remote_server"))
          {
            StringTokenizer koma = new StringTokenizer(token.nextToken(), ",");
            while (koma.hasMoreTokens()) {
              DESTINATIONS.addLast(koma.nextToken());
            }
          }
          else if (param.equalsIgnoreCase("server_id"))
          {
            SERVER_HOST_ID = token.nextToken();
          }
          else if (param.equalsIgnoreCase("local_host"))
          {
            LOCAL_HOST = token.nextToken();
          }
          else if (param.equalsIgnoreCase("scenario"))
          {
            String file_scenario = token.nextToken();
            SCENARIOS.addFirst(file_scenario);
            if (token.hasMoreTokens()) {
              REPORT.put(file_scenario, Integer.valueOf(Integer.parseInt(token.nextToken())));
            }
          }
          else if (param.equalsIgnoreCase("delay_time"))
          {
            DELAY_TIME = Long.parseLong(token.nextToken());
          }
          else if (param.equalsIgnoreCase("ssh_server"))
          {
            StringTokenizer koma = new StringTokenizer(token.nextToken(), ",");
            int i = 0;
            while (koma.hasMoreTokens())
            {
              SSH_SERVER_CONFIGURATION[i] = koma.nextToken();
              i++;
            }
          }
          else if (param.equalsIgnoreCase("db_server"))
          {
            StringTokenizer koma = new StringTokenizer(token.nextToken(), ",");
            int i = 0;
            while (koma.hasMoreTokens())
            {
              DB_SERVER_CONFIGURATION[i] = koma.nextToken();
              i++;
            }
          }
          else if (param.equals("trace_xml"))
          {
              String tmp = token.nextToken();
              if(tmp.equalsIgnoreCase("true")){
                  TRACE_XML = true;
              }
          }
          else if (param.equals("webservice"))
          {
            StringTokenizer koma = new StringTokenizer(token.nextToken(), ",");
            int i = 0;
            while (koma.hasMoreTokens())
            {
              WEBSERVICE_PARAMS[i] = koma.nextToken();
              i++;
            }
          }
        }
      }
    }
    catch (FileNotFoundException ex)
    {
      Logger.getLogger(ConfigReader.class.getName()).log(Level.SEVERE, "File config tidak ada", ex);
      result = false;
    }
    catch (IOException ex)
    {
      Logger.getLogger(ConfigReader.class.getName()).log(Level.SEVERE, "File gagal dibaca", ex);
      result = false;
    }
    return result;
  }
}