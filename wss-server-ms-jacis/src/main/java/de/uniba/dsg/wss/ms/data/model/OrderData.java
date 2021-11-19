package de.uniba.dsg.wss.ms.data.model;

import java.time.LocalDateTime;
import org.jacis.plugin.objectadapter.cloning.JacisCloneable;

/**
 * An order issued by a {@link CustomerData customer} for a certain amount of {@link ProductData
 * products}.
 *
 * @see OrderItemData
 * @author Benedikt Full
 */
public class OrderData extends BaseData implements JacisCloneable<OrderData> {

  private String districtId;
  private String customerId;
  private String carrierId;
  private LocalDateTime entryDate;
  private int itemCount;
  private boolean allLocal;
  private boolean fulfilled;

  public String getDistrictId() {
    return districtId;
  }

  public void setDistrictId(String districtId) {
    checkWritable();
    this.districtId = districtId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    checkWritable();
    this.customerId = customerId;
  }

  public String getCarrierId() {
    return carrierId;
  }

  public void setCarrierId(String carrierId) {
    checkWritable();
    this.carrierId = carrierId;
  }

  public LocalDateTime getEntryDate() {
    return entryDate;
  }

  public void setEntryDate(LocalDateTime entryDate) {
    checkWritable();
    this.entryDate = entryDate;
  }

  public int getItemCount() {
    return itemCount;
  }

  public void setItemCount(int itemCount) {
    checkWritable();
    this.itemCount = itemCount;
  }

  public boolean isAllLocal() {
    return allLocal;
  }

  public void setAllLocal(boolean allLocal) {
    checkWritable();
    this.allLocal = allLocal;
  }

  public boolean isFulfilled() {
    return fulfilled;
  }

  public void setFulfilled(boolean fulfilled) {
    checkWritable();
    this.fulfilled = fulfilled;
  }

  @Override
  public OrderData clone() {
    return (OrderData) super.clone();
  }
}
