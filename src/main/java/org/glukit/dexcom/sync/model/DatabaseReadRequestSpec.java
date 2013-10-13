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

package org.glukit.dexcom.sync.model;

import com.google.common.base.Objects;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Spec of a single command for database pages ({@link org.glukit.dexcom.sync.requests.ReadDatabasePagesCommand}
 * @author alexandre.normand
 */
@EqualsAndHashCode
@ToString
public class DatabaseReadRequestSpec {
  public static final byte MAX_PAGES_PER_COMMAND = 4;
  private long startPage;
  private byte numberOfPages;

  public DatabaseReadRequestSpec(long startPage, byte numberOfPages) {
    checkArgument(numberOfPages > 0 && numberOfPages <= MAX_PAGES_PER_COMMAND, "Command is limited to [%s] pages or " +
                "less, given invalid value of [%s]", MAX_PAGES_PER_COMMAND, numberOfPages);
    this.startPage = startPage;
    this.numberOfPages = numberOfPages;
  }

  public long getStartPage() {
    return startPage;
  }

  public byte getNumberOfPages() {
    return numberOfPages;
  }
}
