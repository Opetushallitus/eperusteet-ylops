/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.koodisto.KoodistoKoodi;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoMetadataDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.TermiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.*;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.CharapterNumberGenerator;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiBase;
import fi.vm.sade.eperusteet.ylops.service.exception.DokumenttiException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.external.KoodistoService;
import fi.vm.sade.eperusteet.ylops.service.external.OrganisaatioService;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.LiiteService;
import fi.vm.sade.eperusteet.ylops.service.ops.TermistoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiUtils.getTextString;

/**
 *
 * @author iSaul
 */
@Service
public class DokumenttiBuilderServiceImpl implements DokumenttiBuilderService {

    private static final Logger LOG = LoggerFactory.getLogger(DokumenttiBuilderServiceImpl.class);

    private static final float COMPRESSION_LEVEL = 0.9f;

    @Autowired
    private TermistoService termistoService;

    @Autowired
    private LiiteService liiteService;

    @Autowired
    private KoodistoService koodistoService;

    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private YleisetOsuudetService yleisetOsuudetService;

    @Autowired
    private PerusopetusService perusopetusService;

    @Autowired
    private LukioService lukioService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private DokumenttiStateService dokumenttiStateService;

    @Override
    public byte[] generatePdf(Opetussuunnitelma ops, Kieli kieli)
            throws TransformerException, IOException, SAXException,
            ParserConfigurationException, NullPointerException, DokumenttiException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        // Luodaan XHTML pohja
        Element rootElement = doc.createElement("html");
        rootElement.setAttribute("lang", kieli.toString());
        doc.appendChild(rootElement);

        Element headElement = doc.createElement("head");

        // Poistetaan HEAD:in <META http-equiv="Content-Type" content="text/html; charset=UTF-8">
        if (headElement.hasChildNodes()) {
            headElement.removeChild(headElement.getFirstChild());
        }

        Element bodyElement = doc.createElement("body");

        rootElement.appendChild(headElement);
        rootElement.appendChild(bodyElement);

        // Apuluokka datan säilömiseen generoinin ajaksi
        DokumenttiBase docBase = new DokumenttiBase();
        docBase.setDocument(doc);
        docBase.setHeadElement(headElement);
        docBase.setBodyElement(bodyElement);
        docBase.setOps(ops);
        docBase.setGenerator(new CharapterNumberGenerator());
        docBase.setKieli(kieli);
        docBase.setMapper(mapper);

        // Kansilehti & Infosivu
        addMetaPages(docBase);

        // Sisältöelementit
        yleisetOsuudetService.addYleisetOsuudet(docBase);

        if (ops.getKoulutustyyppi() != null) {
            PerusteDto perusteDto = eperusteetService.getPeruste(ops.getPerusteenDiaarinumero());
            if (perusteDto == null) {
                throw new DokumenttiException("Peruste puuttuu", new Throwable());
            }
            docBase.setPerusteDto(perusteDto);

            // Perusopetus
            if (ops.getKoulutustyyppi() == KoulutusTyyppi.PERUSOPETUS) {
                perusopetusService.addVuosiluokkakokonaisuudet(docBase);
            }

            // Lukio
            if (ops.getKoulutustyyppi() == KoulutusTyyppi.LUKIOKOULUTUS) {
                lukioService.addOppimistavoitteetJaOpetuksenKeskeisetSisallot(docBase);
            }
        }

        // Liitteet
        yleisetOsuudetService.addLiitteet(docBase);

        // Alaviitteet
        buildFootnotes(docBase);

        // Kuvat
        buildImages(docBase);

        LOG.info("Generate PDF (opsId=" + docBase.getOps().getId() + ")");

        // PDF luonti XHTML dokumentista
        byte[] pdf = pdfService.xhtml2pdf(doc);

        // Validointi
        /*ValidationResult result = DokumenttiUtils.validatePdf(pdf);
        if (result.isValid()) {
            LOG.info("PDF (ops " + ops.getId() + ") is a valid PDF/A-1b file");
        }
        else {
            LOG.warn("PDF (ops " + ops.getId() + ") is not valid, error(s) :");
            for (ValidationResult.ValidationError error : result.getErrorsList()) {
                LOG.warn(error.getErrorCode() + " : " + error.getDetails());
            }
        }*/

