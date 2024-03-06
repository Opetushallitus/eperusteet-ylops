package fi.vm.sade.eperusteet.ylops.repository.liite.impl;

import fi.vm.sade.eperusteet.ylops.domain.liite.Liite;
import fi.vm.sade.eperusteet.ylops.repository.liite.LiiteRepositoryCustom;

import java.io.InputStream;
import java.sql.Blob;
import javax.persistence.EntityManager;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;

public class LiiteRepositoryImpl implements LiiteRepositoryCustom {

    @Autowired
    EntityManager em;

    @Override
    public Liite add(String tyyppi, String nimi, long length, InputStream is) {
        Session session = em.unwrap(Session.class);
        Blob blob = Hibernate.getLobCreator(session).createBlob(is, length);
        Liite liite = new Liite(tyyppi, nimi, blob);
        em.persist(liite);
        return liite;
    }

}
