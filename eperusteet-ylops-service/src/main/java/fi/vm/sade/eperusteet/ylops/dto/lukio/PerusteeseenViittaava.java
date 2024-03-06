package fi.vm.sade.eperusteet.ylops.dto.lukio;

import java.util.UUID;
import java.util.stream.Stream;

public interface PerusteeseenViittaava<T> {
    T getPerusteen();

    void setPerusteen(T vastaava);

    UUID getTunniste();

    default Stream<? extends PerusteeseenViittaava<?>> viittaukset() {
        return Stream.empty();
    }

    default Stream<? extends PerusteeseenViittaava<?>> viittauksineen() {
        return Stream.concat(Stream.of(this), viittaukset()
                .filter(v -> v != null).flatMap(PerusteeseenViittaava::viittauksineen));
    }
}
