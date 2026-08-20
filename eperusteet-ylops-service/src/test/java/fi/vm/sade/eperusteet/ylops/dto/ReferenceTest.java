package fi.vm.sade.eperusteet.ylops.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ReferenceTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void deserializesFromStringAndNumber() throws Exception {
        assertThat(mapper.readValue("\"2530070\"", Reference.class).getId()).isEqualTo("2530070");
        assertThat(mapper.readValue("2530070", Reference.class).getId()).isEqualTo("2530070");
    }
}
