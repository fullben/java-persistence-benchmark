package de.uniba.dsg.jpb.data.access.ms;

import static java.util.Objects.requireNonNull;

import de.uniba.dsg.jpb.data.model.ms.CustomerData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomerRepository extends BaseRepository<CustomerData, Long> {

  private final Map<Long, CustomerData> idToCustomer;
  private final Map<String, CustomerData> emailToCustomer;

  CustomerRepository() {
    super();
    idToCustomer = new HashMap<>();
    emailToCustomer = new HashMap<>();
  }

  public CustomerData getByEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException();
    }
    return read(() -> requireFound(emailToCustomer.get(email)));
  }

  @Override
  public CustomerData getById(Long id) {
    requireNonNull(id);
    return read(() -> requireFound(idToCustomer.get(id)));
  }

  @Override
  public Optional<CustomerData> findById(Long id) {
    requireNonNull(id);
    return read(() -> Optional.ofNullable(idToCustomer.get(id)));
  }

  @Override
  public List<CustomerData> findAll() {
    return read(() -> new ArrayList<>(idToCustomer.values()));
  }

  @Override
  public CustomerData save(CustomerData customer) {
    requireNonNull(customer);
    return write(
        () -> {
          if (customer.getId() == null) {
            customer.setId(generateNextId(idToCustomer::containsKey));
            idToCustomer.put(customer.getId(), customer);
            emailToCustomer.put(customer.getEmail(), customer);
            getStorageManager().storeAll(idToCustomer, emailToCustomer);
          } else {
            if (!idToCustomer.containsKey(customer.getId())) {
              throw new UnknownIdentifierException(
                  "Unable to find customer for id " + customer.getId());
            }
            getStorageManager().createEagerStorer().store(customer);
          }
          return customer;
        });
  }

  @Override
  public List<CustomerData> saveAll(Collection<CustomerData> customers) {
    requireNonNull(customers);
    if (customers.isEmpty()) {
      return new ArrayList<>(0);
    }
    return write(
        () -> {
          List<CustomerData> newCustomers = new ArrayList<>();
          List<CustomerData> updatedCustomers = new ArrayList<>();
          for (CustomerData customer : customers) {
            if (customer.getId() == null) {
              customer.setId(generateNextId(idToCustomer::containsKey));
              idToCustomer.put(customer.getId(), customer);
              emailToCustomer.put(customer.getEmail(), customer);
              newCustomers.add(customer);
            } else {
              if (!idToCustomer.containsKey(customer.getId())) {
                throw new UnknownIdentifierException(
                    "Unable to find customer for id " + customer.getId());
              }
              updatedCustomers.add(customer);
            }
          }
          if (!newCustomers.isEmpty()) {
            getStorageManager().storeAll(idToCustomer, emailToCustomer);
          }
          if (!updatedCustomers.isEmpty()) {
            getStorageManager().createEagerStorer().store(updatedCustomers);
          }
          return new ArrayList<>(customers);
        });
  }

  @Override
  public int count() {
    return read(idToCustomer::size);
  }
}
