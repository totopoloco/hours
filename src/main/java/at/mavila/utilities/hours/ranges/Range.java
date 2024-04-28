package at.mavila.utilities.hours.ranges;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

/**
 * Represents a range of time.
 */
@With
@Builder
@AllArgsConstructor
@Getter
@ToString
public class Range {
  /**
   * The start time of the range.
   */
  private final LocalTime start;
  /**
   * The end time of the range.
   */
  private final LocalTime end;
}
