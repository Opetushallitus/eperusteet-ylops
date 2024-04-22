package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Getter
@Setter
public class DokumenttiBase {
    Document document;
    Element headElement;
    Element bodyElement;
    Opetussuunnitelma ops;
    PerusteDto perusteDto;
    CharapterNumberGenerator generator;
    Kieli kieli;
    Dokumentti dokumentti;
    DtoMapper mapper;
}
