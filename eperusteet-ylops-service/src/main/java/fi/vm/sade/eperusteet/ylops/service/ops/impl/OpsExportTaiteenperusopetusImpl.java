package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.export.OpetussuunnitelmaExportTpoDto;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsExport;
import fi.vm.sade.eperusteet.ylops.service.tpo.TaiteenperusopetusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Transactional
public class OpsExportTaiteenperusopetusImpl implements OpsExport {

    @Autowired
    private OpsDispatcher dispatcher;

    @Autowired
    private TaiteenperusopetusService taiteenperusopetusService;

    @Override
    public <T extends OpetussuunnitelmaExportDto> T export(Long opsId, Class<T> clz) {
        OpetussuunnitelmaExportTpoDto result = (OpetussuunnitelmaExportTpoDto) dispatcher
                .get(KoulutustyyppiToteutus.YKSINKERTAINEN, OpsExport.class).export(opsId, clz);
        result.setTaiteenalat(taiteenperusopetusService.getTaiteenalat(opsId));
        return clz.cast(result);
    }

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.TPO);
    }

    @Override
    public Class<? extends OpetussuunnitelmaExportDto> getExportClass() {
        return OpetussuunnitelmaExportTpoDto.class;
    }
}
