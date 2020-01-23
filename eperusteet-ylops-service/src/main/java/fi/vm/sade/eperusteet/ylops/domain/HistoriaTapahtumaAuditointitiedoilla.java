package fi.vm.sade.eperusteet.ylops.domain;

import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoriaTapahtumaAuditointitiedoilla implements HistoriaTapahtuma {

    private Date luotu;
    private Date muokattu;
    private String luoja;
    private String muokkaaja;

    private Long id;
    private LokalisoituTeksti nimi;
    private NavigationType navigationType;

    public HistoriaTapahtumaAuditointitiedoilla(HistoriaTapahtuma historiaTapahtuma) {
        this.luotu = new Date();
        this.muokattu = new Date();
        this.luoja = SecurityUtil.getAuthenticatedPrincipal().getName();
        this.muokkaaja = SecurityUtil.getAuthenticatedPrincipal().getName();

        this.id = historiaTapahtuma.getId();
        this.nimi = historiaTapahtuma.getNimi();
        this.navigationType = historiaTapahtuma.getNavigationType();
    }

    public HistoriaTapahtumaAuditointitiedoilla(Long id, LokalisoituTeksti nimi, NavigationType navigationType) {
        this.luotu = new Date();
        this.muokattu = new Date();

        this.id = id;
        this.nimi = nimi;
        this.navigationType = navigationType;
    }
}
