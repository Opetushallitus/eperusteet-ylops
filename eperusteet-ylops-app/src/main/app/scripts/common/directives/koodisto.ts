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
    .service("Koodisto", function($http, $modal, SERVICE_LOC, $resource, Kaanna, Notifikaatiot, Utils) {
        var taydennykset = [];
        var koodistoVaihtoehdot = ["oppiaineetyleissivistava2", "lukionkurssit"];
        var nykyinenKoodisto = _.first(koodistoVaihtoehdot);

        function hae(koodisto, cb) {
            if (!_.isEmpty(taydennykset) && koodisto === nykyinenKoodisto) {
                cb();
                return;
            }
            $http.get(SERVICE_LOC + "/ulkopuoliset/koodisto/" + koodisto).then(function(re) {
                taydennykset = koodistoMapping(re.data);
                nykyinenKoodisto = koodisto;
                taydennykset = _.sortBy(taydennykset, Utils.nameSort);
                cb();
            }, Notifikaatiot.serverCb);
        }

        function haeAlarelaatiot(koodi, cb) {
            var resource = $resource(SERVICE_LOC + "/ulkopuoliset/koodisto/relaatio/sisaltyy-alakoodit/:koodi");
            resource.query({ koodi: koodi }, function(vastaus) {
                var relaatiot = koodistoMapping(vastaus);
                cb(relaatiot);
            });
        }

        function haeYlarelaatiot(koodi, tyyppi, cb) {
            if (!_.isEmpty(taydennykset) && koodi === nykyinenKoodisto) {
                cb();
                return;
            }
            var resource = $resource(SERVICE_LOC + "/ulkopuoliset/koodisto/relaatio/sisaltyy-ylakoodit/:koodi");
            resource.query({ koodi: koodi }, function(re) {
                taydennykset = suodataTyypinMukaan(re, tyyppi);
                taydennykset = koodistoMapping(taydennykset);
                taydennykset = _.sortBy(taydennykset, Utils.nameSort);
                nykyinenKoodisto = koodi;
                cb();
            });
        }

        function suodataTyypinMukaan(koodistodata, tyyppi) {
            return _.filter(koodistodata, function(data) {
                return data.koodiUri.substr(0, tyyppi.length) === tyyppi;
            });
        }

        function koodistoMapping(koodistoData) {
            return _(koodistoData)
                .map(function(kd) {
                    var nimi = {
                        fi: "",
                        sv: "",
                        en: ""
                    };
                    _.forEach(kd.metadata, function(obj) {
                        nimi[obj.kieli.toLowerCase()] = obj.nimi;
                    });

                    var haku = _.reduce(_.values(nimi), function(result, v) {
                        return result + v;
                    }).toLowerCase();
                    return {
                        koodiUri: kd.koodiUri,
                        koodiArvo: kd.koodiArvo,
                        nimi: nimi,
                        koodisto: kd.koodisto,
                        haku: haku
                    };
                })
                .value();
        }

        function filtteri(haku) {
            haku = haku.toLowerCase();
            return _.filter(taydennykset, function(t) {
                return t.koodiUri.indexOf(haku) !== -1 || t.haku.indexOf(haku) !== -1;
            });
        }

        function modaali(successCb, resolve, failureCb) {
            return function() {
                failureCb = failureCb || angular.noop;
                $modal
                    .open({
                        templateUrl: "views/common/modals/koodistoModal.html",
                        controller: "KoodistoModalController",
                        size: "lg",
                        resolve: resolve || {}
                    })
                    .result.then(successCb, failureCb);
            };
        }

        return {
            hae: hae,
            filtteri: filtteri,
            vaihtoehdot: _.clone(koodistoVaihtoehdot),
            modaali: modaali,
            haeAlarelaatiot: haeAlarelaatiot,
            haeYlarelaatiot: haeYlarelaatiot
        };
    })
    .controller("KoodistoModalController", function(
        $scope,
        $modalInstance,
        $timeout,
        Koodisto,
        tyyppi,
        ylarelaatioTyyppi
    ) {
        $scope.koodistoVaihtoehdot = Koodisto.vaihtoehdot;
        $scope.tyyppi = tyyppi;
        $scope.ylarelaatioTyyppi = ylarelaatioTyyppi;
        $scope.loydetyt = [];
        $scope.totalItems = 0;
        $scope.itemsPerPage = 10;
        $scope.nykyinen = 1;
        $scope.lataa = true;
        $scope.syote = "";

        $scope.valitseSivu = function(sivu) {
            if (sivu > 0 && sivu <= Math.ceil($scope.totalItems / $scope.itemsPerPage)) {
                $scope.nykyinen = sivu;
            }
        };

        $scope.haku = function(rajaus) {
            $scope.loydetyt = Koodisto.filtteri(rajaus);
            $scope.totalItems = _.size($scope.loydetyt);
            $scope.valitseSivu(1);
        };

        function hakuCb() {
            $scope.lataa = false;
            $scope.haku("");
            $timeout(function() {
                $("#koodisto_modal_autofocus").focus();
            }, 0);
        }

        if ($scope.ylarelaatioTyyppi === "") {
            Koodisto.hae($scope.tyyppi, hakuCb);
        } else {
            Koodisto.haeYlarelaatiot($scope.ylarelaatioTyyppi, $scope.tyyppi, hakuCb);
        }

        $scope.ok = function(koodi) {
            $modalInstance.close(koodi);
        };
        $scope.peruuta = function() {
            $modalInstance.dismiss();
        };
    })
    .directive("koodistoSelect", function(Koodisto) {
        return {
            template:
                '<button class="btn btn-default" type="text" ng-click="activate()">{{ "hae-koodistosta" | kaanna }}</button>',
            restrict: "E",
            scope: {
                valmis: "=",
                filtteri: "=",
                tyyppi: "@",
                ylarelaatioTyyppi: "=?"
            },
            controller: function($scope) {
                $scope.tyyppi = $scope.tyyppi || "tutkinnonosat";
                $scope.ylarelaatioTyyppi = $scope.ylarelaatiotyyppi || "";

                if (!$scope.valmis) {
                    return;
                } else if (_.indexOf(Koodisto.vaihtoehdot, $scope.tyyppi) === -1) {
                    return;
                }
            },
            link: function($scope: any, el, attrs: any) {
                attrs.$observe("ylarelaatiotyyppi", function() {
                    $scope.ylarelaatioTyyppi = attrs.ylarelaatiotyyppi || "";
                });

                $scope.activate = function() {
                    Koodisto.modaali(
                        $scope.valmis,
                        {
                            tyyppi: function() {
                                return $scope.tyyppi;
                            },
                            ylarelaatioTyyppi: function() {
                                return $scope.ylarelaatioTyyppi;
                            }
                        },
                        angular.noop,
                        $scope.filtteri
                    )();
                };
            }
        };
    });
