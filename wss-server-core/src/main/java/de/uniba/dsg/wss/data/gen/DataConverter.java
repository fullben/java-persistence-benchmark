package de.uniba.dsg.wss.data.gen;

/**
 * Implementers of this interface are capable of converting the generic model objects created by a
 * {@link DataGenerator} into a persistence solution-specific model structure.
 *
 * <p>Besides the methods defined in this interface, implementations must also provide access to the
 * converted data by means of appropriate getter methods.
 *
 * @see DataWriter
 * @author Benedikt Full
 */
public interface DataConverter {

  /**
   * Converts the data held by the given generator to the persistence solution-specific model
   * structure. This instance will maintain internal references to the newly created data
   * structures.
   *
   * <p>Before converting the data, implementations of this method must check whether the generators
   * already has generated the data to be converted. If this is not the case, the method must
   * trigger the generation using {@link DataGenerator#generate()}.
   *
   * <p>Note that implementations of this method may not assign the provided generator to any
   * instance fields. Once this method has completed execution, the instance may not hold references
   * to the given generator.
   *
   * @param generator the generator of which the data will be used for conversion, must not be
   *     {@code null}
   */
  void convert(DataGenerator generator);

  /**
   * Indicates whether this converter currently holds references to any converted model data. This
   * is the case if {@link #convert(DataGenerator)} has been called before, without {@link #clear()}
   * having been called in the meantime.
   *
   * <p>If this method returns {@code true}, the getters of this converter should return references
   * to non-{@code null}, valid model data.
   *
   * @return {@code true} if the converter has converted data, {@code false} otherwise
   */
  boolean hasConvertedData();

  /** Removes all internal references to any data previously converted by this instance. */
  void clear();
}
