package fi.vm.sade.eperusteet.ylops.service.aipe.impl;

import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtumaAuditointitiedoilla;
import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.aipe.AIPEKurssi;
import fi.vm.sade.eperusteet.ylops.domain.aipe.AIPEOppiaine;
import fi.vm.sade.eperusteet.ylops.domain.aipe.AIPESisalto;
import fi.vm.sade.eperusteet.ylops.domain.aipe.AIPEVaihe;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.dto.KoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEKurssiDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEKurssiKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEOppiaineKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEPerusteVaiheKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPESisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEVaiheDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEVaiheKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.MuokkaustietoLisatieto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.AIPEKurssiExportDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.AIPEOppiaineExportDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.AIPESisaltoExportDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.AIPEVaiheExportDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.PerusteAIPEKurssiSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.PerusteAIPELaajaalainenosaaminenSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.PerusteAIPEOpetuksenkohdealueSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.PerusteAIPEOpetuksentavoiteKevytSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.PerusteAIPEOpetuksentavoiteSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.PerusteAIPEOppiaineSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.PerusteAIPEVaiheSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.AipePerusteenSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteLaajaalainenosaaminenDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOpetuksenkohdealueDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.aipe.PerusteAIPEKurssiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.aipe.PerusteAIPEOpetuksentavoiteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.aipe.PerusteAIPEOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.aipe.PerusteAIPEVaiheDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.repository.aipe.AIPEVaiheRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.aipe.AIPEService;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmanMuokkaustietoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;

@Service
@Transactional
public class AIPEServiceImpl implements AIPEService {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private AIPEVaiheRepository aipeVaiheRepository;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmanMuokkaustietoService muokkaustietoService;

    @Override
    public List<AIPEPerusteVaiheKevytDto> getPerusteVaiheet(Long opsId) {
        AipePerusteenSisaltoDto aipe = getPerusteAipe(opsId);
        return Optional.ofNullable(aipe.getVaiheet()).orElse(Collections.emptyList()).stream()
                .map(v -> new AIPEPerusteVaiheKevytDto(v.getId(), v.getNimi()))
                .collect(Collectors.toList());
    }

    @Override
    public AIPESisaltoDto getSisalto(Long opsId) {
        AIPESisalto sisalto = getOrCreateSisalto(getOps(opsId));
        AipePerusteenSisaltoDto perusteAipe = getPerusteAipe(opsId);
        AIPESisaltoDto dto = new AIPESisaltoDto();
        dto.setId(sisalto.getId());
        dto.setVaiheet(sisalto.getVaiheet().stream()
                .map(v -> toKevyt(v, findPerusteVaihe(perusteAipe, v.getPerusteenVaiheId())))
                .collect(Collectors.toList()));
        return dto;
    }

    @Override
    public AIPESisaltoExportDto getExportSisalto(Long opsId) {
        AIPESisalto sisalto = getOrCreateSisalto(getOps(opsId));
        AipePerusteenSisaltoDto perusteAipe = getPerusteAipe(opsId);
        AIPESisaltoExportDto dto = new AIPESisaltoExportDto();
        dto.setId(sisalto.getId());
        dto.setVaiheet(sisalto.getVaiheet().stream()
                .filter(v -> !v.isPiilotettu())
                .map(v -> toExportDto(v, findPerusteVaihe(perusteAipe, v.getPerusteenVaiheId()), perusteAipe.getLaajaalaisetosaamiset()))
                .collect(Collectors.toList()));
        return dto;
    }

    @Override
    public List<AIPEVaiheKevytDto> getVaiheet(Long opsId) {
        return getSisalto(opsId).getVaiheet();
    }

    @Override
    public AIPEVaiheDto addVaihe(Long opsId, Long perusteenVaiheId) {
        if (perusteenVaiheId == null) {
            throw new BusinessRuleViolationException("perusteen-vaihe-id-puuttuu");
        }
        Opetussuunnitelma ops = getOps(opsId);
        AIPESisalto sisalto = getOrCreateSisalto(ops);
        boolean exists = sisalto.getVaiheet().stream()
                .anyMatch(v -> perusteenVaiheId.equals(v.getPerusteenVaiheId()));
        if (exists) {
            throw new BusinessRuleViolationException("vaihe-on-jo-lisatty");
        }
        AipePerusteenSisaltoDto perusteAipe = getPerusteAipe(opsId);
        PerusteAIPEVaiheDto perusteVaihe = findPerusteVaihe(perusteAipe, perusteenVaiheId);
        if (perusteVaihe == null) {
            throw new NotExistsException("perusteen-vaihetta-ei-loytynyt");
        }
        AIPEVaihe vaihe = fromPeruste(perusteVaihe);
        sisalto.addVaihe(vaihe);
        vaihe = aipeVaiheRepository.saveAndFlush(vaihe);
        addMuokkaustieto(opsId, vaihe, perusteVaihe.getNimi(), MuokkausTapahtuma.LUONTI);
        return toDto(vaihe, perusteVaihe);
    }

    @Override
    public AIPEVaiheDto getVaihe(Long opsId, Long vaiheId) {
        AIPEVaihe vaihe = requireVaihe(opsId, vaiheId);
        AipePerusteenSisaltoDto perusteAipe = getPerusteAipe(opsId);
        PerusteAIPEVaiheDto perusteVaihe = findPerusteVaihe(perusteAipe, vaihe.getPerusteenVaiheId());
        return toDto(vaihe, perusteVaihe);
    }

