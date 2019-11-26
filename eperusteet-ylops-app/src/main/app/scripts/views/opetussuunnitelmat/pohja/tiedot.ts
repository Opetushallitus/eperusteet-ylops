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

ylopsApp.controller("PohjaTiedotController", function(
    $scope,
    $stateParams,
    $state,
    $q,
    OpetussuunnitelmaCRUD,
    Notifikaatiot,
    Utils,
    OpsService,
    $rootScope,
    Editointikontrollit,
    $timeout,
    Kieli,
    Varmistusdialogi,
    EperusteetPerusopetus,
    EperusteetLukiokoulutus,
    EperusteetValmiitPerusteet,
    perusteet
) {
    $scope.luonnissa = $stateParams.pohjaId === "uusi";
    $scope.yksinkertainen = _.any(
        ["koulutustyyppi_6", "koulutustyyppi_15", "koulutustyyppi_20", "koulutustyyppi_22"],
        i => i === $scope.model.koulutustyyppi
    );
    $scope.kieliOrderFn = Kieli.orderFn;

    if ($scope.luonnissa) {
        $scope.model.tyyppi = "pohja";
        $scope.model.julkaisukielet = ["fi"];
    }

    $scope.editMode = false;
    $scope.kielivalinnat = ["fi", "sv", "se"];
    $scope.perustelista = [];

    $scope.hasRequiredFields = function() {
        var model = $scope.model;
        return (
            Utils.hasLocalizedText(model.nimi) && model.perusteenDiaarinumero && _.any(_.values($scope.julkaisukielet))
        );
    };

    if (perusteet) {
        $scope.perustelista = Utils.perusteFilter(perusteet);
    }

    const fetch = (notify?) =>
        $q((resolve, reject) => {
            OpsService.refetchPohja(res => {
                $scope.model = res;
                if (notify) {
                    $rootScope.$broadcast("rakenne:updated");
                }
                resolve();
            }, reject);
        });

    const successCb = res =>
        $q((resolve, reject) => {
            Notifikaatiot.onnistui("tallennettu-ok");
            if ($scope.luonnissa) {
                reject();
                $state.go("root.pohjat.yksi", { pohjaId: res.id }, { reload: true });
            } else {
                fetch(true)
                    .then(resolve)
                    .catch(reject);
            }
        });

    function mapJulkaisukielet() {
        $scope.julkaisukielet = _.zipObject(
            $scope.kielivalinnat,
            _.map($scope.kielivalinnat, function(kieli) {
                return _.indexOf($scope.model.julkaisukielet, kieli) > -1;
            })
        );
    }

    $scope.$watch("model.julkaisukielet", mapJulkaisukielet);

    var callbacks = {
        edit: fetch,
        validate: function() {
            return $scope.hasRequiredFields();
        },
        save: () =>
            $q((resolve, reject) => {
                $scope.model.julkaisukielet = _($scope.julkaisukielet)
                    .keys()
                    .filter(koodi => $scope.julkaisukielet[koodi])
                    .value();

                const params = $scope.luonnissa
                    ? {}
                    : {
                          opsId: $stateParams.pohjaId
                      };

                OpetussuunnitelmaCRUD.save(
                    params,
                    $scope.model,
                    res => {
                        successCb(res).then(resolve);
                    },
                    Notifikaatiot.serverCb
                );
            }),
        cancel: fetch,
        notify: mode => {
            $scope.editMode = mode;
            if (mode) {
                $scope.haePerusteet();
            }
        }
    };
    Editointikontrollit.registerCallback(callbacks);

    $scope.uusi = {
        cancel: function() {
            $timeout(function() {
                $state.go("root.etusivu");
            });
        },
        create: function() {
            callbacks.save();
        }
    };

    $scope.edit = function() {
        Editointikontrollit.startEditing();
    };

    $scope.haePerusteet = function() {
        if (!($scope.editMode || $scope.luonnissa)) {
            return;
        }

        const isLukio = _.any(
            ["koulutustyyppi_2", "koulutustyyppi_23", "koulutustyyppi_14"],
            i => i === $scope.model.koulutustyyppi
        );
        const isPerusopetus = "koulutustyyppi_16" === $scope.model.koulutustyyppi;

        let perusteet;
        let yksinkertainen = false;
        if (isLukio) {
            perusteet = EperusteetLukiokoulutus;
        } else if (isPerusopetus) {
            perusteet = EperusteetPerusopetus;
        } else {
            perusteet = EperusteetValmiitPerusteet;
            yksinkertainen = true;
        }

        perusteet.query(
            {},
            perusteet => {
                if (yksinkertainen) {
                    $scope.perustelista = _(perusteet)
                        .filter(p => p.koulutustyyppi === $scope.model.koulutustyyppi)
                        .value();
                } else {
                    $scope.perustelista = perusteet;
                }
            },
            Notifikaatiot.serverCb
        );
    };

    $scope.delete = function() {
        Varmistusdialogi.dialogi({
            otsikko: "varmista-poisto",
            primaryBtn: "poista",
            successCb: function() {
                $scope.model.$delete(
                    {},
                    function() {
                        Notifikaatiot.onnistui("poisto-onnistui");
                        $timeout(function() {
                            $state.go("root.etusivu");
                        });
                    },
                    Notifikaatiot.serverCb
                );
            }
        })();
    };
});
