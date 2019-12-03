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
    .controller("VuosiluokkaBaseController", function(
        $scope,
        $stateParams,
        MurupolkuData,
        $state,
        Kaanna,
        VuosiluokatService,
        baseLaajaalaiset,
        Kommentit,
        KommentitByVuosiluokka,
        $timeout,
        vuosiluokkaSisalto
    ) {
        $scope.vuosiluokka = vuosiluokkaSisalto;
        $scope.vuosiluokkaNro = VuosiluokatService.fromEnum($scope.vuosiluokka.vuosiluokka);

        MurupolkuData.set("vuosiluokkaNimi", Kaanna.kaanna("vuosiluokka") + " " + $scope.vuosiluokkaNro);
        $scope.perusteOpVlk = $scope.perusteOppiaine
            ? _.find($scope.perusteOppiaine.vuosiluokkakokonaisuudet, function(vlk) {
                  return vlk._vuosiluokkakokonaisuus === $scope.oppiaineenVlk._vuosiluokkakokonaisuus;
              })
            : null;

        $scope.onValinnaiselle = $scope.oppiaine.tyyppi !== "yhteinen";
        if (!$scope.onValinnaiselle && !$scope.perusteOpVlk) {
            $scope.eiPerustetta = true;
        }
        $scope.sisaltoAlueetMap = _.indexBy($scope.vuosiluokka.sisaltoalueet, "id");

        $scope.laajaalaiset = _.indexBy(baseLaajaalaiset, "tunniste");
        $scope.perusteSisaltoalueet = $scope.perusteOpVlk
            ? _.indexBy($scope.perusteOpVlk.sisaltoalueet, "tunniste")
            : [];

        $scope.isState = function(name) {
            return _.endsWith($state.current.name, "vuosiluokka." + name);
        };

        Kommentit.haeKommentit(KommentitByVuosiluokka, {
            opsId: $stateParams.id,
            vlkId: $stateParams.vlkId,
            oppiaineId: $stateParams.oppiaineId,
            vlId: $stateParams.vlId,
            id: $stateParams.vlId
        });
    })
    .service("VuosiluokkaMapper", function($state, $stateParams, Utils) {
        const getSisaltoalueet = (paikallinen, scope) => {
            let sisAlue = paikallinen.sisaltoalueet[0]
                ? scope.sisaltoAlueetMap[paikallinen.sisaltoalueet[0].sisaltoalueet.id]
                : null;
            if (!sisAlue) {
                sisAlue = _.has(paikallinen, "sisaltoalueet[0].sisaltoalueet")
                    ? paikallinen.sisaltoalueet[0].sisaltoalueet
                    : null;
            }
            return sisAlue;
        };

        const processTavoitteet = scope => {
            var perusteKohdealueet = scope.perusteOppiaine ? _.indexBy(scope.perusteOppiaine.kohdealueet, "id") : [];
            _.each(scope.tavoitteet, item => {
                if (scope.perusteOpVlk) {
                    const perusteTavoite = _.find(
                        scope.perusteOpVlk.tavoitteet,
                        pTavoite => pTavoite.tunniste === item.tunniste
                    );
                    item.$tavoite = perusteTavoite.tavoite;
                    item.$sisaltoalueet = _.map(perusteTavoite.sisaltoalueet, tunniste => {
                        let sisaltoalue = scope.perusteSisaltoalueet[tunniste] || {};
                        sisaltoalue.$url = $state.href("^.sisaltoalueet") + "#" + tunniste;
                        return sisaltoalue;
                    });
                    item.$kohdealue = perusteKohdealueet[_.first(perusteTavoite.kohdealueet)];
                    item.$laajaalaiset = _.map(perusteTavoite.laajaalaisetosaamiset, tunniste => {
                        let laajaalainen = scope.laajaalaiset[tunniste];
                        laajaalainen.$url =
                            $state.href("root.opetussuunnitelmat.yksi.opetus.vuosiluokkakokonaisuus", {
                                vlkId: $stateParams.vlkId
                            }) +
                            "#" +
                            tunniste;
                        return laajaalainen;
                    });
                    item.$arvioinninkohteet = perusteTavoite.arvioinninkohteet;
                    item.$arvioinninKuvaus = perusteTavoite.arvioinninKuvaus;
                    item.$vapaaTeksti = perusteTavoite.vapaaTeksti;
                    item.$tavoitteistaJohdetutOppimisenTavoitteet = perusteTavoite.tavoitteistaJohdetutOppimisenTavoitteet;
                }
            });

            scope.tavoiteMap = _.indexBy(scope.tavoitteet, "tunniste");

            //TODO käytetäänks scope.valinnaisenTekstiosat ees missään?
            if (scope.onValinnaiselle && !_.isEmpty(scope.sisaltoAlueetMap)) {
                const otsikot = _.map(scope.tavoitteet, "tavoite");
                const tekstit = _(scope.tavoitteet)
                    .map("sisaltoalueet")
                    .flatten()
                    .map(_.property("sisaltoalueet.id"))
                    .map(id => {
                        return scope.sisaltoAlueetMap[id].kuvaus;
                    })
                    .value();
                scope.valinnaisenTekstiosat = _.map(_.zip(otsikot, tekstit), function(values) {
                    return _.zipObject(["otsikko", "teksti"], values);
                });
            }

            scope.tunnisteet = _.keys(scope.tavoiteMap);
            _.each(scope.tunnisteet, tunniste => {
                const paikallinen = _.find(scope.tavoitteet, tavoite => tavoite.tunniste === tunniste);

                scope.muokattavat[tunniste] =
                    paikallinen && _.isObject(paikallinen.tavoite)
                        ? {
                              teksti: paikallinen.tavoite,
                              sisaltoalue: getSisaltoalueet(paikallinen, scope)
                          }
                        : { teksti: {}, sisaltoalue: {} };
            });

            scope.valinnaisenTavoitteet = _.map(scope.muokattavat, tavoite => {
                return {
                    otsikko: tavoite.teksti,
                    teksti: tavoite.sisaltoalue ? tavoite.sisaltoalue.kuvaus : {}
                };
            });
        };

        this.mapModel = scope => {
            scope.muokattavat = {};
            scope.tavoitteet = scope.vuosiluokka.tavoitteet;
            scope.sisaltoAlueetMap = _.indexBy(scope.vuosiluokka.sisaltoalueet, "id");
            processTavoitteet(scope);
        };

        this.mapSisaltoalueet = sisaltoalueet =>
            _(sisaltoalueet)
                .map(sa => [
                    sa.tunniste,
                    {
                        piilotettu: sa.piilotettu,
                        teksti: sa.kuvaus || {}
                    }
                ])
                .zipObject()
                .value();
    })
    .controller("VuosiluokkaTavoitteetController", function(
        $scope,
        VuosiluokatService,
        Editointikontrollit,
        Utils,
        $q,
        $state,
        OppiaineService,
        Varmistusdialogi,
        Notifikaatiot,
        $rootScope,
        VuosiluokkaMapper,
        OpsService,
        $timeout,
        vuosiluokkaSisalto
    ) {
        $rootScope.$broadcast("update:kommentit"); // Hack: pakotetaan kommenttien näyttö lataamatta niitä uudelleen.
        $scope.tunnisteet = [];
        $scope.collapsed = {};
        $scope.nimiOrder = Utils.sort;

        const refetch = () =>
            $q(resolve => {
                OppiaineService.fetchVuosiluokka($scope.vuosiluokka.id, res => {
                    $scope.vuosiluokka = res;
                    VuosiluokkaMapper.mapModel($scope);
                    resolve();
                });
            });
        $scope.vuosiluokka = vuosiluokkaSisalto;
        VuosiluokkaMapper.mapModel($scope);
        $scope.options = {
            editing: false,
            isEditable: () => $scope.oppiaine.oma && OpsService.isEditable()
        };

        $scope.sisaltoaluetunnisteet = _($scope.sisaltoAlueetMap)
            .values()
            .indexBy("tunniste")
            .value();

        $scope.muokkaaKuvausta = muokattava => {
            muokattava.isEditing = true;
            Editointikontrollit.startEditing();
        };

        $scope.naytaKuvaus = (sisaltoalue, id, tavoiteTunniste) => {
            const kuvaus = _.find(
                _.find($scope.vuosiluokka.tavoitteet, { id: id }).sisaltoalueet,
                alue => sisaltoalue.tunniste === alue.sisaltoalueet.tunniste
            );

            $scope.muokattavat[tavoiteTunniste].muokattavaKuvaus = {
                kaytaOmaaKuvausta: !!(kuvaus && kuvaus.omaKuvaus),
                omaKuvaus: kuvaus && kuvaus.omaKuvaus ? kuvaus.omaKuvaus : {},
                kuvaus: kuvaus.sisaltoalueet.kuvaus || sisaltoalue.kuvaus,
                kuvauksenId: kuvaus.id,
                sisaltoalueId: sisaltoalue.id,
                isEditing: false
            };
        };

        $scope.getArvioinninKohteenTeksti = (tavoite) => {

            const hyvanOsaamisenArvio = _.find(tavoite.$arvioinninkohteet, (arvioinninkohde: any) => {
                return arvioinninkohde.arvosana == 8
            });

            if(hyvanOsaamisenArvio && !_.isEmpty(hyvanOsaamisenArvio.arvioinninKohde)) {
                return hyvanOsaamisenArvio.arvioinninKohde;
            }

            return tavoite.$arvioinninKuvaus;
        }

        $scope.callbacks = {
            edit: () => $q(resolve => resolve()),
            cancel: refetch,
            save: () =>
                $q(resolve => {
                    if ($scope.onValinnaiselle) {
                        $rootScope.$broadcast("notifyCKEditor");
                        const tavoitteet = angular.copy($scope.valinnaisenTavoitteet);

                        OppiaineService.saveValinnainenVuosiluokka($scope.vuosiluokka.id, tavoitteet, res => {
                            Notifikaatiot.onnistui("tallennettu-ok");
                            $scope.vuosiluokka = res;
                            resolve();
                            //needed to make sure the data is updated when switching between tabs sisältöalueet <-> tavoitteet
                            refetch();
                        });
                    } else {
                        var postdata = angular.copy($scope.vuosiluokka);
                        _.each(postdata.tavoitteet, tavoite => {
                            tavoite.tavoite = $scope.muokattavat[tavoite.tunniste].teksti;

                            if ($scope.muokattavat[tavoite.tunniste].muokattavaKuvaus) {
                                var sisaltoalue = _.findWhere(tavoite.sisaltoalueet, {
                                    id: $scope.muokattavat[tavoite.tunniste].muokattavaKuvaus.kuvauksenId
                                });
                                sisaltoalue.omaKuvaus = $scope.muokattavat[tavoite.tunniste].muokattavaKuvaus
                                    .kaytaOmaaKuvausta
                                    ? $scope.muokattavat[tavoite.tunniste].muokattavaKuvaus.omaKuvaus
                                    : null;
                                $scope.muokattavat[tavoite.tunniste].muokattavaKuvaus.isEditing = false;
                            }

                            delete tavoite.$sisaltoalueet;
                            delete tavoite.$kohdealue;
                            delete tavoite.$laajaalaiset;
                        });
                        OppiaineService.saveVuosiluokka(postdata, res => {
                            $scope.vuosiluokka = res;
                            resolve();
                        });
                    }
                }),
            add: () => {
                $scope.valinnaisenTavoitteet.push({
                    otsikko: {},
                    teksti: {}
                });
                $timeout(() => {
                    const el: any = angular.element("[valinnaisen-ops-teksti]").last();
                    if (el.length === 1 && el.isolateScope()) {
                        el.isolateScope().startEditing();
                    }
                }, 300);
            },
            remove: item => {
                Varmistusdialogi.dialogi({
                    otsikko: "varmista-poisto",
                    primaryBtn: "poista",
                    successCb: () => {
                        const tavoitteet = _.without($scope.valinnaisenTavoitteet, item);

                        OppiaineService.saveValinnainenVuosiluokka($scope.vuosiluokka.id, tavoitteet, res => {
                            Notifikaatiot.onnistui("poisto-onnistui");
                            $scope.vuosiluokka = res;
                            VuosiluokkaMapper.mapModel($scope);
                        });
                    }
                })();
            },
            notify: mode => {
                $scope.options.editing = mode;
                $scope.callbacks.notifier(mode);
            },
            notifier: _.noop
        };
        Editointikontrollit.registerCallback($scope.callbacks);
    }) // end of VuosiluokkaTavoitteetController
    .controller("VuosiluokkaSisaltoalueetController", function(
        $q,
        $scope,
        $rootScope,
        Editointikontrollit,
        $timeout,
        $location,
        $anchorScroll,
        OppiaineService,
        VuosiluokkaMapper,
        OpsService,
        vuosiluokkaSisalto
    ) {
        $rootScope.$broadcast("update:kommentit"); // Hack: pakotetaan kommenttien näyttö lataamatta niitä uudelleen.
        $scope.tunnisteet = [];
        $scope.muokattavat = {};
        $scope.sisaltoInfo = {};
        $scope.sisaltoInfoCollapse = false;

        const mapModel = () => {
            $scope.sisaltoalueet = $scope.vuosiluokka.sisaltoalueet;
            $scope.muokattavat = VuosiluokkaMapper.mapSisaltoalueet($scope.sisaltoalueet);
            $scope.tunnisteet = _.keys($scope.muokattavat);
        };

        // FIXME mapOnce saattaa hajottaa jotain
        const mapOnce = _.once(mapModel);
        const refetch = () =>
            $q(resolve => {
                OppiaineService.fetchVuosiluokka($scope.vuosiluokka.id, res => {
                    $scope.vuosiluokka = res;
                    mapOnce();
                    resolve();
                });
            });
        $scope.vuosiluokka = vuosiluokkaSisalto;
        mapOnce();

        $scope.options = {
            editing: false,
            isEditable: () => $scope.oppiaine.oma && OpsService.isEditable()
        };

        $scope.callbacks = {
            edit: refetch,
            cancel: refetch,
            save: () =>
                $q(resolve => {
                    _.each($scope.vuosiluokka.sisaltoalueet, alue => {
                        alue.kuvaus = $scope.muokattavat[alue.tunniste].teksti;
                        alue.piilotettu = $scope.muokattavat[alue.tunniste].piilotettu;
                    });
                    OppiaineService.saveVuosiluokka($scope.vuosiluokka, res => {
                        $scope.vuosiluokka = res;
                        resolve();
                    });
                }),
            notify: mode => {
                $scope.options.editing = mode;
                $scope.callbacks.notifier(mode);
            },
            notifier: _.noop
        };
        Editointikontrollit.registerCallback($scope.callbacks);

        $timeout(() => {
            if ($location.hash()) {
                $anchorScroll();
            }
        }, 1000);
    })
    .directive("opsTeksti", function($timeout, $window) {
        return {
            restrict: "A",
            scope: {
                muokattava: "=opsTeksti",
                callbacks: "=",
                config: "="
            },
            templateUrl: "views/opetussuunnitelmat/vuosiluokat/directives/opsteksti.html",
            controller: "TekstiosaController",
            link: (scope: any, element, attrs: any) => {
                scope.editable = !!attrs.opsTeksti;
                scope.options = {
                    collapsed: scope.editable
                };
                scope.isEmpty = _.isEmpty;
                scope.focusAndScroll = () => {
                    $timeout(() => {
                        var el = element.find("[ckeditor]");
                        if (el && el.length > 0) {
                            el[0].focus();
                            $window.scrollTo(0, el.eq(0).offset().top - 400);
                        }
                    }, 300);
                };
            }
        };
    })
    .directive("valinnaisenOpsTeksti", function() {
        return {
            restrict: "A",
            scope: {
                muokattava: "=valinnaisenOpsTeksti",
                callbacks: "=",
                config: "="
            },
            templateUrl: "views/opetussuunnitelmat/vuosiluokat/directives/valinnaisenopsteksti.html",
            controller: "TekstiosaController",
            link: function(scope: any) {
                scope.options = {
                    collapsed: false
                };
                scope.focusAndScroll = angular.noop;
            }
        };
    });
