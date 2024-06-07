package at.mavila.utilities.hours.ranges.graphql;

import at.mavila.utilities.hours.ranges.TimeRandomizer;
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

  private final WorkDayController workDayController;
  private final TimeRandomizer timeRandomizer;

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
    return this.workDayController.workDay(start, lunchStart, lunchDuration);
  }


  /**
   * Default work day query.
   *
   * @return the work day
   */
  @QueryMapping
  public WorkDay defaultWorkDay() {
    return this.workDayController.workDay(
        this.timeRandomizer.randomizeEntry(),
        this.timeRandomizer.randomizeLunchBreakStart(),
        30
    );
  }

}