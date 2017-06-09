/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package events;

import WebServices.WSConnection;
import dk.i1.diameter.node.SimpleSyncClient;

/**
 *
 * @author cahyaper
 */
public class SOAPservices extends Event2{
    
    private WSConnection ws;
    private String SecureTicket;
    public SOAPservices(SimpleSyncClient client, WSConnection ws, String SecureTicket) {
        super(client);
        this.ws=ws;
        this.SecureTicket=SecureTicket;
        System.out.println(this.SecureTicket);
    }

    public void attachOffer(String MSISDN, String Offer_id) throws Exception
    {
        String Response=ws.AddOffer(MSISDN, Offer_id, this.SecureTicket);
        setResponseData(Response);
    }
    
    public void addofferwithparameter(String MSISDN, String Offer_id, String Name, String Value) throws Exception {
        String Response=ws.addofferwithparameter(MSISDN, Offer_id, Name, Value, this.SecureTicket);
        setResponseData(Response);
    }
    
    public void removeOffer(String MSISDN, String Offer_id) throws Exception
    {
        String Response=ws.removeOffer(MSISDN, Offer_id, this.SecureTicket);
        setResponseData(Response);
    }
 
    public void createCUG(String groupName) throws Exception
    {
        String Response=ws.createCUG(groupName, this.SecureTicket);
        setResponseData(Response);
    }    
    
    public void getBonusInfo(String MSISDN, String Bonus_id) throws Exception
    {
        String Response=ws.getBonusInfo(MSISDN, Bonus_id, this.SecureTicket);
        setResponseData(Response);
    }
    
    public void getISORecharge(String MSISDN, String rcgAmount) throws Exception
    {
        String Response=ws.ISORecharge(MSISDN, rcgAmount, this.SecureTicket);
        setResponseData(Response);
    }

    public void getaddGroupMember(String groupId, String agreementId, String MSISDN) throws Exception
    {
        String Response=ws.addGroupMember(groupId, agreementId, MSISDN, this.SecureTicket);
        setResponseData(Response);
    }
    
    
    @Override
    protected void prepareRequest() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void successHandle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void noResponseHandle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    @Override
    protected void failHandle(int rc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
