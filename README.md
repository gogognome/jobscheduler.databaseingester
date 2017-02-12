# Job Scheduler Database Ingester
a job ingester that reads jobs from a database and adds them to the job scheduler

## Introduction

This library allows your application or microservices to add, update and remove jobs
to/from the [`JobScheduler`](https://github.com/gogognome/jobscheduler) by writing
job commands in a database table. This library creates worker thread that polls
for job commands in the table and forwards them to the job scheduler and then
removes the job commands.

This library is ideal if you need multiple applications/microservices to work with
a shared job scheduler instance and if these applications/microservices already share
a database. By using the database for 'sending' job commands you have the advantage
that job commands are 'sent' as part of the database transaction. You don't need
distributed transactions as is needed if you are using a message bus like MSMQ.

## Usage

You are responsible for creating a table. The name of the table and columns are configurable.
The default table can be created with this SQL command:

    CREATE TABLE NlGogognomeJobsToIngest (
      id VARCHAR(1000),
      command VARCHAR(10) NOT NULL,
      creationInstant TIMESTAMP NOT NULL,
      scheduledAtInstant TIMESTAMP NULL,
      type VARCHAR(1000) NOT NULL,
      data VARCHAR(100000) NULL,
      state VARCHAR(100) NOT NULL,
      requesterId VARCHAR(1000) NULL,
      PRIMARY KEY (id)
    );

You see that your application or microservices are responsible for generating a unique
id for the job commands. A simple scheme to follow might be `<server-name>-<process-id>-<sequence-number>`
or just use GUIDs.

The value of command must be `CREATE`, `UPDATE`, or `DELETE`. 
The value of state must be one of `IDLE`, `RUNNING` or `ERROR`.

Creating a `JobIngesterRunner` instance is a bit laborious without Spring's depdency injection:

    JobScheduler jobScheduler = ... // see job scheduler project
    JobIngesterProperties properties = new JobIngesterProperties();
    JobCommandDAO jobCommandDao = new JobCommandDAO(properties);
    JobIngester jobIngester = new JobIngester(jobScheduler, jobCommandDao);
    JobIngesterRunner jobIngesterRunner = new JobIngesterRunner(properties, jobIngester);

If you use Spring's depdency injection then you can simply autowire the `JobIngesterRunner`.

This library uses [Gogo Data Access](https://github.com/gogognome/gogodataaccess) to access
the database. All you have to do is register a `DataSource` at initalization time:

    DataSource dataSource = ...; // probably wise to use a DataSource backed up by a connection pool
    CompositeDatasourceTransaction.registerDataSource(properties.getConnectionName(), dataSource);

Once you have an instance of `JobIngesterRunner` you can start a separate thread that will
poll for job commands in a database table and forward all commands to the `JobScheduler` as follows:

    jobIngesterRunner.start();
    
And when you want to stop the `JobIngesterRunner` simply do

    jobIngesterRunner.stop();
