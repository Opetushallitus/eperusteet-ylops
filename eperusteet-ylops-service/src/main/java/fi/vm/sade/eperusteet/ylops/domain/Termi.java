package fi.vm.sade.eperusteet.ylops.domain;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;

import java.io.Serializable;
import java.util.UUID;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Table(name = "termi")
public class Termi implements Serializable {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "ops_id")
    private Opetussuunnitelma ops;

    @Getter
    @Column(name = "avain")
    private String avain;

    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti termi;

    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti selitys;

    @Getter
    @Setter
    private boolean alaviite;

    public void setAvain(String uusi) {
        if (StringUtils.isEmpty(getAvain())) {
            this.avain = uusi;
        }
    }

    @PrePersist
    void onPersist() {
        if (this.id == null || StringUtils.isEmpty(getAvain())) {
            setAvain(UUID.randomUUID().toString());
        }
    }
}
