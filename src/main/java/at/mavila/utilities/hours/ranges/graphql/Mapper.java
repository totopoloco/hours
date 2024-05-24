package at.mavila.utilities.hours.ranges.graphql;

import at.mavila.hours.ranges.model.Hours;
import org.apache.commons.lang3.Validate;

public final class Mapper {
  private Mapper() {
    throw new IllegalStateException("Forbidden to create an instance of Mapper.");
  }

  /**
   * Maps the OpenAPI Hours object to the GraphQL WorkDay object.
   *
   * @param hours The OpenAPI Hours object.
   * @return The GraphQL WorkDay object.
   * @throws NullPointerException If the hours object is null.
   */
  public static WorkDay fromOpenApiToGraphQL(final Hours hours) {

    final Hours validatedHours = Validate.notNull(hours, "The hours object must not be null.");

    return WorkDay.builder()
        .totalHoursInHHMM(validatedHours.getTotalHoursInHHMM())
        .totalHours(validatedHours.getTotalHours())
        .expectedLunchTimeInHHMM(validatedHours.getExpectedLunchTimeInHHMM())
        .periods(validatedHours.getRangeDetails().stream()
            .map(range -> WorkPeriod.builder()
                .start(range.getRange().getStart().toLocalTime().toString())
                .end(range.getRange().getEnd().toLocalTime().toString())
                .duration(range.getDuration())
                .durationInHours(range.getDurationInHours())
                .build())
            .toList())
        .build();
  }

}
