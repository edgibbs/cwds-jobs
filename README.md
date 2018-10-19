## CALS Jobs

The CALS Jobs project provides java based stand alone applications which are meant to be scheduled periodically.

## Installation

## Prerequisites
Prerequisites are job dependent but typically you can expect the following to be needed :

1.  DB2 10.x
2.  Postgres 9.x
3.  Elasticsearch 5.3.2 or newer (newer would require testing)

## Development Environment

### Prerequisites

1. Source code, available at [GitHub](https://github.com/ca-cwds/cals-jobs)
1. Java SE 8 development kit
1. DB2 Database
1. Postgres Database
1. Elasticsearch

### Building

% ./gradlew build


### Facility Indexer Job

Main Class: gov.ca.cwds.jobs.FacilityIndexerJob
run job using following command: 
```bash
$java -cp jobs.jar gov.ca.cwds.jobs.FacilityIndexerJob path/to/config/file.yaml
```

# Questions

If you have any questions regarding the contents of this repository, please email the Office of Systems Integration at FOSS@osi.ca.gov.

