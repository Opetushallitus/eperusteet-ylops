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
.controller('OppiaineBaseController', ($scope, perusteOppiaine, MurupolkuData, $stateParams, $rootScope,
                                       OppiaineService, versionHistory, $location, $state) => {

  $scope.oppiaine = OppiaineService.getOppiaine();
  $scope.palautettavissa = false;
  if($scope.oppiaine.oma){
    OppiaineService.palautettavissa({opsId: $stateParams.id, oppiaineId: $stateParams.oppiaineId}).then( (palautettavissa) => {
      $scope.palautettavissa = palautettavissa;
    });
  }

  $scope.oppiaineenVlk = OppiaineService.getOpVlk();
  $scope.perusteenVlk = perusteOppiaine && _.find(perusteOppiaine.vuosiluokkakokonaisuudet,
    (vlk) => $scope.oppiaineenVlk._vuosiluokkakokonaisuus === vlk._vuosiluokkakokonaisuus);

  const updateHistory = () => {
    $scope.versiot = {
      latest: true,
      list: []
    };

    _.forEach(versionHistory, (value, key) => {
      value.index = versionHistory.length-key;
      $scope.versiot.list.push(value);
    });

    $scope.versiot.first = _.first($scope.versiot.list);
    $scope.versiot.chosen = _.first($scope.versiot.list);
  };

  updateHistory();
  $scope.isLatest = () => $scope.versiot.chosen && _.first($scope.versiot.list).numero === $scope.versiot.chosen.numero;
  $scope.goToLatest = () => {
    const versionUrl = $state.href($state.current.name).replace(/&versio=\d+/i, '').replace(/#/g, '').split('%')[0];
    $location.url(versionUrl);
  };

  $scope.revertToCurrentVersion = () => {
    OppiaineService.revertToVersion($stateParams.versio).then( $scope.goToLatest );
  };

  if($stateParams.versio){
    $scope.versiot.chosen = _.chain($scope.versiot.list)
        .filter((versio) => { return versio.numero === parseInt($stateParams.versio) })
        .first()
        .value();
    $scope.versiot.latest = $scope.isLatest();
  }

  $scope.$$muokkaustiedot = {
    muokattu: $scope.oppiaine.muokattu,
    muokkaaja: $scope.oppiaine.muokkaaja
  };

  $scope.vaihdaVersio = () => {
    let versionUrl = $state.href($state.current.name).replace(/&versio=\d+/i, '').replace(/#/g, '').split('%')[0];
    versionUrl += !$scope.isLatest() ? '&versio='+$scope.versiot.chosen.numero : '';
    $location.url(versionUrl);
  };

  if (perusteOppiaine) {
    if (perusteOppiaine.eiPerustetta) {
      $scope.eiPerustetta = true;
    }
    if (perusteOppiaine.tunniste === $scope.oppiaine.tunniste) {
      $scope.perusteOppiaine = perusteOppiaine;
    }
    else {
      $scope.perusteOppiaine = _.find(perusteOppiaine.oppimaarat, (om) => {
        return om.tunniste === $scope.oppiaine.tunniste;
      });
    }
  }

  const
    onOma = $scope.oppiaine.oma,
    hasVuosiluokkakokonaisuudet = !_.isEmpty($scope.oppiaine.vuosiluokkakokonaisuudet),
    hasVlkTavoitteet = !perusteOppiaine || $scope.perusteenVlk && !_.isEmpty($scope.perusteenVlk.tavoitteet);

  $scope.isVuosiluokkaistettava = hasVuosiluokkakokonaisuudet && hasVlkTavoitteet;

  $scope.$on('oppiainevlk:updated', (event, value) => {
    $scope.oppiaineenVlk = value;
    $scope.oppiaine = OppiaineService.getOppiaine();
  });

  $scope.$on('oppiaine:reload', () => {
    OppiaineService.getVersions($stateParams).then( (res) => {
      versionHistory = res;
      updateHistory();
    });
    OppiaineService.refresh($scope.model, $stateParams.oppiaineId, $stateParams.vlkId);
  });
})

.service('TextUtils', () => {
  this.toPlaintext = (text) => String(text).replace(/<[^>]+>/gm, '');
  this.getCode = (text) => {
    const match = text.match(/(^|[^A-Za-z])([A-Za-z]\d{1,2})($|[^0-9])/);
    return match ? match[2] : null;
  };
})

.controller('OppiaineController', function ($scope, $state, $stateParams, $q, Editointikontrollit, Varmistusdialogi,
  VuosiluokatService, Kaanna, OppiaineService, TextUtils, Utils, Kielitarjonta, OppiaineCRUD, OpsService, Notifikaatiot,
  VuosiluokkakokonaisuusMapper, Lukko, Kommentit, KommentitByOppiaine, opsModel) {

  Kommentit.haeKommentit(KommentitByOppiaine, {
    id: $stateParams.oppiaineId,
    opsId: $stateParams.id,
    oppiaineId: $stateParams.oppiaineId,
    vlkId: $stateParams.vlkId
  });

  $scope.lukkotiedot = null;
  $scope.vuosiluokat = [];
  $scope.alueOrder = Utils.sort;
  $scope.startEditing = Editointikontrollit.startEditing;
  $scope.$$muokkaaNimea = false;
  $scope.muokkaaNimea = () => {
    $scope.$$muokkaaNimea = true;
    $scope.startEditing();
  };

  $scope.firstVuosiluokka = () => _.first($scope.oppiaine.vuosiluokkakokonaisuudet);
  $scope.isValinnainen = () => _.includes(['muu_valinnainen', 'taide_taitoaine'], $scope.oppiaine.tyyppi);

  $scope.getVuosiluokkaUrl = (vuosiluokka) =>
    $state.href('root.opetussuunnitelmat.yksi.opetus.oppiaine.vuosiluokka.tavoitteet', _.merge(_.clone($stateParams), { vlId: vuosiluokka.id }));

  const commonParams = $scope.oppiaineenVlk ? {
    opsId: $stateParams.id,
    vlkId: $scope.oppiaineenVlk.id,
    oppiaineId: $stateParams.oppiaineId
  } : null;

  if (commonParams) {
    Lukko.isLocked($scope, commonParams);
  }

  const vanhempiOnUskontoTaiKieli = (oppiaine) => _.isString(oppiaine.koodiArvo) && _.includes(['AI', 'VK', 'TK', 'KT'], oppiaine.koodiArvo.toUpperCase());

  OppiaineService.getParent()
    .then((res) => {
      $scope.oppiaine.$parent = res;
      $scope.$onKieliTaiUskonto = vanhempiOnUskontoTaiKieli(res);
      if ($scope.$onKieliTaiUskonto) {
        $scope.oppiaine.tehtava = $scope.oppiaine.tehtava || {};
      }
    })
    .catch(_.noop);

  $scope.perusteOpVlk = $scope.perusteOppiaine ?
    _.find($scope.perusteOppiaine.vuosiluokkakokonaisuudet, (vlk) => {return vlk._vuosiluokkakokonaisuus === $scope.oppiaineenVlk._vuosiluokkakokonaisuus;
    }) : {};
  if ($scope.eiPerustetta) {
    VuosiluokkakokonaisuusMapper.createEmptyText($scope.perusteOpVlk, 'tyotavat');
    VuosiluokkakokonaisuusMapper.createEmptyText($scope.perusteOpVlk, 'ohjaus');
    VuosiluokkakokonaisuusMapper.createEmptyText($scope.perusteOpVlk, 'arviointi');
  }

  var perusteTavoitteet = _.indexBy($scope.perusteOpVlk ? $scope.perusteOpVlk.tavoitteet : null, 'tunniste');

  if ($scope.oppiaine.koosteinen && vanhempiOnUskontoTaiKieli($scope.oppiaine)) {
    $scope.valitseOppimaara = () => {
      var opsId = $stateParams.id;
      Kielitarjonta.rakenna(opsId, $scope.oppiaine, $scope.perusteOppiaine, (res) => {
        // var ops = OpsService.get(opsId);
        var ops = opsModel;

        var tunnisteet = _.map(res.vuosiluokkakokonaisuudet, '_vuosiluokkakokonaisuus');
        // TODO järjestys vuosiluokkaenumin mukaan nimen sijasta?
        var lisatytVlkt = _(ops.vuosiluokkakokonaisuudet)
            .map('vuosiluokkakokonaisuus')
            .filter(vlk => _.includes(tunnisteet, vlk._tunniste))
            .sortBy(vlk => Kaanna.kaanna(vlk.nimi))
            .value();

        Notifikaatiot.onnistui(
          Kaanna.kaanna(res.nimi) +
          Kaanna.kaanna('oppiaine-lisattiin-vuosiluokkakokonaisuuksiin') +
          _.map(lisatytVlkt, (vlk) => {
            return Kaanna.kaanna(vlk.nimi);
          }).join(', '));

        var vlkId =
          _(lisatytVlkt).map('id').includes(parseInt($stateParams.vlkId))? $stateParams.vlkId : lisatytVlkt[0].id;

        $state.go('root.opetussuunnitelmat.yksi.opetus.oppiaine.oppiaine', {
          oppiaineId: res.id,
          vlkId: vlkId,
          oppiaineTyyppi: res.tyyppi
        }, { reload: true });
      });
    };
  }

  $scope.poistaOppimaara = () => {
    Varmistusdialogi.dialogi({
      otsikko: 'varmista-poisto',
      primaryBtn: 'poista',
      successCb:  () => {
        OppiaineCRUD.remove({
          opsId: $stateParams.id,
          oppiaineId: $stateParams.oppiaineId
        }, () => {
          Notifikaatiot.onnistui('oppimaaran-poisto-onnistui');
          $state.go($state.current.name, _.merge(_.clone($stateParams), {
            oppiaineId: $scope.oppiaine.$parent.id
          }), { reload: true });
        }, Notifikaatiot.serverCb);
      }
    })();
  };

  const updateVuosiluokat = () => {
    if (!$scope.oppiaineenVlk) {
      return;
    }
    $scope.vuosiluokat = $scope.oppiaineenVlk.vuosiluokat;
    _.each($scope.vuosiluokat, (vlk) => {
      vlk.$numero = VuosiluokatService.fromEnum(vlk.vuosiluokka);
      var allShort = true;
      _.each(vlk.tavoitteet, (tavoite) => {
        var perusteTavoite = perusteTavoitteet[tavoite.tunniste] || {};
        tavoite.$tavoite = perusteTavoite.tavoite;
        var tavoiteTeksti = TextUtils.toPlaintext(Kaanna.kaanna(perusteTavoite.tavoite));
        tavoite.$short = TextUtils.getCode(tavoiteTeksti);
        if (!tavoite.$short) {
          allShort = false;
        }
      });
      vlk.$tavoitteetShort = allShort;
      allShort = true;
      _.each(vlk.sisaltoalueet, (alue) => {
        alue.$short = TextUtils.getCode(Kaanna.kaanna(alue.nimi));
        if (!alue.$short) {
          allShort = false;
        }
      });
      vlk.$sisaltoalueetShort = allShort;
    });
  };
  updateVuosiluokat();

  $scope.$on('oppiainevlk:updated', (event, value) => {
    $scope.oppiaineenVlk = value;
    updateVuosiluokat();
  });

  const refetch = () => $q((resolve) =>{
    resolve();
    OppiaineService.fetchVlk($scope.oppiaineenVlk.id, (res) => {
      $scope.oppiaineenVlk = res;
      updateVuosiluokat();
    });
  });

  $scope.options = {
    editing: false,
    isEditable: () => {
      return OpsService.isEditable() && $scope.oppiaine.oma && (!$scope.lukkotiedot || !$scope.lukkotiedot.lukittu);
    }
  };

  $scope.callbacks = {
    edit: refetch,
    cancel: () => $q((resolve) => {
      $scope.$$muokkaaNimea = false;
      return refetch().then(resolve);
    }),
    save: () => $q((resolve) => {
      $scope.$$muokkaaNimea = false;
      $scope.oppiaine.$save({ opsId: $stateParams.id }, () => {
        OppiaineService.saveVlk($scope.oppiaineenVlk).then(resolve);
      });
    }),
    notify: (mode) => {
      $scope.options.editing = mode;
      $scope.callbacks.notifier(mode);
    },
    notifier: _.noop
  };
  Editointikontrollit.registerCallback($scope.callbacks);

  const vuosiluokkaistamisVaroitus = (cb) => {
    Varmistusdialogi.dialogi({
      otsikko: 'vuosiluokkaistaminen-on-jo-tehty',
      teksti: 'vuosiluokkaistaminen-varoitus',
      primaryBtn: 'jatka',
      successCb: cb
    })();
  };

  $scope.goToVuosiluokka = (vuosiluokka) => {
    $state.go('root.opetussuunnitelmat.yksi.opetus.oppiaine.vuosiluokka', {vlId: vuosiluokka.id});
  };

  $scope.startVuosiluokkaistaminen = () => {
    const start = () => {
      $state.go('root.opetussuunnitelmat.yksi.opetus.oppiaine.vuosiluokkaistaminen', {
        vlkId: $stateParams.vlkId
      });
    };
    if (_.isArray($scope.vuosiluokat) && $scope.vuosiluokat.length > 0) {
      vuosiluokkaistamisVaroitus(start);
    } else {
      start();
    }
  };

  $scope.editOppiaine = () => {
    Lukko.lock(commonParams, () => {
      $state.go('root.opetussuunnitelmat.yksi.opetus.uusioppiaine', {
        vlkId: $stateParams.vlkId,
        oppiaineId: $scope.oppiaine.id
      });
    });
  };

  $scope.removeOppiaine = () => {
    Lukko.lock(commonParams, () => {
      Varmistusdialogi.dialogi({
        otsikko: 'varmista-poisto',
        primaryBtn: 'poista',
        successCb: () => {
          $scope.oppiaine.$delete({opsId: OpsService.getId()}, () => {
            Lukko.unlock(commonParams);
            Notifikaatiot.onnistui('poisto-onnistui');
            $state.go('root.opetussuunnitelmat.yksi.opetus.vuosiluokkakokonaisuus', {vlkId: $stateParams.vlkId}, {reload: true});
          }, () => {
            Lukko.unlock(commonParams);
            Notifikaatiot.serverCb();
          });
        },
        failureCb: () => Lukko.unlock(commonParams)
      })();
    });
  };

  $scope.palautaVanhaan = () => {
    Varmistusdialogi.dialogi({
      otsikko: 'varmista-oppiaineen-palautus',
      htmlSisalto: '<h4><strong>'+ Kaanna.kaanna('varmista-oppiaineen-palautus-teksti') +'</strong></h4>',
      primaryBtn: 'palauta-oppiaine',
      primaryBtnClass: 'danger-btn',
      successCb: () => {
         OppiaineCRUD.palautaYlempaan({
           opsId: $stateParams.id,
           oppiaineId: $stateParams.oppiaineId
         }, {}, (res) => {
           Notifikaatiot.onnistui('palaaminen-vanhaan-onnistui');
            $state.go('root.opetussuunnitelmat.yksi.opetus.oppiaine.oppiaine', {
              vlkId: $stateParams.vlkId,
              oppiaineId: res.id,
              oppiaineTyyppi: res.tyyppi
            }, { reload: true });
         }, Notifikaatiot.serverCb);
      }
    })();
  };

  $scope.kopioiMuokattavaksi = () => {
    Varmistusdialogi.dialogi({
      otsikko: 'varmista-kopiointi',
      htmlSisalto: '<p>'+ Kaanna.kaanna('varmista-kopiointi-teksti') +'</p><h4><strong>' + Kaanna.kaanna('ei-voi-perua') +'</strong></h4>',
      primaryBtn: 'luo-kopio',
      successCb: () => {
        OppiaineCRUD.kloonaaMuokattavaksi({
          opsId: $stateParams.id,
          oppiaineId: $stateParams.oppiaineId
        }, {}, (res) => {
          Notifikaatiot.onnistui('kopion-luonti-onnistui');
          $state.go('root.opetussuunnitelmat.yksi.opetus.oppiaine.oppiaine', {
            vlkId: $stateParams.vlkId,
            oppiaineId: res.id,
            oppiaineTyyppi: res.tyyppi
          }, { reload: true });
        }, Notifikaatiot.serverCb);
      }
    })();
  };
});
