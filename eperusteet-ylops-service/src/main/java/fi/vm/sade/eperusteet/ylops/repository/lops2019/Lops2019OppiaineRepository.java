package fi.vm.sade.eperusteet.ylops.repository.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019Oppiaine;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019Sisalto;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Lops2019OppiaineRepository extends JpaWithVersioningRepository<Lops2019Oppiaine, Long> {
    @Query("SELECT oa FROM Lops2019Oppiaine oa WHERE oa.sisalto = ?1")
    List<Lops2019Oppiaine> findAllBySisalto(Lops2019Sisalto sisalto);

    @Query("SELECT oa FROM Lops2019Oppiaine oa WHERE oa.id = ?2 AND oa.sisalto = ?1")
    Optional<Lops2019Oppiaine> getOneBySisalto(Lops2019Sisalto sisalto, Long oaId);
}
