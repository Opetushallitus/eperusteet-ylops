package fi.vm.sade.eperusteet.ylops.service.dokumentti;

import fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiBase;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * @author isaul
 */
public interface YleisetOsuudetService {
    void addYleisetOsuudet(DokumenttiBase docBase);

    void addLiitteet(DokumenttiBase docBase);
}
