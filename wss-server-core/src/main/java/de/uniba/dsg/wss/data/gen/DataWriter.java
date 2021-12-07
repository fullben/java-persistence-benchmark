package de.uniba.dsg.wss.data.gen;

/**
 * Implementers of this interface are capable of writing the objects provided by a data provider to
 * persistent storage.
 *
 * @param <P> the type representing a product
 * @param <W> the type representing a warehouse
 * @param <E> the type representing an employee
 * @param <C> the type representing a carrier
 * @see DataConverter
 * @author Benedikt Full
 */
public interface DataWriter<P, W, E, C> {

  /**
   * Writes the given model data to the backing persistence solution.
   *
   * <p>Implementations must call {@link #supports(DataModel)} with the given {@code model} before
   * performing any actual writing. In case {@code false} is returned, the method must immediately
   * throw an {@link UnsupportedDataModelException}.
   *
   * <pre>
   *   ...
   *   void write(DataModel&lt;Product, Warehouse, Employee, Carrier&gt; model) {
   *     if (!supports(model)) {
   *       throw new UnsupportedDataModelException(
   *           "Unsupported model type: "
   *               + (model == null ? null : model.getClass().getName()));
   *     }
   *     // Do actual writing here
   *   }
   *   ...
   * </pre>
   *
   * @param model the non-{@code null} data model
   * @throws UnsupportedDataModelException if the given {@code model} is {@code null} or {@link
   *     #supports(DataModel)} returns {@code false}
   * @see #supports(DataModel)
   */
  void write(DataModel<P, W, E, C> model);

  /**
   * Checks whether this {@code DataWriter} supports the provided model.
   *
   * <p>This method is meant for providing writers with the opportunity to restrict their
   * requirements regarding the supported {@link DataModel} implementation beyond the class type
   * parameters.
   *
   * <p>The default implementation accepts any non-{@code null} model.
   *
   * @param model some model or {@code null}
   * @return {@code true} if the given model is not {@code null}, {@code false} otherwise
   * @see #supports(DataModel)
   */
  default boolean supports(DataModel<P, W, E, C> model) {
    return model != null;
  }
}
