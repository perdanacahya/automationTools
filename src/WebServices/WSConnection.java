/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WebServices;

import db.Database;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author cahyaper
 */
public class WSConnection {
    private String URLlinkSecureTicket, SOAP_ticketAction;
    private String URLRPLservices, SOAP_RPLAction;
    private String URLCMservices, SOAP_CMAction;
    private String Username, Password, Environment;
    public WSConnection(String URL, String Username, String Password, String Environment)
    {
        //Login Secure ticket
        this.URLlinkSecureTicket="http://"+URL+"/awsi_rpl9_flat/services/RPLLoginService";
        this.SOAP_ticketAction="http://"+URL+"/awsi_rpl9_flat/services";
        //URL for RPL services
        this.URLRPLservices="http://"+URL+"/awsi_rpl9_flat/services/RPL_RPL1RechargeServices";
        this.SOAP_RPLAction="http://"+URL+"/awsi_rpl9_flat/services";
        //URL for CM Subscriber services
        this.URLCMservices="http://"+URL+"/awsi_cm9_flat/services/CM_SubscriberServices";
        this.SOAP_CMAction="http://"+URL+"/awsi_cm9_flat/services";
        
        this.Username= Username;
        this.Password=Password;
        this.Environment=Environment;
    }
    
    private String SendRequest(String XMLrequest, String URLlink, String Soap_action) throws Exception
    {
        URL obj= new URL(URLlink);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // setup Headers
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("SOAPAction", Soap_action);
        //send Request XML
        OutputStream out = con.getOutputStream();
        Writer wout = new OutputStreamWriter(out);
        wout.write(XMLrequest);
        wout.flush();
        wout.close();
        String Response="";
        int ResponseMessage=con.getResponseCode();
        if(ResponseMessage!=200)
        {
            Response="Response Error because error "+ResponseMessage;
            return Response;
        }
        InputStream in=con.getInputStream();
        int readInput=in.read();
        while(readInput!=-1){
            Response+=(char)readInput;
            readInput=in.read();
        }
        in.close();
        return Response;
    }
    
    public String getSecureTicket() throws Exception
    {
        String XML_request=RPL_services.login(this.Username, this.Password, this.Environment);
        String Response=SendRequest(XML_request, this.URLlinkSecureTicket, SOAP_ticketAction);
        String ticket="";
        if(Response.contains("Error"))
        {
            return Response;
        }
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(Response));
        Document doc=db.parse(is);
        ticket=doc.getElementsByTagName("loginReturn").item(0).getTextContent();
        Response=ticket.replace("<", "&lt;");
        return Response;
    }

//get bonus    
    public String getBonusInfo(String MSISDN, String Bonus_id, String Secure_ticket) throws Exception
    {
        String XML_request=RPL_services.l9getBonusInfo(MSISDN, Bonus_id, Secure_ticket);
        String Response=SendRequest(XML_request, this.URLRPLservices, this.SOAP_RPLAction);
        
        if(Response.contains("Error"))
        {
            return Response;
        }
        //parse XML
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(Response));
        Document doc=db.parse(is);
        NodeList nodes = doc.getElementsByTagName("multiRef");
        String Result="";
        for(int i=0; i<nodes.getLength(); i++){
            Node nNode=nodes.item(i);
            if(nNode.getNodeType()==Node.ELEMENT_NODE)
            {
                 Element eElement = (Element) nNode;
                if(eElement.getAttribute("xsi:type").contains("RPL9GetBonusInfo"))
                {
                    Result+="|"+eElement.getElementsByTagName("itemId").item(0).getTextContent().replace(" ", "")+"#"
                            + eElement.getElementsByTagName("bonusName").item(0).getTextContent()+"#"
                            +eElement.getElementsByTagName("bonusAmount").item(0).getTextContent()+"#"
                            +eElement.getElementsByTagName("bonusType").item(0).getTextContent()+"#"
                            +eElement.getElementsByTagName("bucketIdentifier").item(0).getTextContent()+"#"
                            +eElement.getElementsByTagName("bonusExpiry").item(0).getTextContent();   
                }
            }
        }
        Response="GetBonusInfo ### "+Result;
        return Response;
    }
    
    public String addofferwithparameter(String MSISDN, String Offer_id, String Name, String Value, String SecureTicket) throws Exception
    {
        String XML_Request=CM_services.l9AddOffer(MSISDN, Offer_id, Name, Value, SecureTicket);
        String Response=SendRequest(XML_Request, this.URLCMservices, this.SOAP_CMAction);
        if(Response.contains("Error"))
        {
            Response="### Attach offer (" + Offer_id+ ")"+Name+" ke nomer " + MSISDN + " Gagal";
            return Response;
        }
        Database db = new Database();
        String customer_id = db.getCustomerID(MSISDN);
        while (db.isStillProcessing(customer_id)) {
            Thread.sleep(500L);
        }
        Response="### Attach offer (" + Offer_id+ ")"+Name+" ke nomer " + MSISDN + " sukses";
        return Response;
    }
    
    public String AddOffer(String MSISDN, String Offer_id,String SecureTicket) throws Exception
    {
        String XML_Request=CM_services.l9AddOffer(MSISDN, Offer_id, SecureTicket);
        String Response=SendRequest(XML_Request, this.URLCMservices, this.SOAP_CMAction);
        if(Response.contains("Error"))
        {
            Response="### Attach offer (" + Offer_id+ ")ke nomer " + MSISDN + " Gagal";
            return Response;
        }
        Database db = new Database();
        String customer_id = db.getCustomerID(MSISDN);
        while (db.isStillProcessing(customer_id)) {
            Thread.sleep(500L);
        }
        Response="### Attach offer (" + Offer_id+ ") ke nomer " + MSISDN + " sukses";
        return Response;
    }
    public String removeOffer(String MSISDN, String Offer_id, String SecureTicket) throws Exception
    {
        String XML_Request=CM_services.l9RemoveOffer(MSISDN, Offer_id, SecureTicket);
        String Response=SendRequest(XML_Request, this.URLCMservices, this.SOAP_CMAction);
        if(Response.contains("Error"))
        {
            Response="### Remove Offer (" + Offer_id+ ")"+Offer_id+" ke nomer " + MSISDN + " Gagal";
            return Response;
        }
        Database db = new Database();
        String customer_id = db.getCustomerID(MSISDN);
        while (db.isStillProcessing(customer_id)) {
            Thread.sleep(500L);
        }
        Response="### Remove Offer (" + Offer_id+ ") ke nomer " + MSISDN + " sukses";
        return Response;
    }
    
    public String createCUG(String groupName, String SecureTicket) throws Exception
    {
        String XML_Request=CM_services.createGroup(groupName, SecureTicket);
        String Response=SendRequest(XML_Request, this.URLCMservices, this.SOAP_CMAction);
        
        if(Response.contains("Error"))
        {
            Response="### creatCUGname = " +groupName+ " =" +SecureTicket+ " Gagal";
            return Response;
        }
//ga pake db tapi pake xml
        //parse XML
//        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//        InputSource is = new InputSource();
//        is.setCharacterStream(new StringReader(Response));
//        Document doc=db.parse(is);
//        NodeList nodes = doc.getElementsByTagName("multiRef");
//        String Result="";
//        for(int i=0; i<nodes.getLength(); i++){
//            Node nNode=nodes.item(i);
//            if(nNode.getNodeType()==Node.ELEMENT_NODE)
//            {
//                 Element eElement = (Element) nNode;
//                if(eElement.getAttribute("xmlns:ns").contains("createGroup"))
//                {
//                    Result+="|"+eElement.getElementsByTagName("ns1:groupId").item(0).getTextContent();   
//                }
//            }
//        }

//pake db,database        
        Database db = new Database();
        String group_id = db.getGroupID(groupName);
        while (db.isStillProcessing(group_id)) {
            Thread.sleep(500L);
      }
        Response="### creatCUGname = "+groupName+" = Berhasil";
        return Response;
    }
    
