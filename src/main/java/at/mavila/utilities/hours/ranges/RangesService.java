package at.mavila.utilities.hours.ranges;

import at.mavila.hours.ranges.model.Hours;
import at.mavila.hours.ranges.model.HoursRangeDetailsInner;
import at.mavila.hours.ranges.model.HoursRangeDetailsInnerRange;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RangesService {

  private final RangesCalculator rangesCalculator;
  private final TimeUtilitiesService timeUtilitiesService;
  private final HoursConfiguration hoursConfiguration;

  public List<HoursRangeDetailsInnerRange> calculateRanges(final Integer minutesOfLunchBreak,
                                                           final int entry,
                                                           final int entryMinute,
                                                           final LocalDateTime lunchBreakStart) {
    return this.rangesCalculator.rangeCalculator(
            LocalDateTime.of(LocalDate.now(), LocalTime.of(entry, entryMinute)),
            lunchBreakStart,
            this.hoursConfiguration.getMinutesPerDayOfWork(),
            this.timeUtilitiesService.getMinutesOfLunchBreakParameter(minutesOfLunchBreak),
            this.hoursConfiguration.getMaximumMinutesInARow(),
            this.hoursConfiguration.getMinutesToRestBetweenRows());
  }

  public Hours buildRoot(List<HoursRangeDetailsInnerRange> ranges, long totalMinutes, long hours, long minutes,
                         LocalTime lunchBreakStart) {
    final Hours hoursResponse = new Hours();
    hoursResponse.setRangeDetails(buildDetails(ranges));
    hoursResponse.setTotalHours(TimeUtilities.convertFromMinutesToHours(totalMinutes));
    hoursResponse.setTotalHoursInHHMM(String.format("%02d:%02d", hours, minutes));
    hoursResponse.setExpectedLunchTimeInHHMM(lunchBreakStart.toString());
    hoursResponse.setExtraComments(Optional.of("OK"));
    return hoursResponse;
  }

  private List<HoursRangeDetailsInner> buildDetails(List<HoursRangeDetailsInnerRange> ranges) {
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
}