    @Override
    public AIPEVaiheDto updateVaihe(Long opsId, Long vaiheId, AIPEVaiheDto dto) {
        AIPEVaihe vaihe = requireVaihe(opsId, vaiheId);
        applyPaikallinen(vaihe, dto.getPaikallinenTarkennus(), dto.isPiilotettu());
        PerusteAIPEVaiheDto perusteVaihe = findPerusteVaihe(getPerusteAipe(opsId), vaihe.getPerusteenVaiheId());
        addMuokkaustieto(opsId, vaihe, perusteVaihe != null ? perusteVaihe.getNimi() : null, MuokkausTapahtuma.PAIVITYS);
        return getVaihe(opsId, vaiheId);
    }

    @Override
    public void removeVaihe(Long opsId, Long vaiheId) {
        Opetussuunnitelma ops = getOps(opsId);
        AIPESisalto sisalto = getOrCreateSisalto(ops);
        AIPEVaihe vaihe = sisalto.getVaihe(vaiheId);
        assertExists(vaihe, "Vaihetta ei löytynyt");
        PerusteAIPEVaiheDto perusteVaihe = findPerusteVaihe(getPerusteAipe(opsId), vaihe.getPerusteenVaiheId());
        sisalto.getVaiheet().remove(vaihe);
        addMuokkaustieto(opsId, vaihe, perusteVaihe != null ? perusteVaihe.getNimi() : null, MuokkausTapahtuma.POISTO);
    }

    @Override
    public List<AIPEVaiheKevytDto> updateVaiheJarjestys(Long opsId, List<Long> vaiheIds) {
        AIPESisalto sisalto = getOrCreateSisalto(getOps(opsId));
        sisalto.updateVaiheJarjestys(vaiheIds);
        return getVaiheet(opsId);
    }

    @Override
    public AIPEOppiaineDto getOppiaine(Long opsId, Long oppiaineId) {
        AIPEOppiaine oppiaine = requireOppiaine(opsId, oppiaineId);
        AipePerusteenSisaltoDto perusteAipe = getPerusteAipe(opsId);
        PerusteAIPEOppiaineDto perusteOppiaine = findPerusteOppiaine(perusteAipe, oppiaine.getPerusteenOppiaineId());
        PerusteAIPEVaiheDto perusteVaihe = findPerusteVaiheForOppiaine(perusteAipe, oppiaine.getPerusteenOppiaineId());
        return toDto(oppiaine, perusteOppiaine, perusteAipe.getLaajaalaisetosaamiset(),
                perusteVaihe != null ? perusteVaihe.getOpetuksenKohdealueet() : Collections.emptyList());
    }

    @Override
    public AIPEOppiaineDto updateOppiaine(Long opsId, Long oppiaineId, AIPEOppiaineDto dto) {
        AIPEOppiaine oppiaine = requireOppiaine(opsId, oppiaineId);
        applyPaikallinen(oppiaine, dto.getPaikallinenTarkennus(), dto.isPiilotettu());
        oppiaine.getPiilotetutTavoitteet().clear();
        if (dto.getPiilotetutTavoitteet() != null) {
            oppiaine.getPiilotetutTavoitteet().addAll(dto.getPiilotetutTavoitteet());
        }
        PerusteAIPEOppiaineDto perusteOppiaine = findPerusteOppiaine(getPerusteAipe(opsId), oppiaine.getPerusteenOppiaineId());
        addMuokkaustieto(opsId, oppiaine, nimi(perusteOppiaine), MuokkausTapahtuma.PAIVITYS);
        return getOppiaine(opsId, oppiaineId);
    }

    @Override
    public AIPEKurssiDto getKurssi(Long opsId, Long kurssiId) {
        AIPEKurssi kurssi = requireKurssi(opsId, kurssiId);
        AipePerusteenSisaltoDto perusteAipe = getPerusteAipe(opsId);
        PerusteAIPEKurssiDto perusteKurssi = findPerusteKurssi(perusteAipe, kurssi.getPerusteenKurssiId());
        PerusteAIPEOppiaineDto perusteOppiaine = findPerusteOppiaineForKurssi(perusteAipe, kurssi.getPerusteenKurssiId());
        AIPEOppiaine oppiaine = kurssi.getOppiaine();
        return toDto(kurssi, perusteKurssi,
                perusteOppiaine != null ? perusteOppiaine.getTavoitteet() : Collections.emptyList(),
                oppiaine != null ? oppiaine.getPiilotetutTavoitteet() : Collections.emptyList());
    }

    @Override
    public AIPEKurssiDto updateKurssi(Long opsId, Long kurssiId, AIPEKurssiDto dto) {
        AIPEKurssi kurssi = requireKurssi(opsId, kurssiId);
        applyPaikallinen(kurssi, dto.getPaikallinenTarkennus(), dto.isPiilotettu());
        PerusteAIPEKurssiDto perusteKurssi = findPerusteKurssi(getPerusteAipe(opsId), kurssi.getPerusteenKurssiId());
        addMuokkaustieto(opsId, kurssi, nimi(perusteKurssi), MuokkausTapahtuma.PAIVITYS);
        return getKurssi(opsId, kurssiId);
    }

    @Override
    public boolean onkoPuuttuviaSisaltoja(Long opsId) {
        return kasittelePuuttuvat(opsId, false);
    }

    @Override
    public void lisaaPuuttuvatSisallot(Long opsId) {
        if (kasittelePuuttuvat(opsId, true)) {
            Opetussuunnitelma ops = getOps(opsId);
            muokkaustietoService.addOpsMuokkausTieto(opsId, ops, MuokkausTapahtuma.PAIVITYS, MuokkaustietoLisatieto.AIPE_SISALTO_SYNKRONOITU);
        }
    }

