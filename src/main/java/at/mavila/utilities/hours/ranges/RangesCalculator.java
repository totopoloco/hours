package at.mavila.utilities.hours.ranges;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RangesCalculator {

  public List<Range> rangeCalculator(
      final LocalTime entry,
      final LocalTime lunchBreakStart,
      final long minutesPerDayOfWork,
      final long minutesOfLunchBreak,
      final long maximumMinutesInARow,
      final long minutesOfBreakBetweenRanges) {

    List<Range> ranges = new ArrayList<>();

    long minutesLeft = minutesPerDayOfWork;
    LocalTime start = entry;
    LocalTime endOfLunchBreak = lunchBreakStart.plusMinutes(minutesOfLunchBreak);
    while (minutesLeft > 0) {

      LocalTime end = start.plusMinutes(Math.min(minutesLeft, maximumMinutesInARow));

      if (end.isAfter(lunchBreakStart) && start.isBefore(endOfLunchBreak)) {
        end = lunchBreakStart;
      }

      long minutesWorked = ChronoUnit.MINUTES.between(start, end);
      minutesLeft -= minutesWorked;

      BigDecimal totalHours = BigDecimal.valueOf(minutesWorked).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
      CharSequence hoursMinutesFormatted =
          String.format("%02d:%02d", totalHours.intValue(),
              totalHours.remainder(BigDecimal.ONE).multiply(BigDecimal.valueOf(60)).intValue());
      ranges.add(new Range(start, end, totalHours, hoursMinutesFormatted));

      start = end.plusMinutes(minutesOfBreakBetweenRanges);
      if (start.isBefore(endOfLunchBreak)) {
        start = endOfLunchBreak;
      }
    }

    return ranges;

  }
}