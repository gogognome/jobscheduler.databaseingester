CREATE TABLE NlGogognomeJobscheduler (
  id VARCHAR(1000),
  creationTimestamp TIMESTAMP NOT NULL,
  startTimestamp TIMESTAMP NULL,
  type VARCHAR(1000) NOT NULL,
  data VARCHAR(100000) NULL,
  PRIMARY KEY (id)
);