    private void applyPaikallinen(AIPEVaihe vaihe, LokalisoituTekstiDto tarkennus, boolean piilotettu) {
        vaihe.setPaikallinenTarkennus(mapper.map(tarkennus, LokalisoituTeksti.class));
        vaihe.setPiilotettu(piilotettu);
    }

    private void applyPaikallinen(AIPEOppiaine oppiaine, LokalisoituTekstiDto tarkennus, boolean piilotettu) {
        oppiaine.setPaikallinenTarkennus(mapper.map(tarkennus, LokalisoituTeksti.class));
        oppiaine.setPiilotettu(piilotettu);
    }

    private void applyPaikallinen(AIPEKurssi kurssi, LokalisoituTekstiDto tarkennus, boolean piilotettu) {
        kurssi.setPaikallinenTarkennus(mapper.map(tarkennus, LokalisoituTeksti.class));
        kurssi.setPiilotettu(piilotettu);
    }

    private void addMuokkaustieto(Long opsId, HistoriaTapahtuma kohde, LokalisoituTekstiDto nimi, MuokkausTapahtuma tapahtuma) {
        muokkaustietoService.addOpsMuokkausTieto(opsId,
                new HistoriaTapahtumaAuditointitiedoilla(kohde.getId(), toTeksti(nimi), kohde.getNavigationType()),
                tapahtuma);
    }

    private LokalisoituTeksti toTeksti(LokalisoituTekstiDto dto) {
        if (dto == null || dto.getTekstit() == null) {
            return null;
        }
        return LokalisoituTeksti.of(dto.getTekstit());
    }

    private AIPEVaihe fromPeruste(PerusteAIPEVaiheDto perusteVaihe) {
        AIPEVaihe vaihe = new AIPEVaihe();
        vaihe.setPerusteenVaiheId(perusteVaihe.getId());
        List<AIPEOppiaine> oppiaineet = Optional.ofNullable(perusteVaihe.getOppiaineet()).orElse(Collections.emptyList())
                .stream()
                .map(this::fromPeruste)
                .collect(Collectors.toList());
        vaihe.setOppiaineet(oppiaineet);
        return vaihe;
    }

    private AIPEOppiaine fromPeruste(PerusteAIPEOppiaineDto perusteOppiaine) {
        AIPEOppiaine oppiaine = new AIPEOppiaine();
        oppiaine.setPerusteenOppiaineId(perusteOppiaine.getId());
        oppiaine.setKurssit(Optional.ofNullable(perusteOppiaine.getKurssit()).orElse(Collections.emptyList())
                .stream()
                .map(this::fromPeruste)
                .collect(Collectors.toList()));
        oppiaine.setOppimaarat(Optional.ofNullable(perusteOppiaine.getOppimaarat()).orElse(Collections.emptyList())
                .stream()
                .map(this::fromPeruste)
                .collect(Collectors.toList()));
        return oppiaine;
    }

    private AIPEKurssi fromPeruste(PerusteAIPEKurssiDto perusteKurssi) {
        AIPEKurssi kurssi = new AIPEKurssi();
        kurssi.setPerusteenKurssiId(perusteKurssi.getId());
        return kurssi;
    }

    private boolean kasittelePuuttuvat(Long opsId, boolean lisaa) {
        AIPESisalto sisalto = getOrCreateSisalto(getOps(opsId));
        AipePerusteenSisaltoDto perusteAipe = getPerusteAipe(opsId);
        boolean puuttuvia = false;
        for (AIPEVaihe vaihe : sisalto.getVaiheet()) {
            PerusteAIPEVaiheDto perusteVaihe = findPerusteVaihe(perusteAipe, vaihe.getPerusteenVaiheId());
            if (perusteVaihe == null) {
                continue;
            }
            if (kasittelePuuttuvatOppiaineet(vaihe, null, vaihe.getOppiaineet(),
                    perusteVaihe.getOppiaineet(), lisaa)) {
                puuttuvia = true;
                if (!lisaa) {
                    return true;
                }
            }
        }
        return puuttuvia;
    }

    private boolean kasittelePuuttuvatOppiaineet(AIPEVaihe vaihe,
                                                AIPEOppiaine parent,
                                                List<AIPEOppiaine> opsOppiaineet,
                                                List<PerusteAIPEOppiaineDto> perusteOppiaineet,
                                                boolean lisaa) {
        boolean puuttuvia = false;
        List<PerusteAIPEOppiaineDto> perusteet = Optional.ofNullable(perusteOppiaineet).orElse(Collections.emptyList());
        for (int i = 0; i < perusteet.size(); i++) {
            PerusteAIPEOppiaineDto perusteOppiaine = perusteet.get(i);
            AIPEOppiaine olemassa = findLocalOppiaine(opsOppiaineet, perusteOppiaine.getId());
            if (olemassa == null) {
                puuttuvia = true;
                if (!lisaa) {
                    return true;
                }
                AIPEOppiaine uusi = fromPeruste(perusteOppiaine);
                if (parent != null) {
                    parent.addOppimaara(uusi, i);
                } else {
                    vaihe.addOppiaine(uusi, i);
                }
                aipeVaiheRepository.saveAndFlush(vaihe);
                continue;
            }
            if (kasittelePuuttuvatKurssit(vaihe, olemassa, perusteOppiaine.getKurssit(), lisaa)) {
                puuttuvia = true;
                if (!lisaa) {
                    return true;
                }
            }
            if (kasittelePuuttuvatOppiaineet(vaihe, olemassa, olemassa.getOppimaarat(),
                    perusteOppiaine.getOppimaarat(), lisaa)) {
                puuttuvia = true;
                if (!lisaa) {
                    return true;
                }
            }
        }
        if (lisaa) {
            jarjesta(opsOppiaineet, perusteet, PerusteAIPEOppiaineDto::getId, AIPEOppiaine::getPerusteenOppiaineId);
        }
        return puuttuvia;
    }

