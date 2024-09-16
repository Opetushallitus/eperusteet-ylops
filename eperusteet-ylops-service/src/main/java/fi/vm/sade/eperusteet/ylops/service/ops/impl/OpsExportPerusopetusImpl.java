package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLaajaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineExportDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsExport;
import fi.vm.sade.eperusteet.ylops.service.ops.VuosiluokkakokonaisuusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Set;
import java.util.UUID;

@Component
@Transactional
public class OpsExportPerusopetusImpl implements OpsExport {

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
    private OppiaineService oppiaineService;

    @Autowired
    private VuosiluokkakokonaisuusService vuosiluokkakokonaisuusService;

    @Override
    public OpetussuunnitelmaExportDto export(Long opsId) {
        OpetussuunnitelmaLaajaDto dto = (OpetussuunnitelmaLaajaDto) dispatcher.get(KoulutustyyppiToteutus.YKSINKERTAINEN, OpsExport.class).export(opsId);

        dto.getOppiaineet().forEach(opsOppiaine -> {
            opsOppiaine.getOppiaine().setPohjanOppiaine(oppiaineService.getPohjanVastaavaOppiaine(opsId, opsOppiaine.getOppiaine().getId(), OppiaineExportDto.class));
            if (!ObjectUtils.isEmpty(opsOppiaine.getOppiaine().getOppimaarat())) {
                opsOppiaine.getOppiaine().getOppimaarat().forEach(oppimaara -> oppimaara.setPohjanOppiaine(oppiaineService.getPohjanVastaavaOppiaine(opsId, oppimaara.getId(), OppiaineExportDto.class)));
            }
        });

        dto.getVuosiluokkakokonaisuudet().forEach(vlk -> {
            vlk.setPohjanVuosiluokkakokonaisuus(vuosiluokkakokonaisuusService.getPohjanVuosiluokkakokonaisuus(opsId, UUID.fromString(vlk.getVuosiluokkakokonaisuus().getTunniste().toString())).getVuosiluokkakokonaisuus());
        });

        return dto;
    }

    @Override
    public Class getExportClass() {
        return OpetussuunnitelmaLaajaDto.class;
    }

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.PERUSOPETUS);
    }


}
