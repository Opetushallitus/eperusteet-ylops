/*
 *  Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 *  This program is free software: Licensed under the EUPL, Version 1.1 or - as
 *  soon as they will be approved by the European Commission - subsequent versions
 *  of the EUPL (the "Licence");
 *
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  European Union Public Licence for more details.
 */

package fi.vm.sade.eperusteet.ylops.domain.lukio;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Tekstiosa;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml.WhitelistType;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioKurssiParentDto;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.Copyable;
import fi.vm.sade.eperusteet.ylops.service.util.Validointi;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

/**
 * User: tommiratamaa
 * Date: 17.11.2015
 * Time: 14.02
 */
@Entity
@Audited
@Table(name = "lukiokurssi", schema = "public")
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "lukioKurssiParentDto",
                classes = {
                        @ConstructorResult(
                                targetClass = LukioKurssiParentDto.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "parent_id", type = Long.class)
                                }
                        )
                }
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "parentViewByOps",
                query = "select oa_lk.kurssi_id as id, findParentKurssi(?1, oa_lk.kurssi_id) as parent_id" +
                        "   from oppiaine_lukiokurssi oa_lk" +
                        " where oa_lk.opetussuunnitelma_id = ?1" +
                        " group by oa_lk.kurssi_id order by oa_lk.kurssi_id",
                resultSetMapping = "lukioKurssiParentDto"
        )
})
public class Lukiokurssi extends Kurssi implements Copyable<Lukiokurssi> {

    @Getter
    @Setter
    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LukiokurssiTyyppi tyyppi;

    @Getter
    @Setter
    @Column(name = "laajuus", nullable = true,
            precision = 4, scale = 2, columnDefinition = "DECIMAL(4,2)")
    private BigDecimal laajuus = BigDecimal.ONE;

    @Getter
    @Setter
    @ValidHtml(whitelist = WhitelistType.MINIMAL)
    @JoinColumn(name = "lokalisoitava_koodi_id", nullable = true)
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti lokalisoituKoodi;

    @Getter
    @Setter
    @Valid
    @JoinColumn(name = "tavoitteet_id", nullable = true)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Tekstiosa tavoitteet;

    @Getter
    @Setter
    @Valid
    @JoinColumn(name = "keskeinen_sisalto_id", nullable = true)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Tekstiosa keskeinenSisalto;

    @Getter
    @Setter
    @Valid
    @JoinColumn(name = "tavoitteet_ja_keskeinen_sisalto_id", nullable = true)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Tekstiosa tavoitteetJaKeskeinenSisalto;

    @Getter
    @Audited
    @OneToMany(mappedBy = "kurssi", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OppiaineLukiokurssi> oppiaineet = new HashSet<>(0);

    public Lukiokurssi() {
        super(UUID.randomUUID());
    }

    public Lukiokurssi(UUID tunniste) {
        super(tunniste);
    }

    public Lukiokurssi copy() {
        return copyInto(new Lukiokurssi(this.getTunniste()));
    }

    public Lukiokurssi copyInto(Lukiokurssi lukiokurssi) {
        super.copyInto(lukiokurssi);
        lukiokurssi.setTyyppi(this.tyyppi);
        lukiokurssi.setLaajuus(this.laajuus);
        lukiokurssi.setLokalisoituKoodi(this.lokalisoituKoodi);
        lukiokurssi.setTavoitteet(Tekstiosa.copyOf(this.tavoitteet));
        lukiokurssi.setKeskeinenSisalto(Tekstiosa.copyOf(this.keskeinenSisalto));
        lukiokurssi.setTavoitteetJaKeskeinenSisalto(Tekstiosa.copyOf(this.tavoitteetJaKeskeinenSisalto));
        return lukiokurssi;
    }

    public void validoiTavoitteetJaKeskeinenSisalto(Validointi validointi, Set<Kieli> julkaisukielet) {
        if (tavoitteetJaKeskeinenSisalto != null) {
            LokalisoituTeksti concat = LokalisoituTeksti.concat(this.getNimi(), "(");
            if (lokalisoituKoodi != null) {
                Tekstiosa.validoi(validointi, tavoitteetJaKeskeinenSisalto, julkaisukielet,
                        LokalisoituTeksti.concat(this.getNimi(), " (", lokalisoituKoodi, ")"));
            } else {
                Tekstiosa.validoi(validointi, tavoitteetJaKeskeinenSisalto, julkaisukielet, this.getNimi());
            }
            return;
        } else if (keskeinenSisalto != null && tavoitteet != null) {
            if (lokalisoituKoodi != null) {
                Tekstiosa.validoi(validointi, keskeinenSisalto, julkaisukielet,
                        LokalisoituTeksti.concat(this.getNimi(), " (", lokalisoituKoodi, ")"));
                Tekstiosa.validoi(validointi, tavoitteet, julkaisukielet,
                        LokalisoituTeksti.concat(this.getNimi(), " (", lokalisoituKoodi, ")"));
            } else {
                Tekstiosa.validoi(validointi, keskeinenSisalto, julkaisukielet, this.getNimi());
                Tekstiosa.validoi(validointi, tavoitteet, julkaisukielet, this.getNimi());
            }
            return;
        }
        validointi.varoitus("lukio-kurssi-tavoitteet-keskeinensisalto-puuttuvat", this.getNimi());
    }
}
