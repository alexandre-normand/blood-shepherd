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

package org.glukit.sync;

import com.beust.jcommander.*;
import com.beust.jcommander.converters.FileConverter;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.glukit.dexcom.sync.DexcomDaemon;
import org.glukit.export.XmlDataExporter;
import org.glukit.sync.api.BloodShepherdProperties;

import javax.usb.UsbException;
import java.io.File;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

/**
 * Main class for the bloodsucker sync dexcomDaemon.
 *
 * @author alexandre.normand
 */
public class DexcomReceiverSyncService {
  @Parameter(names = "-outputPath", required = true,
          description = "the output path of the exported files (make it something under your google drive local sync directory",
          validateWith = ExistingDirectoryValidator.class)
  String outputPath;

  public DexcomReceiverSyncService() {

  }

  public void run() {
    BloodShepherdProperties properties = new BloodShepherdProperties();
    properties.putAll(System.getProperties());
    properties.put(BloodShepherdProperties.OUTPUT_PATH, this.outputPath);
    Injector injector = Guice.createInjector(new DexcomModule(properties));
    final DexcomDaemon dexcomDaemon = injector.getInstance(DexcomDaemon.class);

    dexcomDaemon.start();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        dexcomDaemon.stop();
      }
    });

    while (true) {
      Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
    }
  }

  public static void main(String[] args) throws UsbException {
    DexcomReceiverSyncService dexcomReceiverSyncService = new DexcomReceiverSyncService();
    JCommander jCommander = new JCommander(dexcomReceiverSyncService);
    try {
      jCommander.parse(args);
    } catch (ParameterException e) {
      System.err.println(e.getMessage());
      jCommander.usage();
      System.exit(1);
    }

    dexcomReceiverSyncService.run();
  }

  public static class ExistingDirectoryValidator implements IParameterValidator2 {
    @Override
    public void validate(String name, String value, ParameterDescription pd) throws ParameterException {
      validate(name, value);
    }

    @Override
    public void validate(String name, String value) throws ParameterException {
      File outputDirectory = new File(value);
      if (!outputDirectory.exists()) {
        throw new ParameterException(format("Invalid destination: %s doesn't exist", value));
      }

      if (!outputDirectory.isDirectory()) {
        throw new ParameterException(format("Invalid destination: %s is not a directory", value));
      }
    }
  }

}
