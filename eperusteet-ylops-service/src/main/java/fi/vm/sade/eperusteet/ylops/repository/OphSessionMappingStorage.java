package fi.vm.sade.eperusteet.ylops.repository;

import org.jasig.cas.client.session.SessionMappingStorage;

public interface OphSessionMappingStorage extends SessionMappingStorage {

  void clean();
}