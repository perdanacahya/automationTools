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
public class RPL_services {
    //function for login RPL Services and also for CM services
    public static String login(String Username, String Password, String Environment)
    {
        String XMLString="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.api.interfaces.sessions.rpm.amdocs\">\n" +
"   <soapenv:Header/>\n" +
"   <soapenv:Body>\n" +
"      <ws:login>\n" +
"         <!--Optional:-->\n" +
"         <ws:userName>"+Username+"</ws:userName>\n" +
"         <!--Optional:-->\n" +
"         <ws:password>"+Password+"</ws:password>\n" +
"         <!--Optional:-->\n" +
"         <ws:environment>"+Environment+"</ws:environment>\n" +
"      </ws:login>\n" +
"   </soapenv:Body>\n" +
"</soapenv:Envelope>";
        return XMLString;
    }
    
    //function for get BonusInfo
    public static String l9getBonusInfo(String MSISDN, String BonusId, String SecureTicket)
    {
        String XML="<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.api.interfaces.sessions.rpm.amdocs\">\n" +
"   <soapenv:Header/>\n" +
"   <soapenv:Body>\n" +
"      <ws:l9GetBonusInfo soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
"         <request xsi:type=\"dat:RPL9GetBonusRequestInfo\" xmlns:dat=\"http://datatypes.rpm.amdocs\">\n" +
"            <!--Optional:-->\n" +
"            <msisdn xsi:type=\"xsd:string\">"+MSISDN+"</msisdn>\n" +
"            <!--Optional:-->\n" +
"            <bonusId xsi:type=\"xsd:int\">"+BonusId+"</bonusId>\n" +
"         </request>\n" +
"         <_awsi_header xsi:type=\"web:JFWebServiceHeader\" xmlns:web=\"http://webservices.fw.jf.amdocs\">\n" +
"            <!--Optional:-->\n" +
"            <securedTicket xsi:type=\"xsd:string\">"+SecureTicket+"</securedTicket>\n" +
"         </_awsi_header>\n" +
"      </ws:l9GetBonusInfo>\n" +
"   </soapenv:Body>\n" +
"</soapenv:Envelope>";
        return XML;
    }

    //function for get Recharge
    public static String l9ISORecharge(String MSISDN, String rcgAmount, String SecureTicket)
    {
        String XML="<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.api.interfaces.sessions.rpm.amdocs\">\n" +
"   <soapenv:Header/>\n" +
"   <soapenv:Body>\n" +
"      <ws:l9ISORecharge soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
"         <isoResponse xsi:type=\"dat:RPL9IsoRequest\" xmlns:dat=\"http://datatypes.rpm.amdocs\">\n" +
"            <!--Optional:-->\n" +
"            <rcgAmount xsi:type=\"xsd:double\">"+rcgAmount+"</rcgAmount>\n" +
"            <!--Optional:-->\n" +
"            <transactionId xsi:type=\"xsd:string\">2312412</transactionId>\n" +
"            <!--Optional:-->\n" +
"            <regionId xsi:type=\"xsd:string\">1</regionId>\n" +
"            <!--Optional:-->\n" +
"            <subscriberType xsi:type=\"xsd:string\">1</subscriberType>\n" +
"            <!--Optional:-->\n" +
"            <primaryResVal xsi:type=\"xsd:string\">"+MSISDN+"</primaryResVal>\n" +
"            <!--Optional:-->\n" +
"            <subsNodeInfo xsi:type=\"xsd:string\">1</subsNodeInfo>\n" +
"            <!--Optional:-->\n" +
"            <resellerMsisdn xsi:type=\"xsd:string\">1</resellerMsisdn>\n" +
"            <!--Optional:-->\n" +
"            <ipAddress xsi:type=\"xsd:string\">1</ipAddress>\n" +
"            <!--Optional:-->\n" +
"            <channelCode xsi:type=\"xsd:string\">MK</channelCode>\n" +
"            <!--Optional:-->\n" +
"            <bankCode xsi:type=\"xsd:string\">1</bankCode>\n" +
"            <!--Optional:-->\n" +
"            <splitCode xsi:type=\"xsd:string\">1</splitCode>\n" +
"            <!--Optional:-->\n" +
"            <rsLacci xsi:type=\"xsd:string\">1</rsLacci>\n" +
"            <!--Optional:-->\n" +
"            <methodCode xsi:type=\"xsd:string\">EV</methodCode>\n" +
"            <!--Optional:-->\n" +
"            <resNodeInfo xsi:type=\"xsd:string\">1</resNodeInfo>\n" +
"            <!--Optional:-->\n" +
"            <channelType xsi:type=\"xsd:string\">1</channelType>\n" +
"            <!--Optional:-->\n" +
"            <resellerType xsi:type=\"xsd:string\">1</resellerType>\n" +
"            <!--Optional:-->\n" +
"            <subscriberLacci xsi:type=\"xsd:string\">1</subscriberLacci>\n" +
"            <!--Optional:-->\n" +
"            <requestDate xsi:type=\"xsd:dateTime\">2002-05-30T09:00:00</requestDate>\n" +
"         </isoResponse>\n" +
"         <_awsi_header xsi:type=\"web:JFWebServiceHeader\" xmlns:web=\"http://webservices.fw.jf.amdocs\">\n" +
"            <!--Optional:-->\n" +
"            <securedTicket xsi:type=\"xsd:string\">"+SecureTicket+"</securedTicket>\n" +
"         </_awsi_header>\n" +
"      </ws:l9ISORecharge>\n" +
"   </soapenv:Body>\n" +
"</soapenv:Envelope>";
        return XML;
    }

}
