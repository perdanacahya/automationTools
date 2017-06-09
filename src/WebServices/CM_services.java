/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WebServices;

/**
 *
 * @author cahyaper
 */
public class CM_services {
    public static String l9AddOffer(String MSISDN, String Offer_id, String Name, String Value, String SecureTicket)
    {
        String XML="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.api.interfaces.sessions.csm3g.amdocs\" xmlns:dat=\"http://datatypes.csm3g.amdocs\" xmlns:web=\"http://webservices.fw.jf.amdocs\">\n" +
"   <soapenv:Header/>\n" +
"   <soapenv:Body>\n" +
"      <ws:l9AddOffer>\n" +
"         <!--Optional:-->\n" +
"         <ws:MSISDN>\n" +
"            <!--Optional:-->\n" +
"            <dat:msisdn>"+MSISDN+"</dat:msisdn>\n" +
"         </ws:MSISDN>\n" +
"         <ws:offerInfo>\n" +
"            <!--Optional:-->\n" +
"            <dat:soc>"+Offer_id+"</dat:soc>\n" +
"         </ws:offerInfo>\n" +
"         <!--Optional:-->\n" +
"         <!--Zero or more repetitions:-->\n" +
"		 <ws:ParameterInfo>\n" +
"			<!--Optional:-->\n" +
"            <dat:name>"+Name+"</dat:name>           \n" +
"			<!--Zero or more repetitions:-->\n" +
"            <dat:valuesArray>"+Value+"</dat:valuesArray>\n" +
"		 </ws:ParameterInfo>\n" +
"         <!--Optional:-->\n" +
"         <ws:chargeInd>\n" +
"            <!--Optional:-->\n" +
"            <dat:yesNoIndicator>78</dat:yesNoIndicator>\n" +
"         </ws:chargeInd>\n" +
"         <!--Optional:-->\n" +
"         <!--Optional:-->\n" +
"         <ws:_awsi_header>\n" +
"            <!--Optional:-->\n" +
"            <web:securedTicket>"+SecureTicket+"</web:securedTicket>\n" +
"         </ws:_awsi_header>\n" +
"      </ws:l9AddOffer>\n" +
"   </soapenv:Body>\n" +
"</soapenv:Envelope>\n" +
"";
        return XML;
    }
    
    public static String l9AddOffer(String MSISDN, String Offer_id, String SecureTicket)
    {
        String XML="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.api.interfaces.sessions.csm3g.amdocs\" xmlns:dat=\"http://datatypes.csm3g.amdocs\" xmlns:web=\"http://webservices.fw.jf.amdocs\">\n" +
"   <soapenv:Header/>\n" +
"   <soapenv:Body>\n" +
"      <ws:l9AddOffer>\n" +
"         <!--Optional:-->\n" +
"         <ws:MSISDN>\n" +
"            <!--Optional:-->\n" +
"            <dat:msisdn>"+MSISDN+"</dat:msisdn>\n" +
"         </ws:MSISDN>\n" +
"         <ws:offerInfo>\n" +
"            <!--Optional:-->\n" +
"            <dat:soc>"+Offer_id+"</dat:soc>\n" +
"         </ws:offerInfo>\n" +
"         <!--Optional:-->\n" +
"         <!--Zero or more repetitions:-->\n" +

"         <!--Optional:-->\n" +
"         <ws:chargeInd>\n" +
"            <!--Optional:-->\n" +
"            <dat:yesNoIndicator>78</dat:yesNoIndicator>\n" +
"         </ws:chargeInd>\n" +
"         <!--Optional:-->\n" +
"         <!--Optional:-->\n" +
"         <ws:_awsi_header>\n" +
"            <!--Optional:-->\n" +
"            <web:securedTicket>"+SecureTicket+"</web:securedTicket>\n" +
"         </ws:_awsi_header>\n" +
"      </ws:l9AddOffer>\n" +
"   </soapenv:Body>\n" +
"</soapenv:Envelope>\n" +
"";
        return XML;
    }
    
