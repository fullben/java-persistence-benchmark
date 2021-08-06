package de.uniba.dsg.jpb.server.service.ms;

import de.uniba.dsg.jpb.messages.PaymentRequest;
import de.uniba.dsg.jpb.messages.PaymentResponse;
import de.uniba.dsg.jpb.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.PaymentData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import de.uniba.dsg.jpb.server.data.access.ms.CustomerRepository;
import de.uniba.dsg.jpb.server.data.access.ms.WarehouseRepository;
import de.uniba.dsg.jpb.server.service.PaymentService;
import java.time.LocalDateTime;
import java.util.List;

public class MsPaymentService extends PaymentService {

  private WarehouseRepository warehouseRepository;
  private CustomerRepository customerRepository;

  @Override
  public PaymentResponse process(PaymentRequest req) {
    WarehouseData warehouse = warehouseRepository.findById(req.getCustomerId());
    DistrictData district = findDistrictById(req.getDistrictId(), warehouse.getDistricts());
    CustomerData customer = customerRepository.findById(req.getCustomerId());
    warehouse.setYearToDateBalance(warehouse.getYearToDateBalance() + req.getAmount());
    warehouse = warehouseRepository.save(warehouse);
    district.setYearToDateBalance(district.getYearToDateBalance() + req.getAmount());
    // TODO save district
    customer.setBalance(customer.getBalance() - req.getAmount());
    customer.setYearToDatePayment(customer.getYearToDatePayment() + req.getAmount());
    customer = customerRepository.save(customer);
    PaymentData payment = new PaymentData();
    payment.setCustomer(customer);
    customer.getPayments().add(payment);
    payment.setDate(LocalDateTime.now());
    payment.setDistrict(district);
    payment.setData(warehouse.getName() + "    " + district.getName());
    payment.setAmount(req.getAmount());
    // TODO save payment AND customer
    return new PaymentResponse();
  }

  private static DistrictData findDistrictById(Long id, List<DistrictData> districts) {
    return districts.stream()
        .filter(d -> d.getId().equals(id))
        .findAny()
        .orElseThrow(IllegalArgumentException::new);
  }
}
