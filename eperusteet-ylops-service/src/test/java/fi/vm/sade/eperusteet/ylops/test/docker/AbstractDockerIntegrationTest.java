package fi.vm.sade.eperusteet.ylops.test.docker;

import fi.vm.sade.eperusteet.ylops.test.TestUser;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Julkaisutestejä varten tehty ja käyttää eri profiileja kuin muut testit.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles(profiles = {"docker"})
public abstract class AbstractDockerIntegrationTest {

    @Before
    public void setUpSecurityContext() {
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(TestUser.authenticated("test"));
        SecurityContextHolder.setContext(ctx);

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    public void loginAsUser(String user) {
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(TestUser.authenticated(user));
        SecurityContextHolder.setContext(ctx);
    }

    public void invalidateAuthentication() {
        SecurityContextHolder.clearContext();
    }

    public void startNewTransaction() {
        if (TestTransaction.isActive()) {
            TestTransaction.end();
        }
        TestTransaction.start();
        TestTransaction.flagForCommit();
    }

    public void endTransaction() {
        TestTransaction.end();
    }
}
