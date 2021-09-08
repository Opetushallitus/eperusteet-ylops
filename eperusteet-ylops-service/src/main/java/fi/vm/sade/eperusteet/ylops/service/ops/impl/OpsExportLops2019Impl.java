package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.*;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.export.Lops2019OpintojaksoExportDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.export.Lops2019OppiaineExportDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.export.Lops2019PaikallinenOppiaineExportDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.export.OpetussuunnitelmaExportLops2019Dto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpsSisaltoViite;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.Lops2019SisaltoDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OpintojaksoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;

@Component
@Transactional
public class OpsExportLops2019Impl implements OpsExport {

    @Autowired
    private OpsDispatcher dispatcher;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private Lops2019Service lops2019Service;

    @Autowired
    private Lops2019OppiaineService lops2019OppiaineService;

    @Autowired
    private Lops2019OpintojaksoService lops2019OpintojaksoService;

    private <T extends Lops2019OpintojaksoDto> Map<String, LinkedHashSet<OpsSisaltoViite.Opintojakso>> getOppiaineenOpintojaksot(Opetussuunnitelma ops, List<T> opintojaksot) {
        Map<String, LinkedHashSet<OpsSisaltoViite.Opintojakso>> oppiaineenOpintojaksot = new HashMap<>();
        for (T oj : opintojaksot) {
            OpsSisaltoViite.Opintojakso koodi = new OpsSisaltoViite.Opintojakso(ops.getId(), oj.getKoodi(), oj.getId());
            for (Lops2019OpintojaksonOppiaineDto ojoppiaine : oj.getOppiaineet()) {
                if (!oppiaineenOpintojaksot.containsKey(ojoppiaine.getKoodi())) {
                    oppiaineenOpintojaksot.put(ojoppiaine.getKoodi(), new LinkedHashSet<>());
                }
                oppiaineenOpintojaksot.get(ojoppiaine.getKoodi()).add(koodi);
            }
        }
        return oppiaineenOpintojaksot;
    }

    private void lisaaPaikallisenOpintojaksot(List<Lops2019PaikallinenOppiaineExportDto> oppiaineet, Map<String, LinkedHashSet<OpsSisaltoViite.Opintojakso>> opintojaksot) {
        for (Lops2019PaikallinenOppiaineExportDto oppiaine : oppiaineet) {
            if (opintojaksot.containsKey(oppiaine.getKoodi())) {
                oppiaine.getOpintojaksot().addAll(opintojaksot.get(oppiaine.getKoodi()));
            }
        }
    }

    private void lisaaValtakunnallisenOpintojaksot(List<Lops2019OppiaineExportDto> oppiaineet, Map<String, LinkedHashSet<OpsSisaltoViite.Opintojakso>> opintojaksot) {
        for (Lops2019OppiaineExportDto oppiaine : oppiaineet) {
            if (opintojaksot.containsKey(oppiaine.getKoodi().getUri())) {
                oppiaine.getOpintojaksot().addAll(opintojaksot.get(oppiaine.getKoodi().getUri()));
            }
            lisaaValtakunnallisenOpintojaksot(oppiaine.getOppimaarat(), opintojaksot);
        }
    }

    @Override
    public OpetussuunnitelmaExportDto export(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        OpetussuunnitelmaExportLops2019Dto result = mapper.map(ops, OpetussuunnitelmaExportLops2019Dto.class);
        List<Lops2019OpintojaksoExportDto> opintojaksot = lops2019OpintojaksoService.getAllTuodut(ops.getId(), Lops2019OpintojaksoExportDto.class);
        List<Lops2019PaikallinenOppiaineExportDto> paikallisetOppiaineet = lops2019OppiaineService.getAll(opsId, Lops2019PaikallinenOppiaineExportDto.class);
        PerusteInfoDto peruste = lops2019Service.getPeruste(opsId);
        Lops2019SisaltoDto perusteSisalto = lops2019Service.getPerusteSisalto(opsId);
        List<Lops2019OppiaineExportDto> oppiaineet = mapper.mapAsList(perusteSisalto.getOppiaineet(), Lops2019OppiaineExportDto.class);

        Map<String, LinkedHashSet<OpsSisaltoViite.Opintojakso>> oppiaineenOpintojaksot = getOppiaineenOpintojaksot(ops, opintojaksot);
        lisaaPaikallisenOpintojaksot(paikallisetOppiaineet, oppiaineenOpintojaksot);
        lisaaValtakunnallisenOpintojaksot(oppiaineet, oppiaineenOpintojaksot);

        result.setPeruste(peruste);
        result.setLaajaAlaisetOsaamiset(perusteSisalto.getLaajaAlainenOsaaminen().getLaajaAlaisetOsaamiset());
        result.setOpintojaksot(opintojaksot);
        result.setValtakunnallisetOppiaineet(oppiaineet);
        result.setPaikallisetOppiaineet(paikallisetOppiaineet);

        opetussuunnitelmaService.fetchKuntaNimet(result);
        opetussuunnitelmaService.fetchOrganisaatioNimet(result);
        return result;
    }

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.LOPS2019);
    }

    @Override
    public Class getExportClass() {
        return OpetussuunnitelmaExportLops2019Dto.class;
    }
}
