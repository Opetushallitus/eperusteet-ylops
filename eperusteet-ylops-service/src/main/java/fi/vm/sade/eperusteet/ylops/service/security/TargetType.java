package fi.vm.sade.eperusteet.ylops.service.security;

public enum TargetType {
    POHJA("pohja"),
    TARKASTELU("tarkastelu"),
    KYSYMYS("kysymys"),
    OPETUSSUUNNITELMA("opetussuunnitelma");

    private final String target;

    private TargetType(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return target;
    }
}
