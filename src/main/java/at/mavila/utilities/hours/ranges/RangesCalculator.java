package at.mavila.utilities.hours.ranges;

import at.mavila.hours.ranges.model.HoursRangeDetailsInnerRange;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RangesCalculator {

  public List<HoursRangeDetailsInnerRange> rangeCalculator(
      final LocalDateTime entry,
      final LocalDateTime lunchBreakStart,
      final long minutesPerDayOfWork,
      final long minutesOfLunchBreak,
      final long maximumMinutesInARow,
      final long minutesOfBreakBetweenRanges) {

    List<HoursRangeDetailsInnerRange> ranges = new ArrayList<>();

    long minutesLeft = minutesPerDayOfWork;
    LocalDateTime start = entry;
    LocalDateTime endOfLunchBreak = lunchBreakStart.plusMinutes(minutesOfLunchBreak);
    LocalDateTime adjustedLunchBreakStart = lunchBreakStart;
    while (minutesLeft > 0) {

      LocalDateTime end = start.plusMinutes(Math.min(minutesLeft, maximumMinutesInARow));
      end = adjustEndIfNecessary(lunchBreakStart, end, start, endOfLunchBreak);

      long minutesWorked = ChronoUnit.MINUTES.between(start, end);
      minutesLeft -= minutesWorked;
      ranges.add(createRange(start, end));

      // Check if the worker has already worked for maximumMinutesInARow and adjust the lunch break start time if necessary
      if (minutesWorked >= maximumMinutesInARow && end.isBefore(adjustedLunchBreakStart)) {
        adjustedLunchBreakStart = end;
        endOfLunchBreak = adjustedLunchBreakStart.plusMinutes(minutesOfLunchBreak);
      }

      start = calculateNewStart(minutesOfBreakBetweenRanges, end, endOfLunchBreak);
    }
    return ranges;
  }

  /**
   * Adjusts the end time if necessary.
   * If the end time is after the lunch break start and the start time is before the end of the lunch break,
   * the end time is adjusted to the lunch break start.
   *
   * @param lunchBreakStart the start time of the lunch break
   * @param end             the end time of the range
   * @param start           the start time of the range
   * @param endOfLunchBreak the end time of the lunch break
   * @return the adjusted end time
   */
  private static LocalDateTime adjustEndIfNecessary(LocalDateTime lunchBreakStart,
                                                    LocalDateTime end,
                                                    LocalDateTime start,
                                                    LocalDateTime endOfLunchBreak) {
    if (end.isAfter(lunchBreakStart) && start.isBefore(endOfLunchBreak)) {
      return lunchBreakStart;
    }
    return end;
  }

  /**
   * Calculates the new start time for the next range.
   *
   * @param minutesOfBreakBetweenRanges the minutes of break between ranges
   * @param end                         the end time of the previous range
   * @param endOfLunchBreak             the end time of the lunch break
   * @return the new start time
   */
  private static LocalDateTime calculateNewStart(long minutesOfBreakBetweenRanges, LocalDateTime end,
                                                 LocalDateTime endOfLunchBreak) {
    LocalDateTime start = end.plusMinutes(minutesOfBreakBetweenRanges);
    if (start.isBefore(endOfLunchBreak)) {
      return endOfLunchBreak;
    }
    return start;
  }

  private static HoursRangeDetailsInnerRange createRange(
      final LocalDateTime start,
      final LocalDateTime end) {

    final HoursRangeDetailsInnerRange range = new HoursRangeDetailsInnerRange();
    range.setStart(start);
    range.setEnd(end);

    return range;

  }

  @PreDestroy
  public void destroy() {
    log.info("RangesCalculator is being destroyed.");
  }

  @PostConstruct
  public void init() {
    log.info("RangesCalculator is being initialized.");
  }


}