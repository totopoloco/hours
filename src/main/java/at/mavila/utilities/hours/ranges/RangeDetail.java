package at.mavila.utilities.hours.ranges;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.With;
import org.apache.commons.lang3.builder.ToStringBuilder;

@With
@Builder
@JsonPropertyOrder({"range", "duration", "durationInHours"})
public record RangeDetail(
    Range range,
    CharSequence duration,
    CharSequence durationInHours) {

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("range", this.range)
        .append("duration", this.duration)
        .append("durationInHours", this.durationInHours)
        .toString();
  }
}
