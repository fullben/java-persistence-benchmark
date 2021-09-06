package de.uniba.dsg.jpb.data.model.ms;

import java.util.UUID;
import org.jacis.plugin.readonly.object.AbstractReadOnlyModeSupportingObject;

/**
 * The base class for all MicroStream data classes. It defines the identifier for the object, which
 * is a UUID.
 *
 * <p>Furthermore, this class extends the base class of all classes supporting a read-only view of
 * the JACIS library. Consequently, any implementers of this class must call the {@link
 * #checkWritable()} method in all their state-modifying methods prior to making any changes.
 *
 * @author Benedikt Full
 */
public abstract class BaseData extends AbstractReadOnlyModeSupportingObject {

  private String id;

  public BaseData() {
    id = UUID.randomUUID().toString();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    checkWritable();
    this.id = id;
  }
}
