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
    .service("Kieli", function($rootScope, $state, $stateParams, $translate) {
        var sisaltokieli = "fi";
        var uikieli = "fi";
        var stateInited = false;

        var SISALTOKIELET = ["fi", "sv", "se", "ru", "en"];

        var KIELI_ORDER = ["fi", "sv", "se", "ru", "en"];

        var SISALTOKIELETMAP = {};

        var UIKIELET = ["fi", "sv", "en"];

        function orderFn(kielikoodi) {
            return _.indexOf(SISALTOKIELET, kielikoodi);
        }

        function sortKielet(kielet) {
            const sortedKielet = [];
            _.each(KIELI_ORDER, kielikoodi => {
                if (_.includes(kielet, kielikoodi)) {
                    sortedKielet.push(kielikoodi);
                }
            });
            return sortedKielet;
        }

        function isValidKielikoodi(kielikoodi) {
            return _.indexOf(SISALTOKIELET, kielikoodi) > -1;
        }
        this.isValidKielikoodi = isValidKielikoodi;

        this.setSisaltokielet = function(kielikoodit) {
            SISALTOKIELET = kielikoodit;
            SISALTOKIELETMAP = _.zipObject(kielikoodit, _.map(kielikoodit, _.constant(true)));
            $rootScope.$broadcast("update:sisaltokielet");
        };

        this.setSisaltokieli = function(kielikoodi) {
            if (_.indexOf(SISALTOKIELET, kielikoodi) > -1) {
                var old = sisaltokieli;
                sisaltokieli = kielikoodi;
                if (old !== kielikoodi) {
                    $rootScope.$broadcast("changed:sisaltokieli", kielikoodi);
                }
            }
        };

        this.getSisaltokieli = function() {
            return sisaltokieli;
        };

        this.setUiKieli = function(kielikoodi, doStateChange) {
            if (
                isValidKielikoodi(kielikoodi) &&
                (kielikoodi !== uikieli || (stateInited && $stateParams.lang !== kielikoodi))
            ) {
                if (_.isUndefined(doStateChange) || doStateChange === true) {
                    $state.go($state.current.name, _.merge($stateParams, { lang: kielikoodi }), { reload: true });
                }
                uikieli = kielikoodi;
                moment.locale(kielikoodi);
                $translate.use(kielikoodi);
                $rootScope.$broadcast("changed:uikieli", kielikoodi);
            }
        };

        this.validoi = function(olio) {
            var errors = [];
            if (!olio) {
                errors.push("tekstikentalla-ei-lainkaan-sisaltoa");
            } else {
                _.each(SISALTOKIELET, function(kieli) {
                    if (!olio[kieli]) {
                        errors.push("tekstikentalla-ei-sisaltoa-kielella-" + kieli);
                    }
                });
            }
            return errors;
        };

        this.getUiKieli = function() {
            return uikieli;
        };

        $rootScope.$on("$stateChangeSuccess", function(self, toParams) {
            stateInited = true;
            if (isValidKielikoodi(toParams.lang)) {
                uikieli = toParams.lang;
            }
        });

        this.getSisaltokielet = function() {
            return SISALTOKIELET;
        };

        this.SISALTOKIELET = SISALTOKIELET;
        this.UIKIELET = UIKIELET;
        this.orderFn = orderFn;
        this.sortKielet = sortKielet;
    })
    .directive("kielenvaihto", function() {
        return {
            restrict: "AE",
            scope: {
                modal: "@modal"
            },
            controller: "KieliController",
            templateUrl: "views/common/directives/kielenvaihto.html"
        };
    })
    .controller("KieliController", function($scope, Kieli, $q, Profiili) {
        $scope.isModal = $scope.modal === "true";
        $scope.sisaltokielet = Kieli.getSisaltokielet();
        $scope.sisaltokieli = Kieli.getUiKieli();
        $scope.uikielet = Kieli.UIKIELET;
        $scope.uikieli = Kieli.getUiKieli();
        $scope.uiLangChangeAllowed = true;
        var stateInit = $q.defer();

        var info = Profiili.profiili();

        $scope.$on("$stateChangeSuccess", function() {
            stateInit.resolve();
        });

        $q.all([stateInit.promise, info.fetchPromise]).then(function() {
            var lang = Profiili.lang();
            // Disable ui language change if language preference found in CAS
            if (Kieli.isValidKielikoodi(lang)) {
                $scope.uiLangChangeAllowed = false;
                Kieli.setUiKieli(lang);
            }
        });

        $scope.$on("update:sisaltokielet", function() {
            $scope.sisaltokielet = Kieli.getSisaltokielet();
        });

        $scope.$on("changed:sisaltokieli", function(event, value) {
            $scope.sisaltokieli = value;
        });
        $scope.$on("changed:uikieli", function(event, value) {
            $scope.uikieli = value;
        });

        $scope.setSisaltokieli = function(kieli) {
            Kieli.setSisaltokieli(kieli);
        };

        $scope.setUiKieli = function(kielikoodi) {
            Kieli.setUiKieli(kielikoodi);
        };
    });
