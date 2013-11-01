package org.glukit.export;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
import org.apache.commons.lang3.StringUtils;
import org.glukit.sync.api.*;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.*;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.Collections.sort;
import static org.glukit.sync.api.BloodShepherdProperties.OUTPUT_PATH;

/**
 * Exports the data as a XML file resembling the Dexcom Studio files.
 *
 * @author alexandre.normand
 */
public class XmlDataExporter implements DataExporter {
  private static DateTimeFormatter dateTimeFormatter;

  private BloodShepherdProperties properties;

  @Inject
  public XmlDataExporter(BloodShepherdProperties properties) {
    this.properties = properties;
  }

  static {
    DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
    builder.parseCaseInsensitive();
    builder.appendPattern("yyyy-MM-dd HH:mm:ss");


    dateTimeFormatter = builder.toFormatter().withZone(ZoneId.of("UTC"));
  }

  private Function<GlucoseRead, Timestamped> GLUCOSE_READ_TO_TIMESTAMPED_VALUE =
          new Function<GlucoseRead, Timestamped>() {
            @Override
            public Timestamped apply(@javax.annotation.Nullable GlucoseRead glucoseRead) {
              checkNotNull(glucoseRead, "glucoseRead should be non-null");

              return new TimestampedValue(dateTimeFormatter.format(glucoseRead.getInternalTime()),
                      dateTimeFormatter.format(glucoseRead.getLocalTime()), format("%.0f", glucoseRead.getValue()));
            }
          };

  private Function<FoodEvent, EventMarker> FOOD_EVENT_TO_EVENT_MARKER =
          new Function<FoodEvent, EventMarker>() {
            @Override
            public EventMarker apply(@Nullable FoodEvent foodEvent) {
              checkNotNull(foodEvent, "foodEvent should be non-null");

              String formattedValue = format("%.2f", foodEvent.getCarbohydrates());
              return new EventMarker(
                      dateTimeFormatter.format(foodEvent.getInternalTime()),
                      dateTimeFormatter.format(foodEvent.getLocalTime()),
                      dateTimeFormatter.format(foodEvent.getEventLocalTime()), "Carbs",
                      format("Carbs %s grams", formattedValue));
            }
          };

  private Function<InsulinInjection, EventMarker> INSULIN_INJECTION_TO_EVENT_MARKER =
          new Function<InsulinInjection, EventMarker>() {
            @Override
            public EventMarker apply(@javax.annotation.Nullable InsulinInjection insulinInjection) {
              checkNotNull(insulinInjection, "insulinInjection should be non-null");

              String formattedValue = format("%.2f", insulinInjection.getUnitValue());
              return new EventMarker(
                      dateTimeFormatter.format(insulinInjection.getInternalTime()),
                      dateTimeFormatter.format(insulinInjection.getLocalTime()),
                      dateTimeFormatter.format(insulinInjection.getEventLocalTime()), "Insulin",
                      format("Insulin %s units", formattedValue));
            }
          };

  private Function<ExerciseSession.Intensity, String> EXERCISE_INTENSITY_TO_EVENT_TYPE =
          new Function<ExerciseSession.Intensity, String>() {
            @Nullable
            @Override
            public String apply(@Nullable ExerciseSession.Intensity intensity) {
              checkNotNull(intensity, "intensity must be non-null");
              return StringUtils.capitalize(StringUtils.lowerCase(intensity.name()));
            }
          };

  private Function<ExerciseSession, EventMarker> EXERCISE_SESSION_TO_EVENT_MARKER =
          new Function<ExerciseSession, EventMarker>() {
            @Override
            public EventMarker apply(@javax.annotation.Nullable ExerciseSession exerciseSession) {
              checkNotNull(exerciseSession, "exerciseSession should be non-null");

              String intensityLabel = EXERCISE_INTENSITY_TO_EVENT_TYPE.apply(exerciseSession.getIntensity());
              return new EventMarker(
                      dateTimeFormatter.format(exerciseSession.getInternalTime()),
                      dateTimeFormatter.format(exerciseSession.getLocalTime()),
                      dateTimeFormatter.format(exerciseSession.getEventLocalTime()),
                      format("Exercise%s", intensityLabel),
                      format("Exercise %s (%d minutes)", intensityLabel,
                              exerciseSession.getDuration().toMinutes()));
            }
          };

