package fi.vm.sade.eperusteet.ylops.service.teksti;

import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.ylops.test.AbstractH2IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.lokalisoituTekstiOf;

@Transactional
public class TekstiKappaleServiceIT extends AbstractH2IntegrationTest {

    @Autowired
    private TekstiKappaleService tekstiKappaleService;

    @Autowired
    private TekstiKappaleRepository tekstiKappaleRepository;

    @Test
    public void testGet() {
        TekstiKappale tekstiKappale = new TekstiKappale();
        final String NIMI = "Namnet";
        final String TEKSTI = "Teksten";
        tekstiKappale.setNimi(lokalisoituTekstiOf(Kieli.SV, NIMI));
        tekstiKappale.setTeksti(lokalisoituTekstiOf(Kieli.SV, TEKSTI));
        tekstiKappale.setTila(Tila.LUONNOS);

        tekstiKappale = tekstiKappaleRepository.save(tekstiKappale);

        TekstiKappaleDto dto = tekstiKappaleService.get(null, tekstiKappale.getId());
        Assert.assertNotNull(dto);

        Assert.assertEquals(NIMI, dto.getNimi().get(Kieli.SV));
        Assert.assertEquals(TEKSTI, dto.getTeksti().get(Kieli.SV));
        Assert.assertEquals(tekstiKappale.getTila(), dto.getTila());
    }
}