//recharge
    public String ISORecharge(String MSISDN, String rcgAmount, String Secure_ticket) throws Exception
    {
        String XML_request=RPL_services.l9ISORecharge(MSISDN, rcgAmount, Secure_ticket);
        String Response=SendRequest(XML_request, this.URLRPLservices, this.SOAP_RPLAction);
        
        if(Response.contains("Error"))
        {
        Response="### Recharge (" + rcgAmount+ ")"+rcgAmount+" ke nomer " + MSISDN + " Gagal";
        return Response;
        }
        //parse XML
//        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//        InputSource is = new InputSource();
//        is.setCharacterStream(new StringReader(Response));
//        Document doc=db.parse(is);
//        NodeList nodes = doc.getElementsByTagName("multiRef");
//        String Result="";
//        for(int i=0; i<nodes.getLength(); i++){
//            Node nNode=nodes.item(i);
//            if(nNode.getNodeType()==Node.ELEMENT_NODE)
//            {
//                 Element eElement = (Element) nNode;
//                if(eElement.getAttribute("xsi:type").contains("RPL9ISORecharge"))
//                {
//                    Result+="|"+eElement.getElementsByTagName("primaryResVal").item(0).getTextContent().replace(" ", "")+"#"
//                            + eElement.getElementsByTagName("rcgAmount").item(0).getTextContent()+"#"
//                            +eElement.getElementsByTagName("responseCode").item(0).getTextContent();   
//                }
//            }
//        }
        Response="Recharge (" + rcgAmount+ ")"+rcgAmount+" ke nomer " + MSISDN + " BERHASIL";
        return Response;
    }   
    
    public String addGroupMember(String groupId, String agreementId, String MSISDN, String SecureTicket) throws Exception
    {
        String XML_Request=CM_services.addGroupMember(groupId, agreementId, MSISDN, SecureTicket);
        String Response=SendRequest(XML_Request, this.URLCMservices, this.SOAP_CMAction);
        
        if(Response.contains("Error"))
        {
            Response="### addGroupMember ke " +groupId+ " dengan member " +MSISDN+ " Gagal";
            return Response;
        }
//ga pake db tapi pake xml
        //parse XML
//        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//        InputSource is = new InputSource();
//        is.setCharacterStream(new StringReader(Response));
//        Document doc=db.parse(is);
//        NodeList nodes = doc.getElementsByTagName("multiRef");
//        String Result="";
//        for(int i=0; i<nodes.getLength(); i++){
//            Node nNode=nodes.item(i);
//            if(nNode.getNodeType()==Node.ELEMENT_NODE)
//            {
//                 Element eElement = (Element) nNode;
//                if(eElement.getAttribute("xmlns:ns").contains("createGroup"))
//                {
//                    Result+="|"+eElement.getElementsByTagName("ns1:groupId").item(0).getTextContent();   
//                }
//            }
//        }

//pake db,database        
//        Database db = new Database();
//        String group_id = db.getGroupID(groupName);
//        while (db.isStillProcessing(group_id)) {
//            Thread.sleep(500L);
//      }
        Response="### addGroupMember ke " +groupId+ " dengan member " +MSISDN+ " Berhasil";
        return Response;
    }    
}
