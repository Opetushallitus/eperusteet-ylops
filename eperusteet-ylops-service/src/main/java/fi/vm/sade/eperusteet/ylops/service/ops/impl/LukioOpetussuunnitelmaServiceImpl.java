/*
 *  Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 *  This program is free software: Licensed under the EUPL, Version 1.1 or - as
 *  soon as they will be approved by the European Commission - subsequent versions
 *  of the EUPL (the "Licence");
 *
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  European Union Public Licence for more details.
 */

package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.lukio.Aihekokonaisuus;
import fi.vm.sade.eperusteet.ylops.domain.lukio.LukioOppiaineJarjestys;
import fi.vm.sade.eperusteet.ylops.domain.lukio.Lukiokurssi;
import fi.vm.sade.eperusteet.ylops.domain.lukio.LukiokurssiTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.lukio.LukiokurssiTyyppi.Paikallinen;
import fi.vm.sade.eperusteet.ylops.domain.lukio.OppiaineLukiokurssi;
import fi.vm.sade.eperusteet.ylops.domain.lukio.OpsOppiaineParentView;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineOpsTunniste;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpsOppiaine;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.AihekokonaisuudetJarjestaDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.AihekokonaisuudetOpsDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.AihekokonaisuudetPerusteOpsDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.AihekokonaisuusJarjestysDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.AihekokonaisuusSaveDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.KurssinOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioAbstraktiOppiaineTuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioKopioiOppimaaraDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioKurssiParentDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOpetussuunnitelmaRakenneOpsDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppiaineRakenneDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppiaineRakenneListausDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppiaineSaveDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppiaineTiedotDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppimaaraPerusTiedotDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukiokurssiListausOpsDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukiokurssiOppaineMuokkausDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukiokurssiOpsDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukiokurssiSaveDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukiokurssiUpdateDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.OpetuksenYleisetTavoitteetOpsDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.OpetuksenYleisetTavoitteetPerusteOpsDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.OpetuksenYleisetTavoitteetUpdateDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.OppaineKurssiTreeStructureDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.OppiaineJarjestysDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.AihekokonaisuudetDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.AihekokonaisuusOpsDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.LukioPerusteOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.OpetuksenYleisetTavoitteetDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.AihekokonaisuusRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.LukioOppiaineJarjestysRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.LukiokurssiRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetuksenYleisetTavoitteetRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OppiaineLukiokurssiRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OppiaineRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpsOppiaineParentViewRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.external.KoodistoService;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.lukio.LukioOpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.Copier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static fi.vm.sade.eperusteet.ylops.domain.ReferenceableEntity.idEquals;
import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * User: tommiratamaa
 * Date: 19.11.2015
 * Time: 15.32
 */
@Service
public class LukioOpetussuunnitelmaServiceImpl implements LukioOpetussuunnitelmaService {
    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private OppiaineLukiokurssiRepository oppiaineLukiokurssiRepository;

    @Autowired
    private LukioOppiaineJarjestysRepository jarjestysRepository;

    @Autowired
    private OpsOppiaineParentViewRepository oppiaineParentViewRepository;

    @Autowired
    private OppiaineRepository oppiaineRepository;

    @Autowired
    private LukiokurssiRepository lukiokurssiRepository;

    @Autowired
    private AihekokonaisuusRepository aihekokonaisuusRepository;

    @Autowired
    private OpetuksenYleisetTavoitteetRepository opetuksenYleisetTavoitteetRepository;

    @Autowired
    private KoodistoService koodistoService;

