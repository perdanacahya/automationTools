package events;

import db.Database;
import dk.i1.diameter.node.SimpleSyncClient;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
//import webservice.OCSWebService;
//import webservice.cm.CMException_Exception;

public class Offer
        extends Event2 {

    private String msisdn;
    private String offer;
    private LinkedList<String[]> paramInfo = new LinkedList();
    private boolean yesNoIndicator;

    public Offer(SimpleSyncClient client, String msisdn, String offer, String indicator) {
        super(client);

        this.msisdn = msisdn;
        this.offer = offer;
        this.yesNoIndicator = indicator.equalsIgnoreCase("y");
    }

    public Offer(SimpleSyncClient client, String msisdn, String offer, String indicator, String paramInfoLine) {
        this(client, msisdn, offer, indicator);
        
        String[] paramInfos = paramInfoLine.split(";");
        for (String line : paramInfos) {
            this.paramInfo.add(line.split("\\|"));
        }
    }

    /*public void attachOffer() {
        OCSWebService ws = new OCSWebService();
        try {
            if (ws.addOffer(this.msisdn, this.offer, this.yesNoIndicator, this.paramInfo)) {

                Thread.sleep(20000);
                Database db = new Database();
                String customer_id = db.getCustomerID(this.msisdn);
                while (db.isStillProcessing(customer_id)) {
                    Thread.sleep(500L);
                }
                setResponseData("### Attach offer (" + this.offer + ") ke nomer " + this.msisdn + " sukses");
            } else {
                setResponseData("### Attach offer (" + this.offer + ") ke nomer " + this.msisdn + " gagal");
            }
        } catch (CMException_Exception ex) {
            setResponseData("### Attach offer (" + this.offer + ") ke nomer " + this.msisdn + " gagal");
            Logger.getLogger(Offer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Offer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Offer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/

    /*public void removeOffer() {
        OCSWebService ws = new OCSWebService();
        try {
            if (ws.removeOffer(this.msisdn, this.offer)) {
                Database db = new Database();
                String customer_id = db.getCustomerID(this.msisdn);
                while (db.isStillProcessing(customer_id)) {
                    Thread.sleep(500L);
                }
                setResponseData("### Remove offer (" + this.offer + ") ke nomer " + this.msisdn + " sukses");
            } else {
                setResponseData("### Remove offer (" + this.offer + ") ke nomer " + this.msisdn + " gagal");
            }
        } catch (CMException_Exception ex) {
            setResponseData("### Remove offer (" + this.offer + ") ke nomer " + this.msisdn + " gagal");
            Logger.getLogger(Offer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Offer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Offer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/

    protected void prepareRequest() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void successHandle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void noResponseHandle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void failHandle(int rc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
