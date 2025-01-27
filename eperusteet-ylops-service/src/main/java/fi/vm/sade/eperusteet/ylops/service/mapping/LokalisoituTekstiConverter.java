package fi.vm.sade.eperusteet.ylops.service.mapping;

import fi.vm.sade.eperusteet.ylops.domain.ops.KommenttiKahva;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.Kommentti2019Repository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.KommenttiKahvaRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.LokalisoituTekstiRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class LokalisoituTekstiConverter extends BidirectionalConverter<LokalisoituTeksti, LokalisoituTekstiDto> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LokalisoituTekstiRepository repository;

    @Autowired
    private KommenttiKahvaRepository kommenttiKahvaRepository;

    @Autowired
    private Kommentti2019Repository kommentti2019Repository;

//    @Autowired
//    private KayttajanTietoService kayttajanTietoService;

    @Override
    public LokalisoituTekstiDto convertTo(LokalisoituTeksti tekstiPalanen, Type<LokalisoituTekstiDto> type, MappingContext mappingContext) {
        return new LokalisoituTekstiDto(tekstiPalanen.getId(), tekstiPalanen.getTunniste(), tekstitWithKommentit(tekstiPalanen));
    }


    private Map<Kieli, String> tekstitWithKommentit(LokalisoituTeksti source) {
        Map<Kieli, String> map = source.getTeksti();

        Principal ap = SecurityUtil.getAuthenticatedPrincipal();

        if (ap == null || org.springframework.util.StringUtils.isEmpty(ap.getName())) {
            return map;
        }

        try {
            ArrayList<String> h = Collections.list(request.getHeaders("disable-comments"));
            if (!h.isEmpty()) {
                return map;
            }
        } catch (IllegalStateException e) {
            // Dokumenttigeneraattori ajetaan async scopessa
            return map;
        }

        if (!source.getKetjut().isEmpty()) {
            List<KommenttiKahva> ketjut = source.getKetjut().stream()
                    .sorted(Comparator.comparingInt(KommenttiKahva::getStart).reversed())
                    .filter(kahva -> kommentti2019Repository.countByThread(kahva.getThread()) > 0)
                    .collect(Collectors.toList());

            for (KommenttiKahva kahva : ketjut) {
                String kaannos = map.getOrDefault(kahva.getKieli(), "");
                if (!StringUtils.isEmpty(kaannos) && kaannos.length() > kahva.getStop()) {
                    String left = kaannos.substring(0, kahva.getStart());
                    String middle = kaannos.substring(kahva.getStart(), kahva.getStop());
                    String right = kaannos.substring(kahva.getStop());
                    String result = left + "<span kommentti=\"" + kahva.getThread().toString() + "\">" + middle + "</span>" + right;
                    map.put(kahva.getKieli(), result);
                }
            }
        }

        return map;
    }

    /**
     * Extracts all comment-tags from source
     *
     * @param targetDto
     * @param source
     */
    static public LokalisoituTeksti extractComments(LokalisoituTekstiDto targetDto, LokalisoituTeksti source) {
        Set<KommenttiKahva> ketjut = source.getKetjut();
        Map<UUID, KommenttiKahva> ketjuMap = ketjut.stream()
                .collect(Collectors.toMap(KommenttiKahva::getThread, Function.identity()));
        Set<UUID> ketjuSet = ketjuMap.keySet();

        Map<Kieli, String> uudetTekstit = new HashMap<>();
        Set<KommenttiKahva> uudetKetjut = new HashSet<>();

        targetDto.getTekstit().forEach((key, value) -> {
            if (StringUtils.isEmpty(value)) {
                return;
            }

            Document parsed = Jsoup.parse(value);
            Elements elems = parsed.select("span[kommentti]");

            if (elems.isEmpty()) {
                return;
            }

            List<UUID> kommentitList = elems.stream().map(el -> UUID.fromString(el.attr("kommentti"))).collect(Collectors.toList());
            HashSet<UUID> kommentitSet = new HashSet<>(kommentitList);

            // Sama kommenttiketju saa löytyä rakenteesta vain kerran
            if (kommentitList.size() != kommentitSet.size()) {
                throw new BusinessRuleViolationException("sama-ketju-saa-olla-vain-kerran");
            }

            if (!ketjuSet.containsAll(kommentitSet)) {
                throw new BusinessRuleViolationException("uusia-ketjuja-ei-saa-lisata-editoinnissa");
            }

            String htmlStr = parsed.select("body").html();
            int offset = 0;

            for (Element el : elems) {
                UUID kommenttiUuid = UUID.fromString(el.attr("kommentti"));
                String targetOuterHtml = el.outerHtml();
                String targetInnerHtml = el.html();
                int start = htmlStr.indexOf(targetOuterHtml) - offset;
                int stop = start + targetInnerHtml.length();
                offset += targetOuterHtml.length() - targetInnerHtml.length();
                KommenttiKahva uusi = KommenttiKahva.copy(ketjuMap.get(kommenttiUuid), start, stop);
                uudetKetjut.add(uusi);
            }

            elems.unwrap();
            String inner = parsed.select("body").html();
            uudetTekstit.put(key, inner);
        });

        LokalisoituTeksti result = LokalisoituTeksti.of(uudetTekstit, source.getTunniste());
        if (result != null) {
            result.setKetjut(uudetKetjut);
        }
        return result;
    }

    @Override
    public LokalisoituTeksti convertFrom(LokalisoituTekstiDto dto, Type<LokalisoituTeksti> type, MappingContext mappingContext) {

        if (dto.getId() != null) {
            /*
            Jos id on mukana, yritä yhdistää olemassa olevaan tekstipalaseen
            Koska tekstipalanen on muuttumaton ja cachetettu, niin oletustapaus on että
            tekstipalanen on jo cachessa (luettu aikaisemmin) ja tietokantahaku vältetään.
            Huom! vihamielinen/virheellinen client voisi keksiä id:n aiheuttaen turhia tietokantahakuja.
            */
            LokalisoituTeksti current = repository.findOne(dto.getId());
            if (current != null) {
                Map<Kieli, String> teksti = current.getTeksti();
                teksti.putAll(dto.getTekstit());
                LokalisoituTeksti tekstiPalanen = LokalisoituTeksti.of(teksti, current.getTunniste());
                if (current.equals(tekstiPalanen)) {
                    return current;
                }
                else {
                    LokalisoituTeksti withExtracted = extractComments(dto, current);
                    if (withExtracted != null) {
                        return withExtracted;
                    }
                    else {
                        return tekstiPalanen;
                    }
                }
            }
        }
        return LokalisoituTeksti.of(dto.getTekstit());
    }
}
