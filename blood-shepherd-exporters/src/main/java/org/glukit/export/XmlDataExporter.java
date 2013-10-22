package org.glukit.export;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
import org.glukit.sync.api.DataExporter;
import org.glukit.sync.api.GlucoseRead;
import org.glukit.sync.api.SyncData;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.Collections.sort;

/**
 * Exports the data as a XML file ressembling the Dexcom Studio files.
 *
 * @author alexandre.normand
 */
public class XmlDataExporter implements DataExporter {

  private static DateTimeFormatter dateTimeFormatter;

  private PrintStream printStream;

  static {
    DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
    builder.parseCaseInsensitive();
    builder.appendPattern("yyyy-mm-dd HH:mm:ss");


    dateTimeFormatter = builder.toFormatter().withZone(ZoneId.of("UTC"));
  }

  private Function<GlucoseRead, TimestampedValue> GLUCOSE_READ_TO_TIMESTAMPED_VALUE =
          new Function<GlucoseRead, TimestampedValue>() {
            @Override
            public TimestampedValue apply(@javax.annotation.Nullable GlucoseRead glucoseRead) {
              checkNotNull(glucoseRead, "glucoseRead should be non-null");

              return new TimestampedValue(dateTimeFormatter.format(glucoseRead.getInternalTime()),
                      dateTimeFormatter.format(glucoseRead.getLocalTime()), format("%.0f", glucoseRead.getValue()));
            }
          };

  public XmlDataExporter() {

  }

  @VisibleForTesting
  void setPrintStream(PrintStream printStream) {
    this.printStream = printStream;
  }

  @Override
  public void exportData(SyncData syncData) {
    XmlMapper xmlMapper = new XmlMapper();
    xmlMapper.writerWithDefaultPrettyPrinter();
    PatientData patientData = new PatientData();
    patientData.SerialNumber = syncData.getDeviceInfo().getSerialNumber();
    sort(syncData.getGlucoseReads());
    patientData.GlucoseReading = newArrayList(Collections2.transform(syncData.getGlucoseReads(),
            GLUCOSE_READ_TO_TIMESTAMPED_VALUE));

    try {
      PrintStream outputStream = getOutputStream(syncData);
      xmlMapper.writeValue(outputStream, patientData);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  private PrintStream getOutputStream(SyncData syncData) throws FileNotFoundException {
    if (this.printStream == null) {

//      String filename = format("export-%s", syncData.getUpdateTime());
//      File outputFile = new File(".", filename);
//      return new PrintStream(outputFile);
      return System.out;
    } else {
      return this.printStream;
    }
  }

  public static final class PatientData {
    @JacksonXmlProperty(isAttribute=true)
    public String Id;
    @JacksonXmlProperty(isAttribute=true)
    public String FirstName;
    @JacksonXmlProperty(isAttribute=true)
    public String LastName;
    @JacksonXmlProperty(isAttribute=true)
    public String MiddleName;
    @JacksonXmlProperty(isAttribute=true)
    public String SerialNumber;
    @JacksonXmlProperty(isAttribute=true)
    public String Initials;
    @JacksonXmlProperty(isAttribute=true)
    public String PreferredName;
    @JacksonXmlProperty(isAttribute=true)
    public String PatientNumber;
    @JacksonXmlProperty(isAttribute=true)
    public String PatientIdentifier;
    @JacksonXmlProperty(isAttribute=true)
    public String OtherIdentifier;
    @JacksonXmlProperty(isAttribute=true)
    public String Gender;
    @JacksonXmlProperty(isAttribute=true)
    public String DateOfBirth;
    @JacksonXmlProperty(isAttribute=true)
    public String DoctorsName;
    @JacksonXmlProperty(isAttribute=true)
    public String Email;
    @JacksonXmlProperty(isAttribute=true)
    public String PhoneNumber;
    @JacksonXmlProperty(isAttribute=true)
    public String PhoneExtension;
    @JacksonXmlProperty(isAttribute=true)
    public String SiteIdentifier;
    @JacksonXmlProperty(isAttribute=true)
    public String StudyIdentifier;
    @JacksonXmlProperty(isAttribute=true)
    public String Comments;
    @JacksonXmlProperty(isAttribute=true)
    public String IsDataBlinded;
    @JacksonXmlProperty(isAttribute=true)
    public String IsKeepPrivate;

    @JacksonXmlElementWrapper(localName = "MeterReadings")
    public List<TimestampedValue> MeterReading;
    @JacksonXmlElementWrapper(localName = "GlucoseReadings")
    public List<TimestampedValue> GlucoseReading;
    @JacksonXmlElementWrapper(localName = "EventMarkers")
    public List<EventMarker> EventMarker;

    public PatientData() {
    }

    public PatientData(String id, String firstName, String lastName, String middleName, String serialNumber,
                       String initials, String preferredName, String patientNumber, String patientIdentifier,
                       String otherIdentifier, String gender, String dateOfBirth, String doctorsName, String email,
                       String phoneNumber, String phoneExtension, String siteIdentifier, String studyIdentifier,
                       String comments, String isDataBlinded, String isKeepPrivate,
                       List<TimestampedValue> meterReadings, List<TimestampedValue> glucoseReading,
                       List<EventMarker> eventMarker) {
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
      GlucoseReading = glucoseReading;
      EventMarker = eventMarker;
    }
  }

  public static class TimestampedValue {
    @JacksonXmlProperty(isAttribute=true)
    public String InternalTime;
    @JacksonXmlProperty(isAttribute=true)
    public String DisplayTime;
    @JacksonXmlProperty(isAttribute=true)
    public String Value;

    public TimestampedValue() {
    }

    public TimestampedValue(String internalTime, String displayTime, String value) {
      InternalTime = internalTime;
      DisplayTime = displayTime;
      Value = value;
    }
  }

  public static class EventMarker extends TimestampedValue {
    @JacksonXmlProperty(isAttribute=true)
    public String EventTime;
    @JacksonXmlProperty(isAttribute=true)
    public String EventType;
    @JacksonXmlProperty(isAttribute=true)
    public String Description;

    public EventMarker(String internalTime, String displayTime, String value) {
      super(internalTime, displayTime, value);
    }

    public EventMarker(String internalTime,
                       String displayTime,
                       String value,
                       String eventTime,
                       String eventType,
                       String description) {
      super(internalTime, displayTime, value);
      EventTime = eventTime;
      EventType = eventType;
      Description = description;
    }
  }
}