    private boolean kasittelePuuttuvatKurssit(AIPEVaihe vaihe,
                                             AIPEOppiaine oppiaine,
                                             List<PerusteAIPEKurssiDto> perusteKurssit,
                                             boolean lisaa) {
        boolean puuttuvia = false;
        List<PerusteAIPEKurssiDto> perusteet = Optional.ofNullable(perusteKurssit).orElse(Collections.emptyList());
        for (int i = 0; i < perusteet.size(); i++) {
            PerusteAIPEKurssiDto perusteKurssi = perusteet.get(i);
            if (findLocalKurssi(oppiaine.getKurssit(), perusteKurssi.getId()) != null) {
                continue;
            }
            puuttuvia = true;
            if (!lisaa) {
                return true;
            }
            AIPEKurssi uusi = fromPeruste(perusteKurssi);
            oppiaine.addKurssi(uusi, i);
            aipeVaiheRepository.saveAndFlush(vaihe);
        }
        if (lisaa) {
            jarjesta(oppiaine.getKurssit(), perusteet, PerusteAIPEKurssiDto::getId, AIPEKurssi::getPerusteenKurssiId);
        }
        return puuttuvia;
    }

    private <T, P> void jarjesta(List<T> opsLista, List<P> perusteLista, Function<P, Long> perusteId, Function<T, Long> opsPerusteId) {
        if (opsLista == null || opsLista.isEmpty() || perusteLista == null || perusteLista.isEmpty()) {
            return;
        }
        Map<Long, Integer> jarjestys = new HashMap<>();
        for (int i = 0; i < perusteLista.size(); i++) {
            jarjestys.put(perusteId.apply(perusteLista.get(i)), i);
        }
        List<T> jarjestetty = new ArrayList<>(opsLista);
        jarjestetty.sort(Comparator.comparingInt(item -> jarjestys.getOrDefault(opsPerusteId.apply(item), Integer.MAX_VALUE)));
        for (int i = 0; i < jarjestetty.size(); i++) {
            opsLista.set(i, jarjestetty.get(i));
        }
    }

    private AIPEOppiaine findLocalOppiaine(List<AIPEOppiaine> oppiaineet, Long perusteenOppiaineId) {
        return Optional.ofNullable(oppiaineet).orElse(Collections.emptyList()).stream()
                .filter(oa -> Objects.equals(oa.getPerusteenOppiaineId(), perusteenOppiaineId))
                .findFirst()
                .orElse(null);
    }

    private AIPEKurssi findLocalKurssi(List<AIPEKurssi> kurssit, Long perusteenKurssiId) {
        return Optional.ofNullable(kurssit).orElse(Collections.emptyList()).stream()
                .filter(k -> Objects.equals(k.getPerusteenKurssiId(), perusteenKurssiId))
                .findFirst()
                .orElse(null);
    }

    private AIPEVaiheDto toDto(AIPEVaihe vaihe, PerusteAIPEVaiheDto perusteVaihe) {
        AIPEVaiheDto dto = mapper.map(vaihe, AIPEVaiheDto.class);
        dto.setPerusteSisalto(mapPerusteSisalto(perusteVaihe, PerusteAIPEVaiheSisaltoDto.class));
        dto.setOppiaineet(mapOppiaineet(vaihe.getOppiaineet(),
                perusteVaihe != null ? perusteVaihe.getOppiaineet() : Collections.emptyList()));
        return dto;
    }

    private AIPEVaiheExportDto toExportDto(AIPEVaihe vaihe, PerusteAIPEVaiheDto perusteVaihe, List<PerusteLaajaalainenosaaminenDto> laajaalaiset) {
        AIPEVaiheExportDto dto = mapper.map(vaihe, AIPEVaiheExportDto.class);
        dto.setPerusteSisalto(mapPerusteSisalto(perusteVaihe, PerusteAIPEVaiheSisaltoDto.class));
        dto.setOppiaineet(mapOppiaineetExportDto(vaihe.getOppiaineet(),
                perusteVaihe != null ? perusteVaihe.getOppiaineet() : Collections.emptyList(),
                laajaalaiset,
                perusteVaihe != null ? perusteVaihe.getOpetuksenKohdealueet() : Collections.emptyList()));
        return dto;
    }

    private AIPEOppiaineDto toDto(AIPEOppiaine oppiaine, PerusteAIPEOppiaineDto perusteOppiaine,
                                  List<PerusteLaajaalainenosaaminenDto> laajaalaiset,
                                  List<PerusteOpetuksenkohdealueDto> kohdealueet) {
        AIPEOppiaineDto dto = mapper.map(oppiaine, AIPEOppiaineDto.class);
        PerusteAIPEOppiaineSisaltoDto sisalto = mapPerusteSisalto(perusteOppiaine, PerusteAIPEOppiaineSisaltoDto.class);
        if (sisalto != null && perusteOppiaine != null) {
            sisalto.setTavoitteet(mapTavoitteet(perusteOppiaine.getTavoitteet(), laajaalaiset, kohdealueet));
        }
        dto.setPerusteSisalto(sisalto);
        dto.setOppimaarat(mapOppiaineet(oppiaine.getOppimaarat(),
                perusteOppiaine != null ? perusteOppiaine.getOppimaarat() : Collections.emptyList()));
        dto.setKurssit(mapKurssit(oppiaine.getKurssit(),
                perusteOppiaine != null ? perusteOppiaine.getKurssit() : Collections.emptyList()));
        return dto;
    }

