package fi.vm.sade.eperusteet.ylops.service.ops.impl.navigationpublic;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLaajaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineExportDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineenVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpsOppiaineExportDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpsVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.VuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOppiaineDto;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.EperusteetPerusteDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.OppiaineDto;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilderPublic;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import fi.vm.sade.eperusteet.ylops.service.util.NavigationUtil;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Transactional
public class NavigationBuilderPerusopetusPublicImpl implements NavigationBuilderPublic {

    @Autowired
    private OpsDispatcher dispatcher;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private EperusteetService eperusteetService;

    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.PERUSOPETUS);
    }

    @Override
    public NavigationNodeDto buildNavigation(Long opsId, Integer revision) {
        return buildNavigation(opsId, "fi", revision);
    }

    @SneakyThrows
    @Override
    public NavigationNodeDto buildNavigation(Long opsId, String kieli, Integer revision) {

        OpetussuunnitelmaLaajaDto opetussuunnitelmaDto = (OpetussuunnitelmaLaajaDto) opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(opsId, revision);
        EperusteetPerusteDto eperusteetPerusteDto = objectMapper.treeToValue(opetussuunnitelmaService.getJulkaistuOpetussuunnitelmaPeruste(opsId), EperusteetPerusteDto.class);
        Map<UUID, OppiaineDto> perusteenOppiaineet =
                Stream.concat(
                                eperusteetPerusteDto.getPerusopetus().getOppiaineet().stream(),
                                eperusteetPerusteDto.getPerusopetus().getOppiaineet().stream()
                                        .filter(oa -> oa.getOppimaarat() != null)
                                        .map(OppiaineDto::getOppimaarat)
                                        .flatMap(Collection::stream))
                        .collect(Collectors.toMap(OppiaineDto::getTunniste, o -> o));

        List<OpsVuosiluokkakokonaisuusDto> vuosiluokkakokonaisuudet = opetussuunnitelmaDto.getVuosiluokkakokonaisuudet().stream()
                .sorted(Comparator.comparing(vlk -> vlk.getVuosiluokkakokonaisuus().getNimi().getOrDefault(Kieli.of(kieli))))
                .collect(Collectors.toList());

        List<OppiaineExportDto> oppiaineet = opetussuunnitelmaDto.getOppiaineet().stream()
                .sorted(Comparator.comparing(o -> o.getOppiaine().getNimi().getOrDefault(Kieli.of(kieli))))
                .sorted(Comparator.comparing(o -> o.getJnro() != null ? o.getJnro() : Integer.MAX_VALUE))
                .map(OpsOppiaineExportDto::getOppiaine)
                .collect(Collectors.toList());

        return NavigationUtil.initPublic()
                .add(NavigationNodeDto.of(NavigationType.tavoitteet_sisallot_arviointi))
                .addAll(vuosiluokkakokonaisuudet(vuosiluokkakokonaisuudet, oppiaineet, perusteenOppiaineet, kieli))
                .add(perusopetusOppiaineet(oppiaineet, kieli, perusteenOppiaineet).meta(NavigationUtil.POST_SEPARATOR, true))
                .addAll(dispatcher.get(NavigationBuilderPublic.class)
                        .buildNavigation(opsId, revision).getChildren().stream().filter(node -> !node.getType().equals(NavigationType.tiedot)));
    }

    private List<NavigationNodeDto> vuosiluokkakokonaisuudet(
            List<OpsVuosiluokkakokonaisuusDto> opsVuosiluokkakokonaisuudet,
            List<OppiaineExportDto> oppiaineet,
            Map<UUID, OppiaineDto> perusteenOppiaineet,
            String kieli) {
        return opsVuosiluokkakokonaisuudet.stream()
                .map(opsvlk -> {
                        VuosiluokkakokonaisuusDto vlk = opsvlk.getVuosiluokkakokonaisuus();
                        return NavigationNodeDto.of(NavigationType.vuosiluokkakokonaisuus, vlk.getNimi(), vlk.getId())
                                .addAll(perusopetusOppiaine(oppiaineet.stream()
                                                .filter(oppiaine -> oppiaine.getTyyppi() == OppiaineTyyppi.YHTEINEN)
                                                .filter(oppiaine -> naytetaanOppiaine(oppiaine, opsvlk, perusteenOppiaineet.get(oppiaine.getTunniste()))
                                                || (oppiaine.getOppimaarat() != null && oppiaine.getOppimaarat().stream()
                                                        .anyMatch(oppimaara -> naytetaanOppiaine(oppimaara, opsvlk, perusteenOppiaineet.get(oppiaine.getTunniste())))))
                                                .collect(Collectors.toList()),
                                        kieli,
                                        opsvlk,
                                        perusteenOppiaineet))
                                .add(valinnaisetOppiaineet(oppiaineet.stream()
                                                .filter(oppiaine -> oppiaine.getVuosiluokkakokonaisuudet().stream()
                                                        .map(OppiaineenVuosiluokkakokonaisuusDto::getVuosiluokkakokonaisuus)
                                                        .toList()
                                                        .contains(vlk.getTunniste()))
                                                .collect(Collectors.toList()),
                                        kieli,
                                        opsvlk));
                        }
                )
                .collect(Collectors.toList());
    }

    public boolean naytetaanOppiaine(OppiaineExportDto oppiaine, OpsVuosiluokkakokonaisuusDto opsVlk, OppiaineDto perusteenOppiaine) {
        return oppiaine.getVuosiluokkakokonaisuudet().stream()
                .anyMatch(vlk -> vlk.getVuosiluokkakokonaisuus().equals(opsVlk.getVuosiluokkakokonaisuus().getTunniste())
                        && (perusteenOppiaine == null || perusteenOppiaine.getVuosiluokkakokonaisuus(opsVlk.getVuosiluokkakokonaisuus().getTunniste()).isPresent())
                        && (vlk.getPiilotettu() == null || !vlk.getPiilotettu())
                        && (opsVlk.getLisatieto() == null || !opsVlk.getLisatieto().getPiilotetutOppiaineet().contains(oppiaine.getId())));
    }

    private NavigationNodeDto perusopetusOppiaineet(List<OppiaineExportDto> oppiaineet, String kieli, Map<UUID, OppiaineDto> perusteenOppiaineet) {
        List<OppiaineExportDto> eiValinnaiset = oppiaineet.stream().filter(oppiaine -> oppiaine.getTyyppi() == OppiaineTyyppi.YHTEINEN).collect(Collectors.toList());
        return NavigationNodeDto.of(NavigationType.perusopetusoppiaineet)
                .addAll(perusopetusOppiaine(eiValinnaiset, kieli, null, perusteenOppiaineet))
                .add(valinnaisetOppiaineet(oppiaineet, kieli, null));
    }

    private NavigationNodeDto valinnaisetOppiaineet(List<OppiaineExportDto> oppiaineet, String kieli, OpsVuosiluokkakokonaisuusDto opsVlk) {
        List<OppiaineExportDto> valinnaiset = oppiaineet.stream().filter(oppiaine -> oppiaine.getTyyppi() != OppiaineTyyppi.YHTEINEN).collect(Collectors.toList());
        if (valinnaiset.isEmpty()) {
            return null;
        }

        return NavigationNodeDto.of(NavigationType.valinnaisetoppiaineet).meta("vlkId", opsVlk != null && opsVlk.getVuosiluokkakokonaisuus() != null ? opsVlk.getVuosiluokkakokonaisuus().getId() : null)
                .addAll(perusopetusOppiaine(valinnaiset, kieli, opsVlk, Map.of()));
    }

    private Collection<NavigationNodeDto> perusopetusOppiaine(Collection<OppiaineExportDto> oppiaineet, String kieli, OpsVuosiluokkakokonaisuusDto opsVlk, Map<UUID, OppiaineDto> perusteenOppiaineet) {
        if (oppiaineet == null) {
            return Collections.emptyList();
        }

        return oppiaineet.stream()
                .map(oppiaine -> {
                    NavigationNodeDto oppiaineNavigationNode = NavigationNodeDto.of(NavigationType.perusopetusoppiaine, oppiaine.getNimi(), oppiaine.getId())
                            .meta("vlkId", opsVlk != null ? opsVlk.getVuosiluokkakokonaisuus().getId() : null);

                    List<OppiaineExportDto> oppimaarat = oppiaine.getOppimaarat() != null ? oppiaine.getOppimaarat().stream()
                            .filter(oppimaara -> opsVlk == null || naytetaanOppiaine(oppimaara, opsVlk, perusteenOppiaineet.get(oppimaara.getTunniste())))
                            .collect(Collectors.toList()) : Collections.emptyList();
                    if (!CollectionUtils.isEmpty(oppimaarat)) {
                        oppiaineNavigationNode.add(NavigationNodeDto.of(NavigationType.oppimaarat).meta("navigation-subtype", true)
                                .addAll(perusopetusOppiaine(oppimaarat.stream()
                                                .sorted(Comparator.comparing(o -> o.getNimi().getOrDefault(Kieli.of(kieli))))
                                                .collect(Collectors.toList()),
                                        kieli,
                                        opsVlk,
                                        perusteenOppiaineet)));
                    }
                    return oppiaineNavigationNode;
                })
                .collect(Collectors.toList());
    }
}
