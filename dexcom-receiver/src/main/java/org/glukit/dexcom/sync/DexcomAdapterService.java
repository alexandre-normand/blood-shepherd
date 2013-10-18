package org.glukit.dexcom.sync;

import org.glukit.dexcom.sync.model.DexcomSyncData;
import org.glukit.sync.AdapterService;
import org.glukit.sync.api.SyncData;

/**
 * This service adapts Dexcom-specific physical models to higher-level models.
 *
 * @author alexandre.normand
 */
public class DexcomAdapterService implements AdapterService<DexcomSyncData> {

  @Override
  public SyncData convertData(DexcomSyncData source) {
    // TODO
    return null;
  }
}
