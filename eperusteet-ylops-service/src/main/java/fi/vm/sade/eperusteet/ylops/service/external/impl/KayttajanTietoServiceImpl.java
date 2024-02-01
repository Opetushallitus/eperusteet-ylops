package fi.vm.sade.eperusteet.ylops.service.external.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.EtusivuDto;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanProjektitiedotDto;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajaClient;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import static fi.vm.sade.eperusteet.ylops.service.security.PermissionEvaluator.RolePermission.ADMIN;
import static fi.vm.sade.eperusteet.ylops.service.security.PermissionEvaluator.RolePermission.CRUD;

@Service
@Profile("!test")
public class KayttajanTietoServiceImpl implements KayttajanTietoService {

    @Autowired
    @Lazy
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    @Lazy
    private KayttajaClient client;

    @Override
    public KayttajanTietoDto hae(String oid) {
        return client.hae(oid);
    }

    @Override
    public String haeKayttajanimi(String oid) {
        if (oid != null) {
            KayttajanTietoDto tiedot = client.hae(oid);
            if (tiedot != null && tiedot.getKutsumanimi() != null && tiedot.getSukunimi() != null) {
                return tiedot.getKutsumanimi() + " " + tiedot.getSukunimi();
            }
        }
        return oid;
    }

    @Override
    @Async
    public Future<KayttajanTietoDto> haeAsync(String oid) {
        return new AsyncResult<>(hae(oid));
    }

    @Override
    public KayttajanTietoDto haeKirjautaunutKayttaja() {
        Principal ap = SecurityUtil.getAuthenticatedPrincipal();
        KayttajanTietoDto kayttaja = hae(ap.getName());
        if (kayttaja == null) { //"fallback" jos integraatio on rikki eikä löydä käyttäjän tietoja
            kayttaja = new KayttajanTietoDto(ap.getName());
        }
        return kayttaja;
    }

    @Override
    public List<KayttajanProjektitiedotDto> haeOpetussuunnitelmat(String oid) {
        // TODO
        throw new NotImplementedException();
    }

    @Override
    public KayttajanProjektitiedotDto haeOpetussuunnitelma(String oid, Long opsId) {
        // TODO
        throw new NotImplementedException();
    }

    @Override
    public EtusivuDto haeKayttajanEtusivu() {
        EtusivuDto result = new EtusivuDto();
        result.setOpetussuunnitelmatKeskeneraiset(opetussuunnitelmaService.getAmount(Tyyppi.OPS, Sets.newHashSet(Tila.LUONNOS)));
        result.setOpetussuunnitelmatJulkaistut(opetussuunnitelmaService.getAmount(Tyyppi.OPS, Tila.julkiset()));
        result.setPohjatKeskeneraiset(opetussuunnitelmaService.getAmount(Tyyppi.POHJA, Sets.newHashSet(Tila.LUONNOS)));
        result.setPohjatJulkaistut(opetussuunnitelmaService.getAmount(Tyyppi.POHJA, Sets.newHashSet(Tila.VALMIS)));
        return result;
    }

    @Override
    public List<KayttajanTietoDto> haeKayttajatiedot(List<String> oid) {
        return client.haeKayttajatiedot(oid);
    }

    @Override
    public Set<String> haeOrganisaatioOikeudet() {
        return SecurityUtil.getOrganizations(new HashSet<>(Arrays.asList(ADMIN, CRUD)));
    }

}
