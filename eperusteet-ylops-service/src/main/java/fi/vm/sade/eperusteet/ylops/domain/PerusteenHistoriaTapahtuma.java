package fi.vm.sade.eperusteet.ylops.domain;

import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class PerusteenHistoriaTapahtuma implements HistoriaTapahtuma{

    private Long perusteId;

    public PerusteenHistoriaTapahtuma(Long perusteId) {
        this.perusteId = perusteId;
    }

    @Override
    public Date getLuotu() {
        return new Date();
    }

    @Override
    public Date getMuokattu() {
        return new Date();
    }

    @Override
    public String getLuoja() {
        return SecurityUtil.getAuthenticatedPrincipal().getName();
    }

    @Override
    public String getMuokkaaja() {
        return SecurityUtil.getAuthenticatedPrincipal().getName();
    }

    @Override
    public Long getId() {
        return perusteId;
    }

    @Override
    public LokalisoituTeksti getNimi() {
        return null;
    }

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.peruste;
    }
}
