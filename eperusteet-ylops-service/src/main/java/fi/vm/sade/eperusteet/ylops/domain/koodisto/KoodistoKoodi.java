package fi.vm.sade.eperusteet.ylops.domain.koodisto;

import java.io.Serializable;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@EqualsAndHashCode(of = "koodiUri")
@Getter
@Table(name = "koodistokoodi", uniqueConstraints = {
        @UniqueConstraint(columnNames = "koodiUri")})
public class KoodistoKoodi implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotEmpty(message = "koodiUri puuttuu")
    private String koodiUri;
    private String koodiArvo;

    protected KoodistoKoodi() {
    }

    public KoodistoKoodi(String koodiUri, String koodiArvo) {
        this.koodiUri = koodiUri;
        this.koodiArvo = koodiArvo;
    }
}
