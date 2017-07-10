CREATE TABLE NlGogognomeJobsToIngest (
  id VARCHAR(1000),
  command VARCHAR(20) NOT NULL,
  scheduledAtInstant TIMESTAMP NULL,
  type VARCHAR(1000) NOT NULL,
  data VARCHAR(100000) NULL,
  PRIMARY KEY (id)
);
