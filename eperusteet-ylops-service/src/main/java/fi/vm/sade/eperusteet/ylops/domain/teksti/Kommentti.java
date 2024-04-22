package fi.vm.sade.eperusteet.ylops.domain.teksti;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "kommentti")
public class Kommentti extends AbstractAuditedReferenceableEntity {
    @Getter
    @Setter
    private Boolean poistettu;

    @Getter
    @Setter
    private String nimi;

    @Getter
    @Setter
    @Column(length = 1024)
    @Size(max = 1024, message = "Kommentin maksimipituus on {max} merkkiä")
    private String sisalto;

    @Getter
    @Setter
    private Long ylinId;

    @Getter
    @Setter
    private Long parentId;

    @Getter
    @Setter
    private Long opetussuunnitelmaId;

    @Getter
    @Setter
    private Long tekstiKappaleViiteId;

    @Getter
    @Setter
    private Long oppiaineId;

    @Getter
    @Setter
    private Long vlkId;

    @Getter
    @Setter
    private Long vlId;
}
