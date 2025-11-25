package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaInfoDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsPohjanVaihto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Transactional
public class OpsPohjanVaihtoPerusopetusImpl implements OpsPohjanVaihto {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Override
    public void vaihdaPohja(Long opsId, Long pohjaId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findById(opsId).orElseThrow();
        Opetussuunnitelma vanha = opetussuunnitelmaRepository.findById(ops.getPohja().getId()).orElseThrow();
        Opetussuunnitelma uusi = opetussuunnitelmaRepository.findById(pohjaId).orElseThrow();

        if (pohjaId.equals(ops.getPohja().getId()) || pohjaId.equals(opsId)) {
            throw new BusinessRuleViolationException("virheellinen-pohja");
        }

        if (!vanha.getCachedPeruste().getPerusteId().equals(uusi.getCachedPeruste().getPerusteId())) {
            throw new BusinessRuleViolationException("pohja-vaihdettavissa-vain-samaan-perusteeseen");
        }

        ops.setPohja(uusi);
    }

    @Override
    public Set<OpetussuunnitelmaInfoDto> haeVaihtoehdot(Long opsId) {
        return Set.of();
    }

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Set.of(KoulutustyyppiToteutus.PERUSOPETUS);
    }
}
