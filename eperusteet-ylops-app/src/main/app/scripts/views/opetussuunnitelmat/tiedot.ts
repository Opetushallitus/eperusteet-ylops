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

ylopsApp.controller("OpetussuunnitelmaTiedotController", function(
    $scope,
    Editointikontrollit,
    $stateParams,
    $state,
    $timeout,
    $q,
    $rootScope,
    Api,
    OpetussuunnitelmaCRUD,
    Notifikaatiot,
    OpsService,
    Utils,
    KoodistoHaku,
    PeruskouluHaku,
    PeruskoulutoimijaHaku,
    LukiotoimijaHaku,
    LukioHaku,
    kunnat,
    Kieli,
    OpetussuunnitelmaOikeudetService,
    Varmistusdialogi
) {
    function hasLukio() {
        return _.any(
            ["koulutustyyppi_2", "koulutustyyppi_23", "koulutustyyppi_14"],
            i => i === $scope.editableModel.koulutustyyppi
        );
    }

    async function getKoulutustoimijat(kunnat: Array<string>) {
        const oppilaitostyyppi = [12, 19, 64];
        if (hasLukio()) {
            oppilaitostyyppi.push(15);
        } else if ($scope.editableModel.koulutustyyppi === "koulutustyyppi_999907") {
            oppilaitostyyppi.push(1, 61);
        } else if ($scope.editableModel.koulutustyyppi === "koulutustyyppi_17") {
            oppilaitostyyppi.push(11, 15, 21, 22, 23, 24, 63);
        } else {
            oppilaitostyyppi.push(11);
        }

        const toimijat = await Api.all("ulkopuoliset/organisaatiot/koulutustoimijat/").getList({
            kunta: kunnat,
            oppilaitostyyppi
        });
        return toimijat.plain();
    }

    $scope.kouluvalinta = false;
    $scope.kielivalinnat = []; // Täytetään pohjan perusteella
    $scope.luonnissa = $stateParams.id === "uusi";
    $scope.editableModel = _.clone($scope.model);
    if ($scope.luonnissa) {
        OpetussuunnitelmaCRUD.query({ tyyppi: "pohja", tila: "valmis" }, function(pohjat) {
            $scope.pohjat = Utils.opsFilter(pohjat);
        });
        $scope.editableModel.julkaisukielet = [_.first($scope.kielivalinnat)];
        $scope.editableModel._pohja = $stateParams.pohjaId === "" ? null : $stateParams.pohjaId;
        $scope.editVuosiluokkakokonaisuudet = true;
    } else {
        $scope.editVuosiluokkakokonaisuudet =
            OpetussuunnitelmaOikeudetService.onkoOikeudet("pohja", "luonti", true) ||
            OpetussuunnitelmaOikeudetService.onkoOikeudet("opetussuunnitelma", "tilanvaihto", true);
    }

    $scope.$$isOps = true;
    $scope.editMode = false;
    $scope.loading = false;
    $scope.kuntalista = [];
    $scope.koulutoimijalista = [];
    $scope.koululista = [];
    $scope.eiKoulujaVaroitus = false;
    $scope.ryhmalista = null;
    $scope.nimiOrder = function(vlk) {
        return Utils.sort(vlk.vuosiluokkakokonaisuus);
    };

    $scope.model.nimi.$$validointi = Kieli.validoi($scope.model.nimi);

    let loading = false;
    $scope.vaihdaKouluvalinta = async (value: boolean) => {
        $scope.editableModel.kunnat = [];
        $scope.editableModel.koulutoimijat = [];
        $scope.editableModel.koulut = [];
        if (value) {
            if (!$scope.ryhmalista && !loading) {
                loading = true;
                const ryhmalista = await Api.all("ulkopuoliset/organisaatioryhmat").getList();
                $scope.ryhmalista = ryhmalista;
            }
        }
        $scope.kouluvalinta = value;
    };

    $scope.sallitutKoulutustyypit = [
        "koulutustyyppi_2",
        "koulutustyyppi_6",
        "koulutustyyppi_14",
        "koulutustyyppi_15",
        "koulutustyyppi_16",
        "koulutustyyppi_999907",
        "koulutustyyppi_17",
        "koulutustyyppi_20",
        "koulutustyyppi_22",
        "koulutustyyppi_23"
    ];

    $scope.dateOptions = {
        "year-format": "yy",
        "starting-day": 1
    };

    $scope.format = "d.M.yyyy";
    $scope.kalenteriTilat = {};
    $scope.open = function($event) {
        $event.stopPropagation();
        $event.preventDefault();
        $scope.kalenteriTilat.paatospaivamaaraButton = true;
    };

    $scope.hasRequiredFields = function() {
        var model = $scope.editableModel;
        var nimiOk = Utils.hasLocalizedText(model.nimi);
        var organisaatiotOk = !$scope.luonnissa
            || (model.kunnat && model.kunnat.length > 0
            && model.koulutoimijat && model.koulutoimijat.length > 0);
        var julkaisukieletOk = _.any(_.values($scope.julkaisukielet));
        var vlkOk =
            (!$scope.luonnissa && !$scope.editVuosiluokkakokonaisuudet) ||
            model.koulutustyyppi !== "koulutustyyppi_16" ||
            _(model.vuosiluokkakokonaisuudet)
                .filter({ valittu: true })
                .size() > 0;

        if (model.koulutustyyppi === "koulutustyyppi_999907") {
            return nimiOk;
        }
        else {
            return nimiOk && organisaatiotOk && julkaisukieletOk && vlkOk;
        }
    };

    function mapKunnat(lista) {
        return _(lista)
            .map(function(kunta) {
                return {
                    koodiUri: kunta.koodiUri,
                    koodiArvo: kunta.koodiArvo,
                    nimi: _(kunta.metadata)
                        .indexBy(function(item) {
                            return item.kieli.toLowerCase();
                        })
                        .mapValues("nimi")
                        .value()
                };
            })
            .sortBy(Utils.sort)
            .value();
    }

    function filterOrganisaatio(tyyppi) {
        return function(organisaatiot) {
            return _.filter(organisaatiot, function(org) {
                return _.includes(org.tyypit, tyyppi);
            });
        };
    }

    var filterKoulutustoimija = filterOrganisaatio("Koulutustoimija");
    var filterOppilaitos = filterOrganisaatio("Oppilaitos");
    var filterRyhma = filterOrganisaatio("Ryhma");

    if (kunnat) {
        $scope.kuntalista = mapKunnat(kunnat);
    }

    if ($scope.model.organisaatiot) {
        $scope.model.koulutoimijat = filterKoulutustoimija($scope.model.organisaatiot);
        $scope.model.koulut = filterOppilaitos($scope.model.organisaatiot);
        $scope.model.ryhmat = filterRyhma($scope.model.organisaatiot);
    }

    function isValidKielivalinta(chosen, options) {
        return _.all(chosen, function(lang) {
            return _.contains(options, lang);
        });
    }

    function asetaKieletJaVlk(ops) {
        $scope.opsvuosiluokkakokonaisuudet = ops.vuosiluokkakokonaisuudet;
        $scope.editableModel.vuosiluokkakokonaisuudet = ops.vuosiluokkakokonaisuudet;
        $scope.kielivalinnat = Kieli.sortKielet(ops.julkaisukielet);
        if (
            _.isEmpty($scope.editableModel.julkaisukielet) ||
            !isValidKielivalinta($scope.editableModel.julkaisukielet, $scope.kielivalinnat)
        ) {
            $scope.editableModel.julkaisukielet = [_.first($scope.kielivalinnat)];
        }
        mapJulkaisukielet();
    }

    //Jos luodaan uutta ops:ia toisesta opetussuunnitelmasta,
    // niin haetaan pohja opetussuunnitelmasta kunnat ja organisaatiot
    if ($scope.luonnissa) {
        if ($scope.editableModel._pohja) {
            (async function() {
                try {
                    const res = await OpetussuunnitelmaCRUD.get({ opsId: $scope.editableModel._pohja }).$promise;
                    $scope.$$pohja = res;
                    $scope.editableModel.$$pohja = res;
                    $scope.pohjanNimi = res.nimi;
                    $scope.editableModel.kunnat = res.kunnat;
                    $scope.editableModel.koulutoimijat = filterKoulutustoimija(res.organisaatiot);
                    $scope.editableModel.koulut = filterOppilaitos(res.organisaatiot);
                    $scope.editableModel.koulutustyyppi = res.koulutustyyppi;
                    asetaKieletJaVlk(res);
                } catch (ex) {
                    Notifikaatiot.serverCb(ex);
                }
            })();
        }
        else {
            $scope.$watch("editableModel.$$pohja", () => {
                if ($scope.editableModel.$$pohja) {
                    OpetussuunnitelmaCRUD.get({ opsId: $scope.editableModel.$$pohja.id }, pohja => {
                        $scope.editableModel.koulutustyyppi = pohja.koulutustyyppi;
                        $scope.editableModel._pohja = "" + pohja.id;
                        asetaKieletJaVlk(pohja);
                    });
                }
            });
        }
    }
    else {
        $scope.$watch("editableModel.$$pohja", () => {
            if ($scope.editableModel.pohja) {
                OpetussuunnitelmaCRUD.get({ opsId: $scope.editableModel.pohja.id }, pohja => {
                    $scope.editableModel.koulutustyyppi = pohja.koulutustyyppi;
                    $scope.editableModel._pohja = "" + pohja.id;
                    asetaKieletJaVlk(pohja);
                });
            } else {
                asetaKieletJaVlk($scope.editableModel);
            }
        });
    }

    $scope.$watch("editableModel.koulutustyyppi", function(value) {
        if ($scope.luonnissa) {
            $timeout(() => {
                $scope.$apply(() => {
                    $scope.model.kunnat = [];
                    $scope.model.koulutoimijat = [];
                    $scope.model.koulut = [];
                    $scope.editableModel.kunnat = [];
                    $scope.editableModel.koulutoimijat = [];
                    $scope.editableModel.koulut = [];
                    $scope.koulutoimijalista = [];
                    $scope.koululista = [];
                    $scope.haeKunnat();
                });
            });
        }
    });

    $scope.kieliOrderFn = Kieli.orderFn;

    function fixTimefield(field) {
        if (typeof $scope.editableModel[field] === "number") {
            $scope.editableModel[field] = new Date($scope.editableModel[field]);
        }
    }

    const fetch = (notify?) =>
        $q((resolve, reject) => {
            OpsService.refetch(res => {
                $scope.model = res;
                $scope.editableModel = res;
                if ($scope.model.pohja) {
                    $scope.kielivalinnat = Kieli.sortKielet($scope.model.pohja.julkaisukielet);
                }

                if (_.size(res.vuosiluokkakokonaisuudet) > 0) {
                    var vuosiluokkakokonaisuudet = _(res.vuosiluokkakokonaisuudet)
                        .each(v => {
                            v.valittu = true;
                        })
                        .concat($scope.opsvuosiluokkakokonaisuudet)
                        .value();

                    vuosiluokkakokonaisuudet = _.chain(vuosiluokkakokonaisuudet)
                        .filter('vuosiluokkakokonaisuus')
                        .uniq('vuosiluokkakokonaisuus._tunniste')
                        .value();

                    $scope.editableModel.vuosiluokkakokonaisuudet = vuosiluokkakokonaisuudet;
                }

                $scope.editableModel.koulutoimijat = filterKoulutustoimija(res.organisaatiot);
                $scope.editableModel.koulut = filterOppilaitos(res.organisaatiot);
                $scope.editableModel.ryhmat = filterRyhma(res.organisaatiot);
                fixTimefield("paatospaivamaara");
                if (notify) {
                    $rootScope.$broadcast("rakenne:updated");
                }
                $scope.loading = false;
                resolve();
            });
        });

    var successCb = res => {
        Notifikaatiot.onnistui("tallennettu-ok");
        if ($scope.luonnissa) {
            $state.go("root.opetussuunnitelmat.yksi.sisalto", { id: res.id }, { reload: true });
            return $q.reject();
        } else {
            return fetch(true);
        }
    };

    const callbacks = {
        edit: function() {
            $scope.loading = true;
            return fetch();
        },
        asyncValidate: function(save) {
            var muokattuVuosiluokkakokonaisuuksia = _.some(
                _.pluck($scope.editableModel.vuosiluokkakokonaisuudet, "muutettu")
            );
            if (!$scope.luonnissa && muokattuVuosiluokkakokonaisuuksia) {
                Varmistusdialogi.dialogi({
                    otsikko: "vahvista-vuosiluokkakokonaisuudet-muokkaus-otsikko",
                    teksti: "vahvista-vuosiluokkakokonaisuudet-muokkaus-teksti"
                })(save);
            } else {
                save();
            }
            return $q.when();
        },
        validate: function() {
            return $scope.hasRequiredFields();
        },
        save: () =>
            $q((resolve, reject) => {
                $scope.editableModel.julkaisukielet = _($scope.julkaisukielet)
                    .keys()
                    .filter(koodi => {
                        return $scope.julkaisukielet[koodi];
                    })
                    .value();
                if (!$scope.kouluvalinta) {
                    $scope.editableModel.organisaatiot = $scope.editableModel.koulutoimijat.concat(
                        $scope.editableModel.koulut
                    );
                } else {
                    $scope.editableModel.organisaatiot = [
                        $scope.editableModel.organisaatiot
                    ];
                }
                delete $scope.editableModel.tekstit;
                delete $scope.editableModel.oppiaineet;

                $scope.editableModel.vuosiluokkakokonaisuudet = _.remove(
                    $scope.editableModel.vuosiluokkakokonaisuudet,
                    { valittu: true }
                );
                if ($scope.luonnissa) {
                    OpetussuunnitelmaCRUD.save(
                        {},
                        $scope.editableModel,
                        res => {
                            successCb(res).then(resolve);
                        },
                        err => {
                            $scope.savingDisabled = false;
                            Notifikaatiot.serverCb(err);
                        }
                    );
                } else {
                    OpetussuunnitelmaCRUD.save(
                        {
                            opsId: $scope.editableModel.id
                        },
                        $scope.editableModel,
                        res => {
                            successCb(res).then(resolve);
                        },
                        err => {
                            $scope.savingDisabled = false;
                            Notifikaatiot.serverCb(err);
                        }
                    );
                }
            }),
        cancel: function() {
            return fetch();
        },
        notify: function(mode) {
            $scope.editMode = mode;
            if (mode) {
                $scope.haeKunnat();
                $scope.haeKoulutoimijat();
                $scope.haeKoulut();
            }
        }
    };
    Editointikontrollit.registerCallback(callbacks);

    $scope.toggle = function(vkl) {
        vkl.muutettu = vkl.muutettu === undefined ? true : !vkl.muutettu;
    };

    $scope.uusi = {
        cancel: function() {
            $timeout(function() {
                $state.go("root.etusivu");
            });
        },
        create: function() {
            $scope.savingDisabled = true;
            callbacks.save();
        }
    };

    $scope.edit = function() {
        Editointikontrollit.startEditing();
    };

    $scope.haeKunnat = async function() {
        if (!($scope.editMode || $scope.luonnissa)) {
            return;
        }

        try {
            const kunnat = await KoodistoHaku.get({ koodistoUri: "kunta" }).$promise;
            $scope.kuntalista = mapKunnat(kunnat);
        } catch (ex) {
            Notifikaatiot.serverCb(ex);
        }
    };

    function updateKouluVaroitus() {
        $scope.eiKoulujaVaroitus =
            _.isArray($scope.editableModel.koulutoimijat) &&
            $scope.editableModel.koulutoimijat.length === 1 &&
            _.isArray($scope.koululista) &&
            $scope.koululista.length === 0 &&
            $scope.koulut && $scope.koulut.length === 0;
    }

    function updateKoulutoimijaVaroitus() {
        $scope.eiKoulutoimijoitaVaroitus =
            _.isArray($scope.editableModel.kunnat) &&
            $scope.editableModel.kunnat.length === 1 &&
            _.isArray($scope.koulutoimijat) &&
            $scope.koulutoimijat.length === 0;
    }

    $scope.$watch("editableModel.kunnat", function() {
        $scope.haeKoulutoimijat();
    });

    $scope.$watch("editableModel.koulutoimijat", function() {
        $scope.haeKoulut();
        updateKoulutoimijaVaroitus();
    });

    $scope.$watch("koululista", function() {
        updateKouluVaroitus();
    });

    function mapJulkaisukielet() {
        $scope.julkaisukielet = _.zipObject(
            $scope.kielivalinnat,
            _.map($scope.kielivalinnat, function(kieli) {
                return _.indexOf($scope.editableModel.julkaisukielet, kieli) > -1;
            })
        );
    }

    $scope.$watch("editableModel.julkaisukielet", mapJulkaisukielet);

    $scope.haeKoulut = function() {
        const koulutoimijat = $scope.editableModel.koulutoimijat;
        if (!($scope.editMode || $scope.luonnissa) || !koulutoimijat) {
            return;
        }

        if (koulutoimijat.length === 0) {
            $scope.loadingKoulut = false;
            $scope.editableModel.koulut = [];
        } else if (koulutoimijat.length === 1) {
            const koulutoimija = koulutoimijat[0];
            $timeout(() => {
                // Todo: ei toimi muokatessa, koska children puuttuvat
                $scope.koululista = _(koulutoimija.children)
                    .map(koulu => _.pick(koulu, ["oid", "nimi", "tyypit"]))
                    .sortBy(Utils.sort)
                    .value();
            });
        } else {
            $scope.loadingKoulut = false;
            $scope.editableModel.koulut = [];
            $scope.koululista = [];
        }
    };

    $scope.haeKoulutoimijat = async function() {
        $scope.loadingKoulutoimijat = true;
        const kunnat = $scope.editableModel.kunnat;
        if (kunnat && ($scope.editMode || $scope.luonnissa)) {
            if (kunnat.length === 0) {
                $scope.editableModel.koulutoimijat = [];
                $scope.editableModel.koulut = [];
            } else {
                const kuntaUrit = _.map(kunnat, "koodiUri");
                const koulutoimijat = await getKoulutustoimijat(kuntaUrit);
                $scope.koulutoimijalista = koulutoimijat;
                $scope.editableModel.koulutoimijat = _.filter($scope.editableModel.koulutoimijat, function(
                    koulutoimija
                ) {
                    return _.includes(_.map($scope.koulutoimijalista, "oid"), koulutoimija.oid);
                });
            }
        }
        $scope.loadingKoulutoimijat = false;
    };
});
