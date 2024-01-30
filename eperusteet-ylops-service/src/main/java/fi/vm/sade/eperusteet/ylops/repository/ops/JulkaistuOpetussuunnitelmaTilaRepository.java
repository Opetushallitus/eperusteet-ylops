package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.ops.JulkaistuOpetussuunnitelmaTila;
import fi.vm.sade.eperusteet.ylops.repository.CustomJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JulkaistuOpetussuunnitelmaTilaRepository extends CustomJpaRepository<JulkaistuOpetussuunnitelmaTila, Long> {
}