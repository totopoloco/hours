package at.mavila.utilities.hours.ranges;

import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

@With
@Builder
@AllArgsConstructor
@Getter
@ToString
public class Range {
  private final LocalTime start;
  private final LocalTime end;
  private final BigDecimal totalHours;
  private final CharSequence hoursMinutesFormatted;
}