    @Override
    @Transactional(readOnly = true)
    public LukioOpetussuunnitelmaRakenneOpsDto getRakenne(long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        LukioOpetussuunnitelmaRakenneOpsDto rakenne = new LukioOpetussuunnitelmaRakenneOpsDto();
        rakenne.setMuokattu(ops.getMuokattu());
        rakenne.setOpsId(ops.getId());
        rakenne.setRoot(ops.getPohja() == null || ops.getPohja().getTyyppi() == Tyyppi.POHJA);
        Map<Long, OpsOppiaineParentView> parentRelateionsByOppiaineId
                = oppiaineParentViewRepository.findByOpetusuunnitelmaId(ops.getId())
                .stream().collect(toMap(o -> o.getOpsOppiaine().getOppiaineId(), o -> o));
        Opetussuunnitelma alinPohja = ops.getAlinPohja();
        Map<Long, OppiaineJarjestysDto> jarjestykset =
                jarjestysRepository.findJarjestysDtosByOpetussuunnitelmaId(opsId).stream()
                        .collect(toMap(OppiaineJarjestysDto::getId, o -> o)),
                jarjestyksetAlinPohja = jarjestysRepository.findJarjestysDtosByOpetussuunnitelmaId(alinPohja.getId()).stream()
                        .collect(toMap(OppiaineJarjestysDto::getId, o -> o));
        Map<Long, Long> parentKurssis = lukiokurssiRepository.findParentKurssisByOps(opsId)
                .stream().filter(p -> p.getParentId() != null)
                .collect(toMap(LukioKurssiParentDto::getId, LukioKurssiParentDto::getParentId));
        map(ops.getOppiaineet().stream().map(OpsOppiaine::getOppiaine),
                LambdaUtil.map(parentRelateionsByOppiaineId, OpsOppiaineParentView::isOma),
                LambdaUtil.map(parentRelateionsByOppiaineId, OpsOppiaineParentView::getPohjanOppiaine),
                jarjestys(jarjestykset),
                rakenne.getOppiaineet()::add,
                ops.lukiokurssitByOppiaine(),
                parentKurssis::get,
                oa -> new LukioOppiaineRakenneListausDto(), LukiokurssiListausOpsDto::new);
        Set<UUID> juuriUuids = ops.getOppiaineet().stream().map(opsOa -> opsOa.getOppiaine().getTunniste())
                .collect(toSet());
        map(alinPohja.getOppiaineet().stream()
                        .map(OpsOppiaine::getOppiaine).filter(oa -> oa.isAbstraktiBool()
                                && !juuriUuids.contains(oa.getTunniste())),
                oma -> false, pohjan -> null, jarjestys(jarjestyksetAlinPohja),
                rakenne.getPohjanTarjonta()::add,
                uuid -> new ArrayList<>(), id -> null,
                oa -> new LukioOppiaineRakenneListausDto(), LukiokurssiListausOpsDto::new);
        return rakenne;
    }

    @Override
    @Transactional(readOnly = true)
    public LukioOppiaineTiedotDto getOppiaineTiedot(long opsId, long oppiaineId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        LukioOppiaineJarjestys jarjestys = jarjestysRepository.findByOppiaineId(opsId, oppiaineId);
        if (jarjestys == null) {
            throw new BusinessRuleViolationException("Oppiaineta ei löytynyt.");
        }
        Oppiaine oppiaine = jarjestys.getOppiaine();

        LukioOppiaineTiedotDto dto = new LukioOppiaineTiedotDto();
        OpsOppiaineParentView parentRelateion
                = oppiaineParentViewRepository.findByTunnisteAndOpetusuunnitelmaId(ops.getId(),
                (oppiaine.getOppiaine() != null ? oppiaine.getOppiaine() : oppiaine).getTunniste()).stream().findFirst().orElse(null);
        Oppiaine pohjanOppiaine = parentRelateion != null ? parentRelateion.getPohjanOppiaine() : null;
        mapPerustiedot(dto, oppiaine, parentRelateion != null && parentRelateion.isOma(), pohjanOppiaine);
        List<OppiaineLukiokurssi> kurssit = oppiaineLukiokurssiRepository.findByOpsAndOppiaine(opsId, oppiaineId);
        Map<Long, Long> parentKurssis = lukiokurssiRepository.findParentKurssisByOps(opsId)
                .stream().filter(p -> p.getParentId() != null)
                .collect(toMap(LukioKurssiParentDto::getId, LukioKurssiParentDto::getParentId));
        dto.setKurssit(kurssit.stream()
                .map(lk -> mapKurssi(lk, parentKurssis::get, LukiokurssiOpsDto::new)).collect(toList()));
        dto.setKurssiTyyppiKuvaukset(LokalisoituTekstiDto.ofOptionalMap(oppiaine.getKurssiTyyppiKuvaukset()));
        Map<Long, OppiaineJarjestysDto> jarjestykset =
                jarjestysRepository.findJarjestysDtosByOpetussuunnitelmaId(opsId,
                        oppiaine.maarineen().map(Oppiaine::getId).collect(toSet())).stream()
                        .collect(toMap(OppiaineJarjestysDto::getId, o -> o));
        if (oppiaine.isKoosteinen()) {
            oppiaine.getOppimaarat().stream().sorted(compareOppiaineet(jarjestys(jarjestykset))).forEach(om -> {
                LukioOppimaaraPerusTiedotDto oppimaara = new LukioOppimaaraPerusTiedotDto();
                mapPerustiedot(oppimaara, om, dto.isOma(), null);
                dto.getOppimaarat().add(oppimaara);
            });

            Opetussuunnitelma alinPohja = ops.getAlinPohja();
            Map<Long, OppiaineJarjestysDto> jarjestyksetAlinPohja = jarjestysRepository.findJarjestysDtosByOpetussuunnitelmaId(alinPohja.getId()).stream()
                    .collect(toMap(OppiaineJarjestysDto::getId, o -> o));
            Map<UUID, List<Oppiaine>> pohjanTarjontaByOppiaineUUID = alinPohja.getOppiaineet().stream()
                    .filter(ap -> ap.getOppiaine().getTunniste().equals(oppiaine.getTunniste()))
                    .flatMap(opsOppiaine -> opsOppiaine.getOppiaine().maarineen()).filter(oa
                            -> oa.isAbstraktiBool() && oa.getOppiaine() != null)
                    .sorted(compareOppiaineet(jarjestys(jarjestyksetAlinPohja)))
                    .collect(groupingBy(oa -> oa.getOppiaine().getTunniste()));
            List<Oppiaine> pohjanTarjontaOppiaineet = pohjanTarjontaByOppiaineUUID.get(oppiaine.getTunniste());
            if (pohjanTarjontaOppiaineet != null && !pohjanTarjontaOppiaineet.isEmpty()) {
                Set<UUID> pohjanToteutukset = "KT".equals(dto.getKoodiArvo())
                        ? dto.getOppimaarat().stream().map(LukioOppimaaraPerusTiedotDto::getTunniste).collect(toSet())
                        : Collections.emptySet();
                pohjanTarjontaOppiaineet.stream()
                        .filter(t -> !pohjanToteutukset.contains(t.getTunniste())).forEach(tarjottava -> {
                    LukioOppimaaraPerusTiedotDto tarjottavaDto = new LukioOppimaaraPerusTiedotDto();
                    mapPerustiedot(tarjottavaDto, tarjottava, false, null);
                    dto.getPohjanTarjonta().add(tarjottavaDto);
                });
            }
        }

        PerusteDto perusteDto = eperusteetService.getPeruste(ops.getPerusteenDiaarinumero());
        LukioPerusteOppiaineDto perusteOppiaine = perusteDto.getLukiokoulutus().getRakenne().getOppiaineet().stream()
                .flatMap(LukioPerusteOppiaineDto::maarineen)
                .filter(poa -> poa.getTunniste().equals(oppiaine.getTunniste())).findFirst().orElse(null);
        if (perusteOppiaine != null) {
            dto.setPerusteen(perusteOppiaine);
        }

        return dto;
    }

