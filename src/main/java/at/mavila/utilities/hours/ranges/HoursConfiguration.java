package at.mavila.utilities.hours.ranges;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration for the hours.
 * Defaults to Austrian working hours.
 * Override to your needs.
 */
@Component
@Getter
@Setter
public class HoursConfiguration {
  @Value("${HOURS_MINUTES_PER_DAY_OF_WORK:462}")
  private int minutesPerDayOfWork;
  @Value("${HOURS_MAXIMUM_MINUTES_IN_A_ROW:240}")
  private int maximumMinutesInARow;
  @Value("${HOURS_MINUTES_TO_REST_BETWEEN_ROWS:30}")
  private int minutesToRestBetweenRows;
  @Value("${CRON_FOR_TICKING:*/10 * * * * *}")
  private String cronForTicking;
}
