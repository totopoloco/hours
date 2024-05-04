package at.mavila.utilities.hours.ranges;

import static java.util.Objects.nonNull;

import jakarta.annotation.PreDestroy;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class RangesController {

  private final RangesCalculator rangesCalculator;
  private final TimeRandomizer timeRandomizer;
  private static final ThreadLocal<DecimalFormat> DECIMAL_FORMAT = ThreadLocal.withInitial(() -> new DecimalFormat("0.00"));

  /**
   * Get ranges with start lunch and minutes of lunch break.
   *
   * @param start               the start
   * @param lunch               the lunch
   * @param minutesOfLunchBreak the minutes of lunch break
   * @return the ranges with start lunch and minutes of lunch break
   */
  @GetMapping("/rangesWithStartLunchAndMinutesOfLunchBreak/{start}/{lunch}/{minutesOfLunchBreak}")
  public ResponseEntity<RangeResponse> getRangesWithStartLunchAndMinutesOfLunchBreak(
      @PathVariable(name = "start") Integer start,
      @PathVariable(name = "lunch") Integer lunch,
      @PathVariable(name = "minutesOfLunchBreak") Integer minutesOfLunchBreak) {

    return getRangeResponseResponseEntity(
        minutesOfLunchBreak,
        start,
        this.timeRandomizer.randomizeMinuteInHour(),
        LocalTime.of(lunch, this.timeRandomizer.randomizeMinuteInHour())
    );
  }

  /**
   * Get ranges.
   *
   * @param minutesOfLunchBreak the minutes of lunch break
   * @return the ranges
   */
  @GetMapping("/ranges/{minutesOfLunchBreak}")
  public ResponseEntity<RangeResponse> getRanges(@PathVariable(name = "minutesOfLunchBreak") Integer minutesOfLunchBreak) {

    //Initialize random values
    int entry = this.timeRandomizer.randomizeEntry();
    int entryMinute = this.timeRandomizer.randomizeMinuteInHour();
    int lunch = this.timeRandomizer.randomizeLunchBreakStart();
    int lunchMinute = this.timeRandomizer.randomizeMinuteInHour();
    LocalTime lunchBreakStart = LocalTime.of(lunch, lunchMinute);

    return getRangeResponseResponseEntity(minutesOfLunchBreak, entry, entryMinute, lunchBreakStart);
  }

  private ResponseEntity<RangeResponse> getRangeResponseResponseEntity(Integer minutesOfLunchBreak, int entry, int entryMinute,
                                                                       LocalTime lunchBreakStart) {
    //Calculate ranges
    List<Range> ranges = calculateRanges(minutesOfLunchBreak, entry, entryMinute, lunchBreakStart);

    //Calculate total minutes
    long totalMinutes = ranges.stream().mapToLong(range -> ChronoUnit.MINUTES.between(range.getStart(), range.getEnd())).sum();
    long hours = totalMinutes / 60;   // since both are ints, you get an int
    long minutes = totalMinutes % 60;

    //Build response and return
    return ResponseEntity.ok(buildResponse(buildDetails(ranges), lunchBreakStart, totalMinutes, hours, minutes));
  }

  private List<Range> calculateRanges(Integer minutesOfLunchBreak, int entry, int entryMinute, LocalTime lunchBreakStart) {
    return this.rangesCalculator.rangeCalculator(
        LocalTime.of(entry, entryMinute),
        lunchBreakStart,
        462,
        getMinutesOfLunchBreakParameter(minutesOfLunchBreak),
        240,
        30
    );
  }

  private static List<RangeDetail> buildDetails(List<Range> ranges) {
    return ranges.stream().map(range -> {
      LocalTime start = range.getStart();
      LocalTime end = range.getEnd();
      return RangeDetail.builder()
          .range(range)
          .duration(TimeUtilities.getDurationFormatted(start, end))
          .durationInHours(DECIMAL_FORMAT.get()
              .format(TimeUtilities.convertFromMinutesToHours(TimeUtilities.getDurationInMinutes(start, end))))
          .build();
    }).toList();
  }

  private static RangeResponse buildResponse(List<RangeDetail> rangeDetails,
                                             LocalTime lunchBreakStart,
                                             long totalMinutes,
                                             long hours,
                                             long minutes) {
    return RangeResponse.builder()
        .rangeDetails(rangeDetails)
        .expectedLunchTimeInHHMM(lunchBreakStart.toString())
        .totalHours(DECIMAL_FORMAT.get().format(TimeUtilities.convertFromMinutesToHours(totalMinutes)))
        .totalHoursInHHMM(String.format("%02d:%02d", hours, minutes))
        .build();
  }

  private static int getMinutesOfLunchBreakParameter(Integer minutesOfLunchBreak) {
    return nonNull(minutesOfLunchBreak) ? minutesOfLunchBreak : 30;
  }

  @PreDestroy
  public void destroy() {
    DECIMAL_FORMAT.remove();
  }

}
