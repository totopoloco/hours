## Project Overview

This project is a Spring Boot application written in Java, which calculates daily work hours with random entry and lunch
break times. By default, it considers 7.7 hours of work per day (as per Austrian regulations), but it can be adapted to
other territories where the daily work hours are different.

The application uses Gradle as a build tool and it is containerized using Docker for easy distribution and deployment.

## Prerequisites
- Java 21 or higher
- Gradle 8.7 or higher
- (*Optional*) Docker 4.29 or higher

## Building the Project

The project can be built using Gradle. If you have Gradle installed on your system, you can run the following command in
the project's root directory:

```shell
gradle bootJar
```

If you do not have Gradle installed, you can use the Gradle Wrapper included in the project:

```shell
./gradlew bootJar
```

## Running the Application

The application can be run directly from the command line using the following command:

```shell
java -jar build/libs/hours.ranges-0.0.1-SNAPSHOT.jar
```

In Windows (Powershell), you can use the following command:

```shell
java -jar .\build\libs\hours.ranges-0.0.1-SNAPSHOT.jar
```

## Running the Application in Docker

The application can also be run in a Docker container using the latest version of the image available on Docker Hub. If Docker is properly installed on your system, you can use the following command:

```shell
docker run -d -p 8384:8384 totopo/hours:latest
```

Or alternatively, a release version of the image can be used like this:

```shell
docker run -d -p 8384:8384 totopo/hours:1.0.1
```

Check what is the latest version of the image on Docker
Hub [here](https://hub.docker.com/repository/docker/totopo/hours/tags).

## Using the Application

The application exposes a REST API that can be accessed using a web browser or a REST client like Postman. The API has
the following endpoints:

- `ranges/{lunch break in minutes}`: Calculates the range of work for the whole day.
  Use the provided script to calculate the range of work for the whole day. The script will return the range of work for
  the whole day in the format `HH:mm - HH:mm`.

```shell
./getranges.sh
```

Or use the command directly like this:

```shell
curl -X GET -H "Pragma: no-cache" http://localhost:8384/ranges/30
```

### Output

The output will be a text with the following output:

```text
08:46-12:38 -> 03:52 (3.87)
13:08-16:58 -> 03:50 (3.83)
 Total hours: 7.70
 Total hours (hh:mm): 7:42
 Expected lunch time: 12:38
```

Entry and lunch break times are randomly generated, so the output will be different each time you run the script.

## For developers

The project is structured in the following way:

- `src/main/java`: Contains the Java source code.
- `src/main/resources`: Contains the application (to customize the port).
- `src/test/java`: Contains the unit tests.

### Code

The code is structured in the following way:

### Packages

- `at.mavila.utilities.hours.ranges`: The main package.

### Classes

- `Application.java`: The main class of the application.
- `RangesController.java`: The controller class that exposes the REST API.
- `RangesCalculator.java`: The service class that calculates the work hours.
- `TimeRandomizer.java`: The utility class that generates random times.
- `TimeUtilities.java`: The utility class for repetitive functions for conversion and formatting.
- `Range.java`: The model class that represents a range of work hours.

## How to contribute

If you want to contribute to this project, you can fork the repository and create a pull request with your changes. I
will review the changes and merge them if they are appropriate.

## Future development

It would be nice to have a front-end application that consumes the REST API and displays the work hours in a more
user-friendly way. This could be a good opportunity to learn a front-end framework like Angular or React.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.



