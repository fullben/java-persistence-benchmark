package de.uniba.dsg.jpb.data.access.ms;

import de.uniba.dsg.jpb.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.OrderData;
import de.uniba.dsg.jpb.data.model.ms.OrderItemData;
import de.uniba.dsg.jpb.data.model.ms.StockData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import java.lang.reflect.Field;
import one.microstream.persistence.types.PersistenceEagerStoringFieldEvaluator;

/**
 * This evaluator identifies all fields of the MicroStream data model classes that should be
 * persisted in an eager fashion. The fields were selected based on which of the class fields are
 * commonly affected by the business transactions and thus require frequent updating.
 *
 * <p>Note that this evaluator is necessary due to the fact that the default (lazy) storage strategy
 * of MicroStream never persists any changes to objects that have been persisted before.
 *
 * <p>For example, assume some {@code Customer} {@code c} with the first name {@code "Collin"} has
 * been persisted before. If one were to call a state changing method such as {@code
 * c.setFirstName("Mike")}, followed by attempting to persist the instance (e.g. by using the {@code
 * storageManager.storeAll(c)} method), this would not result in the first name field being updated
 * in persistent storage, as the {@code Customer} instance is already known to the storage engine.
 * For actually writing the new first name to storage, one would either have to use an eager storer
 * ({@code storageManager.createEagerStorer()}), or designate the field <i>eager</i> via an
 * implementation of the {@link PersistenceEagerStoringFieldEvaluator} interface and provide this
 * implementation to the storage foundation.
 *
 * <p>When selecting fields for eager persistence, be aware that the lazy storage strategy will only
 * consider the actual field for eager storing, but none of any deeper nested objects.
 *
 * <pre>
 *   public class Inventory {
 *
 *     private String name;
 *     private Lazy&lt;List&lt;Product&gt;&gt; products;
 *     ...
 * </pre>
 *
 * This means that if one were to designate the {@code products} field <i>eager</i>, the storage
 * engine would only persist changes if the {@code Lazy} object was reassigned, but none if only the
 * list or any of the objects in the list were modified.
 *
 * @author Benedikt Full
 */
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
