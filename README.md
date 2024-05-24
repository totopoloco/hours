# Project Overview
This project is a Spring Boot application written in Java, which calculates daily work hours with random entry and lunch
break times. By default, it considers 7.7 hours of work per day (as per Austrian regulations), but it can be adapted to
other territories where the daily work hours are different.
The application uses Gradle as a build tool, and it is containerized using Docker for easy distribution and deployment.

# Prerequisites
- Java 21 or higher
- Gradle 8.7 or higher
- (*Optional*) Docker 4.29 or higher
- PowerShell 7.4 or higher (Windows only,
  see [here](https://learn.microsoft.com/en-us/powershell/scripting/install/installing-powershell-on-windows?view=powershell-7.4))

# Building the Project
The project can be built using Gradle. If you have Gradle installed on your system, you can run the following command in
the project's root directory:
```shell
gradle bootJar
```
If you do not have Gradle installed, you can use the Gradle Wrapper included in the project:
```shell
./gradlew bootJar
```

# Running the Application
The application can be run directly from the command line using the following command:
```shell
java -jar build/libs/hours.ranges-0.0.1-SNAPSHOT.jar
```
In Windows (Powershell), you can use the following command:
```shell
java -jar .\build\libs\hours.ranges-0.0.1-SNAPSHOT.jar
```

# Running the Application in Docker

The application can also be run in a Docker container using the latest version of the image available on Docker Hub.
If Docker is properly installed on your system, you can use the following command:
```shell
docker run -d -p 8384:8384 totopo/hours:latest
```
Or alternatively, a release version of the image can be used like this:
```shell
docker run -d -p 8384:8384 totopo/hours:1.0.1
```

## Docker compose

You can also use Docker Compose to run the application. The `docker-compose.yml` file is included in the project.
To run the application using Docker Compose, you can use the following command:

```shell
docker compose up -d
```

Check what is the latest version of the image on Docker
Hub [here](https://hub.docker.com/repository/docker/totopo/hours/tags).

# Using the Application

The application exposes a REST API that can be accessed using a web browser or a REST client like Postman.
The documentation for the REST API is deployed with the application and can be accessed at the following
URL: [http://localhost:8384/api/swagger-ui/index.html](http://localhost:8384/api/swagger-ui/index.html).

But in this project we provide a script that can be used to get the ranges of the day.
```shell
./getranges.sh
```

With extra parameters, you can specify the start time, the lunch break time, and the duration of the lunch break.

```shell
./getranges.sh -s 9 -l 12 -b 30
```

In Windows (Powershell), you can use the following command:

```powershell
.\getranges.ps1 -b 30
```

With extra parameters, you can specify the start time, the lunch break time, and the duration of the lunch break.
```powershell
.\getranges.ps1 -s 9 -l 12 -b 30
```
Or use the command directly like this:
```shell
curl -X GET -H "Pragma: no-cache" http://localhost:8384/ranges/30
```

```shell
curl -X GET -H "Pragma: no-cache" http://localhost:8384/rangesWith/7/12/30
```

### Output

The output will be a Json object with the following output:

```json
{
  "rangeDetails": [
    {
      "range": {
        "start": "2024-05-14T07:59:00",
        "end": "2024-05-14T11:59:00"
      },
      "duration": "04:00",
      "durationInHours": 4.00
    },
    {
      "range": {
        "start": "2024-05-14T12:29:00",
        "end": "2024-05-14T16:11:00"
      },
      "duration": "03:42",
      "durationInHours": 3.70
    }
  ],
  "totalHours": 7.70,
  "totalHoursInHHMM": "07:42",
  "expectedLunchTimeInHHMM": "12:35"
}
```
Entry and lunch break times are randomly generated, so the output will be different each time you run the script.

# GraphQL

Support for GraphQL has been added to the application. The GraphQL playground can be accessed at the following URL:
[http://localhost:8384/graphiql](http://localhost:8384/graphiql).

Two queries are available:

```graphql
query WorkDayQuery {
  workDay(start: 9, lunchStart: 13, lunchDuration: 30) {
    periods {
      start
      end
      duration
      durationInHours
    }
    totalHours
    totalHoursInHHMM
    expectedLunchTimeInHHMM
  }
}
```

Random values are generated if no parameters are provided:

```graphql
query WorkDayQuery {
  defaultWorkDay {
    periods {
      start
      end
      duration
      durationInHours
    }
    totalHours
    totalHoursInHHMM
    expectedLunchTimeInHHMM
  }
}
```

# For developers
The project is structured in the following way:
- `src/main/java`: Contains the Java source code.
- `src/main/resources`: Contains the application (to customize the port).
- `src/test/java`: Contains the unit tests.

## Schema validation
The project uses the [com.networknt:json-schema-validator](https://github.com/networknt/json-schema-validator) library to validate the JSON schema of the REST API. 
The schema is located in the `src/main/resources/schemas` directory.
On every request, the schema is validated against the response body.
This double-check ensures that the response body is always valid against the schema.

# How to contribute
If you want to contribute to this project, you can fork the repository and create a pull request with your changes. I
will review the changes and merge them if they are appropriate.

# Future development
It would be nice to have a front-end application that consumes the REST API and displays the work hours in a more
user-friendly way. This could be a good opportunity to learn a front-end framework like Angular or React.

# License

This project is licensed under the MIT-License - see the [LICENSE](LICENSE) file for details.