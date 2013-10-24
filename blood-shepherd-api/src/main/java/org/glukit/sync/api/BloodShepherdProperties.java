package org.glukit.sync.api;

import java.util.Properties;

/**
 * A marker class to avoid ambiguity in injecting global blood-shepherd properties
 *
 * @author alexandre.normand
 */
public class BloodShepherdProperties extends Properties {
  public static final String OUTPUT_PATH = "OUTPUT_PATH";
}
