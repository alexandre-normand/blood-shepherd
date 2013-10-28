package org.glukit.sync.api;

import org.threeten.bp.Instant;

import java.util.prefs.Preferences;

/**
 * Wraps {@link Preferences} for blood-shepherd with domain getter.
 *
 * @author alexandre.normand
 */
public class BloodShepherdPreferences {
  public static final String LAST_SYNC = "last.sync";
  private final Preferences preferences = Preferences.userNodeForPackage(this.getClass());

  public BloodShepherdPreferences() {
  }

  public Instant getLastSyncTime() {
    long value = this.preferences.getLong(LAST_SYNC, 0L);
    return Instant.ofEpochMilli(value);
  }

  public void saveLastSyncTime(Instant lastSyncTime) {
    this.preferences.putLong(LAST_SYNC, lastSyncTime.toEpochMilli());
  }
}
