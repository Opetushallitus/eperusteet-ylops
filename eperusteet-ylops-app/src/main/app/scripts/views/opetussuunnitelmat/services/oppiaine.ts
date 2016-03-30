/*
* Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
*
* This program is free software: Licensed under the EUPL, Version 1.1 or - as
* soon as they will be approved by the European Commission - subsequent versions
* of the EUPL (the "Licence");
*
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* European Union Public Licence for more details.
*/

'use strict';

ylopsApp
.service('OppiaineService', function (VuosiluokatService, $rootScope, MurupolkuData, $q, OppiaineCRUD, OppiaineenVlk,
  Notifikaatiot, VuosiluokkaCRUD) {
  let vlkTunniste = null;
  let oppiaineenVlk = null;
  let oppiaine = null;
  let opetussuunnitelma = null;

  const setup = (ops, vlkId, oppiaineModel) => {
    return $q((resolve, reject) => {
      opetussuunnitelma = ops;
      oppiaine = oppiaineModel;
      MurupolkuData.set('oppiaineNimi', oppiaine.nimi);
      const opsVlk = _.find(ops.vuosiluokkakokonaisuudet, (vlk) => '' + vlk.vuosiluokkakokonaisuus.id === vlkId);
      vlkTunniste = opsVlk ? opsVlk.vuosiluokkakokonaisuus._tunniste : null;
      oppiaineenVlk = _.find(oppiaine.vuosiluokkakokonaisuudet, (opVlk) => opVlk._vuosiluokkakokonaisuus === vlkTunniste);
      resolve();
    });
  };

  this.getParent = () => {
    return OppiaineCRUD.getParent({
      opsId: opetussuunnitelma.id,
      oppiaineId: oppiaine.id
    }).$promise;
  };
  this.revertToVersion = (version) => {
    return OppiaineCRUD.revertToVersion({
      opsId: opetussuunnitelma.id,
      oppiaineId: oppiaine.id,
      versio: version
    }, {}).$promise;
  };
  this.getVersions = (params) => {
    return OppiaineCRUD.getVersions({opsId: params.id, oppiaineId: params.oppiaineId}).$promise;
  };
  this.palauta = (params) => {
    return OppiaineCRUD.palautaOppiaine({opsId: params.opsId, oppiaineId: params.id, oppimaara: params.oppimaara}, {}).$promise;
  };
  this.refresh = (ops, oppiaineId, vlkId, versio) => {
    return $q((resolve, reject) => {
      VuosiluokatService.getOppiaine(oppiaineId, versio).$promise
        .then( (res) => {
          setup(ops, vlkId, res).then(resolve);
          $rootScope.$broadcast('oppiainevlk:updated', oppiaineenVlk);
        })
        .catch(reject);
    });
  };
  this.getOpVlk =  () => {
    return oppiaineenVlk;
  };
  this.getOppiaine =  () => {
    return oppiaine;
  };
  this.saveVlk = (model) => $q((resolve) => {
    OppiaineenVlk.save({
      opsId: opetussuunnitelma.id,
      oppiaineId: oppiaine.id
    }, model, () => {
      Notifikaatiot.onnistui('tallennettu-ok');
      $rootScope.$broadcast('oppiaine:reload');
      resolve();
    }, Notifikaatiot.serverCb);
  });
  this.fetchVlk = (vlkId, cb) => {
    return OppiaineenVlk.get({
      opsId: opetussuunnitelma.id,
      oppiaineId: oppiaine.id,
      vlkId: vlkId
    }, cb, Notifikaatiot.serverCb).$promise;
  };
  this.saveVuosiluokka = (model, cb) => {
    VuosiluokkaCRUD.save({
      opsId: opetussuunnitelma.id,
      vlkId: oppiaineenVlk.id,
      oppiaineId: oppiaine.id
    }, model, (res) => {
      Notifikaatiot.onnistui('tallennettu-ok');
      cb(res);
    }, Notifikaatiot.serverCb);
  };
  this.saveValinnainenVuosiluokka = (vlId, model, cb) => {
    VuosiluokkaCRUD.saveValinnainen({
      opsId: opetussuunnitelma.id,
      vlkId: oppiaineenVlk.id,
      oppiaineId: oppiaine.id,
      vvlId: vlId
    }, model, (res) => {
      cb(res);
    }, Notifikaatiot.serverCb);
  };
  this.fetchVuosiluokka = (vlId, cb) => {
    VuosiluokkaCRUD.get({
      opsId: opetussuunnitelma.id,
      oppiaineId: oppiaine.id,
      vlkId: oppiaineenVlk.id,
      vlId: vlId
    }, cb, Notifikaatiot.serverCb);
  };
  this.palautettavissa = (params) => {
    return $q((resolve, reject) => {
      OppiaineCRUD.palautettavissa({opsId: params.opsId, oppiaineId: params.oppiaineId}, {}).$promise.then( (res) => {
        resolve(res.palautettavissa);
      }, reject);
    });
  }
});
