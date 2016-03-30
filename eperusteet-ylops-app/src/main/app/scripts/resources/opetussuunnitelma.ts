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
  .service('YlopsResources', function (SERVICE_LOC) {
    this.OPS = SERVICE_LOC + '/opetussuunnitelmat/:opsId';
    this.LUKIO_OPS = SERVICE_LOC + '/opetussuunnitelmat/lukio/:opsId';
    this.OPPIAINE = this.OPS + '/oppiaineet/:oppiaineId';
    this.VLK = this.OPS + '/vuosiluokkakokonaisuudet/:vlkId';
    this.OPVLK = this.OPPIAINE + '/vuosiluokkakokonaisuudet/:vlkId';
    this.VUOSILUOKKA = this.OPPIAINE + '/vuosiluokkakokonaisuudet/:vlkId/vuosiluokat/:vlId';
    this.VUOSILUOKKAVALINNAINEN = this.OPPIAINE + '/vuosiluokkakokonaisuudet/:vlkId/vuosiluokat/:vvlId/valinnainen';
  })

  .factory('OpetussuunnitelmaCRUD', function ($resource, YlopsResources, SERVICE_LOC) {
    return $resource(YlopsResources.OPS, {
      opsId: '@id'
    }, {
      laajaalaiset: {method: 'GET', url: YlopsResources.OPS + '/laajaalaisetosaamiset', isArray: true},
      setTila: {method: 'POST', url: YlopsResources.OPS + '/tila/:tila'},
      palauta: {method: 'POST', url: YlopsResources.OPS + '/palauta'},
      lisaaKielitarjonta: {method: 'POST', url: YlopsResources.OPS + '/kielitarjonta', isArray: true},
      syncPeruste: {method: 'POST', url: YlopsResources.OPS + '/sync'},
      jarjestaOppiaineet: {method: 'POST', url: YlopsResources.OPS + '/oppiainejarjestys', isArray: true},
      tilastot: { method: 'GET', url: SERVICE_LOC + '/opetussuunnitelmat/tilastot', isArray: true },
      opetussuunnitelmat: { method: 'GET', url: SERVICE_LOC + '/opetussuunnitelmat/:opsId/opetussuunnitelmat', isArray: true },
      opetussuunnitelmatSync: { method: 'POST', url: SERVICE_LOC + '/opetussuunnitelmat/:opsId/opetussuunnitelmat', isArray: false }
    });
  })
  .factory('OpetussuunnitelmaOikeudet', function ($resource, YlopsResources) {
    return $resource(YlopsResources.OPS + '/oikeudet', {
      opsId: '@id'
    }, {
      query: {method: 'GET', isArray: false}
    });
  })

  .factory('OpetussuunnitelmanTekstit', function ($resource, YlopsResources) {
    return $resource(YlopsResources.OPS + '/tekstit/:viiteId', {
      viiteId: '@id'
    }, {
      setChild: {method: 'POST', url: YlopsResources.OPS + '/tekstit/:parentId/lapsi/:childId'},
      addChild: {method: 'POST', url: YlopsResources.OPS + '/tekstit/:viiteId/lapsi'},
      kloonaaTekstikappale: {method: 'POST', url: YlopsResources.OPS + '/tekstit/:viiteId/muokattavakopio'},
      otsikot: {method: 'GET', url: YlopsResources.OPS + '/otsikot'},
      versiot: {method: 'GET', url: YlopsResources.OPS + ':id/tekstit/:tekstiId/versiot', isArray: true},
      versio: {method: 'GET', url: YlopsResources.OPS + '/tekstit/:viiteId/versio/:id'},
      revertTo: {method: 'POST', url: YlopsResources.OPS + '/tekstit/:viiteId/revert/:versio'},
      poistetut: {method: 'GET', url: YlopsResources.OPS + '/tekstit/removed', isArray: true},
      palauta: {method: 'POST', url: YlopsResources.OPS + '/tekstit/:id/returnRemoved'}
    });
  })

  .factory('OpetussuunnitelmanTekstitLukko', function ($resource, YlopsResources) {
    return $resource(YlopsResources.OPS + '/tekstit/:viiteId/lukko', {
      viiteId: '@id'
    });
  })

  .factory('OpetussuunnitelmanTekstitRakenneLukko', function ($resource, YlopsResources) {
    return $resource(YlopsResources.OPS + '/tekstit/lukko');
  })

  .factory('OppiaineLukko', function ($resource, YlopsResources) {
    return $resource(YlopsResources.OPS + '/oppiaineet/:oppiaineId/lukko');
  })

  .factory('OppiaineenVuosiluokkakokonaisuusLukko', function ($resource, YlopsResources) {
    return $resource(YlopsResources.OPS + '/oppiaineet/:oppiaineId/vuosiluokkakokonaisuudet');
  })

  .factory('OppiaineenVuosiluokkakokonaisuusLukko', function ($resource, YlopsResources) {
    return $resource(YlopsResources.OPS + '/oppiaineet/:oppiaineId/vuosiluokkakokonaisuudet/:vlkId/lukko');
  })

  .factory('OppiaineenVuosiluokkaLukko', function ($resource, YlopsResources) {
    return $resource(YlopsResources.OPS + '/oppiaineet/:oppiaineId/vuosiluokkakokonaisuudet/:vlkId/vuosiluokat/:vlId/lukko');
  })

  .factory('OpetusuunnitelmaLukio', function ($resource, YlopsResources) {
    return $resource(YlopsResources.LUKIO_OPS, {
    }, {
      aihekokonaisuudet: {method: 'GET', url: YlopsResources.LUKIO_OPS+'/aihekokonaisuudet', isArray: false},
      saveAihekokonaisuus: {method: 'POST', url: YlopsResources.LUKIO_OPS + '/aihekokonaisuudet/kokonaisuus', isArray: false},
      rearrangeAihekokonaisuudet: {method: 'POST', url: YlopsResources.LUKIO_OPS + '/aihekokonaisuudet/jarjesta', isArray: false},
      updateAihekokonaisuudetYleiskuvaus: {method: 'POST', url: YlopsResources.LUKIO_OPS + '/aihekokonaisuudet/yleiskuvaus', isArray: false},
      updateAihekokonaisuus: {method: 'POST', url: YlopsResources.LUKIO_OPS + '/aihekokonaisuudet/kokonaisuus/:aihekokonaisuusId', isArray: false},
      deleteAihekokonaisuus: {method: 'DELETE', url: YlopsResources.LUKIO_OPS + '/aihekokonaisuudet/kokonaisuus/:aihekokonaisuusId', isArray: false},

      rakenne: {method: 'GET', url: YlopsResources.LUKIO_OPS+'/rakenne', isArray: false},
      opetuksenYleisetTavoitteet: {method: 'GET', url: YlopsResources.LUKIO_OPS+'/opetuksenYleisetTavoitteet', isArray: false},
      updateOpetuksenYleisetTavoitteet: {method: 'POST', url: YlopsResources.LUKIO_OPS+'/opetuksenYleisetTavoitteet', isArray: false},
      oppiaine: {method: 'GET', url: YlopsResources.LUKIO_OPS+'/oppiaine/:oppiaineId', isArray: false},
      saveOppiaine: {method: 'POST', url:YlopsResources.LUKIO_OPS+'/oppiaine', isArray: false},
      updateOppiaine: {method: 'PUT', url:YlopsResources.LUKIO_OPS+'/oppiaine', isArray: false},
      updateStructure: {method: 'POST', url:YlopsResources.LUKIO_OPS+'/rakenne', isArray:false},
      addKielitarjonta: {method: 'POST', url: YlopsResources.LUKIO_OPS + '/oppiaine/:oppiaineId/kielitarjonta', isArray:false},
      addAbstraktiOppiaine: {method: 'POST', url: YlopsResources.LUKIO_OPS + '/oppiaine/abstrakti', isArray:false},

      saveKurssi: {method: 'POST', url: YlopsResources.LUKIO_OPS + '/kurssi', isArray:false},
      updateKurssi: {method: 'POST', url: YlopsResources.LUKIO_OPS + '/kurssi/:kurssiId', isArray:false},
      disconnectKurssi: {method: 'POST', url: YlopsResources.LUKIO_OPS + '/kurssi/:kurssiId/disconnect', isArray:false},
      reconnectKurssi: {method: 'POST', url: YlopsResources.LUKIO_OPS + '/kurssi/:kurssiId/reconnect', isArray:false},
      removeKurssi: {method: 'DELETE', url: YlopsResources.LUKIO_OPS + '/kurssi/:kurssiId/remove', isArray:false}
    });
  })
  .factory('OpetusuunnitelmaLukioLukko', function ($resource, YlopsResources) {
    return $resource(YlopsResources.LUKIO_OPS + '/lukko');
  })

  .factory('OppiaineCRUD', function ($resource, YlopsResources) {
    return $resource(YlopsResources.OPPIAINE, {
      oppiaineId: '@id'
    }, {
      peruste: {method: 'GET', url: YlopsResources.OPPIAINE + '/peruste'},
      saveValinnainen: {method: 'POST', url: YlopsResources.OPPIAINE + '/valinnainen'},
      addKielitarjonta: {method: 'POST', url: YlopsResources.OPPIAINE + '/kielitarjonta'},
      getParent: {method: 'GET', url: YlopsResources.OPPIAINE + '/parent'},
      kloonaaMuokattavaksi: {method: 'POST', url: YlopsResources.OPPIAINE + '/muokattavakopio'},
      palautaYlempaan: {method: 'POST', url: YlopsResources.OPPIAINE + '/palautaYlempi'},
      palautettavissa: {method: 'GET', url: YlopsResources.OPPIAINE + '/palautettavissa'},
      getVersions: {method: 'GET', url: YlopsResources.OPPIAINE + '/versiot', isArray: true},
      getVersion: {method: 'GET', url: YlopsResources.OPPIAINE + '/versio/:versio'},
      revertToVersion: {method: 'POST', url: YlopsResources.OPPIAINE + '/versio/:versio'},
      getRemoved: {method: 'GET', url: YlopsResources.OPPIAINE + '/poistetut', isArray: true},
      palautaOppiaine: {method: 'POST', url: YlopsResources.OPPIAINE + '/palauta/:oppimaara'}
    });
  })

  .factory('VuosiluokkakokonaisuusCRUD', function ($resource, YlopsResources) {
    return $resource(YlopsResources.VLK, {
      vlkId: '@id'
    }, {
      peruste: {method: 'GET', url: YlopsResources.VLK + '/peruste'},
      kloonaaMuokattavaksi: {method: 'POST', url: YlopsResources.VLK + '/muokattavakopio'}
    });
  })

  .factory('OppiaineenVlk', function ($resource, YlopsResources) {
    return $resource(YlopsResources.OPVLK, {
      vlkId: '@id'
    }, {
      getTavoitteet: {method: 'GET', isArray: false, url: YlopsResources.OPVLK + '/tavoitteet'},
      vuosiluokkaista: {method: 'POST', url: YlopsResources.OPVLK + '/tavoitteet'},
      peruste: {method: 'GET', url: YlopsResources.OPVLK + '/peruste'}
    });
  })

  .factory('VuosiluokkaCRUD', function ($resource, YlopsResources) {
    return $resource(YlopsResources.VUOSILUOKKA, {
      vlId: '@id'
    }, {
      saveValinnainen:
      { method: 'POST',
        url: YlopsResources.VUOSILUOKKAVALINNAINEN
      }
    });
  })

  .factory('OpsinKuvat', function ($resource, YlopsResources) {
    return $resource(YlopsResources.OPS + '/kuvat', {
      id: '@id'
    },{
      reScaleImg: {method: 'POST', isArray: false, url: YlopsResources.OPS + '/kuvat/:id'}
    });
  });
