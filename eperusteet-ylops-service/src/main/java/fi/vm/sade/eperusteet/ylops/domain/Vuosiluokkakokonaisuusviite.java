package fi.vm.sade.eperusteet.ylops.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

/**
 * Viittaa perusteessa määriteltyyn vuosiluokkakokonaisuuteen.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Immutable
@Table(name = "vlkokviite")
public class Vuosiluokkakokonaisuusviite implements ReferenceableEntity, Serializable {

    @Id
    @Getter
    private UUID id;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "vlkokviite_vuosiluokat", joinColumns = {
            @JoinColumn(name = "vlkokviite_id")})
    @Column(name = "vuosiluokka")
    private Set<Vuosiluokka> vuosiluokat = EnumSet.noneOf(Vuosiluokka.class);

    public Vuosiluokkakokonaisuusviite() {
    }

    public Set<Vuosiluokka> getVuosiluokat() {
        return Collections.unmodifiableSet(vuosiluokat);
    }

    public Vuosiluokkakokonaisuusviite(UUID id, Set<Vuosiluokka> vuosiluokat) {
        this.id = id;
        this.vuosiluokat = EnumSet.copyOf(vuosiluokat);
    }

    public boolean contains(Vuosiluokka luokka) {
        return vuosiluokat.contains(luokka);
    }
}
