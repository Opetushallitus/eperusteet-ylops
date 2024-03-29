package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Tekstiosa;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteenLokalisoituTekstiDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.exception.SyntaxValidationException;
import org.apache.pdfbox.preflight.parser.PreflightParser;
import org.apache.pdfbox.preflight.utils.ByteArrayDataSource;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.parser.Parser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class DokumenttiUtils {
    public static final int MAX_TIME_IN_MINUTES = 60;

    public static boolean hasLokalisoituteksti(DokumenttiBase docBase, LokalisoituTeksti lTeksti) {
        return lTeksti != null && lTeksti.getTeksti() != null && lTeksti.getTeksti().get(docBase.getKieli()) != null;
    }

    public static void addLokalisoituteksti(DokumenttiBase docBase, LokalisoituTekstiDto lTekstiDto, String tagi) {
        if (lTekstiDto != null) {
            addLokalisoituteksti(docBase, lTekstiDto.getTekstit(), tagi);
        }
    }

    public static void addLokalisoituteksti(DokumenttiBase docBase, PerusteenLokalisoituTekstiDto lTekstiDto, String tagi) {
        if (lTekstiDto != null) {
            addLokalisoituteksti(docBase, lTekstiDto.getTekstit(), tagi);
        }
    }

    public static void addLokalisoituteksti(DokumenttiBase docBase, LokalisoituTeksti lTeksti, String tagi) {
        if (lTeksti != null) {
            addLokalisoituteksti(docBase, lTeksti.getTeksti(), tagi);
        }
    }

    public static void addLokalisoituteksti(DokumenttiBase docBase, Map<Kieli, String> tekstit, String tagi) {
        if (tekstit != null && tekstit.get(docBase.getKieli()) != null) {
            try {
                String teksti = tekstit.get(docBase.getKieli());
                teksti = "<" + tagi + ">" + cleanHtml(teksti) + "</" + tagi + ">";

                Document tempDoc = new W3CDom().fromJsoup(Jsoup.parseBodyFragment(teksti));
                Node node = tempDoc.getDocumentElement().getChildNodes().item(1).getFirstChild();

                docBase.getBodyElement().appendChild(docBase.getDocument().importNode(node, true));
            } catch (Exception e) {
                log.error(e.getMessage());
                throw e;
            }
        }
    }

    public static String unescapeHtml(String str) {
        String unescaped = Parser.unescapeEntities(str, true);
        String cleaned = cleanHtml(unescaped);
        return cleaned;
    }

    public static void addTeksti(DokumenttiBase docBase, String teksti, String tagi) {
        if (teksti != null) {
            teksti = "<" + tagi + ">" + cleanHtml(teksti) + "</" + tagi + ">";

            Document tempDoc = new W3CDom().fromJsoup(Jsoup.parseBodyFragment(teksti));
            Node node = tempDoc.getDocumentElement().getChildNodes().item(1).getFirstChild();

            docBase.getBodyElement().appendChild(docBase.getDocument().importNode(node, true));
        }
    }

    public static void addTekstiosa(DokumenttiBase docBase, Tekstiosa tekstiosa, String tagi) {
        if (tekstiosa != null) {
            LokalisoituTeksti otsikko = tekstiosa.getOtsikko();
            LokalisoituTeksti teksti = tekstiosa.getTeksti();
            if (otsikko != null) {
                addLokalisoituteksti(docBase, otsikko, tagi);
            }
            if (teksti != null) {
                addLokalisoituteksti(docBase, teksti, tagi);
            }
        }
    }

    public static Element getList(DokumenttiBase docBase, Collection<LokalisoituTekstiDto> tekstit) {
        return getStringList(docBase, tekstit.stream()
                .filter(Objects::nonNull)
                .map(kuvaus -> getTextString(docBase, kuvaus))
                .collect(Collectors.toList()));
    }

    public static Element getStringList(DokumenttiBase docBase, Collection<String> tekstit) {
        Element ul = docBase.getDocument().createElement("ul");
        tekstit.stream()
                .filter(str -> !StringUtils.isEmpty(str))
                .forEach(str -> {
                    Element li = docBase.getDocument().createElement("li");
                    Document doc = new W3CDom().fromJsoup(Jsoup.parse(str));
                    Node node = doc.getDocumentElement().getChildNodes().item(1).getFirstChild();
                    li.appendChild(docBase.getDocument().importNode(node, true));
                    ul.appendChild(li);
                });
        return ul;
    }

    public static void addList(DokumenttiBase docBase, Collection<LokalisoituTekstiDto> tekstit) {
        addStringList(docBase, tekstit.stream()
                .filter(Objects::nonNull)
                .map(kuvaus -> getTextString(docBase, kuvaus))
                .collect(Collectors.toList()));
    }

    public static void addStringList(DokumenttiBase docBase, Collection<String> tekstit) {
        Element ul = docBase.getDocument().createElement("ul");
        tekstit.stream()
                .filter(str -> !StringUtils.isEmpty(str))
                .forEach(str -> {
                    Element li = docBase.getDocument().createElement("li");
                    Document doc = new W3CDom().fromJsoup(Jsoup.parse(str));
                    Node node = doc.getDocumentElement().getChildNodes().item(1).getFirstChild();
                    li.appendChild(docBase.getDocument().importNode(node, true));
                    ul.appendChild(li);
                });
        docBase.getBodyElement().appendChild(ul);
    }

    public static void addHeader(DokumenttiBase docBase, String text) {
        addHeader(docBase, text, null);
    }

    public static void addHeader(DokumenttiBase docBase, String text, String id) {
        addHeader(docBase, text, id, true);
    }

    public static void addHeader(DokumenttiBase docBase, String text, boolean showHeaderNumber) {
        addHeader(docBase, text, null, showHeaderNumber);
    }

    public static void addHeader(DokumenttiBase docBase, String text, String id, boolean showHeaderNumber) {
        if (text != null) {
            Element header = docBase.getDocument().createElement("h" + docBase.getGenerator().getDepth());
            header.setAttribute("number", docBase.getGenerator().generateNumber());
            header.setAttribute("showHeaderNumber", showHeaderNumber + "");
            header.appendChild(docBase.getDocument().createTextNode(cleanHtml(text)));
            if (id != null) {
                header.setAttribute("id", id);
            }
            docBase.getBodyElement().appendChild(header);
        }
    }

    public static String getTextString(DokumenttiBase docBase, LokalisoituTekstiDto lokalisoituTekstiDto) {
        return getTextString(docBase, lokalisoituTekstiDto.getTekstit());
    }

    public static String getTextString(DokumenttiBase docBase, LokalisoituTeksti lokalisoituTeksti) {
        if (lokalisoituTeksti == null) {
            return "";
        } else {
            return getTextString(docBase, lokalisoituTeksti.getTeksti());
        }
    }

    public static String getTextString(DokumenttiBase docBase, Map<Kieli, String> tekstit) {
        if (tekstit == null || tekstit.get(docBase.getKieli()) == null) {
            return "";
        } else {
            return cleanHtml(tekstit.get(docBase.getKieli()));
        }
    }

    public static String cleanHtml(String string) {
        if (string == null) {
            return "";
        }
        string = removeInternalLink(string);
        string = Jsoup.clean(stripNonValidXMLCharacters(string), ValidHtml.WhitelistType.NORMAL_PDF.getWhitelist()).replaceAll("&quot;", "”");
        return StringEscapeUtils.unescapeHtml4(string.replace("&nbsp;", " "));
    }

    public static String stripNonValidXMLCharacters(String in) {
        StringBuilder out = new StringBuilder();
        char current;

        if (in == null || ("".equals(in))) return "";
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i);
            if (current == 0x9
                    || current == 0xA
                    || current == 0xD
                    || current >= 0x20 && current <= 0xD7FF
                    || current >= 0xE000 && current <= 0xFFFD) {
                out.append(current);
            }
        }

        return out.toString();
    }

    public static boolean isTimePass(Dokumentti dokumentti) {
        return (dokumentti.getTila().equals(DokumenttiTila.LUODAAN) || dokumentti.getTila().equals(DokumenttiTila.JONOSSA)) && isTimePass(dokumentti.getAloitusaika());
    }

    public static boolean isTimePass(Date date) {
        if (date == null) {
            return true;
        }

        Date newDate = DateUtils.addMinutes(date, MAX_TIME_IN_MINUTES);
        return newDate.before(new Date());
    }

    public static ValidationResult validatePdf(byte[] pdf) throws IOException {
        ValidationResult result;
        InputStream is = new ByteArrayInputStream(pdf);
        PreflightParser parser = new PreflightParser(new ByteArrayDataSource(is));

        try {
            parser.parse();

            PreflightDocument document = parser.getPreflightDocument();
            document.validate();

            // Get validation result
            result = document.getResult();
            document.close();

        } catch (SyntaxValidationException e) {
            result = e.getResult();
        }

        return result;
    }

    public static void addPlaceholder(DokumenttiBase docBase) {
        docBase.getBodyElement().appendChild(docBase.getDocument().createElement("br"));
    }

    private static String removeInternalLink(String text) {
        org.jsoup.nodes.Document stringRoutenodeCleaned = Jsoup.parse(text, "", Parser.xmlParser());
        stringRoutenodeCleaned.select("a[routenode]").forEach(org.jsoup.nodes.Node::unwrap);
        return stringRoutenodeCleaned.toString();
    }
}
