package client;


import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

@Slf4j
public class RetriableTask<T> implements Callable<T>
{
    private static final long DEFAULT_NUMBER_OF_RETRIES = 2;
    private static final long DEFAULT_TIME_TO_WAIT = 30 * 1000;
    private final long numberOfRetries;
    private Callable<T> task;
    private long numberOfTriesMax;
    private long numberOfTriesLeft;
    private long baseTimeToWait;
    private BackoffPolicy backoffPolicy;

    public RetriableTask(Callable<T> task)
    {
        this(DEFAULT_NUMBER_OF_RETRIES, DEFAULT_TIME_TO_WAIT, task);
    }

    public RetriableTask(long numberOfRetries, long baseTimeToWait,
                         Callable<T> task)
    {
        this(numberOfRetries, baseTimeToWait, task, BackoffPolicy.CONSTANT_BASE_TIME);
    }

    public RetriableTask(long numberOfRetries, long baseTimeToWait,
                         Callable<T> task, BackoffPolicy backoffPolicy)
    {
        this.numberOfRetries = numberOfRetries;
        numberOfTriesLeft = numberOfRetries + 1 /* one for its own accord */;
        numberOfTriesMax = numberOfRetries + 1;
        this.baseTimeToWait = baseTimeToWait;
        this.task = task;
        this.backoffPolicy = backoffPolicy;
    }

    public T call() throws Exception
    {
        Exception lastException = null;

        while (numberOfTriesLeft-- > 0) {
            try {
                return task.call();
            }
            catch (CancellationException ce) {
                log.error("CancellationException: no retries will be tried (retries left {})", numberOfTriesLeft);
                throw ce;
            }
            catch (Exception ex) {
                log.error("Task '{}' failed ", task.getClass(), ex);

                lastException = ex;
                if (numberOfTriesLeft > 0) {
                    final long timeToWait = backoffPolicy.getWaitMs(baseTimeToWait, numberOfTriesMax - numberOfTriesLeft);
                    log.info("Retrying budget is still pending {} after a backoff of {}", numberOfTriesLeft, timeToWait);
                    Thread.sleep(timeToWait);
                }
                else {
                    log.error("Task failed.. Budget exhausted!");
                }
            }
        }

        throw new Exception("Retry policy failed with message = " + lastException.getMessage());
    }

    public enum BackoffPolicy
    {
        CONSTANT_BASE_TIME {
            @Override
            long getWaitMs(long baseTimeToWaitMs, long retryCount)
            {
                return baseTimeToWaitMs;
            }
        },
        MULTIPLYING_BASE_TIME {
            @Override
            long getWaitMs(long baseTimeToWaitMs, long retryCount)
            {
                return baseTimeToWaitMs * retryCount;
            }
        },
        CONSTANT_BASE_TIME_AND_RANDOM_ADDITION {
            @Override
            long getWaitMs(long baseTimeToWaitMs, long retryCount)
            {
                final long randomPart = Math.abs(new Random().nextLong()) % (MULTIPLYING_BASE_TIME.getWaitMs(baseTimeToWaitMs, retryCount));
                return baseTimeToWaitMs + randomPart;
            }
        };

        /**
         * @param baseTimeToWaitMs
         * @param retryCount       retry count starts from zero.
         * @return
         */
        abstract long getWaitMs(long baseTimeToWaitMs, long retryCount);
    }
}
