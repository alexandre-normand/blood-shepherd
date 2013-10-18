package org.glukit.dexcom.sync.responses;

import com.google.common.base.Throwables;
import org.glukit.dexcom.sync.DataInputFactory;
import org.glukit.dexcom.sync.model.DatabasePage;
import org.glukit.dexcom.sync.model.DatabasePageHeader;
import org.glukit.dexcom.sync.model.GlucoseReadRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

/**
 * This handles common work to do for all record-based {@link DatabasePagesResponse}s such as
 * {@link org.glukit.dexcom.sync.model.RecordType#EGVData} and
 * {@link org.glukit.dexcom.sync.model.RecordType#UserEventData}.
 *
 * @author alexandre.normand
 */
public abstract class GenericRecordDatabasePagesResponse<T> extends DatabasePagesResponse {
  private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  public GenericRecordDatabasePagesResponse(DataInputFactory dataInputFactory) {
    super(dataInputFactory);
  }

  public List<T> getRecords() {
    List<T> records = newArrayList();
    try {
      for (DatabasePage page : getPages()) {
        DatabasePageHeader header = page.getPageHeader();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(page.getPageData());
        DataInput input = this.dataInputFactory.create(inputStream);

        LOGGER.debug(format("Parsing [%d] records...", header.getNumberOfRecords()));
        for (int i = 0; i < header.getNumberOfRecords(); i++) {
          byte[] recordBytes = new byte[getRecordLength()];
          input.readFully(recordBytes, 0, getRecordLength());

          T record = parseRecord(recordBytes, header, header.getFirstRecordIndex() + i);

          records.add(record);
        }
      }
      return records;
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  protected abstract int getRecordLength();
  protected abstract T parseRecord(byte[] recordBytes, DatabasePageHeader header, long recordNumber) throws IOException;
}
