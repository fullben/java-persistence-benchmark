package de.uniba.dsg.wss.data.model;

import java.time.LocalDateTime;
import org.jacis.plugin.objectadapter.cloning.JacisCloneable;

/**
 * A payment made by a {@link CustomerData customer}.
 *
 * @author Benedikt Full
 */
public class PaymentData extends BaseData implements JacisCloneable<PaymentData> {

  private String customerId;
  private String districtId;
  private LocalDateTime date;
  private double amount;
  private String data;

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    checkWritable();
    this.customerId = customerId;
  }

  public String getDistrictId() {
    return districtId;
  }

  public void setDistrictId(String districtId) {
    checkWritable();
    this.districtId = districtId;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    checkWritable();
    this.date = date;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    checkWritable();
    this.amount = amount;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    checkWritable();
    this.data = data;
  }

  @Override
  public PaymentData clone() {
    return (PaymentData) super.clone();
  }
}
