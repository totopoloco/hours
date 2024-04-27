package at.mavila.utilities.hours.ranges;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TimeRandomizerTest {

  @Autowired
  private TimeRandomizer timeRandomizer;

  @Test
  void randomizeEntry() {
    assertThat(this.timeRandomizer.randomizeEntry()).isBetween(8, 9);
  }

  @Test
  void randomizeMinuteInTheHour() {
    assertThat(this.timeRandomizer.randomizeMinuteInHour()).isBetween(0, 59);

  }

  @Test
  void randomizeLunchBreakStart() {
    assertThat(this.timeRandomizer.randomizeLunchBreakStart()).isBetween(12, 14);

  }

}