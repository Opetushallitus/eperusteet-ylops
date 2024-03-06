package fi.vm.sade.eperusteet.ylops.service.dokumentti;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.service.exception.DokumenttiException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public interface DokumenttiBuilderService {

    @PreAuthorize("isAuthenticated()")
    byte[] generatePdf(Opetussuunnitelma ops, Dokumentti dokumentti, Kieli kieli)
            throws TransformerException, IOException, SAXException,
            ParserConfigurationException, NullPointerException, DokumenttiException;
}
