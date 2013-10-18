package org.glukit.dexcom.sync.responses;

import com.google.common.primitives.UnsignedInts;
import org.glukit.dexcom.sync.DataInputFactory;
import org.glukit.dexcom.sync.model.DatabasePageHeader;
import org.glukit.dexcom.sync.model.UserEventRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.IOException;

import static java.lang.String.format;
import static org.glukit.dexcom.sync.DecodingUtils.toHexString;
import static org.glukit.dexcom.sync.DecodingUtils.validateCrc;

/**
 * Response to a {@link org.glukit.dexcom.sync.requests.ReadDatabasePagesCommand} with
 * {@link org.glukit.dexcom.sync.model.RecordType#UserEventData}.
 *
 * @author alexandre.normand
 */
public class UserEventsDatabasePagesResponse extends GenericRecordDatabasePagesResponse<UserEventRecord> {
  private static Logger LOGGER = LoggerFactory.getLogger(UserEventsDatabasePagesResponse.class);

  public UserEventsDatabasePagesResponse(DataInputFactory dataInputFactory) {
    super(dataInputFactory);
  }

  protected UserEventRecord parseRecord(byte[] recordBytes, DatabasePageHeader header,
                                        long recordNumber) throws IOException {
    DataInput input = this.dataInputFactory.create(new ByteArrayInputStream(recordBytes));
    LOGGER.debug(format("Parsing user event record from bytes [%s]...", toHexString(recordBytes)));

    long systemSeconds = UnsignedInts.toLong(input.readInt());
    long displaySeconds = UnsignedInts.toLong(input.readInt());
    UserEventRecord.UserEventType eventType = UserEventRecord.UserEventType.fromId(input.readByte());
    byte eventSubType = input.readByte();
    long eventTime = UnsignedInts.toLong(input.readInt());
    long eventValue = UnsignedInts.toLong(input.readInt());
    int actualReceiverCrc = input.readUnsignedShort();

    validateCrc(actualReceiverCrc, recordBytes);

    UserEventRecord userEventRecord = new UserEventRecord(systemSeconds, displaySeconds,
        eventType, eventSubType, eventTime, eventValue);
    LOGGER.debug(format("Parsed UserEventRecord: [%s]", userEventRecord));

    return userEventRecord;
  }

  @Override
  protected int getRecordLength() {
    return UserEventRecord.RECORD_LENGTH;
  }
}
