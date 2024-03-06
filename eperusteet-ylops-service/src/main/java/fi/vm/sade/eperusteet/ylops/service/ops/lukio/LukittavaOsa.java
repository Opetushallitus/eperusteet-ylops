package fi.vm.sade.eperusteet.ylops.service.ops.lukio;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.repository.ops.*;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;

import java.util.Optional;
import java.util.function.Function;

public enum LukittavaOsa {
    OPS(OpetussuunnitelmaRepository.class),
    OPPIAINE(OppiaineRepository.class),
    LUKIOKURSSI(LukiokurssiRepository.class),
    YLEISET_TAVOITTEET(OpetuksenYleisetTavoitteetRepository.class, Opetussuunnitelma::getOpetuksenYleisetTavoitteet),
    AIHEKOKONAISUUDET(AihekokonaisuudetRepository.class, Opetussuunnitelma::getAihekokonaisuudet),
    AIHEKOKONAISUUS(AihekokonaisuusRepository.class);

    private Optional<Function<Opetussuunnitelma, ? extends AbstractAuditedReferenceableEntity>> fromOps = Optional.empty();

    private Class<? extends JpaWithVersioningRepository<?, ?>> repository;

    <T> LukittavaOsa(Class<? extends JpaWithVersioningRepository<T, ?>> repository) {
        this.repository = repository;
    }

    <T extends AbstractAuditedReferenceableEntity> LukittavaOsa(Class<? extends JpaWithVersioningRepository<T, ?>> repository,
                                                                Function<Opetussuunnitelma, T> fromOps) {
        this.repository = repository;
        this.fromOps = Optional.of(fromOps);
    }

    public Class<? extends JpaWithVersioningRepository<?, ?>> getRepository() {
        return repository;
    }

    public Optional<Function<Opetussuunnitelma, ? extends AbstractAuditedReferenceableEntity>> getFromOps() {
        return fromOps;
    }

    public boolean isFromOps() {
        return fromOps.isPresent();
    }
}
