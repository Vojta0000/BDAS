package upce.javafx.scheduler;

import upce.javafx.ConnectionSingleton;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Schedules monthly execution of Oracle stored procedure apply_monthly_interest.
 * Default schedule: 1st day of each month at 00:15 in the system default timezone.
 */
public class MonthlyInterestScheduler {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "MonthlyInterestScheduler");
        t.setDaemon(true);
        return t;
    });
    private final ZoneId zoneId;
    private final LocalTime runAtTime;
    private ScheduledFuture<?> current;

    /**
     * Default constructor using system default timezone and 00:15 as run time.
     */
    public MonthlyInterestScheduler() {
        this(ZoneId.systemDefault(), LocalTime.of(0, 15));
    }

    /**
     * Constructor allowing custom timezone and run time.
     * @param zoneId Timezone to use.
     * @param runAtTime Time of day to run the scheduler.
     */
    public MonthlyInterestScheduler(ZoneId zoneId, LocalTime runAtTime) {
        this.zoneId = Objects.requireNonNull(zoneId);
        this.runAtTime = Objects.requireNonNull(runAtTime);
    }

    /**
     * Starts the scheduler.
     */
    public synchronized void start() {
        if (current != null && !current.isCancelled()) return;
        scheduleNext();
        System.out.println("[MonthlyInterestScheduler] Started. Next run in " + millisToNextRun()/1000 + " seconds.");
    }

    /**
     * Shuts down the scheduler.
     */
    public synchronized void shutdown() {
        if (current != null) current.cancel(false);
        executor.shutdownNow();
        System.out.println("[MonthlyInterestScheduler] Stopped.");
    }

    /** Execute the PL/SQL procedure immediately (manual trigger). */
    public void runOnceNow() { executor.submit(this::executeProcedureSafely); }

    /**
     * Schedules the next run of the procedure.
     */
    private void scheduleNext() {
        long delayMs = millisToNextRun();
        current = executor.schedule(() -> {
            executeProcedureSafely();
            // re-schedule for the next month
            synchronized (MonthlyInterestScheduler.this) { scheduleNext(); }
        }, Math.max(0, delayMs), TimeUnit.MILLISECONDS);
    }

    /**
     * Calculates the milliseconds until the next scheduled run.
     * @return Milliseconds until next run.
     */
    private long millisToNextRun() {
        LocalDateTime now = LocalDateTime.now(zoneId);
        LocalDate today = now.toLocalDate();
        LocalDate firstOfThisMonth = today.withDayOfMonth(1);
        LocalDateTime thisMonthRun = LocalDateTime.of(firstOfThisMonth, runAtTime);
        LocalDateTime nextRun;
        if (now.isBefore(thisMonthRun)) {
            nextRun = thisMonthRun;
        } else {
            // next month, day 1, same time
            LocalDate firstOfNext = firstOfThisMonth.plusMonths(1);
            nextRun = LocalDateTime.of(firstOfNext, runAtTime);
        }
        return Duration.between(now, nextRun).toMillis();
    }

    /**
     * Safely executes the interest calculation procedure, including retries on failure.
     */
    private void executeProcedureSafely() {
        System.out.println("[MonthlyInterestScheduler] Executing apply_monthly_interest at " + LocalDateTime.now(zoneId));
        try {
            executeProcedure();
            System.out.println("[MonthlyInterestScheduler] Success: apply_monthly_interest finished.");
        } catch (SQLException e) {
            System.err.println("[MonthlyInterestScheduler] Error executing apply_monthly_interest: " + e.getMessage());
            // simple one-time retry after 60s
            executor.schedule(() -> {
                try {
                    System.out.println("[MonthlyInterestScheduler] Retrying apply_monthly_interest...");
                    executeProcedure();
                    System.out.println("[MonthlyInterestScheduler] Retry success.");
                } catch (SQLException ex) {
                    System.err.println("[MonthlyInterestScheduler] Retry failed: " + ex.getMessage());
                }
            }, 60, TimeUnit.SECONDS);
        }
    }

    /**
     * Executes the Oracle stored procedure to apply monthly interest.
     * @throws SQLException If a database error occurs.
     */
    private void executeProcedure() throws SQLException {
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             CallableStatement cs = conn.prepareCall("{ call apply_monthly_interest }")) {
            boolean auto = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);
                cs.execute();
                conn.commit();
            } catch (SQLException e) {
                try { conn.rollback(); } catch (SQLException ignore) {}
                throw e;
            } finally {
                try { conn.setAutoCommit(auto); } catch (SQLException ignore) {}
            }
        }
    }
}
