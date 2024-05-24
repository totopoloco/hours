package at.mavila.utilities.hours.ranges.graphql;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record WorkDay(List<WorkPeriod> periods,
                      BigDecimal totalHours,
                      String totalHoursInHHMM,
                      String expectedLunchTimeInHHMM) {
}
