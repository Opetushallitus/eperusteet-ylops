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
    .controller("PohjaController", function($scope, $state, pohjaModel, $stateParams) {
        if ($state.current.name === "root.pohjat.yksi") {
            $state.go("root.pohjat.yksi.sisalto", {}, { location: "replace" });
        }
        $scope.model = pohjaModel;
        $scope.luonnissa = $stateParams.pohjaId === "uusi";
        const isLukio = () =>
            _.any(
                ["koulutustyyppi_2", "koulutustyyppi_23", "koulutustyyppi_14"],
                i => i === $scope.model.koulutustyyppi
            );
    })
    .controller("PohjaListaController", function($scope, $state, OpetussuunnitelmaCRUD, ListaSorter, Notifikaatiot) {
        $scope.pohjaMaxLimit = 9999;
        $scope.pohjaMinLimit = 7;

        OpetussuunnitelmaCRUD.query(
            { tyyppi: "pohja" },
            function(res) {
                $scope.items = res;
                $scope.items.$resolved = true;
            },
            Notifikaatiot.serverCb
        );

        $scope.opsLimit = $state.is("root.etusivu") ? $scope.pohjaMinLimit : $scope.pohjaMaxLimit;
        $scope.sorter = ListaSorter.init($scope);

        $scope.showAll = function() {
            $scope.opsLimit = $scope.pohjaMaxLimit;
        };

        $scope.showLess = function() {
            $scope.opsLimit = $scope.pohjaMinLimit;
        };
    })
    .run(function($templateCache) {
        $templateCache.put(
            "pohjaSisaltoNodeEditingTemplate",
            "" +
                '<div style="background: {{ taustanVari }}" class="tekstisisalto-solmu" ng-class="{ \'recursivetree-empty\': (node.lapset.length === 0) }">' +
                '    <span class="treehandle" icon-role="drag"></span>' +
                "    <span ng-bind=\"node.tekstiKappale.nimi || 'nimeton' | kaanna\"></span>" +
                "</div>"
        );
        $templateCache.put(
            "pohjaSisaltoNodeTemplate",
            "" +
                '<div style="background: {{ taustanVari }}" class="tekstisisalto-solmu" ng-class="{ \'search-halo\': node.$$showHalo, \'tekstisisalto-solmu-paataso\': (node.$$depth === 1) }">' +
                '    <span class="tekstisisalto-chevron action-link" ng-show="node.$$hasChildren" href="" ng-click="node.$$hidden = !node.$$hidden">' +
                '       <span ng-show="node.$$hidden" icon-role="chevron-right"></span>' +
                '       <span ng-hide="node.$$hidden" icon-role="chevron-down"></span>' +
                "    </span>" +
                '    <a href="" ui-sref="root.pohjat.yksi.sisalto.tekstikappale({ tekstikappaleId: node.id })">' +
                "       <span ng-bind=\"node.tekstiKappale.nimi || 'nimeton' | kaanna\"></span>" +
                "    </a>" +
                '    <span class="pull-right">' +
                '        <span class="muokattu-aika">' +
                "             <span kaanna=\"'muokattu-viimeksi'\"></span>:" +
                '             <span ng-bind="node.tekstiKappale.muokattu | aikaleima"></span>' +
                "        </span>" +
                "    </span>" +
                "</div>"
        );
    })
    .controller("PohjaSisaltoController", function(
        $rootScope,
        $scope,
        $q,
        Algoritmit,
        Utils,
        $stateParams,
        OpetussuunnitelmanTekstit,
        Notifikaatiot,
        $state,
        TekstikappaleOps,
        OpetussuunnitelmaCRUD,
        pohjaOps,
        Editointikontrollit,
        Lukko,
        tekstit
    ) {
        $scope.model = pohjaOps;
        $scope.model.tekstit = tekstit;
        $scope.navi = TekstikappaleOps.rakennaSivunavi(tekstit, true);
        $scope.shouldShow = function() {
            return $state.is("root.pohjat.yksi.sisalto.tekstikappale");
        };

        $scope.rajaus = {
            term: "",
            onUpdate: function(term) {
                Algoritmit.traverse($scope.model.tekstit, "lapset", function(node) {
                    node.$$showHalo = false;

                    if (_.isEmpty(term)) {
                        node.$$searchHidden = false;
                    } else if (!Algoritmit.match(term, node.tekstiKappale.nimi)) {
                        node.$$searchHidden = true;
                    } else {
                        node.$$searchHidden = false;
                        node.$$showHalo = true;
                        var p = node.$$traverseParent;
                        while (p) {
                            p.$$searchHidden = false;
                            p = p.$$traverseParent;
                        }
                    }
                });
            }
        };

        $scope.toggleState = function() {
            $scope.opened = !$scope.opened;
            _.deepFlatten(tekstit, _.property("lapset"), function(obj, depth) {
                if (depth > 1) {
                    obj.$$hidden = $scope.opened;
                }
            });
        };
        $scope.toggleState();

        $scope.sync = function() {
            OpetussuunnitelmaCRUD.syncPeruste(
                { opsId: $scope.model.id },
                _.bind(Notifikaatiot.onnistui, {}, "paivitys-onnistui"),
                Notifikaatiot.serverCb
            );
        };

        let commonParams = {
            opsId: $stateParams.pohjaId
        };

        $scope.muokkaaRakennetta = function() {
            Editointikontrollit.registerCallback({
                edit: () => $q(resolve => resolve()),
                save: () =>
                    $q((resolve, reject) => {
                        TekstikappaleOps.saveRakenne($scope.model, () => {
                            Lukko.unlock(commonParams);
                            $scope.$$isRakenneMuokkaus = false;
                            $rootScope.$broadcast("genericTree:refresh");
                            resolve();
                        });
                    }),
                cancel: () =>
                    $q(resolve => {
                        resolve();
                        Lukko.unlock(commonParams, $state.reload);
                    })
            });

            Lukko.lock(commonParams, function() {
                Editointikontrollit.startEditing().then(() => {
                    $scope.$$isRakenneMuokkaus = true;
                    $rootScope.$broadcast("genericTree:refresh");
                });
            });
        };

        $scope.sortableConfig = {
            placeholder: "placeholder"
        };

        $scope.tekstitProvider = $q(function(resolve) {
            resolve({
                root: _.constant($q.when($scope.model.tekstit)),
                hidden: function(node) {
                    if ($scope.$$isRakenneMuokkaus || !node.$$nodeParent) {
                        return false;
                    } else {
                        return (_.isEmpty($scope.rajaus.term) && node.$$nodeParent.$$hidden) || node.$$searchHidden;
                    }
                },
                template: function() {
                    return $scope.$$isRakenneMuokkaus ? "pohjaSisaltoNodeEditingTemplate" : "pohjaSisaltoNodeTemplate";
                },
                children: function(node) {
                    return $q.when(node && node.lapset ? node.lapset : []);
                },
                useUiSortable: function() {
                    return !$scope.$$isRakenneMuokkaus;
                },
                acceptDrop: _.constant(true),
                sortableClass: node => {
                    var result = "is-draggable-into";
                    if ($scope.$$isRakenneMuokkaus && _.isEmpty(node.lapset)) {
                        result += " recursivetree-empty";
                    }
                    return result;
                },
                extension: function(node, scope) {
                    scope.taustanVari = node.$$depth === 0 ? "#f2f2f9" : "#ffffff";

                    scope.poistaTekstikappale = function(osio, node) {
                        TekstikappaleOps.varmistusdialogi(
                            node.tekstiKappale.nimi,
                            function() {
                                osio = osio || $scope.model.tekstit;
                                TekstikappaleOps.delete($scope.model, osio, $stateParams.pohjaId, node, function() {
                                    _.remove(osio.lapset, node);
                                });
                            },
                            function() {
                                unlockTeksti(node.id);
                            }
                        );
                    };
                }
            });
        });

        function unlockTeksti(id, cb?) {
            return Lukko.unlockTekstikappale(_.extend({ viiteId: id }, commonParams), cb);
        }

        $scope.lisaaTekstikappale = function() {
            OpetussuunnitelmanTekstit.save(
                {
                    opsId: $stateParams.pohjaId
                },
                {},
                function(res) {
                    res.lapset = res.lapset || [];
                    Notifikaatiot.onnistui("tallennettu-ok");
                    $scope.model.tekstit.lapset.push(res);
                },
                Notifikaatiot.serverCb
            );
        };

        // $scope.uiTreeOptions = {
        //   accept: function(source, destination) {
        //     return (source.$modelValue.$$ylataso && destination.$modelValue === $scope.model.tekstit.lapset) ||
        //       (!source.$modelValue.$$ylataso && destination.$modelValue !== $scope.model.tekstit.lapset);
        //   }
        // };
    });
