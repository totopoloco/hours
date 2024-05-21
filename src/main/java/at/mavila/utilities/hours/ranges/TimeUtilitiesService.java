package at.mavila.utilities.hours.ranges;

import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

@Service
public class TimeUtilitiesService {

  public Integer getMinutesOfLunchBreakParameter(Integer minutesOfLunchBreak) {
    return nonNull(minutesOfLunchBreak) ? minutesOfLunchBreak : 30;
  }
}