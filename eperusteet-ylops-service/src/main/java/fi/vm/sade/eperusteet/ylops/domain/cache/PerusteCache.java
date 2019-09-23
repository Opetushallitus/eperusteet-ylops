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

package fi.vm.sade.eperusteet.ylops.domain.cache;

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml.WhitelistType;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.EperusteetPerusteDto;
import fi.vm.sade.eperusteet.ylops.service.util.JsonMapper;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.IOException;
import java.util.Date;

/**
 * User: tommiratamaa
 * Date: 16.11.2015
 * Time: 13.52
 */
@Entity
@Getter
@Setter
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Immutable
@Table(name = "peruste_cache", schema = "public", uniqueConstraints =
@UniqueConstraint(columnNames = {"peruste_id", "aikaleima"}))
public class PerusteCache {
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    @GeneratedValue(generator = "peruste_cache_id_seq")
    @SequenceGenerator(name = "peruste_cache_id_seq", sequenceName = "peruste_cache_id_seq")
    private Long id;

    @Column(name = "peruste_id", nullable = false, updatable = false)
    private Long perusteId;

    @Column(name = "aikaleima", nullable = false, updatable = false)
    private Date aikaleima;

    @Column(name = "diaarinumero", nullable = false, updatable = false)
    private String diaarinumero;

    @Enumerated(EnumType.STRING)
    @Column(name = "koulutustyyppi", nullable = false, updatable = false)
    private KoulutusTyyppi koulutustyyppi;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "voimassaolo_alkaa")
    private Date voimassaoloAlkaa;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "voimassaolo_loppuu")
    private Date voimassaoloLoppuu;

    @ValidHtml(whitelist = WhitelistType.MINIMAL)
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "nimi_id", nullable = false, updatable = false)
    private LokalisoituTeksti nimi;

    @Column(name = "peruste_json", nullable = false, updatable = false, columnDefinition = "text")
    private String perusteJson;

    public EperusteetPerusteDto getPerusteJson(JsonMapper mapper) throws IOException {
        return mapper.deserialize(EperusteetPerusteDto.class, perusteJson);
    }

    public void setPerusteJson(EperusteetPerusteDto dto, JsonMapper mapper) throws IOException {
        this.perusteJson = mapper.serialize(dto);
    }
}
