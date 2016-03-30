/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import com.codepoetics.protonpack.StreamUtils;
import fi.vm.sade.eperusteet.ylops.domain.LaajaalainenosaaminenViite;
import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokka;
import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokkakokonaisuusviite;
import fi.vm.sade.eperusteet.ylops.domain.lukio.LukioOppiaineJarjestys;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.*;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpsOppiaine;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpsVuosiluokkakokonaisuus;
import fi.vm.sade.eperusteet.ylops.domain.revision.Revision;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Tekstiosa;
import fi.vm.sade.eperusteet.ylops.domain.vuosiluokkakokonaisuus.Vuosiluokkakokonaisuus;
import fi.vm.sade.eperusteet.ylops.dto.RevisionDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.*;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOpetuksentavoiteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOppiaineenVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiosaDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.*;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.exception.LockingException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.locking.AbstractLockService;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsOppiaineCtx;
import fi.vm.sade.eperusteet.ylops.service.ops.VuosiluokkakokonaisuusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * @author mikkom
 */
@Service
@Transactional
public class OppiaineServiceImpl extends AbstractLockService<OpsOppiaineCtx> implements OppiaineService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpsDtoMapper opsDtoMapper;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private OppiaineRepository oppiaineet;

    @Autowired
    private EperusteetService perusteet;

    @Autowired
    private OppiaineenvuosiluokkaRepository vuosiluokat;

    @Autowired
    private VuosiluokkakokonaisuusRepository kokonaisuudet;

    @Autowired
    private LukioOppiaineJarjestysRepository lukioOppiaineJarjestysRepository;

    @Autowired
    private OppiaineLukiokurssiRepository oppiaineLukiokurssiRepository;

    @Autowired
    private OppiaineenvuosiluokkakokonaisuusRepository oppiaineenKokonaisuudet;

    @Autowired
    private VuosiluokkakokonaisuusService vuosiluokkakokonaisuusService;

    @Autowired
    private OpetuksenkeskeinenSisaltoalueRepository opetuksenkeskeinenSisaltoalueRepository;

    @Autowired
    private PoistettuOppiaineRepository poistettuOppiaineRepository;

    public OppiaineServiceImpl() {
    }

    @Override
    public void updateVuosiluokkienTavoitteet(Long opsId, Long oppiaineId, Long vlkId, Map<Vuosiluokka, Set<UUID>> tavoitteet) {
        Oppiaine oppiaine = getOppiaine(opsId, oppiaineId);
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        //TODO:should we use same version of Peruste for with the Opetuusuunnitelma was based on if available?
        PerusteDto peruste = perusteet.getPeruste(ops.getPerusteenDiaarinumero());

        Oppiaineenvuosiluokkakokonaisuus ovk = oppiaine.getVuosiluokkakokonaisuudet().stream()
            .filter(vk -> vk.getId().equals(vlkId))
            .findAny()
            .orElseThrow(() -> new BusinessRuleViolationException("Pyydettyä oppiainetta ei ole opetussuunnitelmassa"));

        PerusteOppiaineenVuosiluokkakokonaisuusDto pov
            = peruste.getPerusopetus().getOppiaine(oppiaine.getTunniste())
            .flatMap(po -> po.getVuosiluokkakokonaisuus(ovk.getVuosiluokkakokonaisuus().getId()))
            .orElseThrow(() -> new BusinessRuleViolationException("Oppiainetta tai vuosiluokkakokonaisuutta ei ole perusteessa"));

        oppiaineet.lock(oppiaine);
        updateVuosiluokkakokonaisuudenTavoitteet(ovk, pov, tavoitteet);

    }

    @Override
    @Transactional(readOnly = true)
    public List<OppiaineDto> getAll(@P("opsId") Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        return mapper.mapAsList(oppiaineet.findByOpsId(opsId), OppiaineDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OppiaineDto> getAll(@P("opsId") Long opsId, boolean valinnaiset) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        Set<Oppiaine> aineet = valinnaiset ?
                               oppiaineet.findValinnaisetByOpsId(opsId) :
                               oppiaineet.findYhteisetByOpsId(opsId);
        return mapper.mapAsList(aineet, OppiaineDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OppiaineDto> getAll(@P("opsId") Long opsId, OppiaineTyyppi tyyppi) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        return mapper.mapAsList(oppiaineet.findByOpsIdAndTyyppi(opsId, tyyppi), OppiaineDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public OpsOppiaineDto get(@P("opsId") Long opsId, Long id) {
        return getOpsOppiaine(opsId, id, null);
    }

    private OpsOppiaineDto getOpsOppiaine(Long opsId, Long id, Integer version) {
        Boolean isOma = oppiaineet.isOma(opsId, id);
        if (isOma == null) {
            throw new BusinessRuleViolationException("Opetussuunnitelmaa tai oppiainetta ei ole.");
        }
        Oppiaine oppiaine = (version == null) ? oppiaineet.findOne(id) : oppiaineet.findRevision(id, version);
        assertExists(oppiaine, "Pyydettyä oppiainetta ei ole olemassa");
        return mapper.map(new OpsOppiaine(oppiaine, isOma), OpsOppiaineDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public OppiaineDto getParent(@P("opsId") Long opsId, Long id) {
        Oppiaine oppiaine = getOppiaine(opsId, id);
        return mapper.map(oppiaine.getOppiaine(), OppiaineDto.class);
    }

    @Override
    public OppiaineLaajaDto add(@P("opsId") Long opsId, OppiaineLaajaDto oppiaineDto) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        opetussuunnitelmaRepository.lock(ops);
        Oppiaine oppiaine = opsDtoMapper.fromDto(oppiaineDto);
        oppiaine = oppiaineet.save(oppiaine);
        ops.addOppiaine(oppiaine);
        return mapper.map(oppiaine, OppiaineLaajaDto.class);
    }

    @Override
    public OppiaineDto addCopyOppimaara(@P("opsId") Long opsId, Long oppiaineId, KopioOppimaaraDto kt) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");

        Opetussuunnitelma opspohja = ops.getAlinPohja();

        Oppiaine parent = oppiaineet.findOne(oppiaineId);
        Oppiaine pohjaparent = oppiaineet.findOneByOpsIdAndTunniste(opspohja.getId(), parent.getTunniste());
        Oppiaine uusi = null;

        if (parent.getKoodiArvo().equalsIgnoreCase("KT") && kt.getTunniste() == null) {
            uusi = Oppiaine.copyOf(pohjaparent, false);
            uusi.setNimi(LokalisoituTeksti.of(kt.getOmaNimi().getTekstit()));
            uusi.setKoosteinen(false);
            uusi.setAbstrakti(false);
            parent.addOppimaara(oppiaineet.save(uusi));
        }
        else {
            for (Oppiaine om : pohjaparent.getOppimaarat()) {
                if (om.getTunniste().equals(kt.getTunniste())) {
                    uusi = Oppiaine.copyOf(om);
                    uusi.setNimi(LokalisoituTeksti.of(kt.getOmaNimi().getTekstit()));
                    parent.addOppimaara(oppiaineet.save(uusi));
                    break;
                }
            }
        }

        assertExists(uusi, "Pyydettyä kielitarjonnan oppiainetta ei ole");
        return mapper.map(uusi, OppiaineDto.class);
    }

    @Override
    public OppiaineDto add(@P("opsId") Long opsId, OppiaineDto oppiaineDto) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        opetussuunnitelmaRepository.lock(ops);
        Oppiaine oppiaine = opsDtoMapper.fromDto(oppiaineDto);
        oppiaine = oppiaineet.save(oppiaine);
        ops.addOppiaine(oppiaine);
        return mapper.map(oppiaine, OppiaineDto.class);
    }

    @Override
    public OppiaineDto addValinnainen(@P("opsId") Long opsId, OppiaineDto oppiaineDto, Long vlkId,
                                      Set<Vuosiluokka> vuosiluokat, List<TekstiosaDto> tavoitteetDto,
                                      Integer oldJnro) {

        OppiaineenVuosiluokkakokonaisuusDto oavlktDto =
            oppiaineDto.getVuosiluokkakokonaisuudet().stream().findFirst().get();
        oppiaineDto.setVuosiluokkakokonaisuudet(Collections.emptySet());

        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        opetussuunnitelmaRepository.lock(ops);
        Oppiaine oppiaine = opsDtoMapper.fromDto(oppiaineDto, true);
        oppiaine = oppiaineet.save(oppiaine);
        ops.addOppiaine(oppiaine);

        Vuosiluokkakokonaisuus vlk = kokonaisuudet.findBy(opsId, vlkId);
        assertExists(vlk, "Pyydettyä vuosiluokkakokonaisuutta ei ole olemassa");

        Oppiaineenvuosiluokkakokonaisuus oavlk = new Oppiaineenvuosiluokkakokonaisuus();
        oavlk.setVuosiluokkakokonaisuus(vlk.getTunniste());
        oavlk.setTehtava(mapper.map(oavlktDto.getTehtava(), Tekstiosa.class));
        oavlk.setYleistavoitteet(mapper.map(oavlktDto.getYleistavoitteet(), Tekstiosa.class));
        oavlk.setTyotavat(mapper.map(oavlktDto.getTyotavat(), Tekstiosa.class));
        oavlk.setOhjaus(mapper.map(oavlktDto.getOhjaus(), Tekstiosa.class));
        oavlk.setArviointi(mapper.map(oavlktDto.getArviointi(), Tekstiosa.class));

        if( oldJnro != null ){
            oavlk.setJnro(oldJnro);
        }else{
            Optional<Integer> maxJnroInOps = getMaxJarjestysnumero(ops);
            Integer jnro = maxJnroInOps.isPresent() ? maxJnroInOps.get()+1 : 0;
            oavlk.setJnro(jnro);
        }

        oavlk.setVuosiluokat(luoOppiaineenVuosiluokat(vuosiluokat, tavoitteetDto));
        oppiaine.addVuosiluokkaKokonaisuus(oavlk);

        oppiaine = oppiaineet.save(oppiaine);

        return mapper.map(oppiaine, OppiaineDto.class);
    }

    private Optional<Integer> getMaxJarjestysnumero(Opetussuunnitelma ops) {
        return ops.getOppiaineet()
                .stream()
                .map(OpsOppiaine::getOppiaine)
                .map(Oppiaine::getVuosiluokkakokonaisuudet)
                .flatMap(l -> l.stream())
                .map(Oppiaineenvuosiluokkakokonaisuus::getJnro)
                .filter(integer -> integer != null)
                .max(Integer::compareTo);
    }

    @Override
    public OppiaineDto updateValinnainen(@P("opsId") Long opsId, OppiaineDto oppiaineDto, Long vlkId,
                                         Set<Vuosiluokka> vuosiluokat, List<TekstiosaDto> tavoitteetDto) {
        Oppiaine oppiaine = getOppiaine(opsId, oppiaineDto.getId());
        assertExists(oppiaine, "Päivitettävää oppiainetta ei ole olemassa");

        Integer oldJnro = oppiaine.getVuosiluokkakokonaisuudet().stream().findFirst().get().getJnro();
        PoistettuOppiaineDto deleted = delete(opsId, oppiaineDto.getId());
        poistettuOppiaineRepository.delete(deleted.getId());

        return addValinnainen(opsId, oppiaineDto, vlkId, vuosiluokat, tavoitteetDto, oldJnro);
    }

    private Oppiaine latestNotNull(Long oppiaineId) {
        List<Revision> revisions = oppiaineet.getRevisions(oppiaineId).stream()
                .sorted((a, b) -> Long.compare(a.getNumero(), b.getNumero()))
                .collect(Collectors.toList());
        Collections.reverse(revisions);

        for (Revision revision : revisions) {
            Oppiaine last = oppiaineet.findRevision(oppiaineId, revision.getNumero());
            if (last != null) {
                return last;
            }
        }
        return null;
    }

    @Override
    public List<OppiaineLaajaDto> getAllVersions(Long opsId, Long oppiaineId) {
        return oppiaineet.getRevisions(oppiaineId).stream()
                .sorted((a, b) -> Long.compare(a.getNumero(), b.getNumero()))
                .map((revision) -> oppiaineet.findRevision(oppiaineId, revision.getNumero()))
                .filter(Objects::nonNull)
                .map((oppiaine) -> mapper.map(oppiaine, OppiaineLaajaDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public OppiainePalautettuDto restore(Long opsId, Long oppiaineId, Long oppimaaraId) {
        PoistettuOppiaine poistettu = poistettuOppiaineRepository.findOne(oppiaineId);
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);

        if (oppiaineet.findOne(poistettu.getOppiaine()) != null) {
            throw new BusinessRuleViolationException("Oppiaine olemassa, ei tarvitse palauttaa.");
        }

        Oppiaine latest = latestNotNull(poistettu.getOppiaine());
        OppiaineLaajaDto oppiaine = mapper.map(latest, OppiaineLaajaDto.class);
        Oppiaine pelastettu = Oppiaine.copyOf(opsDtoMapper.fromDto(oppiaine), true);
        pelastettu.setTyyppi( oppiaine.getTyyppi() );
        pelastettu.setLaajuus( oppiaine.getLaajuus() );

        if( oppimaaraId != null){
            Oppiaine parent = oppiaineet.findOne(oppimaaraId);
            parent.addOppimaara(pelastettu);
        }else{
            ops.addOppiaine(pelastettu);
        }

        pelastettu = oppiaineet.save(pelastettu);
        poistettu.setPalautettu(true);

        Optional<Vuosiluokkakokonaisuus> firstVlk = findFirstVlk(ops, pelastettu);
        OppiainePalautettuDto palautettuDto = mapper.map(pelastettu, OppiainePalautettuDto.class);
        if(firstVlk.isPresent()){
            palautettuDto.setVlkId(firstVlk.get().getId());
        }

        return palautettuDto;
    }

    private Optional<Vuosiluokkakokonaisuus> findFirstVlk(Opetussuunnitelma ops, Oppiaine pelastettu) {
        List<UUID> vlk = pelastettu.getVuosiluokkakokonaisuudet()
                .stream()
                .map(Oppiaineenvuosiluokkakokonaisuus::getVuosiluokkakokonaisuus)
                .map(Vuosiluokkakokonaisuusviite::getId)
                .collect(Collectors.toList());

        return (Optional<Vuosiluokkakokonaisuus>) ops.getVuosiluokkakokonaisuudet()
                .stream()
                .map(OpsVuosiluokkakokonaisuus::getVuosiluokkakokonaisuus)
                .filter(vuosiluokkakokonaisuus -> vlk.contains(vuosiluokkakokonaisuus.getTunniste().getId()))
                .findFirst();
    }

    @Override
    public OpsOppiaineDto kopioiMuokattavaksi(@P("opsId") Long opsId, Long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        opetussuunnitelmaRepository.lock(ops);

        Boolean isOma = oppiaineet.isOma(opsId, id);
        if (isOma == null) {
            throw new BusinessRuleViolationException("Kopioitavaa oppiainetta ei ole olemassa");
        } else if (isOma) {
            throw new BusinessRuleViolationException("Oppiaine on jo muokattavissa");
        }

        Oppiaine oppiaine = getOppiaine(opsId, id);

        if (oppiaine.getOppiaine() != null) {
            throw new BusinessRuleViolationException("Oppimäärää ei voi kopioida");
        }

        Set<OpsOppiaine> opsOppiaineet = ops.getOppiaineet().stream()
                .filter(oa -> !oa.getOppiaine().getId().equals(id))
                .collect(Collectors.toSet());

        Oppiaine newOppiaine = oppiaineet.save(Oppiaine.copyOf(oppiaine));
        OpsOppiaine kopio = new OpsOppiaine(newOppiaine, true);
        opsOppiaineet.add(kopio);
        ops.setOppiaineet(opsOppiaineet);
        if (ops.getKoulutustyyppi().isLukio()) {
            newOppiaine.setAbstrakti(oppiaine.getAbstrakti());
            remapLukiokurssit(ops, oppiaine, newOppiaine);

            updateLukioJarjestyksetOnOpsOppaineRefChange(ops, oppiaine, newOppiaine);
        }

        return mapper.map(kopio, OpsOppiaineDto.class);
    }

    private Set<Oppiaineenvuosiluokka> luoOppiaineenVuosiluokat(Set<Vuosiluokka> vuosiluokat,
                                                                List<TekstiosaDto> tavoitteetDto) {
        return vuosiluokat.stream().map(Oppiaineenvuosiluokka::new)
                          .map(oavl -> asetaOppiaineenVuosiluokanSisalto(oavl, tavoitteetDto))
                          .collect(Collectors.toSet());
    }

    private Oppiaineenvuosiluokka asetaOppiaineenVuosiluokanSisalto(Oppiaineenvuosiluokka oavl,
                                                                    List<TekstiosaDto> tavoitteetDto) {
        List<Tekstiosa> tavoitteet = mapper.mapAsList(tavoitteetDto, Tekstiosa.class);

        List<Keskeinensisaltoalue> sisaltoalueet = tavoitteet.stream()
            .map(tekstiosa -> {
                Keskeinensisaltoalue k = new Keskeinensisaltoalue();
                k.setTunniste(UUID.randomUUID());
                k.setKuvaus(tekstiosa.getTeksti());
                return k;
            })
            .collect(Collectors.toList());

        List<Keskeinensisaltoalue> oavlSisaltoalueet = sisaltoalueet.stream()
            .map(k -> Keskeinensisaltoalue.copyOf(k))
            .collect(Collectors.toList());

        List<Opetuksentavoite> oavlTavoitteet =
            StreamUtils.zip(tavoitteet.stream(), oavlSisaltoalueet.stream(),
                        (tekstiosa, sisaltoalue) -> {
                            Opetuksentavoite t = new Opetuksentavoite();
                            t.setTunniste(UUID.randomUUID());
                            t.setTavoite(tekstiosa.getOtsikko());
                            t.connectSisaltoalueet(Collections.singleton(sisaltoalue));
                            return t;
                        })
                   .collect(Collectors.toList());

        oavl.getTavoitteet().forEach( opetuksentavoite -> {
            opetuksentavoite.getSisaltoalueet().forEach( opetuksenKeskeinensisaltoalue -> {
                opetuksenkeskeinenSisaltoalueRepository.delete( opetuksenKeskeinensisaltoalue );
            } );
        });

        oavl.setSisaltoalueet(oavlSisaltoalueet);
        oavl.setTavoitteet(oavlTavoitteet);
        return oavl;
    }

    @Override
    public OpsOppiaineDto update(@P("opsId") Long opsId, OppiaineDto oppiaineDto) {
        Boolean isOma = oppiaineet.isOma(opsId, oppiaineDto.getId());
        if (isOma == null) {
            throw new BusinessRuleViolationException("Päivitettävää oppiainetta ei ole olemassa");
        } else if (!isOma) {
            throw new BusinessRuleViolationException("Lainattua oppiainetta ei voi muokata");
        }

        Oppiaine oppiaine = getOppiaine(opsId, oppiaineDto.getId());

        // lockService.assertLock ( opsId ) ... ?
        oppiaineet.lock(oppiaine);

        mapper.map(oppiaineDto, oppiaine);

        oppiaine = oppiaineet.save(oppiaine);
        return mapper.map(new OpsOppiaine(oppiaine, isOma), OpsOppiaineDto.class);
    }

    @Override
    public PoistettuOppiaineDto delete(@P("opsId") Long opsId, Long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        Oppiaine oppiaine = getOppiaine(opsId, id);
        oppiaineet.lock(oppiaine);

        if (oppiaine.getOppiaine() == null) {
            // Jos oppiaine käytössä alempien tasojen OPS:eissa, niin ei onnistu, ellei niitä ensin irroiteta:
            oppiaineet.findOtherOpetussuunnitelmasContainingOpsOppiaine(oppiaine.getId(), ops.getId())
                    .forEach(otherOps -> kopioiMuokattavaksi(otherOps.getId(), oppiaine.getId()));
        }

        if (oppiaine.isKoosteinen()) {
            oppiaine.getOppimaarat().forEach(oppimaara -> delete(opsId, oppimaara.getId()));
        }

        oppiaine.getVuosiluokkakokonaisuudet().forEach(vuosiluokkakokonaisuus -> {
            vuosiluokkakokonaisuusService.removeSisaltoalueetInKeskeinensisaltoalueet(vuosiluokkakokonaisuus, true);
        });

        lukioOppiaineJarjestysRepository.delete(
                lukioOppiaineJarjestysRepository.findByOppiaineIds(oppiaine.maarineen().map(Oppiaine::getId).collect(toSet())));
        oppiaineLukiokurssiRepository.delete(oppiaineLukiokurssiRepository.findByOpsAndOppiaine(opsId, id));

        if (oppiaine.getOppiaine() != null) {
            oppiaine.getOppiaine().removeOppimaara(oppiaine);
        } else {
            ops.removeOppiaine(oppiaine);
        }
        return tallennaPoistettu(id, ops, oppiaine);
    }

    private PoistettuOppiaineDto tallennaPoistettu(Long id, Opetussuunnitelma ops, Oppiaine oppiaine) {
        PoistettuOppiaine poistettu = new PoistettuOppiaine();
        poistettu.setOpetussuunnitelma(ops);
        poistettu.setOppiaine(id);
        poistettu = poistettuOppiaineRepository.save(poistettu);
        oppiaineet.delete(oppiaine);
        return mapper.map(poistettu, PoistettuOppiaineDto.class);
    }

    @Override
    public OppiaineenVuosiluokkaDto getVuosiluokka(Long opsId, Long oppiaineId, Long vuosiluokkaId) {
        Oppiaineenvuosiluokka vl = findVuosiluokka(opsId, oppiaineId, vuosiluokkaId);
        return vl == null ? null : mapper.map(vl, OppiaineenVuosiluokkaDto.class);
    }

    private Oppiaineenvuosiluokka findVuosiluokka(Long opsId, Long oppiaineId, Long vuosiluokkaId) throws BusinessRuleViolationException {
        if (!oppiaineExists(opsId, oppiaineId)) {
            throw new BusinessRuleViolationException("Opetussuunnitelmaa tai oppiainetta ei ole.");
        }
        return vuosiluokat.findByOppiaine(oppiaineId, vuosiluokkaId);
    }

    @Override
    public OppiaineenVuosiluokkakokonaisuusDto updateVuosiluokkakokonaisuudenSisalto(@P("opsId") Long opsId, Long id, OppiaineenVuosiluokkakokonaisuusDto dto) {
        Oppiaine oppiaine = getOppiaine(opsId, id);
        Oppiaineenvuosiluokkakokonaisuus oavlk
            = oppiaine.getVuosiluokkakokonaisuudet().stream()
            .filter(ov -> ov.getId().equals(dto.getId()))
            .findAny()
            .orElseThrow(() -> new BusinessRuleViolationException("Pyydettyä oppiaineen vuosiluokkakokonaisuutta ei löydy"));

        oavlk.setTehtava(mapper.map(dto.getTehtava(), Tekstiosa.class));
        oavlk.setYleistavoitteet(mapper.map(dto.getYleistavoitteet(), Tekstiosa.class));
        oavlk.setTyotavat(mapper.map(dto.getTyotavat(), Tekstiosa.class));
        oavlk.setOhjaus(mapper.map(dto.getOhjaus(), Tekstiosa.class));
        oavlk.setArviointi(mapper.map(dto.getArviointi(), Tekstiosa.class));

        mapper.map(oavlk, dto);
        return dto;
    }

    @Override
    public OppiaineenVuosiluokkaDto updateVuosiluokanSisalto(@P("opsId") Long opsId, Long oppiaineId, OppiaineenVuosiluokkaDto dto) {
        if (!oppiaineet.isOma(opsId, oppiaineId)) {
            throw new BusinessRuleViolationException("vain-omaa-oppiainetta-saa-muokata");
        }

        Oppiaineenvuosiluokka oppiaineenVuosiluokka = assertExists(findVuosiluokka(opsId, oppiaineId, dto.getId()), "Vuosiluokkaa ei löydy");

        // Aseta oppiaineen vuosiluokan sisällöstä vain sisaltoalueiden ja tavoitteiden kuvaukset,
        // noin muutoin sisältöön ei pidä kajoaman
        for (KeskeinenSisaltoalueDto sisaltoalueDto : dto.getSisaltoalueet()) {
            oppiaineenVuosiluokka.getSisaltoalue(sisaltoalueDto.getTunniste())
                    .ifPresent(sa -> {
                        sa.setKuvaus(mapper.map(sisaltoalueDto.getKuvaus(), LokalisoituTeksti.class));
                        sa.setPiilotettu(sisaltoalueDto.getPiilotettu());
                    });
        }

        dto.getTavoitteet().forEach(
            tavoiteDto ->
                oppiaineenVuosiluokka.getTavoite(tavoiteDto.getTunniste())
                                     .ifPresent(t -> t.setTavoite(mapper.map(tavoiteDto.getTavoite(), LokalisoituTeksti.class))));

        dto.getTavoitteet().stream()
                .forEach(opetuksenTavoiteDto -> {
                    opetuksenTavoiteDto.getSisaltoalueet()
                        .forEach(opetuksenKeskeinensisaltoalueDto -> {
                            OpetuksenKeskeinensisaltoalue opetuksenKeskeinensisaltoalue
                                = oppiaineenVuosiluokka.getTavoite(opetuksenTavoiteDto.getTunniste())
                                    .get().getOpetuksenkeskeinenSisaltoalueById(opetuksenKeskeinensisaltoalueDto.getId()).get();

                if (opetuksenKeskeinensisaltoalueDto.getOmaKuvaus() != null) {
                    opetuksenKeskeinensisaltoalue.setOmaKuvaus(mapper.map(opetuksenKeskeinensisaltoalueDto.getOmaKuvaus(), LokalisoituTeksti.class));
                }
                else {
                    opetuksenKeskeinensisaltoalue.setOmaKuvaus(null);
                }
            });
        });

        return mapper.map(oppiaineenVuosiluokka, OppiaineenVuosiluokkaDto.class);
    }

    public OppiaineenVuosiluokkaDto updateValinnaisenVuosiluokanSisalto(@P("opsId") Long opsId, Long id,
                                                                        Long oppiaineenVuosiluokkaId,
                                                                        List<TekstiosaDto> tavoitteetDto) {
        Oppiaineenvuosiluokka oavl = assertExists(findVuosiluokka(opsId, id, oppiaineenVuosiluokkaId), "Vuosiluokkaa ei löydy");

        Oppiaine oppiaine = oppiaineet.findOne(id);
        if (oppiaine.getTyyppi() == OppiaineTyyppi.YHTEINEN) {
            throw new BusinessRuleViolationException("Oppiaine ei ole valinnainen");
        }

        oavl = asetaOppiaineenVuosiluokanSisalto(oavl, tavoitteetDto);
        oavl = vuosiluokat.save(oavl);
        return mapper.map(oavl, OppiaineenVuosiluokkaDto.class);
    }

    @Override
    @Transactional
    public OpsOppiaineDto palautaYlempi(Long opsId, Long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        opetussuunnitelmaRepository.lock(ops);

        Opetussuunnitelma pohja = ops.getPohja();
        if (pohja == null) {
            throw new BusinessRuleViolationException("Ei voi palauttaa jos pohjaa ei ole");
        }

        Oppiaine oppiaine = oppiaineet.findOne(id);
        assertExists(oppiaine, "Pyydettyä oppiainetta ei ole olemassa");

        final UUID tunniste = oppiaine.getTunniste();
        List<OpsOppiaine> pohjanOppiaineet = pohja.getOppiaineet().stream()
                .filter(a -> (a.getOppiaine().getTunniste().compareTo(tunniste) == 0))
                .collect(Collectors.toList());

        if( pohjanOppiaineet.size() != 1){
            throw new BusinessRuleViolationException("Oppiainetta ei löytynyt.");
        }

        Oppiaine opp = pohjanOppiaineet.get(0).getOppiaine();
        OpsOppiaine oldOppiaine = new OpsOppiaine(opp, false);

        Set<OpsOppiaine> opsOppiaineet = ops.getOppiaineet().stream()
                .filter(oa -> !oa.getOppiaine().getId().equals(id))
                .collect(Collectors.toSet());

        opsOppiaineet.add( oldOppiaine );
        ops.setOppiaineet( opsOppiaineet );
        if (ops.getKoulutustyyppi().isLukio()) {
            remapLukiokurssit(ops, oppiaine, opp);

            updateLukioJarjestyksetOnOpsOppaineRefChange(ops, oppiaine, opp);
        }

        return mapper.map( oldOppiaine, OpsOppiaineDto.class );
    }

    @Override
    public Boolean palautettavissa(@P("opsId") Long opsId, Long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        if( ops == null ){
            return false;
        }

        Opetussuunnitelma pohja = ops.getPohja();
        if (pohja == null) {
            return false;
        }

        Oppiaine oppiaine = oppiaineet.findOne(id);
        if (oppiaine == null) {
            return false;
        }

        final UUID tunniste = oppiaine.getTunniste();
        List<OpsOppiaine> pohjanOppiaineet = pohja.getOppiaineet().stream()
                .filter(a -> (a.getOppiaine().getTunniste().compareTo(tunniste) == 0))
                .collect(Collectors.toList());

        return pohjanOppiaineet.size() == 1;
    }

    @Override
    public List<RevisionDto> getVersions(Long opsId, Long id) {
        List<Revision> versions = oppiaineet.getRevisions(id);
        return mapper.mapAsList(versions, RevisionDto.class);
    }

    @Override
    public OpsOppiaineDto getVersion(Long opsId, Long id, Integer versio) {
        return getOpsOppiaine(opsId, id, versio);
    }

    @Override
    public OpsOppiaineDto revertTo(Long opsId, Long id, Integer versio) {
        OpsOppiaineDto vanha = getOpsOppiaine(opsId, id, versio);
        return update(opsId, vanha.getOppiaine());
    }

    @Override
    public List<PoistettuOppiaineDto> getRemoved(Long opsId) {
        List<PoistettuOppiaineDto> poistetut = mapper.mapAsList(poistettuOppiaineRepository.findPoistetutByOpsId(opsId), PoistettuOppiaineDto.class);
        poistetut.forEach(poistettuOppiaine -> {
            Oppiaine latest = latestNotNull(poistettuOppiaine.getOppiaine());
            poistettuOppiaine.setNimi(mapper.map(latest.getNimi(), LokalisoituTekstiDto.class));
            poistettuOppiaine.setOppiaine(latest.getId());
        });
        return mapper.mapAsList(poistetut, PoistettuOppiaineDto.class);
    }

    private Boolean canCopyOppiaine(Long opsId, Long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        if(ops == null){
            return false;
        }

        Boolean isOma = oppiaineet.isOma(opsId, id);
        if (isOma == null) {
            return false;
        } else if (isOma) {
            return false;
        }

        try {
            Oppiaine oppiaine = getOppiaine(opsId, id);
            if (oppiaine.getOppiaine() != null) {
                return false;
            }
        }catch (BusinessRuleViolationException e){
            return false;
        }
        return true;
    }

    private void remapLukiokurssit(Opetussuunnitelma ops, Oppiaine oppiaine, Oppiaine newOppiaine) {
        Map<OppiaineOpsTunniste, Oppiaine> oldByUuid = oppiaine.maarineen()
                .collect(toMap(Oppiaine::getOpsUniikkiTunniste, oa->oa));
        Map<OppiaineOpsTunniste, Oppiaine> newByUuid = newOppiaine.maarineen()
                .collect(toMap(Oppiaine::getOpsUniikkiTunniste, oa->oa));
        ops.getLukiokurssit().removeAll(
            ops.getLukiokurssit().stream()
                .filter(oaLk -> oldByUuid.containsKey(oaLk.getOppiaine().getOpsUniikkiTunniste()))
                .filter(oaLk -> !newByUuid.containsKey(oaLk.getOppiaine().getOpsUniikkiTunniste()))
                .collect(toSet())
        );
        ops.getLukiokurssit().stream()
            .filter(oaLk -> oldByUuid.containsKey(oaLk.getOppiaine().getOpsUniikkiTunniste()))
            .forEach(oaLk -> oaLk.setOppiaine(newByUuid.get(oaLk.getOppiaine().getOpsUniikkiTunniste())));
    }

    private void updateLukioJarjestyksetOnOpsOppaineRefChange(Opetussuunnitelma ops,
                                                              Oppiaine oppiaine, Oppiaine newOppiaine) {
        Map<OppiaineOpsTunniste, LukioOppiaineJarjestys> jarjestykset = lukioOppiaineJarjestysRepository
                .findByOppiaineIds(ops.getId(), oppiaine.maarineen().map(Oppiaine::getId).collect(toSet()))
                .stream().collect(toMap(j -> j.getOppiaine().getOpsUniikkiTunniste(), j->j));
        ops.getOppiaineJarjestykset().removeAll(jarjestykset.values());
        opetussuunnitelmaRepository.flush();
        Map<OppiaineOpsTunniste, Oppiaine> uudetOppiaineJaMaaraByTunniste = newOppiaine.maarineen()
                .collect(toMap(Oppiaine::getOpsUniikkiTunniste, oa -> oa));
        for (Map.Entry<OppiaineOpsTunniste, LukioOppiaineJarjestys> oldJarjestys : jarjestykset.entrySet()) {
            Oppiaine uusiOppiaine = uudetOppiaineJaMaaraByTunniste.get(oldJarjestys.getKey());
            if (uusiOppiaine != null && !ops.getOppiaineJarjestykset().stream()
                    .filter(j -> j.getOppiaine().getId().equals(uusiOppiaine.getId())).findAny().isPresent()) {
                ops.getOppiaineJarjestykset().add(new LukioOppiaineJarjestys(ops,
                        uusiOppiaine, oldJarjestys.getValue().getJarjestys()));
            } // else: esim. tilanne, jossa luotu katkaisun jälkeen oma oppimäärä ja palautuksessa poistettu
        }
    }

    private Oppiaine getOppiaine(Long opsId, Long oppiaineId) {
        if (!oppiaineExists(opsId, oppiaineId)) {
            throw new BusinessRuleViolationException("Opetussuunnitelmaa tai oppiainetta ei ole.");
        }

        Oppiaine oppiaine = oppiaineet.findOne(oppiaineId);
        assertExists(oppiaine, "Pyydettyä oppiainetta ei ole olemassa");
        return oppiaine;
    }

    private void updateVuosiluokkakokonaisuudenTavoitteet(
        Oppiaineenvuosiluokkakokonaisuus v,
        PerusteOppiaineenVuosiluokkakokonaisuusDto vuosiluokkakokonaisuus,
        Map<Vuosiluokka, Set<UUID>> tavoitteet) {

        if (!vuosiluokkakokonaisuus.getVuosiluokkaKokonaisuus().getVuosiluokat().containsAll(tavoitteet.keySet())) {
            throw new BusinessRuleViolationException("Yksi tai useampi vuosiluokka ei kuulu tähän vuosiluokkakokonaisuuteen");
        }

        vuosiluokkakokonaisuusService.removeSisaltoalueetInKeskeinensisaltoalueet(v, false);

        tavoitteet.entrySet().stream()
            .filter(e -> v.getVuosiluokkakokonaisuus().getVuosiluokat().contains(e.getKey()))
            .forEach(e -> {
                Oppiaineenvuosiluokka ov = v.getVuosiluokka(e.getKey()).orElseGet(() -> {
                    Oppiaineenvuosiluokka tmp = new Oppiaineenvuosiluokka(e.getKey());
                    v.addVuosiluokka(tmp);
                    return tmp;
                });
                mergePerusteTavoitteet(ov, vuosiluokkakokonaisuus, e.getValue());
                if (ov.getTavoitteet().isEmpty()) {
                    v.removeVuosiluokka(ov);
                }
            });
    }

    private void mergePerusteTavoitteet(Oppiaineenvuosiluokka ov, PerusteOppiaineenVuosiluokkakokonaisuusDto pvk, Set<UUID> tavoiteIds) {
        List<PerusteOpetuksentavoiteDto> filtered = pvk.getTavoitteet().stream()
            .filter(t -> tavoiteIds.contains(t.getTunniste()))
            .collect(Collectors.toList());

        if (tavoiteIds.size() > filtered.size()) {
            throw new BusinessRuleViolationException("Yksi tai useampi tavoite ei kuulu oppiaineen vuosiluokkakokonaisuuden tavoitteisiin");
        }

        LinkedHashMap<UUID, Keskeinensisaltoalue> alueet = pvk.getSisaltoalueet().stream()
            .filter(s -> filtered.stream().flatMap(t -> t.getSisaltoalueet().stream()).anyMatch(Predicate.isEqual(s)))
            .map(ps -> ov.getSisaltoalue(ps.getTunniste()).orElseGet(() -> {
                Keskeinensisaltoalue k = new Keskeinensisaltoalue();
                k.setTunniste(ps.getTunniste());
                k.setNimi(fromDto(ps.getNimi()));
                // Kuvaus-kenttä on paikaillisesti määritettävää sisältöä joten sitä ei tässä aseteta
                return k;
            }))
            .collect(Collectors.toMap(Keskeinensisaltoalue::getTunniste, k -> k, (u, v) -> u, LinkedHashMap::new));

        ov.setSisaltoalueet(new ArrayList<>(alueet.values()));

        List<Opetuksentavoite> tmp = filtered.stream()
            .map(t -> {
                Opetuksentavoite opst = ov.getTavoite(t.getTunniste()).orElseGet(() -> {
                    Opetuksentavoite uusi = new Opetuksentavoite();
                    // Tavoite-kenttä on paikaillisesti määritettävää sisältöä joten sitä ei tässä aseteta
                    uusi.setTunniste(t.getTunniste());
                    return uusi;
                });
                opst.setLaajattavoitteet(t.getLaajattavoitteet().stream()
                    .map(l -> new LaajaalainenosaaminenViite(l.getTunniste().toString()))
                    .collect(Collectors.toSet()));

                opst.connectSisaltoalueet(t.getSisaltoalueet().stream()
                        .map(s -> alueet.get(s.getTunniste()))
                        .collect(Collectors.toSet()));

                opst.setKohdealueet(t.getKohdealueet().stream()
                        .map(k -> ov.getKokonaisuus().getOppiaine().addKohdealue(new Opetuksenkohdealue(fromDto(k.getNimi()))))
                        .collect(Collectors.toSet()));
                opst.setArvioinninkohteet(t.getArvioinninkohteet().stream()
                    .map(a -> new Tavoitteenarviointi(fromDto(a.getArvioinninKohde()), fromDto(a.getHyvanOsaamisenKuvaus())))
                    .collect(Collectors.toSet()));
                return opst;
            })
            .collect(Collectors.toList());
        ov.setTavoitteet(tmp);
    }

    private LokalisoituTeksti fromDto(LokalisoituTekstiDto dto) {
        if (dto == null) {
            return null;
        }
        return LokalisoituTeksti.of(dto.getTekstit());
    }

    @Override
    protected Long getLockId(OpsOppiaineCtx ctx) {
        if (ctx.getKokonaisuusId() == null) {
            return ctx.getOppiaineId();
        }
        if (ctx.getVuosiluokkaId() == null) {
            return ctx.getKokonaisuusId();
        }
        return ctx.getVuosiluokkaId();
    }

    @Override
    protected int latestRevision(OpsOppiaineCtx ctx) {
        if (ctx.getKokonaisuusId() == null) {
            return oppiaineet.getLatestRevisionId(ctx.getOppiaineId());
        }
        if (ctx.getVuosiluokkaId() == null) {
            return oppiaineenKokonaisuudet.getLatestRevisionId(ctx.getKokonaisuusId());
        }
        return vuosiluokat.getLatestRevisionId(ctx.getVuosiluokkaId());
    }

    @Override
    protected Long validateCtx(OpsOppiaineCtx ctx, boolean readOnly) {
        if (ctx.isValid() && oppiaineExists(ctx.getOpsId(), ctx.getOppiaineId())) {
            if (ctx.isOppiane()) {
                return ctx.getOppiaineId();
            }
            if (ctx.isKokonaisuus() && oppiaineenKokonaisuudet.exists(ctx.getOppiaineId(), ctx.getKokonaisuusId())) {
                return ctx.getKokonaisuusId();
            }
            if (ctx.isVuosiluokka() && vuosiluokat.exists(ctx.getOppiaineId(), ctx.getKokonaisuusId(), ctx.getVuosiluokkaId())) {
                return ctx.getVuosiluokkaId();
            }
        }
        throw new LockingException("Virheellinen lukitus");
    }

    private boolean oppiaineExists(Long opsId, Long oppiaineId) {
        return oppiaineet.isOma(opsId, oppiaineId) != null;
    }
}
