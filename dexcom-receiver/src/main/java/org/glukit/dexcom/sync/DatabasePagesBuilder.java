/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Alexandre Normand
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.glukit.dexcom.sync;

import com.google.common.primitives.UnsignedBytes;
import org.glukit.dexcom.sync.model.DatabaseReadRequestSpec;

import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.glukit.dexcom.sync.model.DatabaseReadRequestSpec.MAX_PAGES_PER_COMMAND;

/**
 * Iterator to go through all pages of a database. This uses the data from a
 * {@link org.glukit.dexcom.sync.responses.PageRangeResponse} and returns an iterator that will
 * go over all {@link DatabaseReadRequestSpec}s required to read all of it.
 *
 * @author alexandre.normand
 */
public class DatabasePagesBuilder implements Iterable<DatabaseReadRequestSpec> {
  private long firstPage;
  private long lastPage;

  public DatabasePagesBuilder(long firstPage, long lastPage) {
    this.firstPage = firstPage;
    this.lastPage = lastPage;
  }

  @Override
  public Iterator<DatabaseReadRequestSpec> iterator() {
    List<DatabaseReadRequestSpec> chunks = buildListOfChunks();
    return chunks.iterator();
  }

  private List<DatabaseReadRequestSpec> buildListOfChunks() {
    List<DatabaseReadRequestSpec> specs = newArrayList();
    for (long chunkStart = this.firstPage; chunkStart <= lastPage; chunkStart+= MAX_PAGES_PER_COMMAND) {
      specs.add(new DatabaseReadRequestSpec(chunkStart,
              UnsignedBytes.min((byte) (lastPage - chunkStart + 1), MAX_PAGES_PER_COMMAND)));
    }
    return specs;
  }
}