  @Override
  public void exportData(SyncData syncData) {
    String outputPath = properties.getProperty(OUTPUT_PATH);
    checkNotNull(outputPath, "Missing %s in properties", OUTPUT_PATH);
    File outputDirectory = new File(outputPath);
    checkState(outputDirectory.exists(), "Invalid destination: %s doesn't exist", outputPath);
    checkState(outputDirectory.isDirectory(), "Invalid destination: %s is not a directory", outputPath);

    XmlMapper xmlMapper = new XmlMapper();
    ObjectWriter objectWriter = xmlMapper.writerWithDefaultPrettyPrinter();

    Patient patient = new Patient();
    patient.SerialNumber = syncData.getDeviceInfo().getSerialNumber();
    sort(syncData.getGlucoseReads());
    patient.Glucose = newArrayList(Collections2.transform(syncData.getGlucoseReads(),
            GLUCOSE_READ_TO_TIMESTAMPED_VALUE));
    List<EventMarker> eventMarkers = newArrayList(
            Collections2.transform(syncData.getExerciseSessions(), EXERCISE_SESSION_TO_EVENT_MARKER));
    eventMarkers.addAll(Collections2.transform(syncData.getFoodEvents(), FOOD_EVENT_TO_EVENT_MARKER));
    eventMarkers.addAll(Collections2.transform(syncData.getInsulinInjections(), INSULIN_INJECTION_TO_EVENT_MARKER));
    sort(eventMarkers);
    patient.Event = eventMarkers;

    try {
      OutputStream outputStream = getOutputStream(outputDirectory, syncData);
      objectWriter.writeValue(outputStream, patient);
      outputStream.close();
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  private OutputStream getOutputStream(File destinationDirectory, SyncData syncData) throws FileNotFoundException {
    String fileName = format("blood-shepherd-export-%s.xml", syncData.getUpdateTime().toEpochMilli());
    return new FileOutputStream(new File(destinationDirectory, fileName), false);
  }

  public static final class Patient {
    @JacksonXmlProperty(isAttribute = true)
    public String Id = "";
    @JacksonXmlProperty(isAttribute = true)
    public String FirstName = "";
    @JacksonXmlProperty(isAttribute = true)
    public String LastName = "";
    @JacksonXmlProperty(isAttribute = true)
    public String MiddleName = "";
    @JacksonXmlProperty(isAttribute = true)
    public String SerialNumber = "";
    @JacksonXmlProperty(isAttribute = true)
    public String Initials = "";
    @JacksonXmlProperty(isAttribute = true)
    public String PreferredName = "";
    @JacksonXmlProperty(isAttribute = true)
    public String PatientNumber = "";
    @JacksonXmlProperty(isAttribute = true)
    public String PatientIdentifier = "";
    @JacksonXmlProperty(isAttribute = true)
    public String OtherIdentifier = "";
    @JacksonXmlProperty(isAttribute = true)
    public String Gender = "";
    @JacksonXmlProperty(isAttribute = true)
    public String DateOfBirth = "";
    @JacksonXmlProperty(isAttribute = true)
    public String DoctorsName = "";
    @JacksonXmlProperty(isAttribute = true)
    public String Email = "";
    @JacksonXmlProperty(isAttribute = true)
    public String PhoneNumber = "";
    @JacksonXmlProperty(isAttribute = true)
    public String PhoneExtension = "";
    @JacksonXmlProperty(isAttribute = true)
    public String SiteIdentifier = "";
    @JacksonXmlProperty(isAttribute = true)
    public String StudyIdentifier = "";
    @JacksonXmlProperty(isAttribute = true)
    public String Comments = "";
    @JacksonXmlProperty(isAttribute = true)
    public String IsDataBlinded = "";
    @JacksonXmlProperty(isAttribute = true)
    public String IsKeepPrivate = "";

    @JacksonXmlElementWrapper(localName = "MeterReadings")
    public List<Timestamped> MeterReading = newArrayList();
    @JacksonXmlElementWrapper(localName = "GlucoseReadings")
    public List<Timestamped> Glucose = newArrayList();
    @JacksonXmlElementWrapper(localName = "EventMarkers")
    public List<EventMarker> Event = newArrayList();

    public Patient() {
    }

    public Patient(String id, String firstName, String lastName, String middleName, String serialNumber,
                   String initials, String preferredName, String patientNumber, String patientIdentifier,
                   String otherIdentifier, String gender, String dateOfBirth, String doctorsName, String email,
                   String phoneNumber, String phoneExtension, String siteIdentifier, String studyIdentifier,
                   String comments, String isDataBlinded, String isKeepPrivate,
                   List<Timestamped> meterReadings, List<Timestamped> glucose,
                   List<EventMarker> event) {
      Id = id;
      FirstName = firstName;
      LastName = lastName;
      MiddleName = middleName;
      SerialNumber = serialNumber;
      Initials = initials;
      PreferredName = preferredName;
      PatientNumber = patientNumber;
      PatientIdentifier = patientIdentifier;
      OtherIdentifier = otherIdentifier;
      Gender = gender;
      DateOfBirth = dateOfBirth;
      DoctorsName = doctorsName;
      Email = email;
      PhoneNumber = phoneNumber;
      PhoneExtension = phoneExtension;
      SiteIdentifier = siteIdentifier;
      StudyIdentifier = studyIdentifier;
      Comments = comments;
      IsDataBlinded = isDataBlinded;
      IsKeepPrivate = isKeepPrivate;
      MeterReading = meterReadings;
      Glucose = glucose;
      Event = event;
    }
  }

  public static class Timestamped implements Comparable<Timestamped> {
    @JacksonXmlProperty(isAttribute = true)
    public String InternalTime = "";
    @JacksonXmlProperty(isAttribute = true)
    public String DisplayTime = "";

    public Timestamped() {
    }

    public Timestamped(String internalTime, String displayTime) {
      InternalTime = internalTime;
      DisplayTime = displayTime;
    }

    @Override
    public int compareTo(Timestamped other) {
      return this.InternalTime.compareTo(other.InternalTime);
    }
  }

  public static class TimestampedValue extends Timestamped {
    @JacksonXmlProperty(isAttribute = true)
    public String Value = "";

    public TimestampedValue() {
    }

    public TimestampedValue(String internalTime,
                            String displayTime,
                            String value) {
      super(internalTime, displayTime);
      this.Value = value;
    }
  }

  public static class EventMarker extends Timestamped {
    @JacksonXmlProperty(isAttribute = true)
    public String EventTime = "";
    @JacksonXmlProperty(isAttribute = true)
    public String EventType = "";
    @JacksonXmlProperty(isAttribute = true)
    public String Decription = "";

    public EventMarker(String internalTime,
                       String displayTime,
                       String eventTime,
                       String eventType,
                       String decription) {
      super(internalTime, displayTime);
      EventTime = eventTime;
      EventType = eventType;
      Decription = decription;
    }
  }
}
