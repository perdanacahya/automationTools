package events;

import dk.i1.diameter.AVP;
import dk.i1.diameter.AVP_Grouped;
import dk.i1.diameter.AVP_Integer32;
import dk.i1.diameter.AVP_UTF8String;
import dk.i1.diameter.AVP_Unsigned32;
import dk.i1.diameter.Message;
import dk.i1.diameter.MessageHeader;
import dk.i1.diameter.node.Node;
import dk.i1.diameter.node.SimpleSyncClient;

public class FirstLocationUpdate
  extends Event2
{
  private String a_number;
  private String cell_info;
  
  public FirstLocationUpdate(SimpleSyncClient test, String a_number, String cell_info)
  {
    super(test);
    this.a_number = a_number;
    this.cell_info = cell_info;
  }
  
  protected void prepareRequest()
  {
    this.avps.hdr.command_code = 272;
    this.avps.hdr.application_id = 4;
    this.avps.hdr.setRequest(true);
    this.avps.hdr.setProxiable(true);
    

    this.avps.add(new AVP_UTF8String(263, getSession()));
    

    this.connection.node().addOurHostAndRealm(this.avps);
    

    this.avps.add(new AVP_Unsigned32(258, 4));
    

    this.avps.add(new AVP_UTF8String(461, "0"));
    this.avps.add(new AVP_Integer32(416, 4));
    this.avps.add(new AVP_Unsigned32(415, 1));
    
    this.avps.add(new AVP_Grouped(443, new AVP[] { new AVP_Integer32(450, 0), new AVP_UTF8String(444, this.a_number) }));
    


    this.avps.add(new AVP_Grouped(456, new AVP[] { new AVP_Unsigned32(439, 11) }));
    


    this.avps.add(new AVP_Unsigned32(436, 2));
    

    this.avps.add(new AVP_UTF8String(22, 2011, "1"));
    

    this.avps.add(new AVP_UTF8String(25041, 2011, this.cell_info));
  }
  
  protected void successHandle()
  {
    setResponseData("### " + getSession() + " ### " + this.a_number + " ACTIVATED");
  }
  
  protected void noResponseHandle()
  {
    start();
  }
  protected void failHandle(int rc)
  {
      throw new UnsupportedOperationException("Not supported yet.");
  }
}