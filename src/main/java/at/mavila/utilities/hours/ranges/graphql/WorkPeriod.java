package at.mavila.utilities.hours.ranges.graphql;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record WorkPeriod(
    String start,
    String end,
    String duration,
    BigDecimal durationInHours) {
}
