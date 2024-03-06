package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;

public class DokumenttiTaulukko {

    private String otsikko;
    private ArrayList<String> otsikkoSarakkeet = new ArrayList<>();
    private ArrayList<DokumenttiRivi> rivit = new ArrayList<>();

    public void addOtsikko(String otsikko) {
        this.otsikko = otsikko;
    }

    public void addOtsikkoSarake(String sarake) {
        otsikkoSarakkeet.add(sarake);
    }

    public void addRivi(DokumenttiRivi rivi) {
        rivit.add(rivi);
    }

    public void addToDokumentti(DokumenttiBase docBase) {
        if (rivit.size() > 0) {
            Document tempDoc = new W3CDom().fromJsoup(Jsoup.parseBodyFragment(this.toString()));
            Node node = tempDoc.getDocumentElement().getChildNodes().item(1).getFirstChild();
            docBase.getBodyElement().appendChild(docBase.getDocument().importNode(node, true));
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<div>");

        // Tyhjää taulukkoa on turha antaa
        if (otsikko != null) {
            builder.append("<strong>");
            builder.append(otsikko);
            builder.append("</strong>");
        }

        builder.append("<table border=\"1\">");

        // Otsikko rivi
        if (otsikkoSarakkeet.size() > 0) {
            builder.append("<tr bgcolor=\"#d4e3f4\">");
            otsikkoSarakkeet.stream()
                    .forEach((sarake) -> {
                        builder.append("<th>");
                        builder.append(sarake);
                        builder.append("</th>");
                    });
            builder.append("</tr>");
        }

        rivit.stream()
                .forEach((rivi) -> {
                    builder.append("<tr>");
                    builder.append(rivi.toString());
                    builder.append("</tr>");
                });

        builder.append("</table>");

        builder.append("</div>");
        return builder.toString();
    }
}
