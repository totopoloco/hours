package at.mavila.utilities.hours.ranges;

import at.mavila.hours.ranges.api.RangesApi;
import at.mavila.hours.ranges.api.RangesWithApi;
import at.mavila.hours.ranges.model.Hours;
import at.mavila.hours.ranges.model.HoursRangeDetailsInnerRange;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class RangesController implements RangesApi, RangesWithApi {

  private static final ThreadLocal<DecimalFormat> DECIMAL_FORMAT = ThreadLocal.withInitial(() -> new DecimalFormat("0.00"));
  private static final ThreadLocal<ObjectMapper> OBJECT_MAPPER = ThreadLocal.withInitial(() -> {
    final ObjectMapper objectMapper = new ObjectMapper();
    final JavaTimeModule javaTimeModule = new JavaTimeModule();
    objectMapper.registerModule(javaTimeModule);
    return objectMapper;
  });

  private final RangesService rangesService;
  private final TimeRandomizer timeRandomizer;
  private final JsonSchema jsonSchema;


  @PreDestroy
  public void destroy() {
    DECIMAL_FORMAT.remove();
    OBJECT_MAPPER.remove();
  }

  @Override
  public ResponseEntity<Hours> rangesWithBreakInMinutes(final Integer breakInMinutes) {
    //Initialize random values
    final int entry = this.timeRandomizer.randomizeEntry();
    final int entryMinute = this.timeRandomizer.randomizeMinuteInHour();
    final int lunch = this.timeRandomizer.randomizeLunchBreakStart();
    final int lunchMinute = this.timeRandomizer.randomizeMinuteInHour();
    return getHoursResponseEntity(breakInMinutes, lunch, lunchMinute, entry, entryMinute);
  }

  @Override
  public ResponseEntity<Hours> rangesWithStartLunchPause(final Integer start, final Integer lunch, final Integer breakInMinutes) {

    //Initialize random values
    final int entryMinute = this.timeRandomizer.randomizeMinuteInHour();
    final int lunchMinute = this.timeRandomizer.randomizeMinuteInHour();

    return getHoursResponseEntity(breakInMinutes, lunch, lunchMinute, start, entryMinute);
  }

  private ResponseEntity<Hours> getHoursResponseEntity(final Integer breakInMinutes, final int lunch, final int lunchMinute,
                                                       final int entry, final int entryMinute) {
    final LocalTime lunchBreakStart = LocalTime.of(lunch, lunchMinute);
    final List<HoursRangeDetailsInnerRange> ranges =
            this.rangesService.calculateRanges(breakInMinutes, entry, entryMinute, LocalDateTime.of(LocalDate.now(), lunchBreakStart));

    //Calculate total minutes
    final long totalMinutes =
        ranges.stream().mapToLong(range -> ChronoUnit.MINUTES.between(range.getStart(), range.getEnd())).sum();
    final long hours = totalMinutes / 60;   // since both are ints, you get an int
    final long minutes = totalMinutes % 60;

    final Hours hoursResponse = this.rangesService.buildResponse(ranges, totalMinutes, hours, minutes, lunchBreakStart);

    //Double check if the response is valid given the schema
    log.info("Json: {}", OBJECT_MAPPER.get().valueToTree(hoursResponse));
    Set<ValidationMessage> errors = this.jsonSchema.validate(OBJECT_MAPPER.get().valueToTree(hoursResponse));

    if (CollectionUtils.isNotEmpty(errors)) {
      //If the response is invalid, return an error response
      final String errorsString = errors
          .stream()
          .map(ValidationMessage::getMessage)
          .collect(Collectors.joining("; "));

      hoursResponse.setExtraComments(Optional.of(errorsString));
      return getErrorsHoursResponseEntity(hoursResponse);
    }

    hoursResponse.setExtraComments(Optional.of("OK"));
    return ResponseEntity.ok(hoursResponse);
  }

  private static ResponseEntity<Hours> getErrorsHoursResponseEntity(final Hours hoursResponse) {
    return ResponseEntity.internalServerError().body(hoursResponse);
  }


}
