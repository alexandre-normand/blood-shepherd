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

import org.glukit.dexcom.sync.model.DatabaseReadRequestSpec;
import org.junit.Test;

import java.util.Iterator;

import static org.glukit.dexcom.sync.model.DatabaseReadRequestSpec.MAX_PAGES_PER_COMMAND;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit test of {@link DatabasePagesPager}
 *
 * @author alexandre.normand
 */
public class TestDatabasePagesIterator {

  @Test
  public void firstAndLastPageTheSameShouldReturnSingleElementOf1Page() throws Exception {
    DatabasePagesPager planner = new DatabasePagesPager(147, 147);

    Iterator<DatabaseReadRequestSpec> iterator = planner.iterator();
    assertThat(iterator.hasNext(), is(true));
    assertThat(iterator.next(), equalTo(new DatabaseReadRequestSpec(147, (byte) 1)));

  }

  @Test
  public void firstAndLastPageConsecutiveShouldSingleElementOf2Pages() throws Exception {
    DatabasePagesPager planner = new DatabasePagesPager(146, 147);

    Iterator<DatabaseReadRequestSpec> iterator = planner.iterator();
    assertThat(iterator.hasNext(), is(true));
    assertThat(iterator.next(), equalTo(new DatabaseReadRequestSpec(146, (byte) 2)));
  }

  @Test
  public void twoElementsWithFirstOneOfFourPages() throws Exception {
    DatabasePagesPager planner = new DatabasePagesPager(140, 144);

    Iterator<DatabaseReadRequestSpec> iterator = planner.iterator();
    assertThat(iterator.hasNext(), is(true));
    assertThat(iterator.next(), equalTo(new DatabaseReadRequestSpec(140, MAX_PAGES_PER_COMMAND)));
    assertThat(iterator.hasNext(), is(true));
    assertThat(iterator.next(), equalTo(new DatabaseReadRequestSpec(144, (byte) 1)));
  }
}
