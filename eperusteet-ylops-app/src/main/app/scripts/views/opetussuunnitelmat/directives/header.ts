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
    .directive("opsHeader", function() {
        return {
            restrict: "AE",
            scope: {
                model: "="
            },
            templateUrl: "views/opetussuunnitelmat/directives/header.html",
            controller: "OpsHeaderController"
        };
    })
    .controller("OpsHeaderController", function(
        $scope,
        $state,
        $location,
        $modal,
        $stateParams,
        PdfCreation,
        OpetussuunnitelmaCRUD
    ) {
        var POHJALINKIT = [
            // {state: 'root.pohjat.yksi.tiedot', label: 'pohjan-tiedot', role: 'info'}
        ];
        var OPSLINKIT = [
            // {state: 'root.opetussuunnitelmat.yksi.kasitteet', label: 'kasitteet', role: 'book'},
            // {state: 'root.opetussuunnitelmat.yksi.esikatselu', label: 'esikatselu', role: 'file'},
            // {state: 'root.opetussuunnitelmat.yksi.tiedot', label: 'opsn-tiedot', role: 'info'}
        ];

        var koulutusTyypit = {
            koulutustyyppi_15: "esiopetus",
            koulutustyyppi_16: "perusopetus",
            koulutustyyppi_6: "lisaopetus",
            koulutustyyppi_20: "varhaiskasvatus",
            koulutustyyppi_17: "aipe",
            koulutustyyppi_999907: "tpo",
            koulutustyyppi_2: "lukiokoulutus"
        };

        const selectEsitkatseluURL = () => {
            let currentHost = $location.host() + "";
            if (/localhost/.test(currentHost)) return "http://localhost:9020/#/";
            else if (/testi|itest/.test(currentHost)) return "https://eperusteet.testiopintopolku.fi/#/";
            else return "https://eperusteet.opintopolku.fi/#/";
        };

        $scope.createUrl = function(model) {
            return (
                selectEsitkatseluURL() +
                $stateParams.lang +
                "/ops/" +
                model.id +
                "/" +
                koulutusTyypit[model.koulutustyyppi]
            );
        };

        function mapUrls(arr) {
            return _.map(arr, function(item) {
                return _.extend(
                    {
                        url: $state.href(item.state, $stateParams),
                        active: $state.includes(item.state)
                    },
                    item
                );
            });
        }

        function update() {
            $scope.luonnissa =
                ($state.is("root.opetussuunnitelmat.yksi.tiedot") && $stateParams.id === "uusi") ||
                ($state.is("root.pohjat.yksi.tiedot") && $stateParams.pohjaId === "uusi");
            $scope.isPohjaState = $state.current.name.substr(0, 12) === "root.pohjat.";
            $scope.isPohjaTyyppi = $scope.model.tyyppi === "pohja";
            $scope.linkit = mapUrls($scope.isPohjaState ? POHJALINKIT : OPSLINKIT);
            $scope.opsId = $stateParams.id;
            $scope.pohjaId = $stateParams.pohjaId;
        }
        $scope.$on("$stateChangeSuccess", update);
        update();

        $scope.luoPdf = function() {
            PdfCreation.setOpsId($scope.opsId);
            PdfCreation.openModal();
        };

        $scope.validoiOps = () => {
            OpetussuunnitelmaCRUD.validoi({ opsId: $stateParams.id }).$promise.then(res => {
                $modal
                    .open({
                        templateUrl: "views/opetussuunnitelmat/modals/validointivirheet.html",
                        controller: "ValidointivirheetController",
                        size: "lg",
                        resolve: { virheet: _.constant(res) }
                    })
                    .result.then(_.noop);
            });
        };
    });