        return pdf;
    }

    private void addMetaPages(DokumenttiBase docBase) {
        Element title = docBase.getDocument().createElement("title");
        String nimi = getTextString(docBase, docBase.getOps().getNimi());
        title.appendChild(docBase.getDocument().createTextNode(nimi));
        docBase.getHeadElement().appendChild(title);

        String kuvaus = getTextString(docBase, docBase.getOps().getKuvaus());
        if (kuvaus != null && kuvaus.length() != 0) {
            Element description = docBase.getDocument().createElement("meta");
            description.setAttribute("name", "description");
            description.setAttribute("content", kuvaus);
            docBase.getHeadElement().appendChild(description);
        }

        Set<KoodistoKoodi> koodistoKoodit = docBase.getOps().getKunnat();
        if (koodistoKoodit != null) {
            Element municipalities = docBase.getDocument().createElement("kunnat");
            for (KoodistoKoodi koodistoKoodi : koodistoKoodit) {
                Element kuntaEl = docBase.getDocument().createElement("kunta");
                KoodistoKoodiDto koodistoKoodiDto = koodistoService.get("kunta", koodistoKoodi.getKoodiUri());
                if (koodistoKoodiDto != null && koodistoKoodiDto.getMetadata() != null) {
                    for (KoodistoMetadataDto metadata : koodistoKoodiDto.getMetadata()) {
                        if (metadata.getNimi() != null && metadata.getKieli().toLowerCase()
                                .equals(docBase.getKieli().toString().toLowerCase())) {
                            kuntaEl.setTextContent(metadata.getNimi());
                        }
                    }
                }
                municipalities.appendChild(kuntaEl);
            }
            docBase.getHeadElement().appendChild(municipalities);
        }

        // Organisaatiot
        Element organisaatiot = docBase.getDocument().createElement("organisaatiot");

        docBase.getOps().getOrganisaatiot().stream()
                .map(org -> organisaatioService.getOrganisaatio(org))
                .filter(node -> {
                    JsonNode tyypit = node.get("tyypit");
                    if (tyypit.isArray()) {
                        for (JsonNode asd : tyypit) {
                            if (asd.textValue().equals("Koulutustoimija")) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .map(node -> node.get("nimi"))
                .filter(Objects::nonNull)
                .map(x -> x.get(docBase.getKieli().toString()))
                .filter(Objects::nonNull)
                .map(JsonNode::asText)
                .forEach(koulu -> {
                    Element orgEl = docBase.getDocument().createElement("koulu");
                    orgEl.setTextContent(koulu);
                    organisaatiot.appendChild(orgEl);
                });

        docBase.getHeadElement().appendChild(organisaatiot);


        // Päätöspäivämäärä
        Date paatospaivamaara = docBase.getOps().getPaatospaivamaara();
        Element dateEl = docBase.getDocument().createElement("meta");
        dateEl.setAttribute("name", "date");
        if (paatospaivamaara != null) {
            String paatospaivamaaraText = new SimpleDateFormat("d.M.yyyy").format(paatospaivamaara);
            dateEl.setAttribute("content", paatospaivamaaraText);
        } else {
            dateEl.setAttribute("content", "");
        }
        docBase.getHeadElement().appendChild(dateEl);


        // Koulun nimi
        Element koulutEl = docBase.getDocument().createElement("koulut");

        docBase.getOps().getOrganisaatiot().stream()
                .map(org -> organisaatioService.getOrganisaatio(org))
                .filter(node -> {
                    JsonNode tyypit = node.get("tyypit");
                    if (tyypit.isArray()) {
                        for (JsonNode asd : tyypit) {
                            if (asd.textValue().equals("Oppilaitos")) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .map(node -> node.get("nimi"))
                .filter(Objects::nonNull)
                .map(x -> x.get(docBase.getKieli().toString()))
                .filter(Objects::nonNull)
                .map(JsonNode::asText)
                .forEach(koulu -> {
                    Element kouluEl = docBase.getDocument().createElement("koulu");
                    kouluEl.setTextContent(koulu);
                    koulutEl.appendChild(kouluEl);
                });

        docBase.getHeadElement().appendChild(koulutEl);
    }

    private void buildFootnotes(DokumenttiBase docBase) {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        try {
            XPathExpression expression = xpath.compile("//abbr");
            NodeList list = (NodeList) expression.evaluate(docBase.getDocument(), XPathConstants.NODESET);

            int noteNumber = 1;
            for (int i = 0; i < list.getLength(); i++) {
                Element element = (Element) list.item(i);
                Node node = list.item(i);
                if (node.getAttributes() != null & node.getAttributes().getNamedItem("data-viite") != null) {
                    String avain = node.getAttributes().getNamedItem("data-viite").getNodeValue();

                    if (docBase.getOps() != null && docBase.getOps().getId() != null) {
                        TermiDto termiDto = termistoService.getTermi(docBase.getOps().getId(), avain);

                        // todo: perusteen viite
                        //if (termiDto == null) {}
                        if (termiDto != null && termiDto.isAlaviite() && termiDto.getSelitys() != null) {
                            element.setAttribute("number", String.valueOf(noteNumber));

                            LokalisoituTekstiDto tekstiDto = termiDto.getSelitys();
                            String selitys = getTextString(docBase, tekstiDto)
                                    .replaceAll("<[^>]+>", ""); // Tällä hetkellä tuetaan vain tekstiä
                            element.setAttribute("text", selitys);
                            noteNumber++;
                        }
                    }
                }
            }

        } catch (XPathExpressionException e) {
            LOG.error(e.getLocalizedMessage());
        }
    }

    private void buildImages(DokumenttiBase docBase) {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        try {
            XPathExpression expression = xpath.compile("//img");
            NodeList list = (NodeList) expression.evaluate(docBase.getDocument(), XPathConstants.NODESET);

            for (int i = 0; i < list.getLength(); i++) {
                Element element = (Element) list.item(i);
                String id = element.getAttribute("data-uid");

                UUID uuid = UUID.fromString(id);

                // Ladataan kuvan data muistiin
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                liiteService.export(docBase.getOps().getId(), uuid, byteArrayOutputStream);

                // Tehdään muistissa olevasta datasta kuva
                InputStream in = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                BufferedImage bufferedImage = ImageIO.read(in);

                int width = bufferedImage.getWidth();
                int height = bufferedImage.getHeight();

                // Muutetaan kaikkien kuvien väriavaruus RGB:ksi jotta PDF/A validointi menee läpi
                // Asetetaan lisäksi läpinäkyvien kuvien taustaksi valkoinen väri
                BufferedImage tempImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
                        BufferedImage.TYPE_3BYTE_BGR);
                tempImage.getGraphics().setColor(new Color(255, 255, 255, 0));
                tempImage.getGraphics().fillRect (0, 0, width, height);
                tempImage.getGraphics().drawImage(bufferedImage, 0, 0, null);
                bufferedImage = tempImage;

                ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
                ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
                jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                jpgWriteParam.setCompressionQuality(COMPRESSION_LEVEL);

                // Muunnetaan kuva base64 enkoodatuksi
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                MemoryCacheImageOutputStream imageStream = new MemoryCacheImageOutputStream(out);
                jpgWriter.setOutput(imageStream);
                IIOImage outputImage = new IIOImage(bufferedImage, null, null);
                jpgWriter.write(null, outputImage, jpgWriteParam);
                jpgWriter.dispose();
                String base64 = Base64.getEncoder().encodeToString(out.toByteArray());

                // Lisätään bas64 kuva img elementtiin
                element.setAttribute("width", String.valueOf(width));
                element.setAttribute("height", String.valueOf(height));
                element.setAttribute("src", "data:image/jpg;base64," + base64);
            }

        } catch (XPathExpressionException | IOException e) {
            LOG.error(e.getLocalizedMessage());
        }
    }
}
