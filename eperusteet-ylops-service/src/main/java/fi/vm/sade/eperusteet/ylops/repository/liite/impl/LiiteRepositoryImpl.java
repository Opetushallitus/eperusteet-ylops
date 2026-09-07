package fi.vm.sade.eperusteet.ylops.repository.liite.impl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;

import org.apache.commons.io.IOUtils;
import org.hibernate.engine.jdbc.proxy.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vm.sade.eperusteet.ylops.domain.liite.Liite;
import fi.vm.sade.eperusteet.ylops.repository.liite.LiiteRepositoryCustom;
import jakarta.persistence.EntityManager;

public class LiiteRepositoryImpl implements LiiteRepositoryCustom {

    @Autowired
    EntityManager em;

    @Override
    public Liite add(String tyyppi, String nimi, long length, InputStream is) {
        Blob blob = null;
        try {
            blob = BlobProxy.generateProxy(IOUtils.toByteArray(is));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Liite liite = new Liite(tyyppi, nimi, blob);
        em.persist(liite);
        return liite;
    }

}
