package fi.vm.sade.eperusteet.ylops.service.tpo.impl;

import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtumaAuditointitiedoilla;
import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.VapaatekstiPaikallinentarkennus;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.tpo.TaiteenOpintokokonaisuus;
import fi.vm.sade.eperusteet.ylops.domain.tpo.Taiteenala;
import fi.vm.sade.eperusteet.ylops.domain.tpo.TaiteenalanVaihe;
import fi.vm.sade.eperusteet.ylops.domain.tpo.TpoSisalto;
import fi.vm.sade.eperusteet.ylops.dto.ops.VapaatekstiPaikallinentarkennusDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.TPOOpetuksenSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.aipe.PerusteKevytTekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.PerusteTavoiteAlueDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoPerusteenTaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoPerusteenTaiteenalaVaiheDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenOpintokokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalaKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalanVaiheDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.export.TaiteenalaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.export.TpoSisaltoExportDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.tpo.TaiteenOpintokokonaisuusRepository;
import fi.vm.sade.eperusteet.ylops.repository.tpo.TaiteenalaRepository;
import fi.vm.sade.eperusteet.ylops.repository.tpo.TaiteenalanVaiheRepository;
import fi.vm.sade.eperusteet.ylops.repository.tpo.TpoSisaltoRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteenLokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteenOsaDto;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmanMuokkaustietoService;
import fi.vm.sade.eperusteet.ylops.service.tpo.TaiteenperusopetusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;

@Service
@Transactional
public class TaiteenperusopetusServiceImpl implements TaiteenperusopetusService {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private TpoSisaltoRepository tpoSisaltoRepository;

    @Autowired
    private TaiteenalaRepository taiteenalaRepository;

    @Autowired
    private TaiteenalanVaiheRepository vaiheRepository;

    @Autowired
    private TaiteenOpintokokonaisuusRepository opintokokonaisuusRepository;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmanMuokkaustietoService muokkaustietoService;

    @Override
    public List<TpoPerusteenTaiteenalaDto> getPerusteTaiteenalat(Long opsId) {
        return getPerusteTaiteenalat(getOps(opsId));
    }

    @Override
    public TpoPerusteenTaiteenalaDto getPerusteTaiteenala(Long opsId, Long perusteenTaiteenalaId) {
        return assertExists(findPerusteTaiteenala(getPerusteTaiteenalat(opsId), perusteenTaiteenalaId),
                "Perusteen taiteenalaa ei löytynyt");
    }

    @Override
    public TpoPerusteenTaiteenalaVaiheDto getPerusteVaihe(Long opsId, Long perusteenVaiheId) {
        return assertExists(findPerusteVaihe(getPerusteTaiteenalat(opsId), perusteenVaiheId),
                "Perusteen vaihetta ei löytynyt");
    }

