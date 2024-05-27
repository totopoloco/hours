package at.mavila.utilities.hours.ranges;

import at.mavila.hours.ranges.api.RangesApi;
import at.mavila.hours.ranges.api.RangesWithApi;
import at.mavila.hours.ranges.model.Hours;
import at.mavila.hours.ranges.model.HoursRangeDetailsInnerRange;
import jakarta.annotation.PreDestroy;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class RangesController implements RangesApi, RangesWithApi {

  private static final ThreadLocal<DecimalFormat> DECIMAL_FORMAT;
  private final RangesService rangesService;
  private final TimeRandomizer timeRandomizer;
  private final ValidatorService validatorService;
  private final TimeUtilitiesService timeUtilitiesService;

  static {
    DECIMAL_FORMAT = ThreadLocal.withInitial(() -> new DecimalFormat("0.00"));
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
    final List<HoursRangeDetailsInnerRange> ranges =
            this.rangesService.calculateRanges(breakInMinutes, entry, entryMinute, LocalDateTime.of(LocalDate.now(), lunchBreakStart));

    //Calculate total minutes
    final long totalMinutes = this.timeUtilitiesService.getTotalMinutes(ranges);
    final long hours = totalMinutes / 60;   // since both are ints, you get an int
    final long minutes = totalMinutes % 60;

    final Hours hoursResponse = this.rangesService.buildRoot(ranges, totalMinutes, hours, minutes, lunchBreakStart);

    CharSequence errors = this.validatorService.validateAgainstSchema(hoursResponse);
    return StringUtils.isEmpty(errors)//Double check if the response
            // is valid given the schema
            ? ResponseEntity.ok(hoursResponse)
        : getErrorsHoursResponseEntity(hoursResponse, errors);
  }


  private static ResponseEntity<Hours> getErrorsHoursResponseEntity(final Hours hoursResponse, final CharSequence errors) {
    hoursResponse.setExtraComments(Optional.of(errors.toString()));
    return ResponseEntity.internalServerError().body(hoursResponse);
  }

  @PreDestroy
  public void destroy() {
    DECIMAL_FORMAT.remove();
  }
}