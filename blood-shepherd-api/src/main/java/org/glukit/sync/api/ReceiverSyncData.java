package org.glukit.sync.api;

import org.threeten.bp.Instant;

/**
 * Interface for receiver implementation's sync data. This is fed to the {@link org.glukit.sync.AdapterService} to
 * produce a {@link SyncData}.
 *
 * @author alexandre.normand
 */
public interface ReceiverSyncData {
  /**
   * @return the time of the last update. This is what's used as the lower bound for next syncs.
   */
  Instant getUpdateTime();
}
