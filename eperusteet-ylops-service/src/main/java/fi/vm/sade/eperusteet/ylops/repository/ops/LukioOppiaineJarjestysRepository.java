package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.lukio.LukioOppiaineId;
import fi.vm.sade.eperusteet.ylops.domain.lukio.LukioOppiaineJarjestys;
import fi.vm.sade.eperusteet.ylops.dto.lukio.OppiaineJarjestysDto;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface LukioOppiaineJarjestysRepository extends JpaWithVersioningRepository<LukioOppiaineJarjestys, LukioOppiaineId> {
    @Query(value = "select j from LukioOppiaineJarjestys j where j.opetussuunnitelma.id = ?1 " +
            " order by j.jarjestys, j.oppiaine.id")
    List<LukioOppiaineJarjestys> findByOpetussuunnitelmaId(long opsId);

    @Query(value = "select j from LukioOppiaineJarjestys j where j.oppiaine.id in ?1 " +
            " order by j.jarjestys, j.oppiaine.id")
    List<LukioOppiaineJarjestys> findByOppiaineIds(Set<Long> oppiaineIds);

    @Query(value = "select j from LukioOppiaineJarjestys j where j.opetussuunnitelma.id = ?1" +
            " and j.oppiaine.id in ?2 order by j.jarjestys, j.oppiaine.id")
    List<LukioOppiaineJarjestys> findByOppiaineIds(long opsId, Set<Long> oppiaineIds);

    @Query(value = "select j from LukioOppiaineJarjestys j where j.opetussuunnitelma.id = ?1" +
            " and j.oppiaine.id = ?2")
    LukioOppiaineJarjestys findByOppiaineId(long opsId, long oppiaineId);

    @Query(value = "select new fi.vm.sade.eperusteet.ylops.dto.lukio.OppiaineJarjestysDto(oa.id, j.jarjestys)" +
            " from LukioOppiaineJarjestys j inner join j.oppiaine oa where j.opetussuunnitelma.id = ?1" +
            " order by j.jarjestys, oa.id")
    List<OppiaineJarjestysDto> findJarjestysDtosByOpetussuunnitelmaId(long opsId);

    @Query(value = "select new fi.vm.sade.eperusteet.ylops.dto.lukio.OppiaineJarjestysDto(oa.id, j.jarjestys)" +
            " from LukioOppiaineJarjestys j inner join j.oppiaine oa where j.opetussuunnitelma.id = ?1 " +
            " and j.oppiaine.id in ?2 order by j.jarjestys, oa.id")
    List<OppiaineJarjestysDto> findJarjestysDtosByOpetussuunnitelmaId(long opsId, Set<Long> oppiaineIds);
}
