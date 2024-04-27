package at.mavila.utilities.hours.ranges;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RangesCalculatorTest {

  private final RangesCalculator rangesCalculator;

  @Autowired
  RangesCalculatorTest(final RangesCalculator rangesCalculator) {
    this.rangesCalculator = rangesCalculator;
  }

  /**
   * Test range calculator.
   * When the Distribution is EVENLY, the ranges should be calculated evenly.
   * Given:
   * - The user want to book working hours in 2 ranges.
   * - 30 minutes of lunch break.
   * - 462 minutes of work.
   * - 9:00 as the start of the workday.
   * - 12:00 as the start of the lunch break.
   * - A person must not work more than 4 hours in a row.
   */
  @Test
  void rangeCalculatorEvenly() {
    List<Range> ranges =
        this.rangesCalculator.rangeCalculator(
            LocalTime.of(9, 0),
            LocalTime.of(12, 51),
            462,
            60,
            240,
            30);

    assertThat(ranges).hasSize(2);
    assertThat(ranges.get(0).getStart()).isEqualTo(LocalTime.of(9, 0));
    assertThat(ranges.get(0).getEnd()).isEqualTo(LocalTime.of(12, 51));
    assertThat(ranges.get(1).getStart()).isEqualTo(LocalTime.of(13, 51));
    assertThat(ranges.get(1).getEnd()).isEqualTo(LocalTime.of(17, 42));

    //Assert that the total time worked is 462 minutes
    long totalMinutes = ranges.stream().mapToLong(range -> ChronoUnit.MINUTES.between(range.getStart(), range.getEnd())).sum();
    assertThat(totalMinutes).isEqualTo(462);

  }

  @Test
  void rangeCalculatorEvenly2() {
    List<Range> ranges =
        this.rangesCalculator.rangeCalculator(
            LocalTime.of(9, 0),
            LocalTime.of(13, 0),
            462,
            30,
            240,
            30);

    assertThat(ranges).hasSize(2);
    assertThat(ranges.get(0).getStart()).isEqualTo(LocalTime.of(9, 0));
    assertThat(ranges.get(0).getEnd()).isEqualTo(LocalTime.of(13, 0));
    assertThat(ranges.get(1).getStart()).isEqualTo(LocalTime.of(13, 30));
    assertThat(ranges.get(1).getEnd()).isEqualTo(LocalTime.of(17, 12));

    //Assert that the total time worked is 462 minutes
    long totalMinutes = ranges.stream().mapToLong(range -> ChronoUnit.MINUTES.between(range.getStart(), range.getEnd())).sum();
    assertThat(totalMinutes).isEqualTo(462);

  }

  @Test
  void rangeCalculatorEvenly3() {
    List<Range> ranges =
        this.rangesCalculator.rangeCalculator(
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            462,
            60,
            240,
            30);

    assertThat(ranges).hasSize(3);
    assertThat(ranges.get(0).getStart()).isEqualTo(LocalTime.of(9, 0));
    assertThat(ranges.get(0).getEnd()).isEqualTo(LocalTime.of(10, 0));
    assertThat(ranges.get(1).getStart()).isEqualTo(LocalTime.of(11, 0));
    assertThat(ranges.get(1).getEnd()).isEqualTo(LocalTime.of(15, 0));
    assertThat(ranges.get(2).getStart()).isEqualTo(LocalTime.of(15, 30));
    assertThat(ranges.get(2).getEnd()).isEqualTo(LocalTime.of(18, 12));

    //Assert that the total time worked is 462 minutes
    long totalMinutes = ranges.stream().mapToLong(range -> ChronoUnit.MINUTES.between(range.getStart(), range.getEnd())).sum();
    assertThat(totalMinutes).isEqualTo(462);

  }
}