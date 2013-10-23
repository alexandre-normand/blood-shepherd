package org.glukit.export;

import org.glukit.sync.api.*;
import org.junit.Test;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Unit test of {@link XmlDataExporter}.
 *
 * @author alexandre.normand
 */
public class TestXmlDataExporter {
  @Test
  public void testExportGlucoseRead() throws Exception {
    XmlDataExporter xmlDataExporter = new XmlDataExporter();
    xmlDataExporter.setPrintStream(System.out);

    List<InsulinInjection> injections = Collections.emptyList();
    List<FoodEvent> foods = Collections.emptyList();
    List<ExerciseSession> exerciseSessions = Collections.emptyList();

    // TODO: assert that the content is as expected
    xmlDataExporter.exportData(new SyncData(Arrays.asList(new GlucoseRead(Instant.EPOCH,
            LocalDateTime.of(2013, 10, 10, 12, 00), 83f, GlucoseRead.Unit.MG_PER_DL)), injections,
            foods, exerciseSessions, new DeviceInfo("serialNumber", "hId", "hRv"), Instant.now()));
  }

  @Test
  public void testEventSort() throws Exception {
    XmlDataExporter xmlDataExporter = new XmlDataExporter();
    xmlDataExporter.setPrintStream(System.out);

    List<InsulinInjection> injections = Collections.emptyList();
    List<FoodEvent> foods = newArrayList();
    foods.add(new FoodEvent(Instant.ofEpochSecond(2000), LocalDateTime.now(), LocalDateTime.now(), 10.25f, 0));
    foods.add(new FoodEvent(Instant.ofEpochSecond(100), LocalDateTime.now(), LocalDateTime.now(), 12.5f, 0));
    List<ExerciseSession> exerciseSessions = Collections.emptyList();
    List<GlucoseRead> emptyGlucoseReads = Collections.emptyList();

    // TODO: Assert that the content is as expected
    xmlDataExporter.exportData(new SyncData(emptyGlucoseReads, injections,
            foods, exerciseSessions, new DeviceInfo("serialNumber", "hId", "hRv"), Instant.now()));
  }
}
