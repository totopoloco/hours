package at.mavila.utilities.hours.ranges;

import static java.util.Objects.nonNull;

import at.mavila.hours.ranges.api.RangesApi;
import at.mavila.hours.ranges.api.RangesWithApi;
import at.mavila.hours.ranges.model.Hours;
import at.mavila.hours.ranges.model.HoursRangeDetailsInner;
import at.mavila.hours.ranges.model.HoursRangeDetailsInnerRange;
import jakarta.annotation.PreDestroy;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class RangesController implements RangesApi, RangesWithApi {

  private static final ThreadLocal<DecimalFormat> DECIMAL_FORMAT = ThreadLocal.withInitial(() -> new DecimalFormat("0.00"));
  private final RangesCalculator rangesCalculator;
  private final TimeRandomizer timeRandomizer;

  private static List<HoursRangeDetailsInner> buildDetails(List<HoursRangeDetailsInnerRange> ranges) {
    return ranges.stream().map(range -> {
      final LocalDateTime start = range.getStart();
      final LocalDateTime end = range.getEnd();
      final String durationInHHMM = TimeUtilities.getDurationFormatted(start, end);
      final BigDecimal duration = TimeUtilities.convertFromMinutesToHours(TimeUtilities.getDurationInMinutes(start, end));
      //------------------------
      final HoursRangeDetailsInner hoursRangeDetailsInner = new HoursRangeDetailsInner();
      hoursRangeDetailsInner.setRange(range);
      hoursRangeDetailsInner.setDurationInHours(duration);
      hoursRangeDetailsInner.setDuration(durationInHHMM);
      return hoursRangeDetailsInner;
    }).toList();
  }

  private static Integer getMinutesOfLunchBreakParameter(Integer minutesOfLunchBreak) {
    return nonNull(minutesOfLunchBreak) ? minutesOfLunchBreak : 30;
  }

  private List<HoursRangeDetailsInnerRange> calculateRanges(Integer minutesOfLunchBreak, int entry, int entryMinute,
                                                            LocalDateTime lunchBreakStart) {
    return this.rangesCalculator.rangeCalculator(
        LocalDateTime.of(LocalDate.now(), LocalTime.of(entry, entryMinute)),
        lunchBreakStart,
        462,
        getMinutesOfLunchBreakParameter(minutesOfLunchBreak),
        240,
        30
    );
  }

  @PreDestroy
  public void destroy() {
    DECIMAL_FORMAT.remove();
  }

  @Override
  public ResponseEntity<Hours> rangesWithBreakInMinutes(final Integer breakInMinutes) {
    //Initialize random values
    final int entry = this.timeRandomizer.randomizeEntry();
    final int entryMinute = this.timeRandomizer.randomizeMinuteInHour();
    final int lunch = this.timeRandomizer.randomizeLunchBreakStart();
    final int lunchMinute = this.timeRandomizer.randomizeMinuteInHour();
    return getHoursResponseEntity(breakInMinutes, lunch, lunchMinute, entry, entryMinute);
  }

  @Override
  public ResponseEntity<Hours> rangesWithStartLunchPause(final Integer start,
                                                         final Integer lunch,
                                                         final Integer breakInMinutes) {

    //Initialize random values
    final int entryMinute = this.timeRandomizer.randomizeMinuteInHour();
    final int lunchMinute = this.timeRandomizer.randomizeMinuteInHour();

    return getHoursResponseEntity(breakInMinutes, lunch, lunchMinute, start, entryMinute);
  }

  private ResponseEntity<Hours> getHoursResponseEntity(final Integer breakInMinutes,
                                                       final int lunch,
                                                       final int lunchMinute,
                                                       final int entry,
                                                       final int entryMinute) {
    final LocalTime lunchBreakStart = LocalTime.of(lunch, lunchMinute);
    final List<HoursRangeDetailsInnerRange> ranges = calculateRanges(breakInMinutes, entry,
        entryMinute, LocalDateTime.of(LocalDate.now(), lunchBreakStart));

    //Calculate total minutes
    final long totalMinutes =
        ranges.stream().mapToLong(range -> ChronoUnit.MINUTES.between(range.getStart(), range.getEnd())).sum();
    final long hours = totalMinutes / 60;   // since both are ints, you get an int
    final long minutes = totalMinutes % 60;

    final Hours hoursResponse = new Hours();
    hoursResponse.setRangeDetails(buildDetails(ranges));
    hoursResponse.setTotalHours(TimeUtilities.convertFromMinutesToHours(totalMinutes));
    hoursResponse.setTotalHoursInHHMM(String.format("%02d:%02d", hours, minutes));
    hoursResponse.setExpectedLunchTimeInHHMM(lunchBreakStart.toString());

    return ResponseEntity.ok(hoursResponse);
  }
}
