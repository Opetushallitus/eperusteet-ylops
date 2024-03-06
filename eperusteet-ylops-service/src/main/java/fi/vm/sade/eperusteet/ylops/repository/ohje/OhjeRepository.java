package fi.vm.sade.eperusteet.ylops.repository.ohje;

import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import fi.vm.sade.eperusteet.ylops.domain.ohje.Ohje;

import java.util.List;

import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OhjeRepository extends JpaWithVersioningRepository<Ohje, Long> {
    Ohje findFirstByKohde(UUID kohde);

    List<Ohje> findByKohde(UUID kohde);
}
