package at.mavila.utilities.hours.ranges;

import static java.util.Objects.nonNull;

import at.mavila.hours.ranges.model.HoursRangeDetailsInnerRange;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TimeUtilitiesService {

  public Integer getMinutesOfLunchBreakParameter(Integer minutesOfLunchBreak) {
    return nonNull(minutesOfLunchBreak) ? minutesOfLunchBreak : 30;
  }

  public long getTotalMinutes(List<HoursRangeDetailsInnerRange> ranges) {
    return ranges.stream().mapToLong(range -> ChronoUnit.MINUTES.between(range.getStart(), range.getEnd())).sum();
  }
}