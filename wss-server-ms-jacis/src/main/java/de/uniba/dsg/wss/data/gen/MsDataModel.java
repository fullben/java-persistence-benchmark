package de.uniba.dsg.wss.data.gen;

import de.uniba.dsg.wss.data.model.CarrierData;
import de.uniba.dsg.wss.data.model.CustomerData;
import de.uniba.dsg.wss.data.model.DistrictData;
import de.uniba.dsg.wss.data.model.EmployeeData;
import de.uniba.dsg.wss.data.model.OrderData;
import de.uniba.dsg.wss.data.model.OrderItemData;
import de.uniba.dsg.wss.data.model.PaymentData;
import de.uniba.dsg.wss.data.model.ProductData;
import de.uniba.dsg.wss.data.model.StockData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import java.util.List;

/**
 * Class for storing the converted model data produced by {@link MsDataConverter} instances.
 *
 * @author Benedikt Full
 */
public class MsDataModel
    extends BaseDataModel<ProductData, WarehouseData, EmployeeData, CarrierData> {

  private final List<DistrictData> districts;
  private final List<CustomerData> customers;
  private final List<OrderData> orders;
  private final List<OrderItemData> orderItems;
  private final List<PaymentData> payments;
  private final List<StockData> stocks;

  public MsDataModel(
      List<ProductData> products,
      List<WarehouseData> warehouses,
      List<EmployeeData> employees,
      List<CarrierData> carriers,
      Stats stats,
      List<DistrictData> districts,
      List<CustomerData> customers,
      List<OrderData> orders,
      List<OrderItemData> orderItems,
      List<PaymentData> payments,
      List<StockData> stocks) {
    super(products, warehouses, employees, carriers, stats);
    this.districts = districts;
    this.customers = customers;
    this.orders = orders;
    this.orderItems = orderItems;
    this.payments = payments;
    this.stocks = stocks;
  }

  public List<DistrictData> getDistricts() {
    return districts;
  }

  public List<CustomerData> getCustomers() {
    return customers;
  }

  public List<OrderData> getOrders() {
    return orders;
  }

  public List<OrderItemData> getOrderItems() {
    return orderItems;
  }

  public List<PaymentData> getPayments() {
    return payments;
  }

  public List<StockData> getStocks() {
    return stocks;
  }
}
