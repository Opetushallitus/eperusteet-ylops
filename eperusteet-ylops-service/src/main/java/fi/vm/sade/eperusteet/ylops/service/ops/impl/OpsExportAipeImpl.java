package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.export.OpetussuunnitelmaExportAipeDto;
import fi.vm.sade.eperusteet.ylops.service.aipe.AIPEService;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Transactional
public class OpsExportAipeImpl implements OpsExport {

    @Autowired
    private OpsDispatcher dispatcher;

    @Autowired
    private AIPEService aipeService;

    @Autowired
    private DtoMapper mapper;

    @Override
    public <T extends OpetussuunnitelmaExportDto> T export(Long opsId, Class<T> clz) {
        OpetussuunnitelmaExportDto base = dispatcher
                .get(KoulutustyyppiToteutus.YKSINKERTAINEN, OpsExport.class)
                .export(opsId, OpetussuunnitelmaExportDto.class);
        OpetussuunnitelmaExportAipeDto result = mapper.map(base, OpetussuunnitelmaExportAipeDto.class);
        result.setAipe(aipeService.getExportSisalto(opsId));
        return clz.cast(result);
    }

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.AIPE);
    }

    @Override
    public Class<? extends OpetussuunnitelmaExportDto> getExportClass() {
        return OpetussuunnitelmaExportAipeDto.class;
    }
}
