package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.LokalisointiDto;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.LocalizedMessagesService;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.LokalisointiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Slf4j
@Service
public class LocalizedMessagesServiceImpl implements LocalizedMessagesService {
    @Value("${lokalisointi.service.url:https://virkailija.opintopolku.fi/lokalisointi/cxf/rest/v1/localisation?}")
    private String lokalisointiServiceUrl;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LokalisointiService lokalisointiService;

    @Override
    public String translate(String key, Kieli kieli) {

        // Koitetaan hakea lokalisointipalvelimelta käänös
        LokalisointiDto valueDto = lokalisointiService.get(key, kieli.toString());
        if (valueDto != null) {
            return valueDto.getValue();
        }

        try {
            return messageSource.getMessage(key, null, Locale.forLanguageTag(kieli.toString()));
        } catch (NoSuchMessageException ex) {
            return "[" + kieli.toString() + " " + key + "]";
        }
    }



}
