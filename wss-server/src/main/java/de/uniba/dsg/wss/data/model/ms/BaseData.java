package de.uniba.dsg.wss.data.model.ms;

import java.util.UUID;
import org.jacis.exception.ReadOnlyException;
import org.jacis.plugin.readonly.object.JacisReadonlyModeSupport;

/**
 * The base class for all MicroStream data classes. It defines the identifier for the object, which
 * is a UUID.
 *
 * <p>Note that implementers of this class must call the {@link #checkWritable()} method in all
 * their state-modifying methods prior to making any changes. This is necessary to ensure that the
 * objects can be switched between read and write mode, as defined by the JACIS library (see {@link
 * JacisReadonlyModeSupport} for further information).
 *
 * @author Benedikt Full
 */
public abstract class BaseData implements JacisReadonlyModeSupport {

  private transient boolean writable;
  private String id;

  public BaseData() {
    writable = true;
    id = UUID.randomUUID().toString();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    checkWritable();
    this.id = id;
  }

  /**
   * Verifies whether this object is currently writable by checking a corresponding internal flag.
   *
   * <p>This method should be called prior to modifying the state of an object (e.g. in all
   * setters).
   *
   * <pre>
   *   public void setName(String name) {
   *     checkWritable();
   *     this.name = name;
   *   }
   * </pre>
   *
   * <p>The method will check if the object is writable and will throw a {@link ReadOnlyException}
   * if that is not the case.
   *
   * @throws ReadOnlyException if the object is currently not writable
   */
  protected void checkWritable() throws ReadOnlyException {
    if (!writable) {
      throw new ReadOnlyException(
          "Object currently in read only mode! Accessing Thread: "
              + Thread.currentThread()
              + ". Object: "
              + this);
    }
  }

  @Override
  protected Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError("Could not clone " + this.getClass().getName());
    }
  }

  @Override
  public void switchToReadOnlyMode() {
    writable = false;
  }

  @Override
  public void switchToReadWriteMode() {
    writable = true;
  }

  @Override
  public boolean isReadOnly() {
    return !writable;
  }

  @Override
  public boolean isWritable() {
    return writable;
  }
}
