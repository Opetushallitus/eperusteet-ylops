package fi.vm.sade.eperusteet.ylops.repository.teksti;

import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TekstikappaleviiteRepository extends JpaWithVersioningRepository<TekstiKappaleViite, Long>, TekstikappaleviiteRepositoryCustom {
    List<TekstiKappaleViite> findAllByTekstiKappale(TekstiKappale tekstiKappale);

//    List<TekstiKappaleViite> findAllByOriginalId(Long originalId);

    @Query(
            nativeQuery = true,
            value ="WITH RECURSIVE hierarchy(id, vanhempi_id, original_viite_id) AS ( " +
                    "    SELECT tekstikappaleviite.id, vanhempi_id, tekstikappaleviite.id AS original_viite_id " +
                    "    FROM tekstikappaleviite " +
                    "    INNER join tekstikappale t ON tekstikappaleviite.tekstikappale_id = t.id " +
                    "    WHERE tunniste = CAST(:tunniste AS UUID)" +
                    "    UNION ALL " +
                    "    SELECT child.id, child.vanhempi_id, parent.original_viite_id " +
                    "    FROM tekstikappaleviite AS child " +
                    "    JOIN hierarchy AS parent ON child.id = parent.vanhempi_id " +
                    ") " +
                    "SELECT viite.* " +
                    "FROM hierarchy " +
                    "INNER JOIN opetussuunnitelma ops ON ops.tekstit_id = hierarchy.id " +
                    "INNER JOIN tekstikappaleviite viite ON viite.id = original_viite_id " +
                    "WHERE ops.id = :opsId")
    TekstiKappaleViite findByOpetussuunnitelmaIdAndTekstikappaleTunniste(Long opsId, String tunniste);
}
