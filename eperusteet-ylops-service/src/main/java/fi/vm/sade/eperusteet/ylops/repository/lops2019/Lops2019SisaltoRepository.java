package fi.vm.sade.eperusteet.ylops.repository.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019OpintojaksonOppiaine;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019Sisalto;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface Lops2019SisaltoRepository extends JpaWithVersioningRepository<Lops2019Sisalto, Long> {

    @Query("SELECT oppiaineet FROM Lops2019Sisalto sisalto JOIN sisalto.opintojaksot opintojaksot JOIN opintojaksot.oppiaineet oppiaineet WHERE sisalto.opetussuunnitelma.id = :opetussuunnitelmaId")
    List<Lops2019OpintojaksonOppiaine> findOpintojaksonOppiaineetByOpetussuunnitelma(@Param("opetussuunnitelmaId") Long opetussuunnitelmaId);

    @Query("SELECT oppiaineet FROM Lops2019Sisalto sisalto JOIN sisalto.opintojaksot opintojaksot JOIN opintojaksot.oppiaineet oppiaineet WHERE sisalto.opetussuunnitelma.id = :opetussuunnitelmaId AND oppiaineet.koodi = :koodi")
    List<Lops2019OpintojaksonOppiaine> findOpintojaksonOppiaineetByOpetussuunnitelma(@Param("opetussuunnitelmaId") Long opetussuunnitelmaId, @Param("koodi") String koodi);

}
