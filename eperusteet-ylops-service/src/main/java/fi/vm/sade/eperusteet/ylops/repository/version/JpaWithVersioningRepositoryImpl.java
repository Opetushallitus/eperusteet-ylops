package fi.vm.sade.eperusteet.ylops.repository.version;

import fi.vm.sade.eperusteet.ylops.domain.revision.Revision;
import fi.vm.sade.eperusteet.ylops.domain.revision.RevisionInfo;
import fi.vm.sade.eperusteet.ylops.domain.revision.RevisionInfo_;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

class JpaWithVersioningRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements
        JpaWithVersioningRepository<T, ID> {

    private final EntityManager entityManager;
    private final JpaEntityInformation<T, ID> entityInformation;

    public JpaWithVersioningRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);

        this.entityManager = entityManager;
        this.entityInformation = entityInformation;
    }

    @Override
    public List<Revision> getRevisions(ID id) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        @SuppressWarnings("unchecked")
        List<Object[]> results = (List<Object[]>) auditReader.createQuery()
                .forRevisionsOfEntity(entityInformation.getJavaType(), false, true)
                .addProjection(AuditEntity.id())
                .addProjection(AuditEntity.revisionNumber())
                .addProjection(AuditEntity.revisionProperty(RevisionInfo_.timestamp.getName()))
                .addProjection(AuditEntity.revisionProperty(RevisionInfo_.muokkaajaOid.getName()))
                .addProjection(AuditEntity.revisionProperty(RevisionInfo_.kommentti.getName()))
                .addOrder(AuditEntity.revisionProperty(RevisionInfo_.timestamp.getName()).desc())
                .add(AuditEntity.id().eq(id))
                .getResultList();

        List<Revision> revisions = new ArrayList<>();
        for (Object[] result : results) {
            revisions.add(new Revision((Long) result[0], (Integer) result[1], (Long) result[2], (String) result[3], (String) result[4]));
        }

        return revisions;
    }

    @Override
    public Number getRevisionNumberForDate(Date revisionTime) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return auditReader.getRevisionNumberForDate(revisionTime);
    }

    @Override
    public T findRevision(ID id, Integer revisionId) {
        return AuditReaderFactory.get(entityManager).find(entityInformation.getJavaType(), id, revisionId);
    }

    @Override
    public Integer getLatestRevisionId(ID id) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        final List<Number> revisions = auditReader.getRevisions(entityInformation.getJavaType(), id);
        if (revisions == null || revisions.isEmpty()) {
            return null;
        }
        return revisions.get(revisions.size() - 1).intValue();
    }

    @Override
    public T lock(T entity) {
        entityManager.lock(entity, LockModeType.PESSIMISTIC_WRITE);
        return entity;
    }

    @Override
    public void setRevisioKommentti(String kommentti) {
        RevisionInfo currentRevision = AuditReaderFactory.get(entityManager).getCurrentRevision(RevisionInfo.class, false);
        currentRevision.addKommentti(kommentti);
    }

    @Override
    public T getLatestNotNull(ID id) {
        List<Revision> revisions = this.getRevisions(id);
        revisions = revisions.stream()
                .sorted(Comparator.comparing(Revision::getPvm))
                .collect(Collectors.toList());

        for (Revision revision : revisions) {
            T last = this.findRevision(id, revision.getNumero());
            if (last != null) {
                return last;
            }
        }
        return null;
    }

}
