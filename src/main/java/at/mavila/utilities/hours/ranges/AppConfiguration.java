package at.mavila.utilities.hours.ranges;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class AppConfiguration {

  private static final String SCHEMA_VALIDATION_FILE = "/schemas/hours-schema.json";
  private final HoursConfiguration hoursConfiguration;

  @Bean
  public JsonSchema jsonSchema() {
    return JsonSchemaFactory
            .getInstance(SpecVersion.VersionFlag.V7)
            .getSchema(
                    getClass()
                            .getResourceAsStream(SCHEMA_VALIDATION_FILE)
            );
  }

  @Bean
  public CommandLineRunner printHoursConfiguration() {
    return args -> {
      log.info("Hours Configuration:");
      log.info("Minutes Per Day Of Work: {}", this.hoursConfiguration.getMinutesPerDayOfWork());
      log.info("Maximum Minutes In A Row: {}", this.hoursConfiguration.getMaximumMinutesInARow());
      log.info("Minutes To Rest Between Rows: {}", this.hoursConfiguration.getMinutesToRestBetweenRows());
    };
  }


}
