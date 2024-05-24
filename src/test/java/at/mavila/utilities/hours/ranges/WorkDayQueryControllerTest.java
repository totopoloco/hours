package at.mavila.utilities.hours.ranges;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

@SpringBootTest
@AutoConfigureGraphQlTester
class WorkDayQueryControllerTest {

  @Autowired
  private GraphQlTester graphQlTester;

  @Autowired
  private RangesService rangesService;

  @Autowired
  private TimeRandomizer timeRandomizer;

  @Autowired
  private TimeUtilitiesService timeUtilitiesService;


  @Test
  void shouldHaveTotalHoursNotNull() {
    GraphQlTester.Response defaultWorkDay = this.graphQlTester.documentName("WorkDay")
        .variable("start", 9)
        .variable("lunchStart", 12)
        .variable("lunchDuration", 30)
        .execute();
    GraphQlTester.Path path = defaultWorkDay.path("workDay.totalHours");
    assertThat(path).isNotNull();
    assertThat(path.hasValue()).isNotNull();
    assertThat(path.entity(BigDecimal.class).get()).isNotNull()
        .isCloseTo(BigDecimal.valueOf(7.7D), Percentage.withPercentage(0.1D));
  }
}
