package thread.stage0;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 스레드 풀은 무엇이고 어떻게 동작할까? 테스트를 통과시키고 왜 해당 결과가 나왔는지 생각해보자.
 * <p>
 * Thread Pools https://docs.oracle.com/javase/tutorial/essential/concurrency/pools.html
 * <p>
 * Introduction to Thread Pools in Java https://www.baeldung.com/thread-pool-java-and-guava
 */
class ThreadPoolsTest {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolsTest.class);

    @Test
    void testNewFixedThreadPool() {
        final var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        executor.submit(logWithSleep("hello fixed thread pools"));
        executor.submit(logWithSleep("hello fixed thread pools"));
        executor.submit(logWithSleep("hello fixed thread pools"));

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = 2;
        final int expectedQueueSize = 1;

        assertSoftly(softly -> {
            softly.assertThat(executor.getCorePoolSize()).isEqualTo(expectedPoolSize);
            softly.assertThat(executor.getMaximumPoolSize()).isEqualTo(expectedPoolSize);
            softly.assertThat(executor.getKeepAliveTime(TimeUnit.SECONDS)).isEqualTo(0);
            softly.assertThat(executor.getPoolSize()).isEqualTo(expectedPoolSize);
            softly.assertThat(executor.getQueue().size()).isEqualTo(expectedQueueSize);
        });
    }

    @Test
    void testNewFixedThreadPool_initial() {
        final var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

        final int expectedCorePoolSize = 2;
        final int expectedQueueSize = 0;

        assertSoftly(softly -> {
            softly.assertThat(executor.getCorePoolSize()).isEqualTo(expectedCorePoolSize);
            softly.assertThat(executor.getQueue().size()).isEqualTo(expectedQueueSize);
        });
    }

    @Test
    void testNewCachedThreadPool() {
        final var executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor.submit(logWithSleep("hello cached thread pools"));
        executor.submit(logWithSleep("hello cached thread pools"));
        executor.submit(logWithSleep("hello cached thread pools"));

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = 3;
        final int expectedQueueSize = 0;

        assertSoftly(softly -> {
            softly.assertThat(executor.getMaximumPoolSize()).isEqualTo(Integer.MAX_VALUE);
            softly.assertThat(executor.getCorePoolSize()).isEqualTo(0);
            softly.assertThat(executor.getKeepAliveTime(TimeUnit.SECONDS)).isEqualTo(60);
            softly.assertThat(executor.getPoolSize()).isEqualTo(expectedPoolSize);
            softly.assertThat(executor.getQueue().size()).isEqualTo(expectedQueueSize);
        });
    }

    @Test
    void testNewCachedThreadPool_initial() {
        final var executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        final int expectedPoolSize = 0;
        final int expectedQueueSize = 0;

        assertSoftly(softly -> {
            softly.assertThat(executor.getPoolSize()).isEqualTo(expectedPoolSize);
            softly.assertThat(executor.getQueue().size()).isEqualTo(expectedQueueSize);
        });
    }

    private Runnable logWithSleep(final String message) {
        return () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info(message);
        };
    }
}
