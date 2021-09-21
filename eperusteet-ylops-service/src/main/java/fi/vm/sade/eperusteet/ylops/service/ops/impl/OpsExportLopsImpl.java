package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.export.OpetussuunnitelmaExportLopsDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOpetussuunnitelmaRakenneOpsDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppiaineTiedotDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppiaineTiedotExportDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsExport;
import fi.vm.sade.eperusteet.ylops.service.ops.lukio.LukioOpetussuunnitelmaService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;

@Component
@Transactional
public class OpsExportLopsImpl implements OpsExport {

    @Autowired
    private OpsDispatcher dispatcher;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private LukioOpetussuunnitelmaService lukioOpetussuunnitelmaService;

    @Autowired
    private Lops2019Service lops2019Service;

    @Override
    public OpetussuunnitelmaExportDto export(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        OpetussuunnitelmaExportLopsDto result = mapper.map(ops, OpetussuunnitelmaExportLopsDto.class);
        PerusteInfoDto peruste = lops2019Service.getPeruste(opsId);

        LukioOpetussuunnitelmaRakenneOpsDto rakenne = lukioOpetussuunnitelmaService.getRakenne(opsId);
        List<LukioOppiaineTiedotExportDto> oppiaineet = rakenne.getOppiaineet().stream().map(oppiaine -> {
            LukioOppiaineTiedotDto tiedot = lukioOpetussuunnitelmaService.getOppiaineTiedot(opsId, oppiaine.getId());
            List<LukioOppiaineTiedotDto> oppimaarat = tiedot.getOppimaarat().stream()
                    .map(oppimaara -> lukioOpetussuunnitelmaService.getOppiaineTiedot(opsId, oppimaara.getId())).collect(Collectors.toList());
            tiedot.setOppimaarat(null);
            return LukioOppiaineTiedotExportDto.builder().tiedot(tiedot).oppimaarat(oppimaarat).build();
        }).collect(Collectors.toList());

        result.setPeruste(peruste);
        result.setOppiaineet(oppiaineet);

        opetussuunnitelmaService.fetchKuntaNimet(result);
        opetussuunnitelmaService.fetchOrganisaatioNimet(result);
        return result;
    }

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.LOPS);
    }

    @Override
    public Class getExportClass() {
        return OpetussuunnitelmaExportLopsDto.class;
    }
}
