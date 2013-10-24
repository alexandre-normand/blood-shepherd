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

package org.glukit.app;

import com.beust.jcommander.IParameterValidator2;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterDescription;
import com.beust.jcommander.ParameterException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.glukit.dexcom.sync.DexcomDaemon;
import org.glukit.sync.api.BloodShepherdProperties;

import javax.usb.UsbException;
import java.io.File;

import static java.lang.String.format;

/**
 * Main class for the bloodsucker sync dexcomDaemon.
 *
 * @author alexandre.normand
 */
public class BloodShepherdTray {
    @Parameter(names = "-outputPath", required = true,
        description = "the output path of the exported files (make it something under your google drive local sync directory",
        validateWith = ExistingDirectoryValidator.class)
    String outputPath;

    public BloodShepherdTray() {

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

        Display display = new Display ();
        final Shell shell = new Shell (display);
        Image icon = new Image(display, getClass().getClassLoader().getResourceAsStream("org/glukit/app/images/droplet-16.png"));
        Image image2 = new Image (display, getClass().getClassLoader().getResourceAsStream("org/glukit/app/images/droplet-16.png"));
        GC gc = new GC(image2);
        gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
        gc.fillRectangle(image2.getBounds());
        gc.dispose();
        final Tray tray = display.getSystemTray ();
        if (tray == null) {
            System.err.println ("The system tray is not available");
        } else {
            final TrayItem item = new TrayItem (tray, SWT.NONE);
            item.setToolTipText("SWT TrayItem");
            item.addListener (SWT.Show, new Listener () {
                public void handleEvent (Event event) {
                    System.out.println("show");
                }
            });
            item.addListener (SWT.Hide, new Listener () {
                public void handleEvent (Event event) {
                    System.out.println("hide");
                }
            });
            item.addListener (SWT.Selection, new Listener () {
                public void handleEvent (Event event) {
                    System.out.println("selection");
                }
            });
            item.addListener (SWT.DefaultSelection, new Listener () {
                public void handleEvent (Event event) {
                    System.out.println("default selection");
                }
            });

            final Menu menu = new Menu (shell, SWT.POP_UP);

            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Quit");
            mi.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    shell.dispose();
                }
            });
            menu.setDefaultItem(mi);

            item.addListener (SWT.MenuDetect, new Listener () {
                public void handleEvent (Event event) {
                    menu.setVisible (true);
                }
            });
            item.setImage (image2);
            item.setHighlightImage (icon);
        }
        shell.setBounds(50, 50, 300, 200);
        shell.open ();
        shell.setVisible(false);

        while (!shell.isDisposed ()) {
            if (!display.readAndDispatch ()) display.sleep ();
        }
        icon.dispose();
        image2.dispose ();
        display.dispose ();
    }

    public static void main(String[] args) throws UsbException {
        BloodShepherdTray dexcomReceiverSyncService = new BloodShepherdTray();
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
