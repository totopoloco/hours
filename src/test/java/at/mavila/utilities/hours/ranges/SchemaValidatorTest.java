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
    InputStream schemaStream = SchemaValidator.class.getResourceAsStream("/schemas/rangeSchema.json");

    if (isNull(schemaStream)) {
      throw new AssertionError("Schema stream is null.");
    }

    JSONObject rawSchema = new JSONObject(new JSONTokener(schemaStream));
    Schema schema = SchemaLoader.load(rawSchema);


    JSONObject sampleJson = new JSONObject(
        """
                {
                    "rangeDetails": [
                        {
                            "range": {
                                "start": "08:19:00",
                                "end": "12:19:00"
                            },
                            "duration": "04:00",
                            "durationInHours": "4.00"
                        },
                        {
                            "range": {
                                "start": "12:49:00",
                                "end": "16:31:00"
                            },
                            "duration": "03:42",
                            "durationInHours": "3.70"
                        }
                    ],
                    "totalHours": "7.70",
                    "totalHoursInHHMM": "07:42",
                    "expectedLunchTimeInHHMM": "14:44"
                }
            """);

    // Validate the sample JSON against the schema

    assertThatCode(() -> schema.validate(sampleJson))
        .as("Validation of the sample JSON against the schema")
        .doesNotThrowAnyException();
  }
}
