package org.glukit.dexcom.sync;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import jssc.SerialPortException;
import org.glukit.dexcom.sync.requests.ReadFirmwareHeader;
import org.slf4j.LoggerFactory;

import javax.usb.UsbException;

import static java.lang.String.format;

/**
 * Testing writing to the serial port.
 * @author alexandre.normand
 */
public class SerialTest {
  public static final String DEVICE = "/dev/tty.usbmodem3a21";
  private static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SerialTest.class);

  private IsReceiverOnThisPortRunner isReceiverOnThisPortRunner;

  @Inject
  public SerialTest(IsReceiverOnThisPortRunner receiverOnThisPortRunner) {
    this.isReceiverOnThisPortRunner = receiverOnThisPortRunner;
  }

  public void run() throws SerialPortException {
    boolean isReceiver = this.isReceiverOnThisPortRunner.isReceiver(DEVICE);
    LOGGER.info(format("Is this the receiver on port %s: %b", DEVICE, isReceiver));
  }

  public static void main(String[] args) throws UsbException, SerialPortException {
    Injector injector = Guice.createInjector(new DexcomModule());
    SerialTest serialTest = injector.getInstance(SerialTest.class);

    JCommander jCommander = new JCommander(serialTest, args);
    try {
      jCommander.parse(args);
    } catch (ParameterException e) {
      jCommander.usage();
      System.exit(1);
    }

    serialTest.run();
  }

}
