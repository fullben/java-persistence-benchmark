package de.uniba.dsg.wss.data.model.ms;

import java.time.LocalDateTime;

/**
 * A payment made by a {@link CustomerData customer}.
 *
 * @author Benedikt Full
 */
public class PaymentData extends BaseData{

  private CustomerData customerRef;

  private LocalDateTime date;
  private double amount;
  private String data;

  public PaymentData(CustomerData customerRef, LocalDateTime date, double amount, String data) {
    super();
    this.customerRef = customerRef;
    this.date = date;
    this.amount = amount;
    this.data = data;
  }

  public PaymentData(String id, CustomerData customerRef, LocalDateTime date, double amount, String data) {
    super(id);
    this.customerRef = customerRef;
    this.date = date;
    this.amount = amount;
    this.data = data;
  }

  public CustomerData getCustomerRef() {
    return customerRef;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public double getAmount() {
    return amount;
  }

  public String getData() {
    return data;
  }
}
