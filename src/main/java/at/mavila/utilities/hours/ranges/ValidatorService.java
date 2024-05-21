package at.mavila.utilities.hours.ranges;

import at.mavila.hours.ranges.model.Hours;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import lombok.AllArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ValidatorService {
  private static final ThreadLocal<ObjectMapper> OBJECT_MAPPER = ThreadLocal.withInitial(() -> {
    final ObjectMapper objectMapper = new ObjectMapper();
    final JavaTimeModule javaTimeModule = new JavaTimeModule();
    objectMapper.registerModule(javaTimeModule);
    return objectMapper;
  });
  private final JsonSchema jsonSchema;

  public CharSequence validateAgainstSchema(final Hours hours) {
    final Set<ValidationMessage> errors = this.jsonSchema.validate(OBJECT_MAPPER.get().valueToTree(hours));
    return CollectionUtils.isEmpty(errors) ? StringUtils.EMPTY : convertArrayToString(errors);
  }

  private static String convertArrayToString(final Set<ValidationMessage> errors) {
    return errors
            .stream()
            .map(ValidationMessage::getMessage)
            .collect(Collectors.joining("; "));
  }
}