    @Override
    public List<TaiteenalaKevytDto> getTaiteenalat(Long opsId) {
        Opetussuunnitelma ops = getOps(opsId);
        TpoSisalto sisalto = ops.getTpo();
        if (sisalto == null) {
            return new ArrayList<>();
        }
        List<TpoPerusteenTaiteenalaDto> perusteTaiteenalat = getPerusteTaiteenalat(ops);
        sisalto.getTaiteenalat().forEach(ta -> synkronoiVaiheet(ta, findPerusteTaiteenala(perusteTaiteenalat, ta.getPerusteenTaiteenalaId())));
        return sisalto.getTaiteenalat().stream()
                .map(ta -> toKevytDto(ta, perusteTaiteenalat))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaiteenalaKevytDto> addTaiteenalat(Long opsId, List<Long> perusteenTaiteenalaIdt) {
        Opetussuunnitelma ops = getOps(opsId);
        List<TpoPerusteenTaiteenalaDto> perusteTaiteenalat = getPerusteTaiteenalat(ops);
        TpoSisalto sisalto = getOrCreateSisalto(ops);
        for (Long perusteenTaiteenalaId : new LinkedHashSet<>(Optional.ofNullable(perusteenTaiteenalaIdt).orElse(Collections.emptyList()))) {
            TpoPerusteenTaiteenalaDto perusteTaiteenala = findPerusteTaiteenala(perusteTaiteenalat, perusteenTaiteenalaId);
            if (perusteTaiteenala == null) {
                throw new NotExistsException("perusteen-taiteenalaa-ei-loytynyt");
            }
            if (sisalto.hasPerusteenTaiteenala(perusteenTaiteenalaId)) {
                throw new BusinessRuleViolationException("taiteenala-on-jo-lisatty");
            }
            Taiteenala taiteenala = new Taiteenala();
            taiteenala.setPerusteenTaiteenalaId(perusteenTaiteenalaId);
            synkronoiVaiheet(taiteenala, perusteTaiteenala);
            sisalto.addTaiteenala(taiteenala);
            taiteenala = taiteenalaRepository.saveAndFlush(taiteenala);
            addMuokkaustieto(opsId, taiteenala, perusteTaiteenala.getNimi(), MuokkausTapahtuma.LUONTI);
        }
        jarjestaTaiteenalat(sisalto, perusteTaiteenalat);
        return getTaiteenalat(opsId);
    }

    @Override
    public TaiteenalaDto getTaiteenala(Long opsId, Long taiteenalaId) {
        Taiteenala taiteenala = requireTaiteenala(opsId, taiteenalaId);
        TpoPerusteenTaiteenalaDto perusteTaiteenala = findPerusteTaiteenala(getPerusteTaiteenalat(opsId), taiteenala.getPerusteenTaiteenalaId());
        synkronoiVaiheet(taiteenala, perusteTaiteenala);
        return toDto(taiteenala, perusteTaiteenala);
    }

    @Override
    public TaiteenalaDto updateTaiteenala(Long opsId, Long taiteenalaId, TaiteenalaDto dto) {
        Taiteenala taiteenala = requireTaiteenala(opsId, taiteenalaId);
        taiteenala.setPaikallinenTarkennus(mapper.map(dto.getPaikallinenTarkennus(), LokalisoituTeksti.class));
        TpoPerusteenTaiteenalaDto perusteTaiteenala = findPerusteTaiteenala(getPerusteTaiteenalat(opsId), taiteenala.getPerusteenTaiteenalaId());
        addMuokkaustieto(opsId, taiteenala, perusteTaiteenala != null ? perusteTaiteenala.getNimi() : null, MuokkausTapahtuma.PAIVITYS);
        return getTaiteenala(opsId, taiteenalaId);
    }

    @Override
    public void removeTaiteenala(Long opsId, Long taiteenalaId) {
        Taiteenala taiteenala = requireTaiteenala(opsId, taiteenalaId);
        TpoPerusteenTaiteenalaDto perusteTaiteenala = findPerusteTaiteenala(getPerusteTaiteenalat(opsId), taiteenala.getPerusteenTaiteenalaId());
        taiteenala.getSisalto().removeTaiteenala(taiteenala);
        addMuokkaustieto(opsId, taiteenala, perusteTaiteenala != null ? perusteTaiteenala.getNimi() : null, MuokkausTapahtuma.POISTO);
    }

    @Override
    public TaiteenalanVaiheDto getVaihe(Long opsId, Long vaiheId) {
        TaiteenalanVaihe vaihe = requireVaihe(opsId, vaiheId);
        return toDto(vaihe, findPerusteVaihe(getPerusteTaiteenalat(opsId), vaihe.getPerusteenTaiteenalaVaiheId()));
    }

    @Override
    public TaiteenalanVaiheDto updateVaihe(Long opsId, Long vaiheId, TaiteenalanVaiheDto dto) {
        TaiteenalanVaihe vaihe = requireVaihe(opsId, vaiheId);
        TpoPerusteenTaiteenalaVaiheDto perusteVaihe = findPerusteVaihe(getPerusteTaiteenalat(opsId), vaihe.getPerusteenTaiteenalaVaiheId());
        Set<Long> perusteVapaatTekstit = perusteVaihe != null
                ? perusteVaihe.getVapaatTekstit().stream().map(PerusteKevytTekstiKappaleDto::getId).collect(Collectors.toSet())
                : Collections.emptySet();

        for (VapaatekstiPaikallinentarkennusDto vtDto : Optional.ofNullable(dto.getVapaatTekstit()).orElse(Collections.emptyList())) {
            if (vtDto.getPerusteenVapaaTekstiId() == null) {
                continue;
            }
            VapaatekstiPaikallinentarkennus vt = vaihe.getVapaatTekstit().stream()
                    .filter(v -> Objects.equals(v.getPerusteenVapaaTekstiId(), vtDto.getPerusteenVapaaTekstiId()))
                    .findFirst()
                    .orElse(null);
            if (vt == null) {
                if (!perusteVapaatTekstit.contains(vtDto.getPerusteenVapaaTekstiId())) {
                    continue;
                }
                vt = new VapaatekstiPaikallinentarkennus();
                vt.setPerusteenVapaaTekstiId(vtDto.getPerusteenVapaaTekstiId());
                vaihe.getVapaatTekstit().add(vt);
            }
            vt.setPaikallinenTarkennus(mapper.map(vtDto.getPaikallinenTarkennus(), LokalisoituTeksti.class));
        }

        addMuokkaustieto(opsId, vaihe, perusteVaihe != null ? perusteVaihe.getNimi() : null, MuokkausTapahtuma.PAIVITYS);
        return getVaihe(opsId, vaiheId);
    }

    @Override
    public TaiteenOpintokokonaisuusDto addOpintokokonaisuus(Long opsId, Long vaiheId, TaiteenOpintokokonaisuusDto dto) {
        TaiteenalanVaihe vaihe = requireVaihe(opsId, vaiheId);
        TpoPerusteenTaiteenalaVaiheDto perusteVaihe = findPerusteVaihe(getPerusteTaiteenalat(opsId), vaihe.getPerusteenTaiteenalaVaiheId());
        TaiteenOpintokokonaisuus opintokokonaisuus = new TaiteenOpintokokonaisuus();
        applyOpintokokonaisuus(opintokokonaisuus, dto, perusteVaihe);
        vaihe.addOpintokokonaisuus(opintokokonaisuus);
        opintokokonaisuus = opintokokonaisuusRepository.saveAndFlush(opintokokonaisuus);
        addMuokkaustieto(opsId, opintokokonaisuus, MuokkausTapahtuma.LUONTI);
        return toDto(opintokokonaisuus);
    }

    @Override
    public TaiteenOpintokokonaisuusDto getOpintokokonaisuus(Long opsId, Long opintokokonaisuusId) {
        return toDto(requireOpintokokonaisuus(opsId, opintokokonaisuusId));
    }

    @Override
    public TaiteenOpintokokonaisuusDto updateOpintokokonaisuus(Long opsId, Long opintokokonaisuusId, TaiteenOpintokokonaisuusDto dto) {
        TaiteenOpintokokonaisuus opintokokonaisuus = requireOpintokokonaisuus(opsId, opintokokonaisuusId);
        TpoPerusteenTaiteenalaVaiheDto perusteVaihe = findPerusteVaihe(getPerusteTaiteenalat(opsId),
                opintokokonaisuus.getVaihe().getPerusteenTaiteenalaVaiheId());
        applyOpintokokonaisuus(opintokokonaisuus, dto, perusteVaihe);
        addMuokkaustieto(opsId, opintokokonaisuus, MuokkausTapahtuma.PAIVITYS);
        return toDto(opintokokonaisuusRepository.saveAndFlush(opintokokonaisuus));
    }

    @Override
    public void removeOpintokokonaisuus(Long opsId, Long opintokokonaisuusId) {
        TaiteenOpintokokonaisuus opintokokonaisuus = requireOpintokokonaisuus(opsId, opintokokonaisuusId);
        opintokokonaisuus.getVaihe().removeOpintokokonaisuus(opintokokonaisuus);
        addMuokkaustieto(opsId, opintokokonaisuus, MuokkausTapahtuma.POISTO);
    }

    @Override
    public TpoSisaltoExportDto getExportSisalto(Long opsId) {
        Opetussuunnitelma ops = getOps(opsId);
        TpoSisaltoExportDto dto = new TpoSisaltoExportDto();
        TpoSisalto sisalto = ops.getTpo();
        if (sisalto == null) {
            return dto;
        }
        List<TpoPerusteenTaiteenalaDto> perusteTaiteenalat = getPerusteTaiteenalat(ops);
        dto.setId(sisalto.getId());
        dto.setTaiteenalat(sisalto.getTaiteenalat().stream()
                .map(ta -> toExportDto(ta, findPerusteTaiteenala(perusteTaiteenalat, ta.getPerusteenTaiteenalaId())))
                .collect(Collectors.toList()));
        return dto;
    }

    private void applyOpintokokonaisuus(TaiteenOpintokokonaisuus opintokokonaisuus,
                                        TaiteenOpintokokonaisuusDto dto,
                                        TpoPerusteenTaiteenalaVaiheDto perusteVaihe) {
        List<Long> tavoitealueIdt = Optional.ofNullable(dto.getTavoitealueIdt()).orElse(Collections.emptyList()).stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Set<Long> perusteenTavoitealueet = perusteVaihe != null
                ? perusteVaihe.getTavoitealueet().stream().map(PerusteTavoiteAlueDto::getId).collect(Collectors.toSet())
                : Collections.emptySet();
        boolean tuntematon = tavoitealueIdt.stream()
                .filter(id -> !opintokokonaisuus.getTavoitealueIdt().contains(id))
                .anyMatch(id -> !perusteenTavoitealueet.contains(id));
        if (tuntematon) {
            throw new BusinessRuleViolationException("tavoitealue-ei-kuulu-vaiheeseen");
        }

        dto.setId(opintokokonaisuus.getId());
        mapper.map(dto, opintokokonaisuus);
        opintokokonaisuus.setTavoitealueIdt(tavoitealueIdt);
    }

    private void synkronoiVaiheet(Taiteenala taiteenala, TpoPerusteenTaiteenalaDto perusteTaiteenala) {
        if (perusteTaiteenala == null) {
            return;
        }
        List<TpoPerusteenTaiteenalaVaiheDto> perusteVaiheet = perusteTaiteenala.getVaiheet();
        boolean lisatty = false;
        for (TpoPerusteenTaiteenalaVaiheDto perusteVaihe : perusteVaiheet) {
            boolean loytyy = taiteenala.getVaiheet().stream()
                    .anyMatch(v -> Objects.equals(v.getPerusteenTaiteenalaVaiheId(), perusteVaihe.getId()));
            if (!loytyy) {
                taiteenala.addVaihe(fromPeruste(perusteVaihe));
                lisatty = true;
            }
        }
        if (lisatty) {
            Map<Long, Integer> jarjestys = indeksit(perusteVaiheet.stream().map(TpoPerusteenTaiteenalaVaiheDto::getId).collect(Collectors.toList()));
            taiteenala.getVaiheet().sort(Comparator.comparingInt(v -> jarjestys.getOrDefault(v.getPerusteenTaiteenalaVaiheId(), Integer.MAX_VALUE)));
            taiteenala.updateVaiheetOrder();
            if (taiteenala.getId() != null) {
                taiteenalaRepository.saveAndFlush(taiteenala);
            }
        }
    }

    private void jarjestaTaiteenalat(TpoSisalto sisalto, List<TpoPerusteenTaiteenalaDto> perusteTaiteenalat) {
        Map<Long, Integer> jarjestys = indeksit(perusteTaiteenalat.stream().map(TpoPerusteenTaiteenalaDto::getId).collect(Collectors.toList()));
        sisalto.getTaiteenalat().sort(Comparator.comparingInt(t -> jarjestys.getOrDefault(t.getPerusteenTaiteenalaId(), Integer.MAX_VALUE)));
        sisalto.updateTaiteenalatOrder();
    }

    private Map<Long, Integer> indeksit(List<Long> idt) {
        Map<Long, Integer> result = new HashMap<>();
        for (int i = 0; i < idt.size(); i++) {
            result.put(idt.get(i), i);
        }
        return result;
    }

    private TaiteenalanVaihe fromPeruste(TpoPerusteenTaiteenalaVaiheDto perusteVaihe) {
        TaiteenalanVaihe vaihe = new TaiteenalanVaihe();
        vaihe.setPerusteenTaiteenalaVaiheId(perusteVaihe.getId());
        perusteVaihe.getVapaatTekstit().stream()
                .filter(vt -> vt.getId() != null)
                .forEach(vt -> {
                    VapaatekstiPaikallinentarkennus tarkennus = new VapaatekstiPaikallinentarkennus();
                    tarkennus.setPerusteenVapaaTekstiId(vt.getId());
                    vaihe.getVapaatTekstit().add(tarkennus);
                });
        return vaihe;
    }

    private TaiteenalaKevytDto toKevytDto(Taiteenala taiteenala, List<TpoPerusteenTaiteenalaDto> perusteTaiteenalat) {
        TpoPerusteenTaiteenalaDto perusteTaiteenala = findPerusteTaiteenala(perusteTaiteenalat, taiteenala.getPerusteenTaiteenalaId());
        TaiteenalaKevytDto dto = mapper.map(taiteenala, TaiteenalaKevytDto.class);
        dto.setNimi(nimi(perusteTaiteenala));
        dto.getVaiheet().forEach(vaihe -> vaihe.setNimi(nimi(findById(perusteVaiheet(perusteTaiteenala), vaihe.getPerusteenTaiteenalaVaiheId()))));
        return dto;
    }

    private TaiteenalaDto toDto(Taiteenala taiteenala, TpoPerusteenTaiteenalaDto perusteTaiteenala) {
        TaiteenalaDto dto = mapper.map(taiteenala, TaiteenalaDto.class);
        dto.setNimi(nimi(perusteTaiteenala));
        dto.setPerusteenTaiteenala(perusteTaiteenala);
        dto.getVaiheet().forEach(vaihe -> vaihe.setNimi(nimi(findById(perusteVaiheet(perusteTaiteenala), vaihe.getPerusteenTaiteenalaVaiheId()))));
        return dto;
    }

    private TaiteenalanVaiheDto toDto(TaiteenalanVaihe vaihe, TpoPerusteenTaiteenalaVaiheDto perusteVaihe) {
        TaiteenalanVaiheDto dto = mapper.map(vaihe, TaiteenalanVaiheDto.class);
        dto.setNimi(nimi(perusteVaihe));
        dto.setPerusteenVaihe(perusteVaihe);
        dto.setVapaatTekstit(perusteenJarjestyksessa(dto.getVapaatTekstit(), perusteVaihe));
        return dto;
    }

    private TaiteenOpintokokonaisuusDto toDto(TaiteenOpintokokonaisuus opintokokonaisuus) {
        return mapper.map(opintokokonaisuus, TaiteenOpintokokonaisuusDto.class);
    }

    private TaiteenalaExportDto toExportDto(Taiteenala taiteenala, TpoPerusteenTaiteenalaDto perusteTaiteenala) {
        TaiteenalaExportDto dto = mapper.map(taiteenala, TaiteenalaExportDto.class);
        dto.setNimi(nimi(perusteTaiteenala));
        dto.setPerusteenTaiteenala(perusteTaiteenala);
        dto.getVaiheet().forEach(vaihe -> {
            TpoPerusteenTaiteenalaVaiheDto perusteVaihe = findById(perusteVaiheet(perusteTaiteenala), vaihe.getPerusteenTaiteenalaVaiheId());
            vaihe.setNimi(nimi(perusteVaihe));
            vaihe.setPerusteenVaihe(perusteVaihe);
            vaihe.setVapaatTekstit(perusteenJarjestyksessa(vaihe.getVapaatTekstit(), perusteVaihe));
            vaihe.getOpintokokonaisuudet().forEach(ok -> ok.setTavoitealueet(perusteVaihe != null
                    ? perusteVaihe.getTavoitealueet().stream()
                            .filter(tavoitealue -> ok.getTavoitealueIdt().contains(tavoitealue.getId()))
                            .collect(Collectors.toList())
                    : Collections.emptyList()));
        });
        return dto;
    }

    /**
     * Perusteen vapaat tekstit perusteen järjestyksessä. Puuttuvat tarkennukset palautetaan tyhjinä, jotta perusteeseen
     * myöhemmin lisätyt vapaat tekstit voidaan tarkentaa.
     */
    private List<VapaatekstiPaikallinentarkennusDto> perusteenJarjestyksessa(List<VapaatekstiPaikallinentarkennusDto> tarkennukset,
                                                                              TpoPerusteenTaiteenalaVaiheDto perusteVaihe) {
        if (perusteVaihe == null) {
            return tarkennukset;
        }
        return perusteVaihe.getVapaatTekstit().stream()
                .filter(vt -> vt.getId() != null)
                .map(vt -> tarkennukset.stream()
                        .filter(v -> Objects.equals(v.getPerusteenVapaaTekstiId(), vt.getId()))
                        .findFirst()
                        .orElseGet(() -> new VapaatekstiPaikallinentarkennusDto(vt.getId(), null)))
                .collect(Collectors.toList());
    }

    private List<TpoPerusteenTaiteenalaVaiheDto> perusteVaiheet(TpoPerusteenTaiteenalaDto perusteTaiteenala) {
        return perusteTaiteenala != null ? perusteTaiteenala.getVaiheet() : Collections.emptyList();
    }

    private LokalisoituTekstiDto nimi(PerusteenOsaDto perusteenOsa) {
        return perusteenOsa != null ? mapper.map(perusteenOsa.getNimi(), LokalisoituTekstiDto.class) : null;
    }

    private void addMuokkaustieto(Long opsId, HistoriaTapahtuma kohde, PerusteenLokalisoituTekstiDto nimi, MuokkausTapahtuma tapahtuma) {
        muokkaustietoService.addOpsMuokkausTieto(opsId,
                new HistoriaTapahtumaAuditointitiedoilla(kohde.getId(),
                        nimi != null && nimi.getTekstit() != null ? LokalisoituTeksti.of(nimi.getTekstit()) : null,
                        kohde.getNavigationType()),
                tapahtuma);
    }

    private void addMuokkaustieto(Long opsId, TaiteenOpintokokonaisuus kohde, MuokkausTapahtuma tapahtuma) {
        muokkaustietoService.addOpsMuokkausTieto(opsId,
                new HistoriaTapahtumaAuditointitiedoilla(kohde.getId(), kohde.getNimi(), kohde.getNavigationType()),
                tapahtuma);
    }

    private Opetussuunnitelma getOps(Long opsId) {
        return assertExists(opetussuunnitelmaRepository.findOne(opsId), "Pyydettyä opetussuunnitelmaa ei ole olemassa");
    }

    private TpoSisalto getOrCreateSisalto(Opetussuunnitelma ops) {
        TpoSisalto sisalto = ops.getTpo();
        if (sisalto == null) {
            sisalto = new TpoSisalto();
            sisalto.setOpetussuunnitelma(ops);
            sisalto = tpoSisaltoRepository.save(sisalto);
            ops.setTpo(sisalto);
        }
        return sisalto;
    }

    private List<TpoPerusteenTaiteenalaDto> getPerusteTaiteenalat(Opetussuunnitelma ops) {
        PerusteDto peruste = eperusteetService.getPeruste(ops.getPerusteenDiaarinumero());
        TPOOpetuksenSisaltoDto tpo = peruste.getTpo();
        if (tpo == null) {
            throw new NotExistsException("tpo-perustetta-ei-loytynyt");
        }
        return tpo.getTaiteenalat();
    }

    private TpoPerusteenTaiteenalaDto findPerusteTaiteenala(List<TpoPerusteenTaiteenalaDto> taiteenalat, Long id) {
        return findById(taiteenalat, id);
    }

    private TpoPerusteenTaiteenalaVaiheDto findPerusteVaihe(List<TpoPerusteenTaiteenalaDto> taiteenalat, Long id) {
        return taiteenalat.stream()
                .flatMap(ta -> ta.getVaiheet().stream())
                .filter(v -> Objects.equals(v.getId(), id))
                .findFirst()
                .orElse(null);
    }

    private <T extends PerusteenOsaDto> T findById(List<T> lista, Long id) {
        return lista.stream()
                .filter(item -> Objects.equals(item.getId(), id))
                .findFirst()
                .orElse(null);
    }

    private Taiteenala requireTaiteenala(Long opsId, Long taiteenalaId) {
        return assertExists(taiteenalaRepository.findByIdAndOpetussuunnitelmaId(taiteenalaId, opsId), "Taiteenalaa ei löytynyt");
    }

    private TaiteenalanVaihe requireVaihe(Long opsId, Long vaiheId) {
        return assertExists(vaiheRepository.findByIdAndOpetussuunnitelmaId(vaiheId, opsId), "Vaihetta ei löytynyt");
    }

    private TaiteenOpintokokonaisuus requireOpintokokonaisuus(Long opsId, Long opintokokonaisuusId) {
        return assertExists(opintokokonaisuusRepository.findByIdAndOpetussuunnitelmaId(opintokokonaisuusId, opsId),
                "Opintokokonaisuutta ei löytynyt");
    }
}
