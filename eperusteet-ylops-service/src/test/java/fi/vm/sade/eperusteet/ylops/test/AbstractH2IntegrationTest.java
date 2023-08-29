package fi.vm.sade.eperusteet.ylops.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/it-test-context.xml")
@ActiveProfiles(profiles = "test")
public abstract class AbstractH2IntegrationTest extends AbstractIntegrationTest{
}
