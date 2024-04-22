package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaInfoDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstikappaleviiteRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaHierarkiaKopiointiService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsPohjanVaihto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional
public class OpsPohjanVaihtoLops2019Impl implements OpsPohjanVaihto {
    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private TekstikappaleviiteRepository tekstikappaleviiteRepository;

    @Autowired
    private TekstiKappaleRepository tekstiKappaleRepository;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private OpetussuunnitelmaHierarkiaKopiointiService hierarkiaKopiointiService;

    @Override
    public void vaihdaPohja(Long opsId, Long pohjaId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.getOne(opsId);
        Opetussuunnitelma vanha = opetussuunnitelmaRepository.getOne(ops.getPohja().getId());
        Opetussuunnitelma uusi = opetussuunnitelmaRepository.getOne(pohjaId);
        if (pohjaId.equals(ops.getPohja().getId()) || pohjaId.equals(opsId)) {
            throw new BusinessRuleViolationException("virheellinen-pohja");
        }

        if (!vanha.getCachedPeruste().getPerusteId().equals(uusi.getCachedPeruste().getPerusteId())) {
            throw new BusinessRuleViolationException("pohja-vaihdettavissa-vain-samaan-perusteeseen");
        }

        Set<String> a = ops.getOrganisaatiot();
        Set<String> b = uusi.getOrganisaatiot();
        if (!a.containsAll(b)) {
            throw new BusinessRuleViolationException("uuden-pohjan-organisaatiot-vaarat");
        }

        hierarkiaKopiointiService.kopioiPohjanRakenne(ops, uusi);
        ops.setPohja(uusi);
    }

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.LOPS2019);
    }

    @Override
    public Set<OpetussuunnitelmaInfoDto> haeVaihtoehdot(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.getOne(opsId);
        Long perusteId = ops.getCachedPeruste().getPerusteId();
        Set<OpetussuunnitelmaInfoDto> pohjat = opetussuunnitelmaService.getOpetussuunnitelmaOpsPohjat().stream()
                .filter(pohja -> Objects.equals(pohja.getPerusteenId(), perusteId))
                .filter(pohja -> !Objects.equals(pohja.getId(), ops.getPohja().getId()))
                .filter(pohja -> !Objects.equals(pohja.getId(), opsId))
                .collect(Collectors.toSet());
        return pohjat;
    }
}
