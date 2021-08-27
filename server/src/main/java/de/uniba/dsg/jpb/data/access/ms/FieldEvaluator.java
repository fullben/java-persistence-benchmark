package de.uniba.dsg.jpb.data.access.ms;

import de.uniba.dsg.jpb.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.OrderData;
import de.uniba.dsg.jpb.data.model.ms.OrderItemData;
import de.uniba.dsg.jpb.data.model.ms.StockData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import java.lang.reflect.Field;
import one.microstream.persistence.types.PersistenceEagerStoringFieldEvaluator;

public class FieldEvaluator implements PersistenceEagerStoringFieldEvaluator {

  @Override
  public boolean isEagerStoring(Class<?> clazz, Field field) {
    if (clazz == OrderData.class
        && (field.getName().equals("carrier") || field.getName().equals("fulfilled"))) {
      return true;
    } else if (clazz == OrderItemData.class && field.getName().equals("deliveryDate")) {
      return true;
    } else if (clazz == CustomerData.class
        && (field.getName().equals("balance")
            || field.getName().equals("yearToDatePayment")
            || field.getName().equals("paymentCount")
            || field.getName().equals("deliveryCount")
            || field.getName().equals("data"))) {
      return true;
    } else if (clazz == StockData.class
        && (field.getName().equals("quantity")
            || field.getName().equals("yearToDateBalance")
            || field.getName().equals("orderCount"))) {
      return true;
    } else if (clazz == WarehouseData.class && field.getName().equals("yearToDateBalance")) {
      return true;
    } else if (clazz == DistrictData.class && field.getName().equals("yearToDateBalance")) {
      return true;
    } else {
      return false;
    }
  }
}
