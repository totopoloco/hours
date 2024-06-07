package at.mavila.utilities.hours.ranges;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulingConfig {
  @PostConstruct
  public void init() {
    log.info("SchedulingConfig initialized.");
  }
}