    private AIPEOppiaineExportDto toExportDto(AIPEOppiaine oppiaine, PerusteAIPEOppiaineDto perusteOppiaine,
                                             List<PerusteLaajaalainenosaaminenDto> laajaalaiset,
                                             List<PerusteOpetuksenkohdealueDto> kohdealueet) {
        AIPEOppiaineExportDto dto = mapper.map(oppiaine, AIPEOppiaineExportDto.class);
        PerusteAIPEOppiaineSisaltoDto sisalto = mapPerusteSisalto(perusteOppiaine, PerusteAIPEOppiaineSisaltoDto.class);
        if (sisalto != null && perusteOppiaine != null) {
            List<Long> piilotetutTavoitteet = Optional.ofNullable(oppiaine.getPiilotetutTavoitteet())
                    .orElse(Collections.emptyList());
            sisalto.setTavoitteet(mapTavoitteet(perusteOppiaine.getTavoitteet(), laajaalaiset, kohdealueet).stream()
                    .filter(tavoite -> tavoite.getId() != null && !piilotetutTavoitteet.contains(tavoite.getId()))
                    .collect(Collectors.toList()));
        }
        dto.setPerusteSisalto(sisalto);
        dto.setOppimaarat(mapOppiaineetExportDto(oppiaine.getOppimaarat(),
                perusteOppiaine != null ? perusteOppiaine.getOppimaarat() : Collections.emptyList(),
                laajaalaiset,
                kohdealueet));
        dto.setKurssit(mapKurssitExportDto(oppiaine.getKurssit(),
                perusteOppiaine != null ? perusteOppiaine.getKurssit() : Collections.emptyList(),
                perusteOppiaine != null ? perusteOppiaine.getTavoitteet() : Collections.emptyList(),
                oppiaine.getPiilotetutTavoitteet()));
        return dto;
    }

    private AIPEKurssiDto toDto(AIPEKurssi kurssi, PerusteAIPEKurssiDto perusteKurssi,
                               List<PerusteAIPEOpetuksentavoiteDto> oppiaineTavoitteet,
                               List<Long> piilotetutTavoitteet) {
        AIPEKurssiDto dto = mapper.map(kurssi, AIPEKurssiDto.class);
        dto.setPerusteSisalto(mapKurssiPerusteSisalto(perusteKurssi, oppiaineTavoitteet, piilotetutTavoitteet));
        return dto;
    }

    private AIPEKurssiExportDto toExportDto(AIPEKurssi kurssi, PerusteAIPEKurssiDto perusteKurssi,
                                           List<PerusteAIPEOpetuksentavoiteDto> oppiaineTavoitteet,
                                           List<Long> piilotetutTavoitteet) {
        AIPEKurssiExportDto dto = mapper.map(kurssi, AIPEKurssiExportDto.class);
        dto.setPerusteSisalto(mapKurssiPerusteSisalto(perusteKurssi, oppiaineTavoitteet, piilotetutTavoitteet));
        return dto;
    }

    private AIPEVaiheKevytDto toKevyt(AIPEVaihe vaihe, PerusteAIPEVaiheDto perusteVaihe) {
        AIPEVaiheKevytDto dto = new AIPEVaiheKevytDto();
        dto.setId(vaihe.getId());
        dto.setPerusteenVaiheId(vaihe.getPerusteenVaiheId());
        dto.setPiilotettu(vaihe.isPiilotettu());
        dto.setNimi(perusteVaihe != null ? perusteVaihe.getNimi() : null);
        dto.setOppiaineet(mapOppiaineet(vaihe.getOppiaineet(),
                perusteVaihe != null ? perusteVaihe.getOppiaineet() : Collections.emptyList()));
        return dto;
    }

    private List<AIPEOppiaineExportDto> mapOppiaineetExportDto(List<AIPEOppiaine> oppiaineet,
                                                              List<PerusteAIPEOppiaineDto> perusteOppiaineet,
                                                              List<PerusteLaajaalainenosaaminenDto> laajaalaiset,
                                                              List<PerusteOpetuksenkohdealueDto> kohdealueet) {
        List<PerusteAIPEOppiaineDto> perusteet = Optional.ofNullable(perusteOppiaineet).orElse(Collections.emptyList());
        return Optional.ofNullable(oppiaineet).orElse(Collections.emptyList()).stream()
                .filter(oa -> !oa.isPiilotettu())
                .map(oa -> toExportDto(oa, findByPerusteId(perusteet, oa.getPerusteenOppiaineId()), laajaalaiset, kohdealueet))
                .collect(Collectors.toList());
    }

