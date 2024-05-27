package at.mavila.utilities.hours.ranges;


import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.InputStream;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.hibernate.tool.schema.spi.SchemaValidator;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;

class SchemaValidatorTest {

  @Test
  void testSchema() {
    // Load the schema
    InputStream schemaStream = SchemaValidator.class.getResourceAsStream("/schemas/hours-schema.json");

    if (isNull(schemaStream)) {
      throw new AssertionError("Schema stream is null.");
    }

    JSONObject rawSchema = new JSONObject(new JSONTokener(schemaStream));
    Schema schema = SchemaLoader.load(rawSchema);


    JSONObject sampleJson = new JSONObject(
        """
            {
                "rangeDetails":
                [
                    {
                        "range":
                        {
                            "start":
                            [
                                2024,
                                5,
                                20,
                                9,
                                47
                            ],
                            "end":
                            [
                                2024,
                                5,
                                20,
                                12,
                                54
                            ]
                        },
                        "duration": "03:07",
                        "durationInHours": 3.12
                    },
                    {
                        "range":
                        {
                            "start":
                            [
                                2024,
                                5,
                                20,
                                13,
                                24
                            ],
                            "end":
                            [
                                2024,
                                5,
                                20,
                                17,
                                24
                            ]
                        },
                        "duration": "04:00",
                        "durationInHours": 4
                    },
                    {
                        "range":
                        {
                            "start":
                            [
                                2024,
                                5,
                                20,
                                17,
                                54
                            ],
                            "end":
                            [
                                2024,
                                5,
                                20,
                                18,
                                29
                            ]
                        },
                        "duration": "00:35",
                        "durationInHours": 0.58
                    }
                ],
                "totalHours": 7.7,
                "totalHoursInHHMM": "07:42",
                "expectedLunchTimeInHHMM": "12:54",
                "extraComments": "OK"
            }
            """);

    // Validate the sample JSON against the schema

    assertThatCode(() -> schema.validate(sampleJson))
        .as("Validation of the sample JSON against the schema")
        .doesNotThrowAnyException();
  }
}
