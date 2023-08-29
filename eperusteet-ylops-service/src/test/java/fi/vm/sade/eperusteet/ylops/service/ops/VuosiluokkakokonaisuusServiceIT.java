package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokka;
import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokkakokonaisuusviite;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.ops.VuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.VuosiluokkakokonaisuusviiteRepository;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import fi.vm.sade.eperusteet.ylops.test.AbstractH2IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;

@DirtiesContext
public class VuosiluokkakokonaisuusServiceIT extends AbstractH2IntegrationTest {

    @Autowired
    private OpetussuunnitelmaRepository suunnitelmat;

    @Autowired
    private VuosiluokkakokonaisuusviiteRepository viitteet;

    @Autowired
    private VuosiluokkakokonaisuusService service;

    private Long opsId;
    private Reference viite1Ref;
    private Reference viite2Ref;

    @Before
    public void setup() {
        Vuosiluokkakokonaisuusviite viite = new Vuosiluokkakokonaisuusviite(UUID.randomUUID(), EnumSet.of(Vuosiluokka.VUOSILUOKKA_1, Vuosiluokka.VUOSILUOKKA_2));
        this.viite1Ref = Reference.of(viitteet.save(viite));
        viite = new Vuosiluokkakokonaisuusviite(UUID.randomUUID(), EnumSet.of(Vuosiluokka.VUOSILUOKKA_3, Vuosiluokka.VUOSILUOKKA_4, Vuosiluokka.VUOSILUOKKA_5, Vuosiluokka.VUOSILUOKKA_6));
        this.viite2Ref = Reference.of(viitteet.save(viite));
        Opetussuunnitelma ops = new Opetussuunnitelma();
        ops.setPerusteenDiaarinumero("xyz");
        ops.setOrganisaatiot(Collections.singleton(SecurityUtil.OPH_OID));
        ops = suunnitelmat.save(ops);
        opsId = ops.getId();
    }

    @Test
    public void crudTest() {

        VuosiluokkakokonaisuusDto dto = new VuosiluokkakokonaisuusDto();
        dto.setTunniste(viite1Ref);

        dto = service.add(opsId, dto);
        dto = service.get(opsId, dto.getId()).getVuosiluokkakokonaisuus();
        dto.setTunniste(viite2Ref);
        dto = service.update(opsId, dto).getVuosiluokkakokonaisuus();
        service.delete(opsId, dto.getId());
    }

}
