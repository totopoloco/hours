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
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class WorkDayController {

  private final RangesService rangesService;
  private final TimeRandomizer timeRandomizer;
  private final TimeUtilitiesService timeUtilitiesService;

  public WorkDay workDay(final Integer start,
                         final Integer lunchStart,
                         final Integer lunchDuration) {
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
}