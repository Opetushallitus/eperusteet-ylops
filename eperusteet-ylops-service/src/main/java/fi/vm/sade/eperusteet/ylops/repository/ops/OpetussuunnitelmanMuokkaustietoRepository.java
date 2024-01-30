package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.ops.OpetussuunnitelmanMuokkaustieto;
import fi.vm.sade.eperusteet.ylops.repository.CustomJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OpetussuunnitelmanMuokkaustietoRepository extends CustomJpaRepository<OpetussuunnitelmanMuokkaustieto, Long> {

    List<OpetussuunnitelmanMuokkaustieto> findByOpetussuunnitelmaIdAndLuotuBeforeOrderByLuotuDesc(Long opsId, Date viimeisinLuontiaika, Pageable pageable);

    default List<OpetussuunnitelmanMuokkaustieto> findTop10ByOpetussuunnitelmaIdAndLuotuBeforeOrderByLuotuDesc(Long opsId, Date viimeisinLuontiaika, int lukumaara) {
        return findByOpetussuunnitelmaIdAndLuotuBeforeOrderByLuotuDesc(opsId, viimeisinLuontiaika, PageRequest.of(0, Math.min(lukumaara, 100)));
    }

    List<OpetussuunnitelmanMuokkaustieto> findByKohdeId(Long kohdeId);

    OpetussuunnitelmanMuokkaustieto findTop1ByOpetussuunnitelmaIdAndLisatietoOrderByLuotuDesc(Long opsId, String lisatieto);
}
