package org.glukit.sync;

import org.glukit.sync.api.SyncData;

/**
 * Adapter service interface. The contact is simply to return a {@link org.glukit.sync.api.SyncData} from
 * whatever is the source of data.
 *
 * @author alexandre.normand
 */
public interface AdapterService<T> {
  SyncData convertData(T source);
}
