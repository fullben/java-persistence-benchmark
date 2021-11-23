package de.uniba.dsg.wss.data.model.ms;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MsDataRoot {

    private final Map<String, WarehouseData> warehouses;
    private final Map<String, EmployeeData> employees;
    private final Map<String, CustomerData> customers;
    // map contains a compound key: warehouseId+productId
    private final Map<String, StockData> stocks;
    private final Map<String, OrderData> orders;
    private final Map<String, CarrierData> carriers;
    private final Map<String, ProductData> products;

    public MsDataRoot(){
        warehouses = new ConcurrentHashMap<>();
        employees = new ConcurrentHashMap<>();
        customers = new ConcurrentHashMap<>();
        stocks = new ConcurrentHashMap<>();
        orders = new ConcurrentHashMap<>();
        carriers = new ConcurrentHashMap<>();
        products = new ConcurrentHashMap<>();
    }

    public Map<String, WarehouseData> getWarehouses() {
        return warehouses;
    }

    public Map<String, EmployeeData> getEmployees(){
        return employees;
    }

    public Map<String, CustomerData> getCustomers() {
        return customers;
    }

    public Map<String, StockData> getStocks() {
        return stocks;
    }

    public Map<String, OrderData> getOrders() {
        return orders;
    }

    public Map<String, CarrierData> getCarriers() {
        return carriers;
    }

    public Map<String, ProductData> getProducts() {
        return products;
    }
}
