package de.uniba.dsg.jpb.server.data.model.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "customers")
public class CustomerEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private DistrictEntity district;

  private String firstName;
  private String middleName;
  private String lastName;
  @Embedded private AddressEmbeddable address;
  private String phoneNumber;

  @Column(unique = true)
  private String email;

  private LocalDateTime since;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", cascade = CascadeType.ALL)
  @JsonIgnore
  private List<PaymentEntity> payments;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", cascade = CascadeType.ALL)
  @JsonIgnore
  private List<OrderEntity> orders;

  private String credit;
  private double creditLimit;
  private double discount;
  private double balance;
  private double yearToDatePayment;
  private int paymentCount;
  private int deliveryCount;
  @Lob @Column private String data;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public DistrictEntity getDistrict() {
    return district;
  }

  public void setDistrict(DistrictEntity district) {
    this.district = district;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public AddressEmbeddable getAddress() {
    return address;
  }

  public void setAddress(AddressEmbeddable address) {
    this.address = address;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public LocalDateTime getSince() {
    return since;
  }

  public void setSince(LocalDateTime since) {
    this.since = since;
  }

  public List<PaymentEntity> getPayments() {
    return payments;
  }

  public void setPayments(List<PaymentEntity> payments) {
    this.payments = payments;
  }

  public List<OrderEntity> getOrders() {
    return orders;
  }

  public void setOrders(List<OrderEntity> orders) {
    this.orders = orders;
  }

  public String getCredit() {
    return credit;
  }

  public void setCredit(String credit) {
    this.credit = credit;
  }

  public double getCreditLimit() {
    return creditLimit;
  }

  public void setCreditLimit(double creditLimit) {
    this.creditLimit = creditLimit;
  }

  public double getDiscount() {
    return discount;
  }

  public void setDiscount(double discount) {
    this.discount = discount;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  public double getYearToDatePayment() {
    return yearToDatePayment;
  }

  public void setYearToDatePayment(double yearToDatePayment) {
    this.yearToDatePayment = yearToDatePayment;
  }

  public int getPaymentCount() {
    return paymentCount;
  }

  public void setPaymentCount(int paymentCount) {
    this.paymentCount = paymentCount;
  }

  public int getDeliveryCount() {
    return deliveryCount;
  }

  public void setDeliveryCount(int deliveryCount) {
    this.deliveryCount = deliveryCount;
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
    CustomerEntity that = (CustomerEntity) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
