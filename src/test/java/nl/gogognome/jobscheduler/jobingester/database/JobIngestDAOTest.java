package nl.gogognome.jobscheduler.jobingester.database;

import nl.gogognome.dataaccess.migrations.DatabaseMigratorDAO;
import nl.gogognome.dataaccess.migrations.Migration;
import nl.gogognome.dataaccess.transaction.CompositeDatasourceTransaction;
import nl.gogognome.dataaccess.transaction.CurrentTransaction;
import nl.gogognome.dataaccess.transaction.NewTransaction;
import nl.gogognome.jobscheduler.scheduler.Job;
import nl.gogognome.jobscheduler.scheduler.JobState;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

public class JobIngestDAOTest {

    private Properties properties = new Properties();
    private Connection connectionToKeepInMemoryDatabaseAlive;
    private JobIngestDAO jobIngestDAO;

    @Before
    public void setupInMemoryDatabase() throws SQLException {
        String jdbcUrl = "jdbc:h2:mem:" + UUID.randomUUID();
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(jdbcUrl);
        connectionToKeepInMemoryDatabaseAlive = dataSource.getConnection();
        CompositeDatasourceTransaction.registerDataSource(properties.getConnectionName(), dataSource);

        NewTransaction.runs(() -> {
            DatabaseMigratorDAO databaseMigratorDAO = new DatabaseMigratorDAO(properties.getConnectionName());
            List<Migration> migrations = databaseMigratorDAO.loadMigrationsFromResource("/database/_migrations.txt");
            databaseMigratorDAO.applyMigrations(migrations);
        });

        jobIngestDAO = new JobIngestDAO(properties);
    }

    @After
    public void removeInMemoryDatabase() {
        connectionToKeepInMemoryDatabaseAlive = null;
    }

    @Test
    public void findAll_zeroJobsInDatabase_returnsEmptyList() throws SQLException {
        NewTransaction.runs(() -> {
            List<Job> jobs = jobIngestDAO.findAll();

            assertEquals(emptyList(), jobs);
        });
    }

    @Test
    public void findAll_oneJobsInDatabase_returnsOneJob() throws SQLException {
        Job job1 = new Job("1");
        job1.setCreationTimestamp(Instant.now());
        job1.setType("Test");
        job1.setData("Data");

        NewTransaction.runs(() -> {
            jobIngestDAO.create(job1);
            List<Job> jobs = jobIngestDAO.findAll();

            assertEquals(singleton(job1), jobs);
            Job retrievedJob1 = jobs.get(0);
            assertEquals(job1.getId(), retrievedJob1.getId());
            assertEquals(job1.getCreationTimestamp(), retrievedJob1.getCreationTimestamp());
            assertEquals(job1.getStartTimestamp(), retrievedJob1.getStartTimestamp());
            assertEquals(job1.getType(), retrievedJob1.getType());
            assertEquals(job1.getData(), retrievedJob1.getData());
            assertEquals(job1.getState(), retrievedJob1.getState());
        });
    }

}
