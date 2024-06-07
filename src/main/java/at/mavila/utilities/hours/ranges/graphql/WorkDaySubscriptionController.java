package at.mavila.utilities.hours.ranges.graphql;

import at.mavila.utilities.hours.ranges.TimeRandomizer;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Controller
@Slf4j
public class WorkDaySubscriptionController {

  private static final int LUNCH_DURATION = 30;
  private final WorkDayController workDayController;
  private final TimeRandomizer timeRandomizer;
  private final Sinks.Many<WorkDay> sink;

  public WorkDaySubscriptionController(
      final WorkDayController workDayController,
      final TimeRandomizer timeRandomizer
  ) {
    this.workDayController = workDayController;
    this.timeRandomizer = timeRandomizer;
    this.sink = Sinks.many().multicast().onBackpressureBuffer();
  }

  private WorkDay workDay(final Integer start,
                          final Integer lunchStart) {
    return this.workDayController.workDay(start, lunchStart, LUNCH_DURATION);
  }

  @Scheduled(cron = "0 0/1 * * * *") // every minute
  public void generateWorkDay() {

    final WorkDay workDay = workDay(
        this.timeRandomizer.randomizeEntry(),
        this.timeRandomizer.randomizeLunchBreakStart()
    );
    log.info("Generated work day: {}", workDay);
    this.sink.tryEmitNext(workDay);
  }

  public void completeWorkDayFlux() {
    this.sink.tryEmitComplete();
  }

  /**
   * Default work day query.
   *
   * @return the work day
   */
  @SubscriptionMapping
  public Flux<WorkDay> defaultWorkDay() {
    return this.sink.asFlux();
  }

  @PreDestroy
  public void destroy() {
    log.info("Destroying the WorkDaySubscriptionController...");
  }
}