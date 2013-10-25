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
  private final Preferences preferences;

  public BloodShepherdPreferences(Preferences preferences) {
    this.preferences = preferences;
  }

  public Instant getLastSyncTime() {
    String value = this.preferences.get(LAST_SYNC, "0");
    return Instant.ofEpochMilli(Long.valueOf(value));
  }

  public void saveLastSyncTime(Instant lastSyncTime) {
    this.preferences.put(LAST_SYNC, Long.toString(lastSyncTime.toEpochMilli()));
  }
}
