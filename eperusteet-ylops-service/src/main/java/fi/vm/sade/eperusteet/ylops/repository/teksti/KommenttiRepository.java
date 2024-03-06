package fi.vm.sade.eperusteet.ylops.repository.teksti;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kommentti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KommenttiRepository extends JpaRepository<Kommentti, Long> {
    List<Kommentti> findByOpetussuunnitelmaId(Long opetussuunnitelmaId);

    @Query("SELECT k FROM Kommentti k WHERE k.opetussuunnitelmaId = ?1 AND k.tekstiKappaleViiteId = ?2")
    List<Kommentti> findByTekstiKappaleViiteId(Long opsId, Long tekstiKappaleViiteId);

    @Query("SELECT k FROM Kommentti k WHERE k.opetussuunnitelmaId = ?1 AND k.vlkId = ?2 AND k.oppiaineId = ?3 AND k.vlId = NULL")
    List<Kommentti> findByOppiaine(Long opsId, Long vlkId, Long oppiaineId);

    @Query("SELECT k FROM Kommentti k WHERE k.opetussuunnitelmaId = ?1 AND k.vlkId = ?2 AND k.oppiaineId = ?3 AND k.vlId = ?4")
    List<Kommentti> findByVuosiluokka(Long opsId, Long vlkId, Long oppiaineId, Long vlId);

    List<Kommentti> findByParentId(Long parentId);

    List<Kommentti> findByYlinId(Long ylinId);
}
