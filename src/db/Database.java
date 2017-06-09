package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database
{
  private String host;
  private String user;
  private String pwd;
  
  public Database()
  {
    this.host = rtestingtools.ConfigReader.DB_SERVER_CONFIGURATION[0];
    this.user = rtestingtools.ConfigReader.DB_SERVER_CONFIGURATION[1];
    this.pwd = rtestingtools.ConfigReader.DB_SERVER_CONFIGURATION[2];
  }
  
  public String getSubscriberStatus(String subscriber)
    throws ClassNotFoundException
  {
    String status = null;
    try
    {
      Class.forName("oracle.jdbc.driver.OracleDriver");
      Connection connection = null;
      connection = DriverManager.getConnection("jdbc:oracle:thin:@" + this.host, this.user, this.pwd);
      
      Statement st = connection.createStatement();
      String sql = "select f.prim_resource_val,f.subscriber_no,f.customer_id,a.soc_cd,a.soc_name,e.effective_date,e.expiration_date,e.soc_status,f.subscriber_type,h.l9_lifecycle_status  from csm_offer a, agreement_resource b, service_agreement e,subscriber f, rpm_bucket_id g,rpm_bucket h\nwhere a.soc_cd = e.soc and b.agreement_no = e.agreement_no and b.resource_type = 'C' and b.resource_value = f.prim_resource_val\nand f.prim_resource_val =" + subscriber + "\n" + "and g.bucket_id = h.bucket_id and f.customer_id = g.customer_id";
      
      ResultSet rs = st.executeQuery(sql);
      if (rs.next()) {
        status = rs.getString("L9_LIFECYCLE_STATUS");
      }
      connection.close();
    }
    catch (SQLException ex)
    {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    return status;
  }
  
  public String getAccountRef(String msisdn)
    throws ClassNotFoundException
  {
    String accountReff = null;
    try
    {
      Class.forName("oracle.jdbc.driver.OracleDriver");
      Connection connection = null;
      connection = DriverManager.getConnection("jdbc:oracle:thin:@" + this.host, this.user, this.pwd);
      
      Statement st = connection.createStatement();
      String sql = "select pym_channel_no from csm_pay_channel\nwhere customer_id=(select customer_id from subscriber\nwhere prim_resource_val='" + msisdn + "')";
      


      ResultSet rs = st.executeQuery(sql);
      if (rs.next()) {
        accountReff = rs.getString("pym_channel_no");
      }
      connection.close();
    }
    catch (SQLException ex)
    {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    return accountReff;
  }
  
  public String getCustomerID(String msisdn)
    throws ClassNotFoundException
  {
    String customer_id = null;
    try
    {
      Class.forName("oracle.jdbc.driver.OracleDriver");
      Connection connection = null;
      connection = DriverManager.getConnection("jdbc:oracle:thin:@" + this.host, this.user, this.pwd);
      
      Statement st = connection.createStatement();
      String sql = "select customer_id from subscriber\nwhere prim_resource_val='" + msisdn + "'";
      

      ResultSet rs = st.executeQuery(sql);
      if (rs.next()) {
        customer_id = rs.getString("customer_id");
      }
      connection.close();
    }
    catch (SQLException ex)
    {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    return customer_id;
  }
  
  public boolean isStillProcessing(String customer_id)
    throws ClassNotFoundException
  {
    boolean isProcessing = true;
    try
    {
      Class.forName("oracle.jdbc.driver.OracleDriver");
      Connection connection = null;
      connection = DriverManager.getConnection("jdbc:oracle:thin:@" + this.host, this.user, this.pwd);
      
      Statement st = connection.createStatement();
      String sql = "select * from trb1_pub_log\nwhere ENTITY_ID='" + customer_id + "'";
      

      ResultSet rs = st.executeQuery(sql);
      if (!rs.next()) {
        isProcessing = false;
      }
      connection.close();
    }
    catch (SQLException ex)
    {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    return isProcessing;
  }
  public String getSubscriberType(String msisdn)
    throws ClassNotFoundException
  {
    String subs_type = null;
    try
    {
      Class.forName("oracle.jdbc.driver.OracleDriver");
      Connection connection = null;
      connection = DriverManager.getConnection("jdbc:oracle:thin:@" + this.host, this.user, this.pwd);
      
      Statement st = connection.createStatement();
      String sql = "select calc_pym_category from subscriber\nwhere prim_resource_val='" + msisdn + "'";
      

      ResultSet rs = st.executeQuery(sql);
      if (rs.next()) {
        subs_type = rs.getString("calc_pym_category");
      }
      connection.close();
    }
    catch (SQLException ex)
    {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    return subs_type;
  }
  
  public String getGroupID(String groupName)
    throws ClassNotFoundException
  {
    String group_id = null;
    try
    {
      Class.forName("oracle.jdbc.driver.OracleDriver");
      Connection connection = null;
      connection = DriverManager.getConnection("jdbc:oracle:thin:@" + this.host, this.user, this.pwd);
      
      Statement st = connection.createStatement();
      String sql = "select GROUP_ID from CM_USER_GROUPS where GROUP_NAME = '"+groupName+"'";
      

      ResultSet rs = st.executeQuery(sql);
      if (rs.next()) {
        group_id = rs.getString("GROUP_ID");
      }
      connection.close();
    }
    catch (SQLException ex)
    {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    return group_id;
  }  
  
}