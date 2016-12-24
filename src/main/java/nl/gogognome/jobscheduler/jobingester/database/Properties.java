package nl.gogognome.jobscheduler.jobingester.database;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("database")
public class Properties {

    private String connectionName = "nl.gogognome.jobscheduler.jobingester";
    private String tableName = "NlGogognomeJobscheduler";
    private String commandColumn = "command";
    private String idColumn = "id";
    private String creationTimestampColumn = "creationTimestamp";
    private String startTimestampColumn = "startTimestamp";
    private String typeColumn = "type";
    private String dataColumn = "data";
    private String jobStateColumn = "state";
    private String requesterIdColumn = "requesterId";

    private long delayBetweenPolls = 1000L;

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getCommandColumn() {
        return commandColumn;
    }

    public void setCommandColumn(String commandColumn) {
        this.commandColumn = commandColumn;
    }

    public String getIdColumn() {
        return idColumn;
    }

    public void setIdColumn(String idColumn) {
        this.idColumn = idColumn;
    }

    public String getCreationTimestampColumn() {
        return creationTimestampColumn;
    }

    public void setCreationTimestampColumn(String creationTimestampColumn) {
        this.creationTimestampColumn = creationTimestampColumn;
    }

    public String getStartTimestampColumn() {
        return startTimestampColumn;
    }

    public void setStartTimestampColumn(String startTimestampColumn) {
        this.startTimestampColumn = startTimestampColumn;
    }

    public String getTypeColumn() {
        return typeColumn;
    }

    public void setTypeColumn(String typeColumn) {
        this.typeColumn = typeColumn;
    }

    public String getDataColumn() {
        return dataColumn;
    }

    public void setDataColumn(String dataColumn) {
        this.dataColumn = dataColumn;
    }

    public String getJobStateColumn() {
        return jobStateColumn;
    }

    public void setJobStateColumn(String jobStateColumn) {
        this.jobStateColumn = jobStateColumn;
    }

    public String getRequesterIdColumn() {
        return requesterIdColumn;
    }

    public void setRequesterIdColumn(String requesterIdColumn) {
        this.requesterIdColumn = requesterIdColumn;
    }

    public long getDelayBetweenPolls() {
        return delayBetweenPolls;
    }

    public void setDelayBetweenPolls(long delayBetweenPolls) {
        this.delayBetweenPolls = delayBetweenPolls;
    }
}
