package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.lukio.OppiaineLukiokurssi;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OppiaineLukiokurssiRepository extends JpaWithVersioningRepository<OppiaineLukiokurssi, Long> {
    @Query(value = "select oalk from OppiaineLukiokurssi  oalk where oalk.opetussuunnitelma.id = ?1 " +
            " and oalk.oppiaine.id = ?2 order by oalk.jarjestys")
    List<OppiaineLukiokurssi> findByOpsAndOppiaine(long opsId, long id);

    @Query(value = "select oalk from OppiaineLukiokurssi  oalk where oalk.opetussuunnitelma.id = ?1 " +
            " and oalk.kurssi.id = ?2 order by oalk.jarjestys")
    List<OppiaineLukiokurssi> findByOpsAndKurssi(long opsId, long id);
}
