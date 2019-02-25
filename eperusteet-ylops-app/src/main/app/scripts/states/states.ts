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

ylopsApp.config(function($stateProvider) {
    $stateProvider
        .state("root", {
            url: "/:lang",
            template: "<div ui-view></div>",
            abstract: true,
            onEnter: function(Kieli, $stateParams, $rootScope) {
                Kieli.setUiKieli($stateParams.lang, false);
                $rootScope.$broadcast("fetched:oikeusTiedot");
            },
            resolve: {
                casTiedot: function(Oikeudet, $q) {
                        return $q.all([Oikeudet.getKayttaja().$promise, Oikeudet.getCasTiedot()]);
                },
                opsOikeudet: "OpetussuunnitelmaOikeudetService",
                kayttajaOikeudetNouto: [
                    "casTiedot",
                    "opsOikeudet",
                    function(casTiedot, opsOikeudet) {
                        return opsOikeudet.query();
                    }
                ]
            }
        })
        .state("root.admin", {
            url: "/admin",
            templateUrl: "views/admin.html",
            controller: "AdminController",
            resolve: {
                opsStatistiikka: OpetussuunnitelmaCRUD => {
                    return OpetussuunnitelmaCRUD.tilastot().$promise;
                },
                opsit: OpetussuunnitelmaCRUD => {
                    return OpetussuunnitelmaCRUD.adminlist({}).$promise;
                }
            }
        })
        .state("root.virhe", {
            url: "/virhe",
            templateUrl: "views/virhe.html",
            params: { lisatiedot: null },
            controller: "VirheController"
        })
        .state("root.etusivu", {
            url: "",
            templateUrl: "views/etusivu.html",
            controller: "EtusivuController"
        })
        .state("root.opetussuunnitelmat", {
            url: "/opetussuunnitelmat",
            abstract: true,
            template: "<div ui-view></div>"
        })
        .state("root.opetussuunnitelmat.lista", {
            url: "",
            templateUrl: "views/opetussuunnitelmat/lista.html",
            controller: "OpetussuunnitelmatListaController"
        })
        .state("root.opetussuunnitelmat.yksi", {
            url: "/:id",
            templateUrl: "views/opetussuunnitelmat/opetussuunnitelmabase.html",
            abstract: true,
            resolve: {
                opsService: "OpsService",
                vuosiluokatService: "VuosiluokatService",
                opsId: [
                    "$stateParams",
                    function($stateParams) {
                        return $stateParams.id;
                    }
                ],
                opsModel: [
                    "opsService",
                    "opsId",
                    function(opsService, opsId) {
                        var fetched = opsService.fetch(opsId);
                        return fetched.$promise ? fetched.$promise : fetched;
                    }
                ],
                vuosiluokkakokonaisuudet: [
                    "vuosiluokatService",
                    "opsModel",
                    function(vuosiluokatService, opsModel) {
                        return vuosiluokatService.getVuosiluokkakokonaisuudet(opsModel);
                    }
                ],
                opsOikeudet: "OpetussuunnitelmaOikeudetService",
                opsOikeudetNouto: [
                    "opsOikeudet",
                    "$stateParams",
                    function(opsOikeudet, $stateParams) {
                        return opsOikeudet.fetch($stateParams);
                    }
                ]
            },
            controller: function($scope, $stateParams, opsModel, vuosiluokkakokonaisuudet, opsService) {
                $scope.model = opsModel;
                $scope.isLukio = _.some(
                    ["koulutustyyppi_2", "koulutustyyppi_23", "koulutustyyppi_14"],
                    i => i === opsModel.koulutustyyppi
                );
                $scope.isEditable = opsService.isEditable;
                $scope.vuosiluokkakokonaisuudet = vuosiluokkakokonaisuudet;
                $scope.luonnissa = $stateParams.id === "uusi";
            }
        })
        .state("root.opetussuunnitelmat.yksi.sisalto", {
            url: "/osiot",
            templateUrl: "views/opetussuunnitelmat/sisalto.html",
            controller: "OpetussuunnitelmaSisaltoController",
            resolve: {
                tekstit: [
                    "OpetussuunnitelmanTekstit",
                    "$stateParams",
                    function(ot, $stateParams) {
                        return ot.otsikot({ opsId: $stateParams.id }).$promise;
                    }
                ]
            }
        })
        .state("root.opetussuunnitelmat.yksi.sisalto.tekstikappale", {
            url: "/tekstikappale/:tekstikappaleId{versio:(?:/[^/]+)?}",
            templateUrl: "views/opetussuunnitelmat/tekstikappale.html",
            controller: "TekstikappaleController",
            resolve: {
                tekstikappaleId: [
                    "$stateParams",
                    function($stateParams, $scope) {
                        return $stateParams.tekstikappaleId;
                    }
                ],
                teksti: [
                    "$q",
                    "$stateParams",
                    "OpetussuunnitelmanTekstit",
                    function($q, $stateParams, OpetussuunnitelmanTekstit) {
                        if ($stateParams.id === "uusi") {
                            return $q.when({
                                tekstiKappale: {
                                    nimi: {},
                                    teksti: {}
                                }
                            });
                        } else {
                            if (_.isEmpty($stateParams.versio)) {
                                return OpetussuunnitelmanTekstit.get({
                                    opsId: $stateParams.id,
                                    viiteId: $stateParams.tekstikappaleId
                                }).$promise;
                            } else {
                                return OpetussuunnitelmanTekstit.versio({
                                    opsId: $stateParams.id,
                                    viiteId: $stateParams.tekstikappaleId.split("/")[0],
                                    id: $stateParams.versio.replace("/", "")
                                }).$promise.then(function(dat) {
                                    return {
                                        id: $stateParams.tekstikappaleId.split("/")[0],
                                        tekstiKappale: dat
                                    };
                                });
                            }
                        }
                    }
                ]
            }
        })
        .state("root.opetussuunnitelmat.yksi.tiedot", {
            url: "/tiedot?:pohjaId",
            templateUrl: "views/opetussuunnitelmat/tiedot.html",
            controller: "OpetussuunnitelmaTiedotController",
            resolve: {
                tiedotId: [
                    "$stateParams",
                    function($stateParams) {
                        return $stateParams.id;
                    }
                ],
                kunnat: [
                    "KoodistoHaku",
                    "tiedotId",
                    function(KoodistoHaku, tiedotId) {
                        if (tiedotId === "uusi") {
                            return KoodistoHaku.get({ koodistoUri: "kunta" }).$promise;
                        }
                        return null;
                    }
                ]
            }
        })
        .state("root.opetussuunnitelmat.yksi.poistetut", {
            url: "/poistetut?:pohjaId",
            templateUrl: "views/opetussuunnitelmat/poistetut.html",
            controller: "OpetussuunnitelmaPoistetutController",
            resolve: {
                tekstiKappaleet: (OpetussuunnitelmanTekstit, $stateParams) => {
                    return OpetussuunnitelmanTekstit.poistetut({ opsId: $stateParams.id }).$promise;
                },
                oppiaineet: (OppiaineCRUD, $stateParams) => {
                    return OppiaineCRUD.getRemoved({ opsId: $stateParams.id }).$promise;
                }
            }
        })
        .state("root.opetussuunnitelmat.yksi.kasitteet", {
            url: "/kasitteet",
            templateUrl: "views/opetussuunnitelmat/kasitteet.html",
            controller: "KasitteetController",
            resolve: {}
        })
        .state("root.opetussuunnitelmat.yksi.dokumentti", {
            url: "/dokumentti",
            templateUrl: "views/opetussuunnitelmat/dokumentti.html",
            controller: "DokumenttiController"
        })
        .state("root.pohjat", {
            url: "/pohjat",
            template: "<div ui-view></div>",
            abstract: true
        })
        .state("root.pohjat.lista", {
            url: "",
            templateUrl: "views/opetussuunnitelmat/pohja/lista.html",
            controller: "PohjaListaController"
        })
        .state("root.pohjat.yksi", {
            url: "/:pohjaId",
            templateUrl: "views/opetussuunnitelmat/pohja/base.html",
            controller: "PohjaController",
            resolve: {
                opsService: "OpsService",
                pohjaId: [
                    "$stateParams",
                    function($stateParams) {
                        return $stateParams.pohjaId;
                    }
                ],
                pohjaModel: [
                    "opsService",
                    "pohjaId",
                    function(opsService, pohjaId) {
                        return opsService.fetchPohja(pohjaId);
                    }
                ],
                opsOikeudet: "OpetussuunnitelmaOikeudetService",
                opsOikeudetNouto: [
                    "opsOikeudet",
                    "$stateParams",
                    function(opsOikeudet, $stateParams) {
                        var params = _.clone($stateParams);
                        if (params.pohjaId === "uusi") {
                            params = _.omit(params, "pohjaId");
                        }
                        return opsOikeudet.fetch(params);
                    }
                ]
            }
        })
        .state("root.pohjat.yksi.sisalto", {
            url: "/sisalto",
            templateUrl: "views/opetussuunnitelmat/pohja/sisalto.html",
            controller: "PohjaSisaltoController",
            resolve: {
                pohjaOps: [
                    "OpsService",
                    "$stateParams",
                    function(OpsService, $stateParams) {
                        return OpsService.haeOikeasti($stateParams.pohjaId);
                    }
                ],
                tekstit: [
                    "OpetussuunnitelmanTekstit",
                    "$stateParams",
                    function(ot, $stateParams) {
                        return ot.otsikot({ opsId: $stateParams.pohjaId }).$promise;
                    }
                ]
            }
        })
        .state("root.pohjat.yksi.tiedot", {
            url: "/tiedot",
            templateUrl: "views/opetussuunnitelmat/pohja/tiedot.html",
            controller: "PohjaTiedotController",
            resolve: {
                pohjaId: [
                    "$stateParams",
                    function($stateParams) {
                        return $stateParams.pohjaId;
                    }
                ],
                perusteet: [
                    "EperusteetValmiitPerusteet",
                    "pohjaId",
                    function(EperusteetValmiitPerusteet, pohjaId) {
                        if (pohjaId === "uusi") {
                            return EperusteetValmiitPerusteet.query({}).$promise;
                        }
                        return null;
                    }
                ]
            }
        })
        .state("root.pohjat.yksi.sisalto.tekstikappale", {
            url: "/tekstikappale/:tekstikappaleId",
            templateUrl: "views/opetussuunnitelmat/pohja/tekstikappale.html",
            controller: "PohjaTekstikappaleController",
            resolve: {
                tekstikappaleId: [
                    "$stateParams",
                    function($stateParams) {
                        return $stateParams.tekstikappaleId;
                    }
                ],
                tekstikappaleModel: [
                    "pohjaId",
                    "tekstikappaleId",
                    "OpetussuunnitelmanTekstit",
                    function(pohjaId, tekstikappaleId, OpetussuunnitelmanTekstit) {
                        return OpetussuunnitelmanTekstit.get({ opsId: pohjaId, viiteId: tekstikappaleId }).$promise;
                    }
                ]
            }
        });
});
