package nl.gogognome.jobscheduler.jobingester.database;

import nl.gogognome.dataaccess.dao.AbstractDomainClassDAO;
import nl.gogognome.dataaccess.dao.NameValuePairs;
import nl.gogognome.dataaccess.dao.ResultSetWrapper;
import nl.gogognome.jobscheduler.scheduler.Job;
import nl.gogognome.jobscheduler.scheduler.JobState;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Component
class JobIngestDAO extends AbstractDomainClassDAO<Job>{

    private final Properties properties;

    public JobIngestDAO(Properties properties) {
        super(properties.getTableName(), null, properties.getConnectionName());
        this.properties = properties;
    }

    public void delete(List<Job> jobs) {
        if (!jobs.isEmpty()) {
            StringBuilder query = new StringBuilder();
            query.append("DELETE FROM ").append(tableName).append(" WHERE " + properties.getIdColumn() + " + IN (");
            for (int i=0; i<jobs.size(); i ++) {
                if (i != 0) {
                    query.append(',');
                }
                query.append('?');
            }
            query.append(')');

            execute(query.toString(), jobs.toArray());
        }
    }

    @Override
    protected Job getObjectFromResultSet(ResultSetWrapper result) throws SQLException {
        Job job = new Job(result.getString(properties.getIdColumn()));
        job.setCreationTimestamp(result.getInstant(properties.getCreationTimestampColumn()));
        job.setStartTimestamp(result.getInstant(properties.getStartTimestampColumn()));
        job.setType(result.getString(properties.getTypeColumn()));
        job.setData(result.getString(properties.getDataColumn()));
        job.setState(JobState.IDLE);
        return job;
    }

    @Override
    protected NameValuePairs getNameValuePairs(Job job) throws SQLException {
        return new NameValuePairs()
                .add(properties.getIdColumn(), job.getId())
                .add(properties.getCreationTimestampColumn(), job.getCreationTimestamp())
                .add(properties.getStartTimestampColumn(), job.getStartTimestamp())
                .add(properties.getTypeColumn(), job.getType())
                .add(properties.getDataColumn(), job.getData());
    }
}
