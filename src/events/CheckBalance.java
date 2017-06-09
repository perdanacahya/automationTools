package events;

import dk.i1.diameter.AVP;
import dk.i1.diameter.AVP_Grouped;
import dk.i1.diameter.AVP_Integer32;
import dk.i1.diameter.AVP_UTF8String;
import dk.i1.diameter.AVP_Unsigned32;
import dk.i1.diameter.AVP_Unsigned64;
import dk.i1.diameter.InvalidAVPLengthException;
import dk.i1.diameter.Message;
import dk.i1.diameter.MessageHeader;
import dk.i1.diameter.node.Node;
import dk.i1.diameter.node.SimpleSyncClient;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import sshroaming.SSHFunction;

public class CheckBalance
  extends Event
{
  private String number;
  private String cell_info;
  private int bonus_type;
  private TYPE request;
  private int nomor;
  
  protected void noResponseHandle()
  {
    start();
  }
  
  public static enum TYPE
  {
    BALANCE,  BONUS,  LAST_TRANSACTION;
    
    private TYPE() {}
    
    public int getValue()
    {
      return ordinal() + 6;
    }
  }
  
  public static final String[] UOM_CODE = { "rupiah", "sms", "detik", "MB", "KB", "GB", "mms", "Mbytes", "menit" };
  
  public CheckBalance(SimpleSyncClient client, String number, String cell_info, int bonus_type, TYPE request, int nomor)
  {
    super(client);
    this.number = number;
    this.cell_info = cell_info;
    this.bonus_type = bonus_type;
    this.request = request;
    this.nomor=nomor;
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
    this.avps.add(new AVP_Unsigned32(415, 0));
    
    this.avps.add(new AVP_Grouped(443, new AVP[] { new AVP_Integer32(450, 0), new AVP_UTF8String(444, this.number) }));
    


    this.avps.add(new AVP_Grouped(456, new AVP[] { new AVP_Integer32(439, this.request.getValue()), new AVP_Unsigned32(27002, 0), new AVP_Unsigned32(26009, 0) }));
    




    this.avps.add(new AVP_UTF8String(25041, this.cell_info));
    
    this.avps.add(new AVP_Integer32(436, 2));
    

    AVP_Unsigned32 tmp = new AVP_Unsigned32(25037, 2);
    tmp.vendor_id = 2011;
    this.avps.add(tmp);
    

    this.avps.add(new AVP_Unsigned32(27009, this.bonus_type));
  }
  
  protected void successHandle()
  {
      SSHFunction ssh = new SSHFunction();
    try
    {
      switch (nomor)
      {
      case 1: 
        AVP balance = this.avps.find(1291);
        if (balance != null)
        {
          AVP_Grouped n = new AVP_Grouped(balance);
          n = new AVP_Grouped(n.queryAVPs()[1]);
          long bal = new AVP_Unsigned64(n.queryAVPs()[0]).queryValue();
          setResponseData("### " + ssh.getXML(getSession(), "84u", number) + " ### 888, (" + this.number + "): " + bal);
          
          System.out.println("Balance: " + bal);
        }
        break;
      case 2: 
        AVP last = this.avps.find(25032);
        AVP_Grouped o = new AVP_Grouped(last);
        AVP[] avps_temp = o.queryAVPs();
        for (int i = 0; i < avps_temp.length; i++) {
          if (avps_temp[i].code == 446) {
            o = new AVP_Grouped(avps_temp[i]);
          }
        }
        o = new AVP_Grouped(o.queryAVPs()[0]);
        o = new AVP_Grouped(o.queryAVPs()[0]);
        if (o != null)
        {
          long bal = new AVP_Unsigned64(o.queryAVPs()[0]).queryValue();
          setResponseData("### " + ssh.getXML(getSession(), "84u", number) + " ### 887, Pemakaian terakhir (" + this.number + "): " + bal);
          
          System.out.println("Pemakaian terakhir: " + bal);
        }
        break;
      case 3: 
        Iterator<AVP> iter = this.avps.iterator();
        boolean isBonusExists = false;
        String bonusResponse = "";
        while (iter.hasNext())
        {
          AVP bonus = (AVP)iter.next();
          if (bonus.code == 25001)
          {
            if (isBonusExists) {
              bonusResponse = bonusResponse + ", ";
            }
            isBonusExists = true;
            AVP_Grouped p = new AVP_Grouped(bonus);
            AVP[] bonus_temp = p.queryAVPs();
            String bonus_name = null;
            int uom_code = -1;
            System.out.println("AA " + bonus_temp.length);
            for (int i = 0; i < bonus_temp.length; i++) {
              if (bonus_temp[i].code == 445)
              {
                p = new AVP_Grouped(bonus_temp[i]);
              }
              else if (bonus_temp[i].code == 25036)
              {
                bonus_name = new AVP_UTF8String(bonus_temp[i]).queryValue();
              }
              else if (bonus_temp[i].code == 25004)
              {
                uom_code = new AVP_Unsigned32(bonus_temp[i]).queryValue();
                if (uom_code >= UOM_CODE.length) {
                  uom_code = 0;
                }
              }
            }
            long bal = new AVP_Unsigned64(p.queryAVPs()[0]).queryValue();
            bonusResponse = bonusResponse + bonus_name + "(" + bal + " " + UOM_CODE[uom_code] + ")";
            System.out.println("Pemakaian terakhir: " + bal);
          }
        }
        if (!isBonusExists) {
          setResponseData("### " + ssh.getXML(getSession(), "84u", number) + " ### 889, (" + this.number + "): Tidak memiliki bonus");
        } else {
          setResponseData("### " + ssh.getXML(getSession(), "84u", number) + " ### 889, Sisa bonus " + " (" + this.number + "): " + bonusResponse);
        }
        break;
      }
    }
    catch (InvalidAVPLengthException ex)
    {
      Logger.getLogger(CheckBalance.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
