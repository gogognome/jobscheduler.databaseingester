package nl.gogognome.jobscheduler.jobingester.database;

import org.springframework.stereotype.Component;

@Component
public class JobIngesterRunner {

    private final Properties properties;
    private final JobIngester jobIngester;

    private Thread thread;
    private boolean threadRunning;
    private Object lock = new Object();

    public JobIngesterRunner(Properties properties, JobIngester jobIngester) {
        this.properties = properties;
        this.jobIngester = jobIngester;
    }

    public void start() {
        synchronized (lock) {
            if (threadRunning) {
                throw new IllegalStateException("The job ingester is still running");
            }
            threadRunning = true;
            thread = new Thread(() -> timerThread());
            thread.start();
        }
    }

    public void stop() {
        synchronized (lock) {
            if (!threadRunning) {
                throw new IllegalStateException("The job ingester is not running");
            }
            threadRunning = false;
            lock.notify();
        }
        try {
            thread.join();
        } catch (InterruptedException e) {
            // ignore this exception
        }
        thread = null;
    }

    private void timerThread() {
        while (true) {
            synchronized (lock) {
                if (!threadRunning) {
                    return;
                }
            }
            jobIngester.ingestJobs();

            synchronized (lock) {
                try {
                    lock.wait(properties.getDelayBetweenPolls());
                } catch (InterruptedException e) {
                    // ignore this exception
                }
            }
        }
    }
}
