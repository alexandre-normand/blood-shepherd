package org.glukit.dexcom.sync;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import jssc.SerialPortException;
import org.slf4j.LoggerFactory;

import javax.usb.UsbException;

import static java.lang.String.format;

/**
 * Testing writing to the serial port.
 * @author alexandre.normand
 */
public class SerialTest {
  public static final String DEVICE = "/dev/tty.usbmodem5d11";
  private static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SerialTest.class);


  public void run() throws SerialPortException {
    IsReceiverOnThisPortRunner command = new IsReceiverOnThisPortRunner(DEVICE);
    boolean isReceiver = command.isReceiver();
    LOGGER.info(format("Is this the receiver on port %s: %b", DEVICE, isReceiver));
  }

  public static void main(String[] args) throws UsbException, SerialPortException {
    SerialTest serialTest = new SerialTest();

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
