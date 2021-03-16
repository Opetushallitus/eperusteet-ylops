package fi.vm.sade.eperusteet.ylops.domain.ops;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Entity
@Immutable
@Table(name = "opetussuunnitelman_julkaisu_data")

// aiheuttaa sen, etta taulua ei luoda testissa
// @TypeDef(name = "jsonb", defaultForType = JsonBType.class, typeClass = JsonBType.class)
public class JulkaistuOpetussuunnitelmaData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

}
