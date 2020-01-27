package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpetussuunnitelmanMuokkaustieto;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.ops.MuokkaustietoKayttajallaDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmanMuokkaustietoRepository;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajaClient;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmanMuokkaustietoService;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class OpetussuunnitelmanMuokkaustietoServiceImpl implements OpetussuunnitelmanMuokkaustietoService {

    @Autowired
    private OpetussuunnitelmanMuokkaustietoRepository muokkausTietoRepository;

    @Autowired
    private KayttajaClient kayttajaClient;

    @Autowired
    private DtoMapper mapper;

    @Override
    public List<MuokkaustietoKayttajallaDto> getOpsMuokkausTietos(Long opsId, Date viimeisinLuontiaika, int lukumaara) {

        List<MuokkaustietoKayttajallaDto> muokkaustiedot = mapper
                .mapAsList(muokkausTietoRepository.findTop10ByOpetussuunnitelmaIdAndLuotuBeforeOrderByLuotuDesc(opsId, viimeisinLuontiaika, lukumaara),  MuokkaustietoKayttajallaDto.class);

        Map<String, KayttajanTietoDto> kayttajatiedot = kayttajaClient
                .haeKayttajatiedot(muokkaustiedot.stream().map(MuokkaustietoKayttajallaDto::getMuokkaaja).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(kayttajanTieto -> kayttajanTieto.getOidHenkilo(), kayttajanTieto -> kayttajanTieto));

        muokkaustiedot.forEach(muokkaustieto -> muokkaustieto.setKayttajanTieto(kayttajatiedot.get(muokkaustieto.getMuokkaaja())));

        return muokkaustiedot;
    }

    @Override
    public void addOpsMuokkausTieto(Long opsId, HistoriaTapahtuma historiaTapahtuma, MuokkausTapahtuma muokkausTapahtuma) {
        addOpsMuokkausTieto(opsId, historiaTapahtuma, muokkausTapahtuma, historiaTapahtuma.getNavigationType(), null);
    }

    @Override
    public void addOpsMuokkausTieto(Long opsId, HistoriaTapahtuma historiaTapahtuma, MuokkausTapahtuma muokkausTapahtuma, String lisatieto) {
        addOpsMuokkausTieto(opsId, historiaTapahtuma, muokkausTapahtuma, historiaTapahtuma.getNavigationType(), lisatieto);
    }

    @Override
    public void addOpsMuokkausTieto(Long opsId, HistoriaTapahtuma historiaTapahtuma, MuokkausTapahtuma muokkausTapahtuma, NavigationType navigationType) {
        addOpsMuokkausTieto(opsId, historiaTapahtuma, muokkausTapahtuma, navigationType, null);
    }

    @Override
    public void addOpsMuokkausTieto(Long opsId, HistoriaTapahtuma historiaTapahtuma, MuokkausTapahtuma muokkausTapahtuma, NavigationType navigationType, String lisatieto) {
        try {
            // Merkataan aiemmat tapahtumat poistetuksi
            if (Objects.equals(muokkausTapahtuma.getTapahtuma(), MuokkausTapahtuma.POISTO.toString())) {
                List<OpetussuunnitelmanMuokkaustieto> aiemminPoistetut = muokkausTietoRepository
                        .findByKohdeId(historiaTapahtuma.getId()).stream()
                        .peek(tapahtuma -> tapahtuma.setPoistettu(true))
                        .collect(Collectors.toList());
                muokkausTietoRepository.save(aiemminPoistetut);
            }

            // Lisäään uusi tapahtuma
            OpetussuunnitelmanMuokkaustieto muokkaustieto = OpetussuunnitelmanMuokkaustieto.builder()
                    .opetussuunnitelmaId(opsId)
                    .nimi(historiaTapahtuma.getNimi())
                    .tapahtuma(muokkausTapahtuma)
                    .muokkaaja(SecurityUtil.getAuthenticatedPrincipal().getName())
                    .kohde(navigationType)
                    .kohdeId(historiaTapahtuma.getId())
                    .luotu(historiaTapahtuma.getMuokattu())
                    .lisatieto(lisatieto)
                    .poistettu(Objects.equals(muokkausTapahtuma.getTapahtuma(), MuokkausTapahtuma.POISTO.toString()))
                    .build();

            muokkausTietoRepository.save(muokkaustieto);
        } catch(RuntimeException e) {
            log.error("Historiatiedon lisääminen epäonnistui", e);
        }
    }
}

