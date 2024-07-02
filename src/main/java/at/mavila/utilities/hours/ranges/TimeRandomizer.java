package at.mavila.utilities.hours.ranges;

import static java.util.List.of;

import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Randomizes the time for the entry, lunch break start and the minute in the hour.
 *
 * @author mavila
 */
@Component
@Slf4j
public class TimeRandomizer {

  private static final List<Integer> QUARTERS;
  private static final ThreadLocal<Random> RANDOM;

  static {
    QUARTERS = of(0, 15, 30, 45);
    RANDOM = ThreadLocal.withInitial(Random::new);
  }

  /**
   * Randomizes the entry time between 8:00 and 10:00
   *
   * @return the randomized entry time, the hour is between 8 and 9. 8, or 9.
   */
  public int randomizeEntry() {
    return RANDOM.get().nextInt(2) + 8;
  }

  /**
   * Randomizes between 0 and 59
   *
   * @return the randomized minute in the hour, between 0 and 59
   */
  public int randomizeMinuteInHour() {
    return QUARTERS.get(RANDOM.get().nextInt(QUARTERS.size()));
  }

  /**
   * Randomizes the lunch break start time between 12:00 and 14:00
   *
   * @return the randomized lunch break start time, the hour is between 12 and 14, 12, 13 or 14
   */
  public int randomizeLunchBreakStart() {
    return RANDOM.get().nextInt(3) + 12;
  }

  @PreDestroy
  public void destroy() {
    RANDOM.remove();
    log.info("Randomizer destroyed.");
  }

}