    private <Kt extends LukiokurssiListausOpsDto,
            T extends LukioOppiaineRakenneDto<T, Kt>> void map(Stream<Oppiaine> from, Function<Long, Boolean> isOma,
                                                               Function<Long, Oppiaine> pohjanOppiaineById,
                                                               Function<Long, Integer> jarjestys,
                                                               Consumer<T> to,
                                                               Function<Long, List<OppiaineLukiokurssi>> lukiokurssiByOppiaineId,
                                                               Function<Long, Long> parentKurssisById,
                                                               Function<Oppiaine, T> constructor, Supplier<Kt> kurssiConstructor) {
        from.sorted(compareOppiaineet(jarjestys)).forEach(oa -> {
            T dto = constructor.apply(oa);
            Oppiaine pohjanOppiaine = pohjanOppiaineById.apply(oa.getId());
            mapPerustiedot(dto, oa, isOma.apply(oa.getId()), pohjanOppiaine);
            dto.setKurssit(lukiokurssiByOppiaineId.apply(oa.getId()).stream()
                    .map(lk -> mapKurssi(lk, parentKurssisById, kurssiConstructor)).collect(toList()));
            Set<Oppiaine> maarat = oa.getOppimaaratReal();
            map(maarat.stream(), childId -> dto.isOma(),
                    childId -> null, jarjestys,
                    dto.getOppimaarat()::add,
                    lukiokurssiByOppiaineId, parentKurssisById,
                    constructor, kurssiConstructor);
            to.accept(dto);
        });
    }

    private <T extends LukioOppimaaraPerusTiedotDto> void mapPerustiedot(
            T dto, Oppiaine oa, boolean oma, Oppiaine pohjanOppiaine) {
        mapper.map(oa, dto);
        dto.setOppiaineId(oa.getOppiaine() == null ? null : oa.getOppiaine().getId());
        dto.setOma(oma);
        dto.setMaariteltyPohjassa(pohjanOppiaine != null);
    }

    private Function<Long, Integer> jarjestys(Map<Long, OppiaineJarjestysDto> jarjestykset) {
        return LambdaUtil.map(jarjestykset, OppiaineJarjestysDto::getJarjestys);
    }

    private Comparator<Oppiaine> compareOppiaineet(Function<Long, Integer> jarjestys) {
        return comparing((Oppiaine oa) -> ofNullable(jarjestys.apply(oa.getId())).orElse(Integer.MAX_VALUE))
                .thenComparing(comparing((Oppiaine oa) -> {
                    if (oa == null || oa.getNimi() == null) {
                        return "";
                    }
                    return oa.getNimi().firstByKieliOrder().orElse("");
                }));
    }

