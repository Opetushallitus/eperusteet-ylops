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

ylopsApp
    .factory("KommentitByParent", function(SERVICE_LOC, $resource) {
        return $resource(
            SERVICE_LOC + "/kommentit/parent/:id",
            { id: "@id" },
            {
                get: { method: "GET", isArray: true }
            }
        );
    })
    .factory("KommentitByYlin", function(SERVICE_LOC, $resource) {
        return $resource(
            SERVICE_LOC + "/kommentit/ylin/:id",
            { id: "@id" },
            {
                get: { method: "GET", isArray: true }
            }
        );
    })
    .factory("KommentitByOppiaine", function(SERVICE_LOC, $resource) {
        return $resource(
            SERVICE_LOC + "/kommentit/opetussuunnitelmat/:opsId/vuosiluokat/:vlkId/oppiaine/:id",
            {},
            {
                get: { method: "GET", isArray: true }
            }
        );
    })
    .factory("KommentitByVuosiluokka", function(SERVICE_LOC, $resource) {
        return $resource(
            SERVICE_LOC +
                "/kommentit/opetussuunnitelmat/:opsId/opetus/vuosiluokat/:vlkId/oppiaine/:oppiaineId/vuosiluokka/:id",
            {},
            {
                get: { method: "GET", isArray: true }
            }
        );
    })
    .factory("KommentitByTekstikappaleViite", function(SERVICE_LOC, $resource) {
        return $resource(
            SERVICE_LOC + "/kommentit/opetussuunnitelmat/:opsId/tekstikappaleviitteet/:id",
            {},
            {
                get: { method: "GET", isArray: true }
            }
        );
    })
    .factory("KommentitCRUD", function(SERVICE_LOC, $resource) {
        return $resource(
            SERVICE_LOC + "/kommentit/:id",
            { id: "@id" },
            {
                get: { method: "GET", isArray: true },
                update: { method: "PUT" }
            }
        );
    })
    .service("KommenttiSivuCache", function() {
        this.perusteProjektiId = null;
    })
    .service("Kommentit", function(
        $stateParams,
        $location,
        $timeout,
        $rootScope,
        Notifikaatiot,
        KommenttiSivuCache,
        KommentitCRUD
    ) {
        var kommenttipuu: any = {};
        var nykyinenParams: any = {};
        var stored: any = {};

        function rakennaKommenttiPuu(viestit) {
            viestit = _(viestit)
                .map(function(viesti) {
                    viesti.muokattu = viesti.luotu === viesti.muokattu ? null : viesti.muokattu;
                    viesti.viestit = [];
                    return viesti;
                })
                .sortBy("luotu")
                .value();

            var viestiMap = _.zipObject(_.map(viestit, "id"), viestit);

            _.forEach(viestit, function(viesti) {
                if (viesti.parentId && viestiMap[viesti.parentId]) {
                    viestiMap[viesti.parentId].viestit.unshift(viesti);
                }
            });

            var sisaltoObject = {
                $resolved: true,
                $yhteensa: _.size(viestit),
                seuraajat: [],
                viestit: _(viestiMap)
                    .values()
                    .reject(function(viesti) {
                        return viesti.parentId !== null;
                    })
                    .sortBy("luotu")
                    .reverse()
                    .value()
            };
            return sisaltoObject;
        }

        function haeKommentit(Resource, params) {
            nykyinenParams = params;
            var url = $location.url();
            var lataaja = function(cb) {
                Resource.get(
                    params,
                    function(res) {
                        kommenttipuu = rakennaKommenttiPuu(res);
                        cb(kommenttipuu);
                    },
                    Notifikaatiot.serverCb
                );
            };
            stored = { url: url, lataaja: lataaja };
            $timeout(function() {
                $rootScope.$broadcast("update:kommentit", url, lataaja);
            });
        }

        function lisaaKommentti(parent, viesti, success) {
            success = success || angular.noop;

            var payload = _.merge(
                {
                    parentId: parent && parent.id ? parent.id : null,
                    sisalto: viesti,
                    opetussuunnitelmaId: nykyinenParams.opsId
                },
                _.clone(nykyinenParams)
            );
            delete payload.id;
            KommentitCRUD.save(payload, function(res) {
                res.muokattu = null;
                res.viestit = [];
                parent.viestit.unshift(res);
                success(res);
            });
        }

        function poistaKommentti(viesti) {
            KommentitCRUD.remove(
                { id: viesti.id },
                function() {
                    viesti.sisalto = "";
                    viesti.muokattu = new Date().getTime();
                    viesti.poistettu = true;
                    viesti.muokkaaja = null;
                    viesti.lahettaja = null;
                    Notifikaatiot.onnistui("kommentti-poistettu");
                },
                Notifikaatiot.serverCb
            );
        }

        function muokkaaKommenttia(viesti, uusiviesti, cb) {
            KommentitCRUD.update(
                {
                    id: viesti.id
                },
                { sisalto: uusiviesti },
                function(res) {
                    viesti.sisalto = res.sisalto;
                    viesti.muokattu = res.muokattu;
                    (cb || angular.noop)(res);
                },
                Notifikaatiot.serverCb
            );
        }

        return {
            haeKommentit: haeKommentit,
            lisaaKommentti: lisaaKommentti,
            poistaKommentti: poistaKommentti,
            muokkaaKommenttia: muokkaaKommenttia,
            stored: function() {
                var ret = _.clone(stored);
                stored = {};
                return ret;
            }
        };
    });
