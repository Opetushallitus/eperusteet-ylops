package fi.vm.sade.eperusteet.ylops.service.dokumentti;

import fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiBase;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public interface LukioService {
    void addOppimistavoitteetJaOpetuksenKeskeisetSisallot(DokumenttiBase docBase) throws ParserConfigurationException, SAXException, IOException;
}