    public static String l9RemoveOffer(String MSISDN, String Offer_id, String SecureTicket)
    {
        String XML="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.api.interfaces.sessions.csm3g.amdocs\" xmlns:dat=\"http://datatypes.csm3g.amdocs\" xmlns:web=\"http://webservices.fw.jf.amdocs\">\n" +
"   <soapenv:Header/>\n" +
"   <soapenv:Body>\n" +
"      <ws:l9RemoveOffer>\n" +
"         <!--Optional:-->\n" +
"         <ws:MSISDN>\n" +
"            <!--Optional:-->\n" +
"            <dat:msisdn>"+MSISDN+"</dat:msisdn>\n" +
"         </ws:MSISDN>\n" +
"         <!--Optional:-->\n" +
"         <ws:offerInfo>\n" +
"            <!--Optional:-->\n" +
"            <dat:soc>"+Offer_id+"</dat:soc>\n" +
"         </ws:offerInfo>\n" +
"         <!--Optional:-->\n" +
"         <!--Optional:-->\n" +
"         <ws:_awsi_header>\n" +
"            <!--Optional:-->\n" +
"            <web:securedTicket>"+SecureTicket+"</web:securedTicket>\n" +
"         </ws:_awsi_header>\n" +
"      </ws:l9RemoveOffer>\n" +
"   </soapenv:Body>\n" +
"</soapenv:Envelope>";
        return XML;
    }
    
    //function for creatgroupcug
    public static String createGroup(String groupName, String SecureTicket)
    {
        String XML="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.api.interfaces.sessions.csm3g.amdocs\" xmlns:dat=\"http://datatypes.csm3g.amdocs\" xmlns:web=\"http://webservices.fw.jf.amdocs\">\n" +
"   <soapenv:Header/> \n" +
"   <soapenv:Body>\n" +
"      <ws:createGroup>\n" +
"         <!--Optional:-->\n" +
"         <ws:userGroupInfo>\n" +
"            <!--Optional:-->\n" +
"            <dat:groupName>"+groupName+"</dat:groupName>\n" +
"            <!--Optional:-->\n" +
"            <dat:groupType>CUG</dat:groupType>\n" +
"            <!--Optional:-->\n" +
"            <dat:groupDescription>CUG Bebas</dat:groupDescription>\n" +
"	  </ws:userGroupInfo>\n" +
"         <!--Optional:-->\n" +
" 	  <ws:activityInfo>\n" +
"            <!--Optional:-->\n" +
"	     <dat:generalFieldString>JKT</dat:generalFieldString>\n" +
"	  </ws:activityInfo>\n" +
"         <!--Optional:-->\n" +
"         <ws:_awsi_header>\n" +
"            <!--Optional:-->\n" +
"            <web:securedTicket>"+SecureTicket+"</web:securedTicket>\n" +
"         </ws:_awsi_header>\n" +
"      </ws:createGroup>\n" +
"   </soapenv:Body>\n" +
"</soapenv:Envelope>";
        return XML;
    }
    
    //function for memberCUGgroup
    public static String addGroupMember(String groupId, String agreementId, String MSISDN, String SecureTicket)
    {
        String XML="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.api.interfaces.sessions.csm3g.amdocs\" xmlns:dat=\"http://datatypes.csm3g.amdocs\" xmlns:web=\"http://webservices.fw.jf.amdocs\">\n" +
"   <soapenv:Header/>\n" +
"   <soapenv:Body>\n" +
"      <ws:addGroupMember>\n" +
"         <!--Optional:-->\n" +
"         <ws:userGroupIdInfo>\n" +
"            <!--Optional:-->\n" +
"            <dat:groupId>"+groupId+"</dat:groupId>\n" +
"         </ws:userGroupIdInfo>\n" +
"         <!--Zero or more repetitions:-->\n" +
"         <ws:userGroupMemberInfoArray>\n" +
"            <!--Optional:-->\n" +
"            <dat:agreementId>"+agreementId+"</dat:agreementId>\n" +
"            <!--Optional:-->\n" +
"            <dat:resourceType>C</dat:resourceType>\n" +
"            <!--Optional:-->\n" +
"            <dat:resourceValue>"+MSISDN+"</dat:resourceValue>\n" +
"         </ws:userGroupMemberInfoArray>\n" +
"         <!--Optional:-->\n" +
"         <ws:activityInfo>\n" +
"            <!--Optional:-->\n" +
"            <dat:generalFieldString>JKT</dat:generalFieldString>\n" +
"         </ws:activityInfo>\n" +
"         <!--Optional:-->\n" +
"         <ws:_awsi_header>\n" +
"            <!--Optional:-->\n" +
"            <web:securedTicket>"+SecureTicket+"</web:securedTicket>\n" +
"         </ws:_awsi_header>\n" +
"      </ws:addGroupMember>\n" +
"   </soapenv:Body>\n" +
"</soapenv:Envelope>";
        return XML;
    }     
}
