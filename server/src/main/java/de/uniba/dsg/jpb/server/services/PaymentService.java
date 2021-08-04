package de.uniba.dsg.jpb.server.services;

import de.uniba.dsg.jpb.messages.PaymentRequest;
import de.uniba.dsg.jpb.messages.PaymentResponse;
import de.uniba.dsg.jpb.model.Customer;
import de.uniba.dsg.jpb.model.District;
import de.uniba.dsg.jpb.model.History;
import de.uniba.dsg.jpb.model.Warehouse;
import de.uniba.dsg.jpb.server.repositories.CustomerRepository;
import de.uniba.dsg.jpb.server.repositories.DistrictRepository;
import de.uniba.dsg.jpb.server.repositories.HistoryRepository;
import de.uniba.dsg.jpb.server.repositories.WarehouseRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

  private final WarehouseRepository warehouseRepository;
  private final DistrictRepository districtRepository;
  private final CustomerRepository customerRepository;
  private final HistoryRepository historyRepository;

  @Autowired
  public PaymentService(
      WarehouseRepository warehouseRepository,
      DistrictRepository districtRepository,
      CustomerRepository customerRepository,
      HistoryRepository historyRepository) {
    this.warehouseRepository = warehouseRepository;
    this.districtRepository = districtRepository;
    this.customerRepository = customerRepository;
    this.historyRepository = historyRepository;
  }

  @Transactional
  public PaymentResponse process(PaymentRequest req) {
    Warehouse warehouse = warehouseRepository.getById(req.getWarehouseId());
    District district = districtRepository.getById(req.getDistrictId());
    Customer customer = customerRepository.getById(req.getCustomerId());
    warehouse.setYearToDateBalance(warehouse.getYearToDateBalance() + req.getAmount());
    warehouse = warehouseRepository.save(warehouse);
    district.setYearToDateBalance(district.getYearToDateBalance() + req.getAmount());
    district = districtRepository.save(district);
    customer.setBalance(customer.getBalance() - req.getAmount());
    customer.setYearToDatePayment(customer.getYearToDatePayment() + req.getAmount());
    customer = customerRepository.save(customer);
    History history = new History();
    history.setCustomer(customer);
    history.setDate(LocalDateTime.now());
    history.setDistrict(district);
    history.setData(warehouse.getName() + "    " + district.getName());
    history.setAmount(req.getAmount());
    history = historyRepository.save(history);
    return new PaymentResponse();
  }
}
