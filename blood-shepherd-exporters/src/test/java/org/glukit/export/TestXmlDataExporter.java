package org.glukit.export;

import org.glukit.sync.api.*;
import org.junit.Test;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Unit test of {@link XmlDataExporter}.
 * @author alexandre.normand
 */
public class TestXmlDataExporter {
  @Test
  public void testExport() throws Exception {
    XmlDataExporter xmlDataExporter = new XmlDataExporter();
    xmlDataExporter.setPrintStream(System.out);

    List<InsulinInjection> injections = Collections.emptyList();
    List<FoodEvent> foods = Collections.emptyList();
    List<ExerciseSession> exerciseSessions = Collections.emptyList();
    xmlDataExporter.exportData(new SyncData(Arrays.asList(new GlucoseRead(Instant.EPOCH,
            LocalDateTime.of(2013, 10, 10, 12, 00), 83f, GlucoseRead.Unit.MG_PER_DL)), injections,
            foods, exerciseSessions, new DeviceInfo("serialNumber", "hId", "hRv"), Instant.now()));
  }
}