    private <Kt extends LukiokurssiListausOpsDto> Kt mapKurssi(OppiaineLukiokurssi oaLk, Function<Long, Long> parentKurssisById,
                                                               Supplier<Kt> constructor) {
        Kt kurssiDto = mapper.map(oaLk.getKurssi(), constructor.get());
        kurssiDto.setOma(oaLk.isOma());
        kurssiDto.setPalautettava(parentKurssisById.apply(oaLk.getKurssi().getId()) != null);
        return kurssiDto;
    }

    @Override
    @Transactional(readOnly = true)
    public AihekokonaisuudetPerusteOpsDto getAihekokonaisuudet(long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        PerusteDto perusteDto = eperusteetService.getPeruste(ops.getPerusteenDiaarinumero());
        return new AihekokonaisuudetPerusteOpsDto(
                mapper.map(perusteDto.getLukiokoulutus().getAihekokonaisuudet(), AihekokonaisuudetDto.class),
                sorted(mapper.map(ops.getAihekokonaisuudet(), AihekokonaisuudetOpsDto.class))
        );
    }

    private AihekokonaisuudetOpsDto sorted(AihekokonaisuudetOpsDto aihekokonaisuudetOpsDto) {
        Collections.sort(aihekokonaisuudetOpsDto.getAihekokonaisuudet(),
                Comparator.comparing((AihekokonaisuusOpsDto ak) -> Optional.ofNullable(ak.getJnro())
                        .orElse(Long.MAX_VALUE))
                        .thenComparing(AihekokonaisuusOpsDto::getId));
        return aihekokonaisuudetOpsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public OpetuksenYleisetTavoitteetPerusteOpsDto getOpetuksenYleisetTavoitteet(long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        PerusteDto perusteDto = eperusteetService.getPeruste(ops.getPerusteenDiaarinumero());
        return new OpetuksenYleisetTavoitteetPerusteOpsDto(
                mapper.map(perusteDto.getLukiokoulutus().getOpetuksenYleisetTavoitteet(),
                        OpetuksenYleisetTavoitteetDto.class),
                mapper.map(ops.getOpetuksenYleisetTavoitteet(),
                        OpetuksenYleisetTavoitteetOpsDto.class)
        );
    }

    @Override
    @Transactional
    public void updateOpetuksenYleisetTavoitteet(long opsId, OpetuksenYleisetTavoitteetUpdateDto tavoitteet) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        opetuksenYleisetTavoitteetRepository.lock(ops.getOpetuksenYleisetTavoitteet());
        mapper.map(tavoitteet, ops.getOpetuksenYleisetTavoitteet());
    }

    @Override
    @Transactional
    public void updateOppiaine(long opsId, LukioOppiaineSaveDto dto) {
        Oppiaine oppiaine = oppiaineRepository.findOne(dto.getOppiaineId());
        if (oppiaine == null) {
            throw new BusinessRuleViolationException("Oppiainetta ei löydy");
        }
        oppiaineRepository.lock(oppiaine);
        if (oppiaine.getOppiaine() != null) {
            dto.setKoosteinen(false);
        }
        mapper.map(dto, oppiaine);

        oppiaine.getKurssiTyyppiKuvaukset().forEach((lukiokurssiTyyppi, lokalisoituTeksti)
                -> lukiokurssiTyyppi.oppiaineKuvausSetter().set(oppiaine, null));

        if (dto.getKurssiTyyppiKuvaukset() != null) {
            for (Map.Entry<LukiokurssiTyyppi, LokalisoituTekstiDto> kv : dto.getKurssiTyyppiKuvaukset().entrySet()) {
                kv.getKey().oppiaineKuvausSetter().set(oppiaine, kv.getValue() != null ? LokalisoituTeksti.of(kv.getValue().getTekstit()) : null);
            }
        }
    }

    @Override
    @Transactional
    public long addOppimaara(long opsId, long oppiaineId, LukioKopioiOppimaaraDto kt) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        Opetussuunnitelma opspohja = ops.getAlinPohja();
        Oppiaine parent = ops.findYlatasonOppiaine(idEquals(oppiaineId), OpsOppiaine::isOma)
                .orElseThrow(() -> new BusinessRuleViolationException("Oppiaine ei ole Opetusuunnitelman oma."));
        Oppiaine pohjaparent = oppiaineRepository.findOneByOpsIdAndTunniste(opspohja.getId(), parent.getTunniste());
        assertExists(pohjaparent, "Oppimäärää ei ole määritelty ylätasolla.");

