package de.uniba.dsg.jpb.server.messages;

public class PaymentResponse {

  // TODO this is supposed to contain a lotta data (see page 36 of spec)
  private Long paymentId;

  public Long getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(Long paymentId) {
    this.paymentId = paymentId;
  }
}
