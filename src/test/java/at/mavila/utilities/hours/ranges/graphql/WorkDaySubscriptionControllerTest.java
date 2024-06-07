package at.mavila.utilities.hours.ranges.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import at.mavila.utilities.hours.ranges.TimeRandomizer;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.Disposable;

@SpringBootTest
class WorkDaySubscriptionControllerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(WorkDaySubscriptionControllerTest.class);

  @Mock
  private TimeRandomizer timeRandomizer;

  @Autowired
  private WorkDaySubscriptionController workDaySubscriptionController;

  @BeforeEach
  public void setup() {
    when(timeRandomizer.randomizeEntry()).thenReturn(9);
    when(timeRandomizer.randomizeLunchBreakStart()).thenReturn(12);
  }

  @Test
  void testDefaultWorkDay() throws InterruptedException {
    // Act
    final CountDownLatch latch = new CountDownLatch(2);

    final Disposable subscribe = this.workDaySubscriptionController.defaultWorkDay()
        .doOnSubscribe(subscription -> LOGGER.info("Subscribed"))
        .doOnNext(item -> {
          assertThat(item).isNotNull();
          LOGGER.info("Received: {}", item);
          assertThat(item.periods()).isNotEmpty();
          latch.countDown();
        })
        .doOnError(error -> LOGGER.info("Encountered error: {}", error.toString()))
        .doOnComplete(() -> LOGGER.info("Flux completed"))
        .subscribe();

    // Assert
    latch.await();
    this.workDaySubscriptionController.completeWorkDayFlux();
    LOGGER.info("Disposed: {}", subscribe.isDisposed());
  }
}