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
    .service("ListaSorter", function(Utils, Kaanna) {
        this.init = function($scope) {
            $scope.nimiSort = Utils.sort;
            return {
                key: "luotu",
                desc: true,
                set: function(key) {
                    if (key === $scope.sorter.key) {
                        $scope.sorter.desc = !$scope.sorter.desc;
                    } else {
                        $scope.sorter.key = key;
                        $scope.sorter.desc = false;
                    }
                },
                fn: function(item) {
                    switch ($scope.sorter.key) {
                        case "nimi":
                            return Utils.sort(item);
                        case "luotu":
                            return item.luotu;
                        case "tila":
                            return Utils.nameSort(item, "tila");
                        case "koulutustoimija":
                            return _(item.organisaatiot)
                                .filter(function(org) {
                                    return _.includes(org.tyypit, "Koulutustoimija");
                                })
                                .map(function(org) {
                                    return Kaanna.kaanna(org.nimi).toLowerCase();
                                })
                                .sortBy()
                                .first();
                    }
                }
            };
        };
    })
    .controller("OpetussuunnitelmatListaController", function(
        $scope,
        $state,
        OpetussuunnitelmaCRUD,
        Utils,
        ListaSorter,
        Notifikaatiot
    ) {
        // $scope.luontiOikeus = true;
        // $scope.luontiOikeus = OpetussuunnitelmaOikeudetService.onkoOikeudet('pohja', 'luku', true)
        //   || OpetussuunnitelmaOikeudetService.onkoOikeudet('pohja', 'luku', true);
        $scope.opsMaxLimit = 9999;
        $scope.opsMinLimit = 3;

        $scope.sorter = ListaSorter.init($scope);
        $scope.opsiLista = true;

        $scope.items = OpetussuunnitelmaCRUD.query(
            {},
            function(res) {
                $scope.items = Utils.opsFilter(res);
                $scope.items.$resolved = true;
            },
            Notifikaatiot.serverCb
        );

        $scope.opsLimit = $state.is("root.etusivu") ? $scope.opsMinLimit : $scope.opsMaxLimit;

        $scope.showAll = function() {
            $scope.opsLimit = $scope.opsMaxLimit;
        };

        $scope.showLess = function() {
            $scope.opsLimit = $scope.opsMinLimit;
        };
    })
    .controller("TiedotteetController", function($scope) {
        $scope.tiedotteet = [
            { nimi: { fi: "Tiedote 1" }, muokattu: "14.1.2015" },
            { nimi: { fi: "Jotain tärkeää on tapahtunut jossain" }, muokattu: "12.1.2015" }
        ];
    });
