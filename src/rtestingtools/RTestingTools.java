package rtestingtools;

import dk.i1.diameter.node.Capability;
import dk.i1.diameter.node.InvalidSettingException;
import dk.i1.diameter.node.NodeSettings;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RTestingTools
{
  public static void main(String[] args) throws Exception
  {
    try
    {
      if (!ConfigReader.read())
      {
        Logger.getLogger(RTestingTools.class.getName()).log(Level.SEVERE, "Error reading file config");
        
        return;
      }
      String host_id = ConfigReader.LOCAL_HOST;
      String realm = "amdocs.com";
      String[] destination = new String[ConfigReader.DESTINATIONS.size()];
      destination = (String[])ConfigReader.DESTINATIONS.toArray(destination);
      Capability capability = new Capability();
      capability.addAuthApp(4);
      NodeSettings node_settings;
      //System.out.println(ConfigReader.SECURE_TICKET);
      try
      {
        node_settings = new NodeSettings(host_id, realm, 2011, capability, 113, "SPV1", 0);

        node_settings.setUseTCP(Boolean.valueOf(true));
      }
      catch (InvalidSettingException e)
      {
        System.out.println(e.toString());
        return;
      }
      LinkedList<Thread> threadList = new LinkedList();
      
      Iterator<String> iterator = ConfigReader.SCENARIOS.iterator();
      while (iterator.hasNext())
      {
        threadList.addLast(new Thread(new ScenarioManagement(destination, node_settings, (String)iterator.next())));
        //System.out.println((String)iterator.next());
        ((Thread)threadList.getLast()).start();
      }
      Iterator<Thread> threadIterator = threadList.iterator();
      while (threadIterator.hasNext()) {
        ((Thread)threadIterator.next()).join();
      }
    }
    catch (InterruptedException ex)
    {
      Logger.getLogger(RTestingTools.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}