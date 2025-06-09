package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.repository.dokumentti.DokumenttiRepository;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DokumenttiServiceImplTest extends AbstractIntegrationTest {

    @Autowired
    private DokumenttiRepository dokumenttiRepository;

    @Autowired
    private DokumenttiService dokumenttiService;

    @Test
    public void testCleanStuckPrintings() {
        startNewTransaction();
        Dokumentti dokumentti = new Dokumentti();
        dokumentti.setOpsId(1l);
        dokumentti.setKieli(Kieli.FI);
        dokumentti.setTila(DokumenttiTila.LUODAAN);
        dokumentti.setAloitusaika(new DateTime().minusHours(2).toDate());

        dokumentti = dokumenttiRepository.save(dokumentti);

        startNewTransaction();
        dokumenttiService.cleanStuckPrintings();

        startNewTransaction();
        dokumentti = dokumenttiRepository.findOne(dokumentti.getId());
        Assertions.assertThat(dokumentti.getTila()).isEqualTo(DokumenttiTila.LUODAAN);

        dokumentti.setAloitusaika(new DateTime().minusHours(4).toDate());
        dokumentti = dokumenttiRepository.saveAndFlush(dokumentti);

        startNewTransaction();
        dokumenttiService.cleanStuckPrintings();

        startNewTransaction();
        dokumentti = dokumenttiRepository.findOne(dokumentti.getId());
        Assertions.assertThat(dokumentti.getTila()).isEqualTo(DokumenttiTila.EPAONNISTUI);

        endTransaction();
    }
}
