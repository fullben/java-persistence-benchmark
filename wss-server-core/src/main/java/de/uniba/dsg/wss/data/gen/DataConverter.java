package de.uniba.dsg.wss.data.gen;

import de.uniba.dsg.wss.data.gen.model.Carrier;
import de.uniba.dsg.wss.data.gen.model.Employee;
import de.uniba.dsg.wss.data.gen.model.Product;
import de.uniba.dsg.wss.data.gen.model.Warehouse;

/**
 * Implementers of this interface are capable of converting the generic model objects created by a
 * {@link DataGenerator} into a persistence solution-specific model structure.
 *
 * <p>Besides the methods defined in this interface, implementations must also provide access to the
 * converted data by means of appropriate getter methods.
 *
 * @param <P> the type representing a product
 * @param <W> the type representing a warehouse
 * @param <E> the type representing an employee
 * @param <C> the type representing a carrier
 * @see DataWriter
 * @see DataGenerator
 * @author Benedikt Full
 */
public interface DataConverter<P, W, E, C> {

  /**
   * Converts the given data model to the persistence solution-specific model structure.
   *
   * <p>Implementations may not hold any references to the given or created model once having
   * completed this method.
   *
   * @param model the model to be converted, must not be {@code null}
   * @return the converted model
   */
  DataModel<P, W, E, C> convert(DataModel<Product, Warehouse, Employee, Carrier> model);
}
