package fi.vm.sade.eperusteet.ylops.repository.version;

import fi.vm.sade.eperusteet.ylops.domain.revision.Revision;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@NoRepositoryBean
public interface JpaWithVersioningRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    List<Revision> getRevisions(final ID id);

    Number getRevisionNumberForDate(Date revisionTime);

    T findRevision(final ID id, final Integer revisionId);

    Integer getLatestRevisionId(final ID id);

    T getLatestNotNull(ID id);

    public class DomainClassNotAuditedException extends BeanCreationException {

        public DomainClassNotAuditedException(Class<?> clazz) {
            super("Defined domain class '" + clazz.getSimpleName() + "' does not contain @audited-annotation");
        }
    }

    /**
     * Lukitsee entiteetin muokkausta varten. Lukitus vapautuu automaattisesti transaktion loppuessa.
     * <p>
     * Enversillä on ongelmia yhtäaikaisten transaktioiden kanssa, joten pessimistisen lukituksen käyttäminen on joissakin tapauksissa tarpeen.
     *
     * @param entity
     * @return päivitetty, lukittu entiteetti
     */
    @Transactional(propagation = Propagation.MANDATORY)
    T lock(T entity);

    /**
     * Asettaa revisiokohtaisen kommentin.
     * <p>
     * Jos revisioon on jo asetettu kommentti, uusi kommentti korvaa aikaisemman. Kommentti on globaali koko revisiolle; jos samassa muutoksessa muokataan
     * useita entiteettejä, kommentti koskee niitä kaikkia.
     *
     * @param kommentti valinnainen kommentti
     */
    void setRevisioKommentti(String kommentti);

}
