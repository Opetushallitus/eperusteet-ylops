package fi.vm.sade.eperusteet.ylops.domain.ohje;

public enum OhjeTyyppi {

    OHJE("ohje"),
    PERUSTETEKSTI("perusteteksti");

    private final String tyyppi;

    private OhjeTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }

    @Override
    public String toString() {
        return tyyppi;
    }
}
