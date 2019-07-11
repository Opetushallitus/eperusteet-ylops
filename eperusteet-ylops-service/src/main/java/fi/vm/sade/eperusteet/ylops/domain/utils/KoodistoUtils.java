package fi.vm.sade.eperusteet.ylops.domain.utils;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;

public class KoodistoUtils {
    static public String getVieraskielikoodi(String koodi, Kieli kieli) {
        if (koodi == null) {
            return "";
        }

        if (Kieli.SV.equals(kieli)) {
            switch (koodi) {
                case "lukiokielitarjonta_ea": return "SP";
                case "lukiokielitarjonta_en": return "EN";
                case "lukiokielitarjonta_ia": return "IA";
                case "lukiokielitarjonta_jp": return "JP";
                case "lukiokielitarjonta_ki": return "KI";
                case "lukiokielitarjonta_la": return "LA";
                case "lukiokielitarjonta_po": return "PO";
                case "lukiokielitarjonta_ra": return "FR";
                case "lukiokielitarjonta_sa": return "TY";
                case "lukiokielitarjonta_kx": return "SX";
                case "lukiokielitarjonta_sm": return "SA";
                case "lukiokielitarjonta_ve": return "RY";
                default: return "SX";
            }
        }
        else {
            switch (koodi) {
                case "lukiokielitarjonta_ea": return "EA";
                case "lukiokielitarjonta_en": return "EN";
                case "lukiokielitarjonta_ia": return "IA";
                case "lukiokielitarjonta_jp": return "JP";
                case "lukiokielitarjonta_ki": return "KI";
                case "lukiokielitarjonta_la": return "LA";
                case "lukiokielitarjonta_po": return "PO";
                case "lukiokielitarjonta_ra": return "RA";
                case "lukiokielitarjonta_sa": return "SA";
                case "lukiokielitarjonta_kx": return "KX";
                case "lukiokielitarjonta_sm": return "SM";
                case "lukiokielitarjonta_ve": return "VE";
                default: return "KX";
            }
        }
    }
}
