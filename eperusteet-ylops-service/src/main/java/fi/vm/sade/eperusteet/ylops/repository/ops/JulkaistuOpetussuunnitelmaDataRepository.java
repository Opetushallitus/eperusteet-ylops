package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.ops.JulkaistuOpetussuunnitelmaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JulkaistuOpetussuunnitelmaDataRepository extends JpaRepository<JulkaistuOpetussuunnitelmaData, Long> {
}
