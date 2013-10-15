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

import com.fasterxml.jackson.xml.annotate.JacksonXmlProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Manufacturing data structure.
 *
 * @author alexandre.normand
 */
public class ManufacturingParameters {
  @JacksonXmlProperty(isAttribute=true, localName = "SerialNumber")
  private String serialNumber;
  @JacksonXmlProperty(isAttribute=true, localName = "HardwarePartNumber")
  private String hardwarePartNumber;
  @JacksonXmlProperty(isAttribute=true, localName = "HardwareRevision")
  private String hardwareRevision;
  @JacksonXmlProperty(isAttribute=true, localName = "DateTimeCreated")
  private String dateTimeCreated;
  @JacksonXmlProperty(isAttribute=true, localName = "HardwareId")
  private String hardwareId;

  public String getSerialNumber() {
    return serialNumber;
  }

  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  @JacksonXmlProperty(isAttribute=true)
  public String getHardwarePartNumber() {
    return hardwarePartNumber;
  }

  @JacksonXmlProperty(isAttribute=true)
  public void setHardwarePartNumber(String hardwarePartNumber) {
    this.hardwarePartNumber = hardwarePartNumber;
  }

  public String getHardwareRevision() {
    return hardwareRevision;
  }

  public void setHardwareRevision(String hardwareRevision) {
    this.hardwareRevision = hardwareRevision;
  }

  public String getDateTimeCreated() {
    return dateTimeCreated;
  }

  public void setDateTimeCreated(String dateTimeCreated) {
    this.dateTimeCreated = dateTimeCreated;
  }

  public String getHardwareId() {
    return hardwareId;
  }

  public void setHardwareId(String hardwareId) {
    this.hardwareId = hardwareId;
  }
}