    private List<AIPEKurssiExportDto> mapKurssitExportDto(List<AIPEKurssi> kurssit,
                                                         List<PerusteAIPEKurssiDto> perusteKurssit,
                                                         List<PerusteAIPEOpetuksentavoiteDto> oppiaineTavoitteet,
                                                         List<Long> piilotetutTavoitteet) {
        List<PerusteAIPEKurssiDto> perusteet = Optional.ofNullable(perusteKurssit).orElse(Collections.emptyList());
        return Optional.ofNullable(kurssit).orElse(Collections.emptyList()).stream()
                .filter(k -> !k.isPiilotettu())
                .map(k -> {
                    PerusteAIPEKurssiDto peruste = perusteet.stream()
                            .filter(p -> Objects.equals(p.getId(), k.getPerusteenKurssiId()))
                            .findFirst()
                            .orElse(null);
                    return toExportDto(k, peruste, oppiaineTavoitteet, piilotetutTavoitteet);
                })
                .collect(Collectors.toList());
    }

    private PerusteAIPEOppiaineDto findByPerusteId(List<PerusteAIPEOppiaineDto> oppiaineet, Long perusteOppiaineId) {
        return oppiaineet.stream()
                .filter(p -> Objects.equals(p.getId(), perusteOppiaineId))
                .findFirst()
                .orElse(null);
    }

