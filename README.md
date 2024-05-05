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
The application exposes a REST API that can be accessed using a web browser or a REST client like Postman. The API has
the following endpoints:

- `/ranges/{lunch break in minutes}`: Calculates the range of work for the whole day.
  Use the provided script to calculate the range of work for the whole day. The script will return the range of work for
  the whole day in the format `HH:mm - HH:mm`.
- `/rangesWithStartLunchAndMinutesOfLunchBreak/{start}/{lunch}/{minutesOfLunchBreak}`: Same as the previous endpoint,
  but
  you can specify the start time and the lunch break time and duration of the lunch break.
  In UNIX systems, you can use the following command:
```shell
./getranges.sh
```

With extra parameters, you can specify the start time, the lunch break time, and the duration of the lunch break.

```shell
./getranges.sh -s 9 -l 12 -b 30
```

In Windows (Powershell), you can use the following command:

```powershell
.\getranges.ps1 30
```

With extra parameters, you can specify the start time, the lunch break time, and the duration of the lunch break.
```powershell
.\getranges.ps1 9 12 30
```
Or use the command directly like this:
```shell
curl -X GET -H "Pragma: no-cache" http://localhost:8384/ranges/30
```

```shell
curl -X GET -H "Pragma: no-cache" http://localhost:8384/rangesWithStartLunchAndMinutesOfLunchBreak/7/12/30
```

### Output

The output will be a Json object with the following output:

```json
{
  "rangeDetails": [
    {
      "range": {
        "start": "09:06:00",
        "end": "13:06:00"
      },
      "duration": "04:00",
      "durationInHours": "4.00"
    },
    {
      "range": {
        "start": "13:36:00",
        "end": "17:18:00"
      },
      "duration": "03:42",
      "durationInHours": "3.70"
    }
  ],
  "totalHours": "7.70",
  "totalHoursInHHMM": "07:42",
  "expectedLunchTimeInHHMM": "13:30"
}
```
Entry and lunch break times are randomly generated, so the output will be different each time you run the script.

# For developers
The project is structured in the following way:
- `src/main/java`: Contains the Java source code.
- `src/main/resources`: Contains the application (to customize the port).
- `src/test/java`: Contains the unit tests.

# How to contribute
If you want to contribute to this project, you can fork the repository and create a pull request with your changes. I
will review the changes and merge them if they are appropriate.

# Future development
It would be nice to have a front-end application that consumes the REST API and displays the work hours in a more
user-friendly way. This could be a good opportunity to learn a front-end framework like Angular or React.

# License

This project is licensed under the MIT-License - see the [LICENSE](LICENSE) file for details.