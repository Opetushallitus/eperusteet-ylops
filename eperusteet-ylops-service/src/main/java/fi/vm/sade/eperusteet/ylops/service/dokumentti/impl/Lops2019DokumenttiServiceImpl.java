package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl;

import fi.vm.sade.eperusteet.ylops.service.dokumentti.Lops2019DokumenttiService;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Slf4j
@Service
public class Lops2019DokumenttiServiceImpl implements Lops2019DokumenttiService {

    @Override
    public void addLops2019Sisalto(DokumenttiBase docBase) throws ParserConfigurationException, SAXException, IOException {
        log.info("addLops2019Sisalto");
    }

}
