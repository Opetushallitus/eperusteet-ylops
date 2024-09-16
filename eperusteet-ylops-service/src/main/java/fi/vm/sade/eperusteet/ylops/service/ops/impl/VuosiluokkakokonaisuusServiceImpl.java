package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaineenvuosiluokkakokonaisuus;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpsVuosiluokkakokonaisuus;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpsVuosiluokkakokonaisuusLisatieto;
import fi.vm.sade.eperusteet.ylops.domain.vuosiluokkakokonaisuus.Vuosiluokkakokonaisuus;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpsVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.VuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetuksenkeskeinenSisaltoalueRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.VuosiluokkakokonaisuusRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmanMuokkaustietoService;
import fi.vm.sade.eperusteet.ylops.service.ops.VuosiluokkakokonaisuusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class VuosiluokkakokonaisuusServiceImpl implements VuosiluokkakokonaisuusService {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private VuosiluokkakokonaisuusRepository kokonaisuudet;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetuksenkeskeinenSisaltoalueRepository opetuksenkeskeinenSisaltoalueRepository;

    @Autowired
    private OpetussuunnitelmanMuokkaustietoService muokkaustietoService;

    @Override
    public VuosiluokkakokonaisuusDto add(Long opsId, VuosiluokkakokonaisuusDto dto) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        if (ops == null) {
            throw new BusinessRuleViolationException("Opetussuunnitelmaa ei löydy");
        }
        Vuosiluokkakokonaisuus vk;
        if (dto.getId() != null) {
            vk = kokonaisuudet.findOne(opsId);
            ops.attachVuosiluokkaKokonaisuus(vk);
            //TODO: tarkista lukuoikeus
        } else {
            vk = mapper.map(dto, Vuosiluokkakokonaisuus.class);
            vk = kokonaisuudet.save(vk);
            ops.addVuosiluokkaKokonaisuus(vk);
        }

        return mapper.map(vk, VuosiluokkakokonaisuusDto.class);
    }

    @Override
    public OpsVuosiluokkakokonaisuusDto get(Long opsId, Long kokonaisuusId) {
        Boolean isOma = kokonaisuudet.isOma(opsId, kokonaisuusId);
        if (isOma == null) {
            throw new BusinessRuleViolationException("Vuosiluokkakokonaisuutta ei ole");
        }

        final Vuosiluokkakokonaisuus vk = kokonaisuudet.findBy(opsId, kokonaisuusId);
        OpsVuosiluokkakokonaisuus ovk = new OpsVuosiluokkakokonaisuus(vk, isOma);
        return mapper.map(ovk, OpsVuosiluokkakokonaisuusDto.class);
    }

    @Override
    public OpsVuosiluokkakokonaisuusDto getPohjanVuosiluokkakokonaisuus(Long opsId, UUID tunniste) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);

        if (ops != null && ops.getPohja() != null) {
            Vuosiluokkakokonaisuus pohjanVuosiluokkakokonaisuus = kokonaisuudet.findByOpetussuunnitelmaIdAndTunniste(ops.getPohja().getId(), tunniste);
            if (pohjanVuosiluokkakokonaisuus != null) {
                return mapper.map(new OpsVuosiluokkakokonaisuus(pohjanVuosiluokkakokonaisuus, false), OpsVuosiluokkakokonaisuusDto.class);
            }
        }

        return null;
    }

    @Override
    public void delete(Long opsId, Long kokonaisuusId) {
        Vuosiluokkakokonaisuus vk = kokonaisuudet.findBy(opsId, kokonaisuusId);
        if (vk != null) {
            Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
            ops.removeVuosiluokkakokonaisuus(vk);
            if (!kokonaisuudet.isInUse(kokonaisuusId)) {
                kokonaisuudet.delete(vk);
            }

            muokkaustietoService.addOpsMuokkausTieto(opsId, vk, MuokkausTapahtuma.POISTO);
        }
    }

    @Override
    public void removeSisaltoalueetInKeskeinensisaltoalueet(
            Opetussuunnitelma opetussuunnitelma,
            Oppiaineenvuosiluokkakokonaisuus vuosiluokkakokonaisuus,
            boolean clearSisaltoalueet
    ) {
        vuosiluokkakokonaisuus.getVuosiluokat().forEach(oppiaineenvuosiluokka -> {
            oppiaineenvuosiluokka.getTavoitteet().forEach(opetuksentavoite -> {
                opetuksentavoite.getSisaltoalueet().forEach(opetuksenKeskeinensisaltoalue -> {
                    opetuksenkeskeinenSisaltoalueRepository.delete(opetuksenKeskeinensisaltoalue);
                });
                if (clearSisaltoalueet) {
                    opetuksentavoite.getSisaltoalueet().clear();
                }
            });
        });
    }

    @Override
    public OpsVuosiluokkakokonaisuusDto update(Long opsId, VuosiluokkakokonaisuusDto dto) {
        Boolean isOma = kokonaisuudet.isOma(opsId, dto.getId());
        if (isOma == null) {
            throw new BusinessRuleViolationException("Vuosiluokkakokonaisuutta ei ole");
        } else if (!isOma) {
            throw new BusinessRuleViolationException("Lainattua vuosiluokkakokonaisuutta ei voi muokata");
        }

        final Vuosiluokkakokonaisuus vk = kokonaisuudet.findBy(opsId, dto.getId());
        mapper.map(dto, vk);
        OpsVuosiluokkakokonaisuus ovk = new OpsVuosiluokkakokonaisuus(vk, isOma);

        muokkaustietoService.addOpsMuokkausTieto(opsId, vk, MuokkausTapahtuma.PAIVITYS);
        return mapper.map(ovk, OpsVuosiluokkakokonaisuusDto.class);
    }

    @Override
    public OpsVuosiluokkakokonaisuusDto kopioiMuokattavaksi(Long opsId, Long kokonaisuusId) {
        Boolean isOma = kokonaisuudet.isOma(opsId, kokonaisuusId);
        if (isOma == null) {
            throw new BusinessRuleViolationException("Vuosiluokkakokonaisuutta ei ole");
        } else if (isOma) {
            throw new BusinessRuleViolationException("Vuosiluokkakokonaisuus on jo muokattavissa");
        }

        Vuosiluokkakokonaisuus vk = kokonaisuudet.findBy(opsId, kokonaisuusId);
        if (vk == null) {
            throw new BusinessRuleViolationException("Päivitettävää vuosiluokkakokonaisuutta ei ole olemassa");
        }

        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);

        Set<OpsVuosiluokkakokonaisuus> opsVlkt =
                ops.getVuosiluokkakokonaisuudet().stream()
                        .filter(vlk -> !vlk.getVuosiluokkakokonaisuus().getId().equals(kokonaisuusId))
                        .collect(Collectors.toSet());

        vk = Vuosiluokkakokonaisuus.copyOf(vk);
        vk = kokonaisuudet.save(vk);
        OpsVuosiluokkakokonaisuus kopio = new OpsVuosiluokkakokonaisuus(vk, true);

        opsVlkt.add(kopio);
        ops.setVuosiluokkakokonaisuudet(opsVlkt);

        return mapper.map(kopio, OpsVuosiluokkakokonaisuusDto.class);
    }

    @Override
    public void piilotaOppiaine(Long opsId, Long oppiaineId, Long vuosiluokkakokonaisuusId, boolean piilota) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        OpsVuosiluokkakokonaisuus opsVuosiluokkakokonaisuus = ops.getVuosiluokkakokonaisuudet().stream()
                .filter(vlk -> vlk.getVuosiluokkakokonaisuus().getId().equals(vuosiluokkakokonaisuusId))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleViolationException("Pyydettyä oppiaineen vuosiluokkakokonaisuutta ei löydy"));

        if (opsVuosiluokkakokonaisuus.getLisatieto() == null) {
            opsVuosiluokkakokonaisuus.setLisatieto(new OpsVuosiluokkakokonaisuusLisatieto());
        }

        if (piilota) {
            opsVuosiluokkakokonaisuus.getLisatieto().getPiilotetutOppiaineet().add(oppiaineId);
        } else {
            opsVuosiluokkakokonaisuus.getLisatieto().getPiilotetutOppiaineet().remove(oppiaineId);
        }
    }
}
