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
public class OfferSOAP extends Event2{

    private String MSISDN, Offer_id, Name, Value, SecureTicket;
    private WSConnection ws;
    
    public OfferSOAP(SimpleSyncClient client,String MSISDN, String Offer_id, String Name, String Value, String SecureTicket, WSConnection ws) {
        super(client);
        this.MSISDN=MSISDN;
        this.Offer_id=Offer_id;
        this.Name=Name;
        this.Value=Value;
        this.SecureTicket=SecureTicket;
        this.ws=ws;
    }
    public void addofferwithparameter() throws Exception {
        String Response=ws.addofferwithparameter(this.MSISDN, this.Offer_id, this.Name, this.Value, this.SecureTicket);
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
