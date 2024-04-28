package at.mavila.utilities.hours.ranges;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class RangesController {

  private final RangesCalculator rangesCalculator;
  private final TimeRandomizer timeRandomizer;

  @GetMapping("/ranges")
  public ResponseEntity<String> getRanges() {
    int entry = this.timeRandomizer.randomizeEntry();
    int entryMinute = this.timeRandomizer.randomizeMinuteInHour();
    int lunch = this.timeRandomizer.randomizeLunchBreakStart();
    int lunchMinute = this.timeRandomizer.randomizeMinuteInHour();

    List<Range> ranges = this.rangesCalculator.rangeCalculator(
        LocalTime.of(entry, entryMinute),
        LocalTime.of(lunch, lunchMinute),
        462,
        30,
        240,
        30
    );

    long totalMinutes = ranges.stream().mapToLong(range -> ChronoUnit.MINUTES.between(range.getStart(), range.getEnd())).sum();
    long hours = totalMinutes / 60;   // since both are ints, you get an int
    long minutes = totalMinutes % 60;

    final StringBuilder stringBuilder = new StringBuilder();

    ranges.forEach(range -> {
      LocalTime start = range.getStart();
      LocalTime end = range.getEnd();
      stringBuilder.append(start.toString());
      stringBuilder.append("-");
      stringBuilder.append(end.toString());
      stringBuilder.append(" -> ");
      stringBuilder.append(TimeUtilities.getDurationFormatted(start, end));
      stringBuilder.append(" (");
      stringBuilder.append(TimeUtilities.convertFromMinutesToHours(TimeUtilities.getDurationInMinutes(start, end)));
      stringBuilder.append(")");
      stringBuilder.append("\n");
    });


    return ResponseEntity.ok(
        stringBuilder + " Total hours: " + TimeUtilities.convertFromMinutesToHours(totalMinutes) + "\n Total hours (hh:mm): " +
        hours + ":" + minutes);

  }

}
