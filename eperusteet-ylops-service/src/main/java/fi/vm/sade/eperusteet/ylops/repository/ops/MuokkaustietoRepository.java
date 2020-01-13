package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.ops.OpetussuunnitelmanMuokkaustieto;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MuokkaustietoRepository extends JpaRepository<OpetussuunnitelmanMuokkaustieto, Long> {

    List<OpetussuunnitelmanMuokkaustieto> findByOpetussuunnitelmaIdAndLuotuBeforeOrderByLuotuDesc(Long opsId, Date viimeisinLuontiaika, Pageable pageable);

    default List<OpetussuunnitelmanMuokkaustieto> findTop10ByOpetussuunnitelmaIdAndLuotuBeforeOrderByLuotuDesc(Long opsId, Date viimeisinLuontiaika, int lukumaara) {
        return findByOpetussuunnitelmaIdAndLuotuBeforeOrderByLuotuDesc(opsId, viimeisinLuontiaika, new PageRequest(0, Math.min(lukumaara, 100)));
    }
}
