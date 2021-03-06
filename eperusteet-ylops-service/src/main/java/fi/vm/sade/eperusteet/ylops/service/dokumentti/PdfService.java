package fi.vm.sade.eperusteet.ylops.service.dokumentti;


import fi.vm.sade.eperusteet.utils.dto.dokumentti.DokumenttiMetaDto;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.IOException;

/**
 * @author isaul
 */
public interface PdfService {
    byte[] xhtml2pdf(Document document, DokumenttiMetaDto meta) throws IOException, TransformerException, SAXException;
}
