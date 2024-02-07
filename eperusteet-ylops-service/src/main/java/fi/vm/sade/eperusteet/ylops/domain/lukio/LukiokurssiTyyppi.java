package fi.vm.sade.eperusteet.ylops.domain.lukio;

import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.PerusteenLukiokurssiTyyppi;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.Copier;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.Setter;

import java.util.Optional;
import java.util.function.Function;

public enum LukiokurssiTyyppi {
    VALTAKUNNALLINEN_PAKOLLINEN(Oppiaine::getValtakunnallinenPakollinenKuvaus, Oppiaine::setValtakunnallinenPakollinenKuvaus),
    VALTAKUNNALLINEN_SYVENTAVA(Oppiaine::getValtakunnallinenSyventavaKurssiKuvaus, Oppiaine::setValtakunnallinenSyventavaKurssiKuvaus),
    VALTAKUNNALLINEN_SOVELTAVA(Oppiaine::getValtakunnallinenSoveltavaKurssiKuvaus, Oppiaine::setValtakunnallinenSoveltavaKurssiKuvaus),
    PAIKALLINEN_SYVENTAVA(Oppiaine::getPaikallinenSyventavaKurssiKuvaus, Oppiaine::setPaikallinenSyventavaKurssiKuvaus, true),
    PAIKALLINEN_SOVELTAVA(Oppiaine::getPaikallinenSoveltavaKurssiKuvaus, Oppiaine::setPaikallinenSoveltavaKurssiKuvaus, true);

    public enum Paikallinen {
        PAIKALLINEN_SYVENTAVA(LukiokurssiTyyppi.PAIKALLINEN_SYVENTAVA, "lukionkurssit_psy"),
        PAIKALLINEN_SOVELTAVA(LukiokurssiTyyppi.PAIKALLINEN_SOVELTAVA, "lukionkurssit_pso");

        private final LukiokurssiTyyppi tyyppi;
        private final String koodi;

        Paikallinen(LukiokurssiTyyppi tyyppi, String koodi) {
            this.tyyppi = tyyppi;
            this.koodi = koodi;
        }

        public LukiokurssiTyyppi toKurssiiTyyppi() {
            return tyyppi;
        }

        public String getKurssiKoodi() {
            return koodi;
        }
    }


    public static LukiokurssiTyyppi ofPerusteTyyppi(PerusteenLukiokurssiTyyppi tyyppi) {
        switch (tyyppi) {
            case PAKOLLINEN:
                return VALTAKUNNALLINEN_PAKOLLINEN;
            case VALTAKUNNALLINEN_SOVELTAVA:
                return VALTAKUNNALLINEN_SOVELTAVA;
            case VALTAKUNNALLINEN_SYVENTAVA:
                return VALTAKUNNALLINEN_SYVENTAVA;
            default:
                throw new IllegalStateException("Unimplemented peruste lukiokurssityyppi: " + tyyppi);
        }
    }

    // Vähän hassusti ovat nyt versioinnin takia samassa käsitteessä kaikki propertyinä, niin pientä helpotusta
    private final Function<Oppiaine, LokalisoituTeksti> oppiaineKuvausGetter;
    private final Setter<Oppiaine, LokalisoituTeksti> oppiaineKuvausSetter;
    private final boolean paikallinen;

    LukiokurssiTyyppi(Function<Oppiaine, LokalisoituTeksti> oppiaineKuvausGetter,
                      Setter<Oppiaine, LokalisoituTeksti> oppiaineKuvausSetter) {
        this(oppiaineKuvausGetter, oppiaineKuvausSetter, false);
    }

    LukiokurssiTyyppi(Function<Oppiaine, LokalisoituTeksti> oppiaineKuvausGetter,
                      Setter<Oppiaine, LokalisoituTeksti> oppiaineKuvausSetter, boolean paikallinen) {
        this.oppiaineKuvausGetter = oppiaineKuvausGetter;
        this.oppiaineKuvausSetter = oppiaineKuvausSetter;
        this.paikallinen = paikallinen;
    }

    public Function<Oppiaine, LokalisoituTeksti> oppiaineKuvausGetter() {
        return oppiaineKuvausGetter;
    }

    public Setter<Oppiaine, LokalisoituTeksti> oppiaineKuvausSetter() {
        return oppiaineKuvausSetter;
    }

    public boolean isPaikallinen() {
        return paikallinen;
    }

    public Copier<Oppiaine> oppiaineKuvausCopier() {
        return Copier.of(this.oppiaineKuvausGetter, this.oppiaineKuvausSetter);
    }

    public Optional<Paikallinen> paikallinen() {
        return isPaikallinen() ? Optional.of(Paikallinen.valueOf(name())) : Optional.empty();
    }
}
