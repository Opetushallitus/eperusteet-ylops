package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util;

import java.util.ArrayList;

public class DokumenttiRivi {

    private ArrayList<String> sarakkeet = new ArrayList<>();

    public void addSarake(String sarake) {
        sarakkeet.add(sarake);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        sarakkeet.stream()
                .forEach((sarake) -> {
                    builder.append("<td>");
                    builder.append(sarake);
                    builder.append("</td>");
                });
        return builder.toString();
    }
}
