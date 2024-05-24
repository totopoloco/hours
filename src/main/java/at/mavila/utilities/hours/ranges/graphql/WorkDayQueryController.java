package at.mavila.utilities.hours.ranges.graphql;

import at.mavila.hours.ranges.model.HoursRangeDetailsInnerRange;
import at.mavila.utilities.hours.ranges.RangesService;
import at.mavila.utilities.hours.ranges.TimeRandomizer;
import at.mavila.utilities.hours.ranges.TimeUtilitiesService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * Controller for the WorkDay query.
 */
@Controller
@AllArgsConstructor(onConstructor_ = @Autowired)
public class WorkDayQueryController {

  private final RangesService rangesService;
  private final TimeRandomizer timeRandomizer;
  private final TimeUtilitiesService timeUtilitiesService;

  /**
   * WorkDay query.
   *
   * @param start         the start time
   * @param lunchStart    the lunch start time
   * @param lunchDuration the lunch duration
   * @return the work day
   */
  @QueryMapping
  public WorkDay workDay(@Argument final Integer start,
                         @Argument final Integer lunchStart,
                         @Argument final Integer lunchDuration) {
    //Initialize random values
    final int entryMinute = this.timeRandomizer.randomizeMinuteInHour();
    final int lunchMinute = this.timeRandomizer.randomizeMinuteInHour();
    final LocalTime lunchBreakStart = LocalTime.of(lunchStart, lunchMinute);
    final List<HoursRangeDetailsInnerRange> ranges = buildRanges(start, lunchDuration, entryMinute, lunchBreakStart);

    //Calculate total minutes
    final long totalMinutes = this.timeUtilitiesService.getTotalMinutes(ranges);
    final long hours = totalMinutes / 60;   // since both are ints, you get an int
    final long minutes = totalMinutes % 60;

    return Mapper.fromOpenApiToGraphQL(this.rangesService.buildRoot(ranges, totalMinutes, hours, minutes, lunchBreakStart));
  }

  private List<HoursRangeDetailsInnerRange> buildRanges(final Integer start,
                                                        final Integer lunchDuration,
                                                        final int entryMinute,
                                                        final LocalTime lunchBreakStart) {
    return this.rangesService.calculateRanges
        (
            lunchDuration,
            start,
            entryMinute,
            LocalDateTime.of(LocalDate.now(), lunchBreakStart)
        );
  }

  /**
   * Default work day query.
   *
   * @return the work day
   */
  @QueryMapping
  public WorkDay defaultWorkDay() {
    return workDay(
        this.timeRandomizer.randomizeEntry(),
        this.timeRandomizer.randomizeLunchBreakStart(),
        30
    );
  }
}