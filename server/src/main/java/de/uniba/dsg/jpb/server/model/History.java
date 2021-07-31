package de.uniba.dsg.jpb.server.model;

import de.uniba.dsg.jpb.server.model.id.HistoryId;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
@IdClass(HistoryId.class)
public class History {

  @Id
  @OneToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumns({
    @JoinColumn(name = "customer_id", referencedColumnName = "id"),
    @JoinColumn(name = "customer_district_id", referencedColumnName = "district_id"),
    @JoinColumn(
        name = "customer_district_warehouse_id",
        referencedColumnName = "district_warehouse_id")
  })
  private Customer customer;

  @Column(name = "history_date")
  private LocalDateTime date;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private District district;

  private double amount;
  private String data;

  public History() {
    customer = null;
    date = null;
    amount = 0;
    data = null;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public District getDistrict() {
    return district;
  }

  public void setDistrict(District district) {
    this.district = district;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    History history = (History) o;
    return customer.equals(history.customer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(customer);
  }
}
