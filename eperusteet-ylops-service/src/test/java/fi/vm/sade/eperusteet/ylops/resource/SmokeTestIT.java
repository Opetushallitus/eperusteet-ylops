package fi.vm.sade.eperusteet.ylops.resource;

import fi.vm.sade.eperusteet.ylops.test.AbstractWebIntegrationTest;
import org.junit.Ignore;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Ignore // FIXME
public class SmokeTestIT extends AbstractWebIntegrationTest {

    @Test
    public void testOpetussuunnitelmatResource() throws Exception {
        mockMvc.perform(get("/opetussuunnitelmat")).andExpect(status().isOk());
        mockMvc.perform(post("/e2e/reset")).andExpect(status().is(404));
    }

}
