package at.mavila.utilities.hours.ranges;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Utility class for time calculations.
 */
public final class TimeUtilities {

  private TimeUtilities() {
    throw new IllegalStateException("TimeUtilities is forbidden to be instanced.");
  }

  /**
   * Calculates the duration in minutes between two LocalTime objects.
   *
   * @param start the start time
   * @param end   the end time
   * @return the duration in minutes
   */
  public static long getDurationInMinutes(LocalTime start, LocalTime end) {
    //Validate the input for null values
    if (Objects.isNull(start)) {
      throw new IllegalArgumentException("The start time must not be null.");
    }
    if (Objects.isNull(end)) {
      throw new IllegalArgumentException("The end time must not be null.");
    }
    return ChronoUnit.MINUTES.between(start, end);
  }

  /**
   * Formats the duration between two LocalTime objects in the format HH:mm.
   *
   * @param start the start time
   * @param end   the end time
   * @return the formatted duration
   */
  public static String getDurationFormatted(LocalTime start, LocalTime end) {
    long duration = getDurationInMinutes(start, end);
    long hours = duration / 60;
    long minutes = duration % 60;
    return String.format("%02d:%02d", hours, minutes);
  }

  /**
   * Converts minutes to hours.
   *
   * @param minutes the minutes to convert
   * @return the hours
   */
  public static BigDecimal convertFromMinutesToHours(long minutes) {
    return BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
  }
}