    private List<AIPEOppiaineKevytDto> mapOppiaineet(List<AIPEOppiaine> oppiaineet, List<PerusteAIPEOppiaineDto> perusteOppiaineet) {
        List<PerusteAIPEOppiaineDto> perusteet = Optional.ofNullable(perusteOppiaineet).orElse(Collections.emptyList());
        return Optional.ofNullable(oppiaineet).orElse(Collections.emptyList()).stream()
                .map(oa -> {
                    PerusteAIPEOppiaineDto peruste = perusteet.stream()
                            .filter(p -> Objects.equals(p.getId(), oa.getPerusteenOppiaineId()))
                            .findFirst()
                            .orElse(null);
                    AIPEOppiaineKevytDto dto = new AIPEOppiaineKevytDto();
                    dto.setId(oa.getId());
                    dto.setPerusteenOppiaineId(oa.getPerusteenOppiaineId());
                    dto.setPiilotettu(oa.isPiilotettu());
                    dto.setNimi(nimi(peruste));
                    dto.setOppimaarat(mapOppiaineet(oa.getOppimaarat(),
                            peruste != null ? peruste.getOppimaarat() : Collections.emptyList()));
                    dto.setKurssit(mapKurssit(oa.getKurssit(),
                            peruste != null ? peruste.getKurssit() : Collections.emptyList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<AIPEKurssiKevytDto> mapKurssit(List<AIPEKurssi> kurssit, List<PerusteAIPEKurssiDto> perusteKurssit) {
        List<PerusteAIPEKurssiDto> perusteet = Optional.ofNullable(perusteKurssit).orElse(Collections.emptyList());
        return Optional.ofNullable(kurssit).orElse(Collections.emptyList()).stream()
                .map(k -> {
                    PerusteAIPEKurssiDto peruste = perusteet.stream()
                            .filter(p -> Objects.equals(p.getId(), k.getPerusteenKurssiId()))
                            .findFirst()
                            .orElse(null);
                    AIPEKurssiKevytDto dto = new AIPEKurssiKevytDto();
                    dto.setId(k.getId());
                    dto.setPerusteenKurssiId(k.getPerusteenKurssiId());
                    dto.setPiilotettu(k.isPiilotettu());
                    dto.setNimi(nimi(peruste));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private LokalisoituTekstiDto nimi(PerusteAIPEOppiaineDto oa) {
        if (oa == null) {
            return null;
        }
        LokalisoituTekstiDto koodiNimi = koodiNimi(oa.getKoodi());
        return koodiNimi != null ? koodiNimi : oa.getNimi();
    }

    private LokalisoituTekstiDto nimi(PerusteAIPEKurssiDto kurssi) {
        if (kurssi == null) {
            return null;
        }
        LokalisoituTekstiDto koodiNimi = koodiNimi(kurssi.getKoodi());
        return koodiNimi != null ? koodiNimi : kurssi.getNimi();
    }

    private LokalisoituTekstiDto koodiNimi(KoodiDto koodi) {
        if (koodi == null || koodi.getNimi() == null || koodi.getNimi().isEmpty()) {
            return null;
        }
        Map<Kieli, String> tekstit = new EnumMap<>(Kieli.class);
        koodi.getNimi().forEach((k, v) -> {
            try {
                Kieli kieli = Kieli.of(k);
                if (kieli != null) {
                    tekstit.put(kieli, v);
                }
            } catch (IllegalArgumentException ignored) {
            }
        });
        return tekstit.isEmpty() ? null : new LokalisoituTekstiDto(null, tekstit);
    }

    private Opetussuunnitelma getOps(Long opsId) {
        return assertExists(opetussuunnitelmaRepository.findOne(opsId), "Pyydettyä opetussuunnitelmaa ei ole olemassa");
    }

    private AIPESisalto getOrCreateSisalto(Opetussuunnitelma ops) {
        if (ops.getAipe() == null) {
            AIPESisalto sisalto = new AIPESisalto();
            sisalto.setOpetussuunnitelma(ops);
            ops.setAipe(sisalto);
            opetussuunnitelmaRepository.save(ops);
        }
        return ops.getAipe();
    }

    private AipePerusteenSisaltoDto getPerusteAipe(Long opsId) {
        Opetussuunnitelma ops = getOps(opsId);
        PerusteDto peruste = eperusteetService.getPeruste(ops.getPerusteenDiaarinumero());
        if (peruste.getAipe() == null) {
            throw new NotExistsException("aipe-perustetta-ei-loytynyt");
        }
        return peruste.getAipe();
    }

    private PerusteAIPEVaiheDto findPerusteVaihe(AipePerusteenSisaltoDto aipe, Long perusteVaiheId) {
        return Optional.ofNullable(aipe.getVaiheet()).orElse(Collections.emptyList()).stream()
                .filter(v -> Objects.equals(v.getId(), perusteVaiheId))
                .findFirst()
                .orElse(null);
    }

    private PerusteAIPEVaiheDto findPerusteVaiheForOppiaine(AipePerusteenSisaltoDto aipe, Long perusteOppiaineId) {
        return Optional.ofNullable(aipe.getVaiheet()).orElse(Collections.emptyList()).stream()
                .filter(v -> flattenOppiaineet(v.getOppiaineet()).stream()
                        .anyMatch(oa -> Objects.equals(oa.getId(), perusteOppiaineId)))
                .findFirst()
                .orElse(null);
    }

    private PerusteAIPEOppiaineDto findPerusteOppiaine(AipePerusteenSisaltoDto aipe, Long perusteOppiaineId) {
        return Optional.ofNullable(aipe.getVaiheet()).orElse(Collections.emptyList()).stream()
                .flatMap(v -> flattenOppiaineet(v.getOppiaineet()).stream())
                .filter(oa -> Objects.equals(oa.getId(), perusteOppiaineId))
                .findFirst()
                .orElse(null);
    }

    private PerusteAIPEKurssiDto findPerusteKurssi(AipePerusteenSisaltoDto aipe, Long perusteKurssiId) {
        return Optional.ofNullable(aipe.getVaiheet()).orElse(Collections.emptyList()).stream()
                .flatMap(v -> flattenOppiaineet(v.getOppiaineet()).stream())
                .flatMap(oa -> Optional.ofNullable(oa.getKurssit()).orElse(Collections.emptyList()).stream())
                .filter(k -> Objects.equals(k.getId(), perusteKurssiId))
                .findFirst()
                .orElse(null);
    }

    private PerusteAIPEOppiaineDto findPerusteOppiaineForKurssi(AipePerusteenSisaltoDto aipe, Long perusteKurssiId) {
        return Optional.ofNullable(aipe.getVaiheet()).orElse(Collections.emptyList()).stream()
                .flatMap(v -> flattenOppiaineet(v.getOppiaineet()).stream())
                .filter(oa -> Optional.ofNullable(oa.getKurssit()).orElse(Collections.emptyList()).stream()
                        .anyMatch(k -> Objects.equals(k.getId(), perusteKurssiId)))
                .findFirst()
                .orElse(null);
    }

    private List<PerusteAIPEOppiaineDto> flattenOppiaineet(List<PerusteAIPEOppiaineDto> oppiaineet) {
        List<PerusteAIPEOppiaineDto> result = new ArrayList<>();
        for (PerusteAIPEOppiaineDto oa : Optional.ofNullable(oppiaineet).orElse(Collections.emptyList())) {
            result.add(oa);
            result.addAll(flattenOppiaineet(oa.getOppimaarat()));
        }
        return result;
    }

    private AIPEVaihe requireVaihe(Long opsId, Long vaiheId) {
        AIPEVaihe vaihe = getOrCreateSisalto(getOps(opsId)).getVaihe(vaiheId);
        return assertExists(vaihe, "Vaihetta ei löytynyt");
    }

    private AIPEOppiaine requireOppiaine(Long opsId, Long oppiaineId) {
        return assertExists(findOppiaine(getOrCreateSisalto(getOps(opsId)), oppiaineId), "Oppiainetta ei löytynyt");
    }

    private AIPEKurssi requireKurssi(Long opsId, Long kurssiId) {
        return assertExists(findKurssi(getOrCreateSisalto(getOps(opsId)), kurssiId), "Kurssia ei löytynyt");
    }

    private AIPEOppiaine findOppiaine(AIPESisalto sisalto, Long oppiaineId) {
        return sisalto.getVaiheet().stream()
                .flatMap(v -> flattenLocalOppiaineet(v.getOppiaineet()).stream())
                .filter(oa -> Objects.equals(oa.getId(), oppiaineId))
                .findFirst()
                .orElse(null);
    }

    private AIPEKurssi findKurssi(AIPESisalto sisalto, Long kurssiId) {
        return sisalto.getVaiheet().stream()
                .flatMap(v -> flattenLocalOppiaineet(v.getOppiaineet()).stream())
                .flatMap(oa -> oa.getKurssit().stream())
                .filter(k -> Objects.equals(k.getId(), kurssiId))
                .findFirst()
                .orElse(null);
    }

    private <S, D> D mapPerusteSisalto(S source, Class<D> dest) {
        return source != null ? mapper.map(source, dest) : null;
    }

    private PerusteAIPEKurssiSisaltoDto mapKurssiPerusteSisalto(PerusteAIPEKurssiDto perusteKurssi,
                                                               List<PerusteAIPEOpetuksentavoiteDto> oppiaineTavoitteet,
                                                               List<Long> piilotetutTavoitteet) {
        PerusteAIPEKurssiSisaltoDto sisalto = mapPerusteSisalto(perusteKurssi, PerusteAIPEKurssiSisaltoDto.class);
        if (sisalto != null && perusteKurssi != null) {
            sisalto.setTavoitteet(resolveKurssiTavoitteet(perusteKurssi.getTavoitteet(), oppiaineTavoitteet, piilotetutTavoitteet));
        }
        return sisalto;
    }

    private List<PerusteAIPEOpetuksentavoiteSisaltoDto> mapTavoitteet(List<PerusteAIPEOpetuksentavoiteDto> tavoitteet,
                                                                     List<PerusteLaajaalainenosaaminenDto> laajaalaiset,
                                                                     List<PerusteOpetuksenkohdealueDto> kohdealueet) {
        return Optional.ofNullable(tavoitteet).orElse(Collections.emptyList()).stream()
                .map(tavoite -> toTavoiteDto(tavoite, laajaalaiset, kohdealueet))
                .collect(Collectors.toList());
    }

    private PerusteAIPEOpetuksentavoiteSisaltoDto toTavoiteDto(PerusteAIPEOpetuksentavoiteDto source,
                                                               List<PerusteLaajaalainenosaaminenDto> laajaalaiset,
                                                               List<PerusteOpetuksenkohdealueDto> kohdealueet) {
        PerusteAIPEOpetuksentavoiteSisaltoDto dto = mapper.map(source, PerusteAIPEOpetuksentavoiteSisaltoDto.class);
        dto.setLaajattavoitteet(resolveLaajaalaiset(source.getLaajattavoitteet(), laajaalaiset).stream()
                .map(l -> mapper.map(l, PerusteAIPELaajaalainenosaaminenSisaltoDto.class))
                .collect(Collectors.toCollection(LinkedHashSet::new)));
        dto.setKohdealueet(resolveKohdealueet(source.getKohdealueet(), kohdealueet).stream()
                .map(k -> mapper.map(k, PerusteAIPEOpetuksenkohdealueSisaltoDto.class))
                .collect(Collectors.toCollection(LinkedHashSet::new)));
        return dto;
    }

    private Set<PerusteAIPEOpetuksentavoiteKevytSisaltoDto> resolveKurssiTavoitteet(
            Set<Reference> refs,
            List<PerusteAIPEOpetuksentavoiteDto> tavoitteet,
            List<Long> piilotetutTavoitteet) {
        List<PerusteAIPEOpetuksentavoiteDto> all = Optional.ofNullable(tavoitteet).orElse(Collections.emptyList());
        List<Long> piilotetut = Optional.ofNullable(piilotetutTavoitteet).orElse(Collections.emptyList());
        return Optional.ofNullable(refs).orElse(Collections.emptySet()).stream()
                .map(ref -> findTavoite(all, ref))
                .filter(Objects::nonNull)
                .filter(tavoite -> tavoite.getId() != null && !piilotetut.contains(tavoite.getId()))
                .map(tavoite -> mapper.map(tavoite, PerusteAIPEOpetuksentavoiteKevytSisaltoDto.class))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private PerusteAIPEOpetuksentavoiteDto findTavoite(List<PerusteAIPEOpetuksentavoiteDto> tavoitteet, Reference ref) {
        if (ref == null || ref.getId() == null) {
            return null;
        }
        String id = ref.getId();
        return tavoitteet.stream()
                .filter(t -> matchesReference(t.getId(), t.getTunniste(), id))
                .findFirst()
                .orElse(null);
    }

    private Set<PerusteLaajaalainenosaaminenDto> resolveLaajaalaiset(Set<Reference> refs,
                                                                    List<PerusteLaajaalainenosaaminenDto> laajaalaiset) {
        List<PerusteLaajaalainenosaaminenDto> all = Optional.ofNullable(laajaalaiset).orElse(Collections.emptyList());
        return Optional.ofNullable(refs).orElse(Collections.emptySet()).stream()
                .map(ref -> findLaajaalainen(all, ref))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<PerusteOpetuksenkohdealueDto> resolveKohdealueet(Set<Reference> refs,
                                                                List<PerusteOpetuksenkohdealueDto> kohdealueet) {
        List<PerusteOpetuksenkohdealueDto> all = Optional.ofNullable(kohdealueet).orElse(Collections.emptyList());
        return Optional.ofNullable(refs).orElse(Collections.emptySet()).stream()
                .map(ref -> findKohdealue(all, ref))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private PerusteLaajaalainenosaaminenDto findLaajaalainen(List<PerusteLaajaalainenosaaminenDto> laajaalaiset,
                                                            Reference ref) {
        if (ref == null || ref.getId() == null) {
            return null;
        }
        String id = ref.getId();
        return laajaalaiset.stream()
                .filter(l -> matchesReference(l.getId(), l.getTunniste(), id))
                .findFirst()
                .orElse(null);
    }

    private PerusteOpetuksenkohdealueDto findKohdealue(List<PerusteOpetuksenkohdealueDto> kohdealueet, Reference ref) {
        if (ref == null || ref.getId() == null) {
            return null;
        }
        String id = ref.getId();
        return kohdealueet.stream()
                .filter(k -> matchesReference(k.getId(), null, id))
                .findFirst()
                .orElse(null);
    }

    private boolean matchesReference(Long id, UUID tunniste, String ref) {
        return (id != null && Objects.equals(String.valueOf(id), ref))
                || (tunniste != null && Objects.equals(tunniste.toString(), ref));
    }

    private List<AIPEOppiaine> flattenLocalOppiaineet(List<AIPEOppiaine> oppiaineet) {
        List<AIPEOppiaine> result = new ArrayList<>();
        for (AIPEOppiaine oa : Optional.ofNullable(oppiaineet).orElse(Collections.emptyList())) {
            result.add(oa);
            result.addAll(flattenLocalOppiaineet(oa.getOppimaarat()));
        }
        return result;
    }
}
