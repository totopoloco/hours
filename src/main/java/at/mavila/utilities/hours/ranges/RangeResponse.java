package at.mavila.utilities.hours.ranges;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Builder;
import lombok.With;
import org.apache.commons.lang3.builder.ToStringBuilder;

@With
@Builder
@JsonPropertyOrder({"rangeDetails", "totalHours", "totalHoursInHHMM", "expectedLunchTimeInHHMM"})
public record RangeResponse(
    List<RangeDetail> rangeDetails,
    CharSequence totalHours,
    CharSequence totalHoursInHHMM,
    CharSequence expectedLunchTimeInHHMM
) {

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("ranges", this.rangeDetails)
        .append("totalHours", this.totalHours)
        .append("totalHoursInHHMM", this.totalHoursInHHMM)
        .append("expectedLunchTimeInHHMM", this.expectedLunchTimeInHHMM)
        .toString();
  }
}
