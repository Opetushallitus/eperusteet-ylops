package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.TekstiKappaleViiteExportDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsExport;
import fi.vm.sade.eperusteet.ylops.service.ops.TekstiKappaleViiteService;
import fi.vm.sade.eperusteet.ylops.service.util.CollectionUtil;
import fi.vm.sade.eperusteet.ylops.service.util.JulkaisuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus.YKSINKERTAINEN;
import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;

@Component
@Transactional
public class OpsExportDefaultImpl implements OpsExport {

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
    private TekstiKappaleViiteService tekstiKappaleViiteService;

    @Override
    public <T extends OpetussuunnitelmaExportDto> T export(Long opsId, Class<T> clz) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        T dto = mapper.map(ops, clz);
        PerusteInfoDto peruste = lops2019Service.getPeruste(opsId);
        dto.setPeruste(peruste);
        opetussuunnitelmaService.fetchKuntaNimet(dto);
        opetussuunnitelmaService.fetchOrganisaatioNimet(dto);

        CollectionUtil.treeToStream(dto.getTekstit(), TekstiKappaleViiteExportDto.Puu::getLapset)
                .filter(viite -> viite.getTekstiKappale() != null)
                .forEach(tekstiKappaleViite -> {
                    List<TekstiKappaleViiteExportDto> viiteExports =  tekstiKappaleViiteService.getPohjanTekstikappaleViite(opsId, tekstiKappaleViite.getId(), TekstiKappaleViiteExportDto.class);
                    if (!viiteExports.isEmpty()) {
                        tekstiKappaleViite.setPohjanTeksti(viiteExports.get(0));
                        if (viiteExports.size() == 2) {
                            tekstiKappaleViite.getPohjanTeksti().setPohjanTeksti(viiteExports.get(1));
                        }
                    }

                    if (JulkaisuUtil.opetussuunnitelmanPerusteDataMergeTuettu(dto)) {
                        dto.setSisaltaaPerusteenTekstit(true);

                        fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto perusteenTekstikappaleViiteDto = tekstiKappaleViiteService.getPerusteTekstikappale(opsId, tekstiKappaleViite.getId());
                        if (perusteenTekstikappaleViiteDto != null && perusteenTekstikappaleViiteDto.getTekstiKappale() != null) {
                            TekstiKappaleDto perusteenTekstikappelDto = new TekstiKappaleDto();
                            perusteenTekstikappelDto.setNimi(LokalisoituTekstiDto.of(perusteenTekstikappaleViiteDto.getTekstiKappale().getNimi().getTekstit()));

                            if (perusteenTekstikappaleViiteDto.getTekstiKappale().getTeksti() != null) {
                                perusteenTekstikappelDto.setTeksti(LokalisoituTekstiDto.of(perusteenTekstikappaleViiteDto.getTekstiKappale().getTeksti().getTekstit()));
                            }

                            tekstiKappaleViite.setPerusteenTekstikappale(perusteenTekstikappelDto);
                        }
                    }
                });

        return dto;
    }

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(YKSINKERTAINEN);
    }
}