        Copier<Oppiaine> copier = Oppiaine.basicCopier()
                .and(OpetussuunnitelmaServiceImpl.getLukiokurssitOppiaineCopier(opspohja, ops,
                        opspohja.getTyyppi() == Tyyppi.POHJA));
        if (parent.getKoodiArvo().equalsIgnoreCase("KT") && kt.getTunniste() == null) {
            // case uskonto
            if (parent.getOppimaarat().stream().filter(existing
                    -> existing.getTunniste().equals(pohjaparent.getTunniste())).findAny().isPresent()) {
                throw new BusinessRuleViolationException("Uskonto on jo toteutettuna.");
            }
            Oppiaine uusi = copier.copied(pohjaparent, new Oppiaine(pohjaparent.getTunniste()));
            uusi.setNimi(LokalisoituTeksti.of(kt.getNimi().getTekstit()));
            uusi.setKoosteinen(false);
            uusi.setAbstrakti(false);
            parent.addOppimaara(oppiaineRepository.save(uusi));
            ops.getOppiaineJarjestykset().add(new LukioOppiaineJarjestys(ops, uusi, null));
            return uusi.getId();
        } else {
            return pohjaparent.getOppimaarat().stream().filter(t -> t.getTunniste().equals(kt.getTunniste()))
                    .findFirst().map(om -> {
                        LokalisoituTeksti kieli = LokalisoituTeksti.of(kt.getKieli().getTekstit());
                        if (parent.getOppimaarat().stream().filter(existing
                                -> existing.getOpsUniikkiTunniste().equals(
                                new OppiaineOpsTunniste(om.getTunniste(), kt.getKieliKoodiArvo(), kieli)))
                                .findAny().isPresent()) {
                            throw new BusinessRuleViolationException("Kieli on jo toteutettuna.");
                        }
                        Oppiaine uusi = copier.copied(om, new Oppiaine(om.getTunniste()));
                        uusi.setNimi(LokalisoituTeksti.of(kt.getNimi().getTekstit()));
                        uusi.setKieli(kieli);
                        uusi.setKieliKoodiArvo(kt.getKieliKoodiArvo());
                        uusi.setKieliKoodiUri(kt.getKieliKoodiUri());
                        parent.addOppimaara(oppiaineRepository.save(uusi));
                        ops.getOppiaineJarjestykset().add(new LukioOppiaineJarjestys(ops, uusi, null));
                        return uusi.getId();
                    }).orElseThrow(() -> new BusinessRuleViolationException("Pyydettyä kielitarjonnan oppiainetta ei ole"));
        }
    }

    @Override
    @Transactional
    public long addAbstraktiOppiaine(long opsId, LukioAbstraktiOppiaineTuontiDto tuonti) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        Opetussuunnitelma opspohja = ops.getAlinPohja();
        Oppiaine pohjaparent = oppiaineRepository.findOneByOpsIdAndTunniste(opspohja.getId(), tuonti.getTunniste());
        assertExists(pohjaparent, "Oppimäärää ei ole määritelty ylätasolla.");
        if (ops.getOppiaineet().stream().filter(existing
                -> existing.getOppiaine().getTunniste().equals(pohjaparent.getTunniste())).findAny().isPresent()) {
            throw new BusinessRuleViolationException("Oppiaine on jo toteutettuna.");
        }
        Copier<Oppiaine> copier = Oppiaine.basicCopier()
                .and(OpetussuunnitelmaServiceImpl.getLukiokurssitOppiaineCopier(opspohja, ops,
                        opspohja.getTyyppi() == Tyyppi.POHJA));
        Oppiaine uusi = copier.copied(pohjaparent, new Oppiaine(pohjaparent.getTunniste()));
        uusi.setNimi(LokalisoituTeksti.of(tuonti.getNimi().getTekstit()));
        uusi.setAbstrakti(true);
        ops.addOppiaine(oppiaineRepository.save(uusi));
        ops.getOppiaineJarjestykset().add(new LukioOppiaineJarjestys(ops, uusi, null));
        return uusi.getId();
    }

    @Override
    @Transactional
    public long saveOppiaine(long opsId, LukioOppiaineSaveDto dto) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        opetussuunnitelmaRepository.lock(ops);

        Oppiaine oppiaine = mapper.map(dto, new Oppiaine(OppiaineTyyppi.LUKIO));
        if (dto.getKurssiTyyppiKuvaukset() != null) {
            for (Map.Entry<LukiokurssiTyyppi, LokalisoituTekstiDto> kv : dto.getKurssiTyyppiKuvaukset().entrySet()) {
                kv.getKey().oppiaineKuvausSetter().set(oppiaine, kv.getValue() != null ? LokalisoituTeksti.of(kv.getValue().getTekstit()) : null);
            }
        }
        if (dto.getOppiaineId() != null) {
            OpsOppiaine parent = ops.getOppiaineet().stream().filter(o -> o.getOppiaine().getId().equals(dto.getOppiaineId()))
                    .findAny().orElseThrow(() -> new BusinessRuleViolationException("Oppiainetta oppimäärälle " +
                            "ei löydy Opetussuunnitelmasta: " + dto.getOppiaineId()));
            if (!parent.getOppiaine().isKoosteinen()) {
                throw new BusinessRuleViolationException("Yritetään lisätä ei koosteiseen oppiaineeeseen.");
            }
            oppiaine.setKoosteinen(false); // ei sallita lapsia oppimäärälle
            parent.getOppiaine().addOppimaara(oppiaine);
        } else {
            ops.addOppiaine(oppiaine);
        }
        opetussuunnitelmaRepository.flush();
        ops.getOppiaineJarjestykset().add(new LukioOppiaineJarjestys(ops, oppiaine, null));
        return oppiaine.getId();
    }

    @Override
    @Transactional
    public long saveKurssi(long opsId, LukiokurssiSaveDto kurssi) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        opetussuunnitelmaRepository.lock(ops);
        LukioOppiaineJarjestys jarjestys = jarjestysRepository.findByOppiaineId(opsId, kurssi.getOppiaineId());
        assertExists(jarjestys, "Oppiainetta ei löydy Opetussuunnitelmasta.");
        Oppiaine oppiaine = jarjestys.getOppiaine();
        if (oppiaine.isKoosteinen()) {
            throw new BusinessRuleViolationException("Ei voida lisätä kurssia koosteiseen oppiaineeseen.");
        }

        kurssi.setLaajuus(kurssi.getLaajuus().max(BigDecimal.valueOf(0.5)));
        Lukiokurssi lukiokurssi = mapper.map(kurssi, new Lukiokurssi());
        lukiokurssi.setTyyppi(kurssi.getTyyppi().toKurssiiTyyppi());
        resolvePaikallinenKurssityyppi(kurssi.getTyyppi(), lukiokurssi);
        ops.getLukiokurssit().add(new OppiaineLukiokurssi(ops, oppiaine, lukiokurssi, null, true));
        opetussuunnitelmaRepository.flush();
        return lukiokurssi.getId();
    }

    private void resolvePaikallinenKurssityyppi(Paikallinen kurssiTyyppi, Lukiokurssi lukiokurssi) {
        KoodistoKoodiDto koodi = koodistoService.get("lukionkurssit", kurssiTyyppi.getKurssiKoodi());
        if (koodi == null) {
            throw new BusinessRuleViolationException("Koodistokoodia "
                    + kurssiTyyppi.getKurssiKoodi() + " paikalliselle kurssityypille ei löydy koodistosta.");
        }
        lukiokurssi.setKoodiUri(koodi.getKoodiUri());
        lukiokurssi.setKoodiArvo(koodi.getKoodiArvo());
    }

    @Override
    @Transactional
    public void updateKurssi(long opsId, long kurssiId, LukiokurssiUpdateDto kurssi) {
        OppiaineLukiokurssi oaLukiokurssi = oppiaineLukiokurssiRepository.findByOpsAndKurssi(opsId, kurssiId).stream().findAny()
                .orElseThrow(() -> new BusinessRuleViolationException("Kurssia ei löytynyt."));
        if (!oaLukiokurssi.isOma()) {
            throw new BusinessRuleViolationException("Yritetiin muokata pohjaan yhdistettyä kurssia.");
        }
        Lukiokurssi lukiokurssi = oaLukiokurssi.getKurssi();
        lukiokurssiRepository.lock(lukiokurssi);
        if (!lukiokurssi.getTyyppi().isPaikallinen()) {
            // Ei anneta muokata laajuutta, jos määritelty pohjassa/ylätasolla:
            kurssi.setLaajuus(lukiokurssi.getLaajuus());
            kurssi.setTyyppi(lukiokurssi.getTyyppi());
        }
        kurssi.setLaajuus(kurssi.getLaajuus().max(BigDecimal.valueOf(0.5)));
        mapper.map(kurssi, lukiokurssi);

        if (lukiokurssi.getTyyppi().isPaikallinen() && kurssi.getTyyppi().isPaikallinen()) {
            lukiokurssi.setTyyppi(kurssi.getTyyppi());
            resolvePaikallinenKurssityyppi(kurssi.getTyyppi().paikallinen().get(), lukiokurssi);
        }
    }

    @Override
    @Transactional
    public long disconnectKurssi(Long kurssiId, Long oppiaineId, Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        opetussuunnitelmaRepository.lock(ops);

        OppiaineLukiokurssi oaKurssi = oppiaineLukiokurssiRepository.findByOpsAndKurssi(opsId, kurssiId).stream()
                .findAny()
                .orElseThrow(() -> new BusinessRuleViolationException("Kurssia ei löytynyt."));

        Lukiokurssi kurssi = oaKurssi.getKurssi();
        Lukiokurssi copy = kurssi.copy();

        kurssi.getOppiaineet().forEach(oppiaineLukiokurssi -> {
            if (oppiaineLukiokurssi.getOpetussuunnitelma().getId().equals(opsId)
                    && oppiaineLukiokurssi.getOppiaine().getId().equals(oppiaineId)) {
                oppiaineLukiokurssi.setKurssi(copy);
                oppiaineLukiokurssi.setOma(true);
            }
        });

        opetussuunnitelmaRepository.flush();
        return copy.getId();
    }

    @Override
    @Transactional
    public long reconnectKurssi(Long kurssiId, Long oppiaineId, Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        opetussuunnitelmaRepository.lock(ops);
        OppiaineLukiokurssi oaKurssi = oppiaineLukiokurssiRepository.findByOpsAndKurssi(opsId, kurssiId).stream()
                .findAny()
                .orElseThrow(() -> new BusinessRuleViolationException("Kurssia ei löytynyt."));

        final UUID tunniste = oaKurssi.getKurssi().getTunniste();
        OppiaineLukiokurssi pohjanKurssi = ops.getPohja().getLukiokurssit().stream()
                .filter(a -> (a.getKurssi().getTunniste().compareTo(tunniste) == 0))
                .findAny().orElseThrow(() -> new BusinessRuleViolationException("Oppiainetta ei löytynyt."));

        oaKurssi.getKurssi().getOppiaineet().forEach(oppiaineLukiokurssi -> {
            if (oppiaineLukiokurssi.getOpetussuunnitelma().getId().equals(opsId)
                    && oppiaineLukiokurssi.getOppiaine().getId().equals(oppiaineId)) {
                oppiaineLukiokurssi.setKurssi(pohjanKurssi.getKurssi());
                oppiaineLukiokurssi.setOma(false);
            }
        });

        opetussuunnitelmaRepository.flush();
        return oaKurssi.getKurssi().getId();
    }

    @Override
    @Transactional
    public long saveAihekokonaisuus(long opsId, AihekokonaisuusSaveDto kokonaisuus) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        opetussuunnitelmaRepository.lock(ops);
        Aihekokonaisuus uusi = mapper.map(kokonaisuus, new Aihekokonaisuus(ops.getAihekokonaisuudet()));
        ops.getAihekokonaisuudet().getAihekokonaisuudet().add(uusi);
        opetussuunnitelmaRepository.flush();
        return uusi.getId();
    }

    @Override
    @Transactional
    public void reArrangeAihekokonaisuudet(long opsId, AihekokonaisuudetJarjestaDto jarjestys) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        opetussuunnitelmaRepository.lock(ops);
        Map<Long, Aihekokonaisuus> byId = ops.getAihekokonaisuudet().getAihekokonaisuudet()
                .stream().collect(toMap(Aihekokonaisuus::getId, ak -> ak));
        Long jnro = 1L;
        for (AihekokonaisuusJarjestysDto jarjestysDto : jarjestys.getAihekokonaisuudet()) {
            byId.get(jarjestysDto.getId()).setJnro(jnro++);
        }
    }

    @Override
    @Transactional
    public void updateAihekokonaisuusYleiskuvaus(long opsId, AihekokonaisuusSaveDto yleiskuvaus) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        opetussuunnitelmaRepository.lock(ops);
        mapper.map(yleiskuvaus, ops.getAihekokonaisuudet());
    }

    @Override
    @Transactional
    public void updateAihekokonaisuus(long opsId, long id, AihekokonaisuusSaveDto kokonaisuus) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        opetussuunnitelmaRepository.lock(ops);
        mapper.map(kokonaisuus, ops.getAihekokonaisuudet().getAihekokonaisuudet()
                .stream().filter(ak -> ak.getId().equals(id)).findFirst()
                .orElseThrow(() -> new BusinessRuleViolationException("Aihekokonaisuutta ei löytynyt.")));
    }

    @Override
    @Transactional
    public void deleteAihekokonaisuus(long opsId, long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        opetussuunnitelmaRepository.lock(ops);
        Aihekokonaisuus aihekokonaisuus = ops.getAihekokonaisuudet().getAihekokonaisuudet()
                .stream().filter(ak -> ak.getId().equals(id)
                        && ak.getParent() == null).findFirst()
                .orElseThrow(() -> new BusinessRuleViolationException("Aihekokonaisuutta ei löytynyt."));
        aihekokonaisuusRepository.findByParent(aihekokonaisuus.getId()).forEach(ak
                -> ak.setParent(null));
        aihekokonaisuus.setAihekokonaisuudet(null);
        ops.getAihekokonaisuudet().getAihekokonaisuudet().remove(aihekokonaisuus);
        aihekokonaisuusRepository.delete(aihekokonaisuus);
    }

    @Override
    @Transactional
    public void removeKurssi(long opsId, long kurssiId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        opetussuunnitelmaRepository.lock(ops);
        OppiaineLukiokurssi oaKurssi = oppiaineLukiokurssiRepository.findByOpsAndKurssi(opsId, kurssiId).stream().findAny()
                .orElseThrow(() -> new BusinessRuleViolationException("Kurssia ei löytynyt."));

        if (!oaKurssi.isOma()) {
            throw new BusinessRuleViolationException("kurssia ei voi poistaa");
        }
        if (!oaKurssi.getKurssi().getTyyppi().isPaikallinen() &&
                oaKurssi.getKurssi().getTyyppi().compareTo(LukiokurssiTyyppi.VALTAKUNNALLINEN_SOVELTAVA) != 0) {
            throw new BusinessRuleViolationException("Valtakunnallista kurssia ei voida poistaa.");
        }

        oaKurssi.getKurssi().getOppiaineet().clear();
        oppiaineLukiokurssiRepository.delete(oaKurssi);
    }

    @Override
    @Transactional
    public void updateTreeStructure(Long opsId, OppaineKurssiTreeStructureDto structureDto) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        opetussuunnitelmaRepository.lock(ops);

        Map<Long, LukioOppiaineJarjestys> byOppiaineId = jarjestysRepository.findByOpetussuunnitelmaId(ops.getId()).stream()
                .collect(toMap(j -> j.getId().getOppiaineId(), j -> j));
        for (OppiaineJarjestysDto oppiaineJarjestysDto : structureDto.getOppiaineet()) {
            if (byOppiaineId.get(oppiaineJarjestysDto.getId()) == null) {
                // Harvinainen case (järjestys puuttuu, on rikki, koitetaan korjata ja jos ei onnistuta, niin
                // epävalidi pyyntö:
                Oppiaine opsinOppiaine = ops.findOppiaine(oppiaineJarjestysDto.getId());
                if (opsinOppiaine == null) {
                    throw new BusinessRuleViolationException("Oppiaineelta " + oppiaineJarjestysDto.getId()
                            + " puuttuu järjestys.");
                }
                LukioOppiaineJarjestys jarjestys = new LukioOppiaineJarjestys(ops, opsinOppiaine, null);
                ops.getOppiaineJarjestykset().add(jarjestys);
                byOppiaineId.put(oppiaineJarjestysDto.getId(), jarjestys);
            }
            byOppiaineId.get(oppiaineJarjestysDto.getId()).setJarjestys(oppiaineJarjestysDto.getJarjestys());
        }

        Map<Long, Map<Long, OppiaineLukiokurssi>> oppiaineetByKurssiId = ops.getLukiokurssit().stream()
                .collect(groupingBy(oaLk -> oaLk.getKurssi().getId(),
                        mapping(oaLk -> oaLk, toMap(oaLk -> oaLk.getOppiaine().getId(), oaLk -> oaLk))));
        for (LukiokurssiOppaineMuokkausDto kurssiDto : structureDto.getKurssit()) {
            // Jos kurssitByOppiaineId olisi null, niin käyttöliittymältä on tullut laittomia kurssi id:itä,
            // sillä kurssin on aiemmin ollut pakko kuulua johonkin oppiaineeseen (liittämättömiä ei sallita):
            Map<Long, OppiaineLukiokurssi> kurssitByOppiaineId = oppiaineetByKurssiId.get(kurssiDto.getId());
            Set<Long> oppiaineIds = new HashSet<>(kurssitByOppiaineId.keySet());
            for (KurssinOppiaineDto oaDto : kurssiDto.getOppiaineet()) {
                OppiaineLukiokurssi existing = kurssitByOppiaineId.get(oaDto.getOppiaineId());
                if (existing != null) {
                    existing.setJarjestys(oaDto.getJarjestys());
                } else {
                    // oli jo oltava jossain (koska liittämättömiä ei voi olla):
                    OppiaineLukiokurssi onePrevious = kurssitByOppiaineId.values().iterator().next();
                    // oppiaineen löydyttävä entuudestaan:
                    Oppiaine oppiaine = byOppiaineId.get(oaDto.getOppiaineId()).getOppiaine();
                    if (oppiaine.isKoosteinen()) {
                        throw new BusinessRuleViolationException("Ei voida lisätä kurssia koosteiseen oppiaineeseen.");
                    }
                    ops.getLukiokurssit().add(new OppiaineLukiokurssi(ops,
                            oppiaine,
                            onePrevious.getKurssi(),
                            oaDto.getJarjestys(),
                            onePrevious.isOma() // sama oma-tila kaikkialla OPS:ssa missä samaa kurssia käytetty
                    ));
                }
                oppiaineIds.remove(oaDto.getOppiaineId()); // käsitelty
            }
            // Poistetaan käsittelemättömät oppiaine-liitokset:
            ops.getLukiokurssit().removeAll(oppiaineIds.stream().map(kurssitByOppiaineId::get).collect(toSet()));
        }
    }

}
