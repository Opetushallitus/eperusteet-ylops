interface KoodistoArvo {
    koodiArvo: string;
    koodiUri: string;
    nimi: l.Lokalisoitu;
}

ylopsApp
    .controller("LukioOppiaineetController", function(
        $scope,
        $q: IQService,
        $stateParams,
        Kaanna,
        $log,
        LukioOpetussuunnitelmaService: LukioOpetussuunnitelmaServiceI,
        LukioTreeUtils: LukioTreeUtilsI,
        Kommentit,
        $modal,
        Editointikontrollit,
        $state,
        Notifikaatiot
    ) {
        $scope.editMode = false;
        $scope.sortableConfig = <SortableConfig>{
            placeholder: "placeholder",
            handle: ".treehandle",
            cursorAt: { top: 5, left: 5 }
        };
        $scope.sortableLiittamattomatConfig = <SortableConfig>_.cloneDeep($scope.sortableConfig);
        $scope.sortableLiitetytConfig = <SortableConfig>_.cloneDeep($scope.sortableConfig);
        $scope.treeRoot = <LukioKurssiTreeNode>{
            dtype: LukioKurssiTreeNodeType.root,
            lapset: [],
            paginationUsed: false
        };
        $scope.canAddFromTarjonta = () => $scope.rakenne && !_.isEmpty($scope.rakenne.pohjanTarjonta);
        $scope.liittamattomatRoot = <LukioKurssiTreeNode>_.cloneDeep($scope.treeRoot);
        $scope.liittamattomatRoot.paginationUsed = true;
        $scope.liitetytRoot = <LukioKurssiTreeNode>_.cloneDeep($scope.treeRoot);
        $scope.liitetytRoot.paginationUsed = true;
        var updatePagination = () => {
            LukioTreeUtils.updatePagination($scope.liittamattomatRoot.lapset, $scope.liittamattomatPagination);
            LukioTreeUtils.updatePagination($scope.liitetytRoot.lapset, $scope.liitetytPagination);
        };
        var state: LukioKurssiTreeState = {
            isEditMode: () => $scope.editMode,
            scope: $scope,
            defaultCollapse: false,
            root: () => $scope.treeRoot,
            liitetytKurssit: () => $scope.liitetytRoot.lapset,
            liittamattomatKurssit: () => $scope.liittamattomatRoot.lapset,
            updatePagination: () => updatePagination()
        };
        $scope.toggleCollapse = LukioTreeUtils.collapseToggler(() => $scope.treeRoot, state);
        var resolveRakenne = (d: IDeferred<LukioKurssiTreeNode>) =>
            LukioOpetussuunnitelmaService.getRakenne().then(rakenne => {
                $scope.rakenne = rakenne;
                $scope.treeRoot.lapset.length = 0; // empty the Array (but maintain object reference)
                _.each(LukioTreeUtils.treeRootLapsetFromRakenne(rakenne), (lapsi: LukioKurssiTreeNode) => {
                    $scope.treeRoot.lapset.push(lapsi);
                });
                $scope.liitetytRoot.lapset.length = 0; // empty the Array (but maintain object reference)
                _.each(
                    _($scope.treeRoot)
                        .flattenTree(n => n.lapset)
                        .filter((n: LukioKurssiTreeNode) => n.dtype == LukioKurssiTreeNodeType.kurssi)
                        .uniq((n: LukioKurssiTreeNode) => n.id)
                        .value(),
                    (k: LukioKurssiTreeNode) => {
                        k = _.cloneDeep(k);
                        k.$$nodeParent = null;
                        $scope.liitetytRoot.lapset.push(k);
                    }
                );
                $scope.liittamattomatRoot.lapset.length = 0; // empty the Array (but maintain object reference)
                updatePagination();
                d.resolve($scope.treeRoot);
                return rakenne;
            });
        $scope.treeProvider = <VanillaGenericTreeConfig<LukioKurssiTreeNode>>{
            root: () => {
                var d = $q.defer<LukioKurssiTreeNode>();
                resolveRakenne(d);
                return d.promise;
            },
            children: node => node.lapset || [],
            useUiSortable: () => !state.isEditMode(),
            hidden: LukioTreeUtils.defaultHidden(false),
            collapsed: LukioTreeUtils.defaultCollapsed,
            template: node => LukioTreeUtils.templates(state).nodeTemplate(node),
            extension: LukioTreeUtils.extensions(state, (n, scope) => {}),
            acceptDrop: LukioTreeUtils.acceptDropWrapper(state),
            sortableClass: _.constant("is-draggable-into")
        };

        $scope.liittamattomatPagination = <PaginationDetails>{ showPerPage: 5, currentPage: 1, state: state };
        $scope.liittamattomatTreeProvider = <VanillaGenericTreeConfig<LukioKurssiTreeNode>>{
            root: () => $q.when($scope.liittamattomatRoot),
            children: node => node.lapset || [],
            useUiSortable: () => !state.isEditMode(),
            hidden: LukioTreeUtils.defaultHidden(true),
            collapsed: LukioTreeUtils.defaultCollapsed,
            template: node => LukioTreeUtils.templates(state).nodeTemplateKurssilista(node),
            extension: LukioTreeUtils.extensions(state, (n, scope) => {}),
            acceptDrop: LukioTreeUtils.acceptDropWrapper(state),
            sortableClass: _.constant("is-draggable-into")
        };

        $scope.liitetytPagination = <PaginationDetails>{ showPerPage: 5, currentPage: 1, state: state };
        $scope.liitetytTreeProvider = <VanillaGenericTreeConfig<LukioKurssiTreeNode>>{
            root: () => $q.when($scope.liitetytRoot),
            children: node => node.lapset || [],
            hidden: LukioTreeUtils.defaultHidden(true),
            collapsed: LukioTreeUtils.defaultCollapsed,
            useUiSortable: () => !state.isEditMode(),
            template: node => LukioTreeUtils.templates(state).nodeTemplateKurssilista(node),
            extension: LukioTreeUtils.extensions(state, (n, scope) => {}),
            acceptDrop: LukioTreeUtils.acceptDropWrapper(state),
            sortableClass: _.constant("is-draggable-into")
        };
        $scope.haku = LukioTreeUtils.luoHaku(state);
        $scope.liitetytHaku = LukioTreeUtils.luoHaku(state, $scope.liitetytRoot);
        $scope.liittamattomatHaku = LukioTreeUtils.luoHaku(state, $scope.liittamattomatRoot);
        $scope.addOppiaine = () => $state.go("root.opetussuunnitelmat.lukio.opetus.uusioppiaine");

        Editointikontrollit.registerCallback({
            doNotShowMandatoryMessage: true,
            validate: () => {
                if (!_.isEmpty(state.liittamattomatKurssit())) {
                    Notifikaatiot.varoitus("lukio-puun-muokkaus-ei-saa-olla-liittamattomia-kursseja");
                    return false;
                }
                return true;
            },
            edit: () =>
                $q(resolve => {
                    LukioOpetussuunnitelmaService.lukitseRakenne().then(() => resolve());
                }),
            cancel: () =>
                $q(resolve => {
                    $scope.editMode = false;
                    LukioOpetussuunnitelmaService.vapautaRakenne().then(() => {
                        resolveRakenne($q.defer()).then(() => {
                            $scope.$broadcast("genericTree:refresh"); // templates get updated
                            resolve();
                        });
                    });
                }),
            save: kommentti =>
                $q(resolve => {
                    if (!_.isEmpty(state.liittamattomatKurssit())) {
                        Notifikaatiot.varoitus("lukio-puun-muokkaus-ei-saa-olla-liittamattomia-kursseja");
                    } else {
                        LukioOpetussuunnitelmaService.updateOppiaineKurssiStructure(
                            $scope.treeRoot,
                            kommentti
                        ).then(() => {
                            Notifikaatiot.onnistui("lukio-puun-muokkaus-onnistui");
                            LukioOpetussuunnitelmaService.vapautaRakenne().then(() => {
                                $scope.editMode = false;
                                resolveRakenne($q.defer()).then(() => {
                                    $scope.$broadcast("genericTree:refresh"); // templates get updated
                                    resolve();
                                });
                            });
                        });
                    }
                })
        });

        $scope.addTarjonnasta = pohjanOppiaine => {
            $modal
                .open({
                    templateUrl: "views/opetussuunnitelmat/modals/lukioAbstraktiOppiaineModaali.html",
                    controller: "LukioTuoAbstraktiOppiaineController",
                    size: "lg",
                    resolve: {
                        opsId: _.constant($stateParams.id),
                        valittu: _.constant(pohjanOppiaine)
                    }
                })
                .result.then(
                    res => {
                        $state.go("root.opetussuunnitelmat.lukio.opetus.oppiaine", {
                            id: $stateParams.id,
                            oppiaineId: res.id
                        });
                    },
                    () => {}
                );
        };

        $scope.toEditMode = () => {
            $scope.editMode = true;
            state.defaultCollapse = true;
            LukioTreeUtils.updateCollapse(state);
            $scope.$broadcast("genericTree:refresh"); // templates get updated
            Editointikontrollit.startEditing();
        };

        // TODO:kommentit (ei nyt rakenteelle omaa käsitettä)
    })
    .service("LukioControllerHelpers", function($rootScope, Koodisto, MuokkausUtils, Kieli, Kaanna, $log) {
        var mapKoodiArvo = (and?: (arvoKoodistoArvo) => void) => (obj: any, koodisto: KoodistoArvo) => {
            MuokkausUtils.nestedSet(obj, "koodiUri", ",", koodisto.koodiUri);
            MuokkausUtils.nestedSet(obj, "koodiArvo", ",", koodisto.koodiArvo);
            MuokkausUtils.nestedSet(obj, "nimi", ",", koodisto.nimi);
            if (and) {
                and(koodisto);
            }
        };
        var openKoodisto = (
            obj: any,
            koodisto: string,
            map: (o: any, arvoKoodistoArvo: KoodistoArvo) => void
        ) => () => {
            Koodisto.modaali(
                (koodisto: KoodistoArvo) => {
                    map(obj, _.cloneDeep(koodisto));
                    $rootScope.$broadcast("notifyCKEditor");
                },
                {
                    tyyppi: function() {
                        return koodisto;
                    },
                    ylarelaatioTyyppi: function() {
                        return "";
                    }
                }
            )();
        };
        var kielella = val => {
            var loc = {};
            loc[Kieli.getSisaltokieli()] = val;
            return loc;
        };
        var prefixFunc = (func: string | ((id: string) => string)) => {
            if (!(func instanceof Function)) {
                var orig = func;
                return id => orig + id;
            }
            return <(id: string) => string>func;
        };
        var tekstit = (idt: string[], label: string | ((id: string) => string), container?: (any) => any) => obj => {
            var labelFunc = prefixFunc(label);
            if (container) {
                obj = container(obj);
            }
            var os = _(idt)
                    .map(id => {
                        return { id: id, label: labelFunc(id), obj: obj ? obj[id] : null };
                    })
                    .value(),
                byId = _(os)
                    .indexBy(o => o.id)
                    .value();
            return {
                osat: os,
                isAddAvailable: () => _.any(os, o => !o.obj),
                isEmpty: () => !_.any(os, o => o.obj),
                addOsa: id => {
                    var newOsa = kielella("");
                    obj[id] = newOsa;
                    byId[id].obj = newOsa;
                    $rootScope.$broadcast("notifyCKEditor");
                    $rootScope.$broadcast("enableEditing");
                },
                removeOsa: id => {
                    obj[id] = null;
                    byId[id].obj = null;
                    $rootScope.$broadcast("notifyCKEditor");
                }
            };
        };
        var osat = (idt: string[], label: string | ((id: string) => string), container?: (any) => any) => obj => {
            var labelFunc = prefixFunc(label);
            if (container) {
                obj = container(obj);
            }
            var os = _(idt)
                    .map(id => {
                        return { id: id, obj: obj ? obj[id] : null, label: labelFunc(id) };
                    })
                    .value(),
                byId = _(os)
                    .indexBy(o => o.id)
                    .value();
            return {
                osat: os,
                isAddAvailable: () => _.any(os, o => !o.obj),
                isEmpty: () => !_.any(os, o => o.obj),
                addOsa: id => {
                    var newOsa = {
                        otsikko: kielella(Kaanna.kaanna(labelFunc(id))),
                        teksti: kielella("")
                    };
                    obj[id] = newOsa;
                    byId[id].obj = newOsa;
                    $rootScope.$broadcast("notifyCKEditor");
                    $rootScope.$broadcast("enableEditing");
                },
                removeOsa: id => {
                    obj[id] = null;
                    byId[id].obj = null;
                    $rootScope.$broadcast("notifyCKEditor");
                }
            };
        };
        var valtakunnallisetKurssiTyypit = [
            "VALTAKUNNALLINEN_PAKOLLINEN",
            "VALTAKUNNALLINEN_SYVENTAVA",
            "VALTAKUNNALLINEN_SOVELTAVA"
        ];
        var paikallisetKurssiTyypit = ["PAIKALLINEN_SYVENTAVA", "PAIKALLINEN_SOVELTAVA"];
        var kaikkiKurssiTyypit = _.union(valtakunnallisetKurssiTyypit, paikallisetKurssiTyypit);
        return {
            openOppiaineKoodisto: (obj: Lukio.Oppiaine) =>
                openKoodisto(obj, "oppiaineetyleissivistava2", mapKoodiArvo()),
            openKurssiKoodisto: (obj: Lukio.LukiokurssiOps) =>
                openKoodisto(
                    obj,
                    "lukionkurssit",
                    mapKoodiArvo(koodi => {
                        obj.lokalisoituKoodi = {};
                        obj.lokalisoituKoodi[Kieli.getSisaltokieli()] = koodi.koodiArvo;
                    })
                ),
            kielella: kielella,
            openOppiaineKieliKoodisto: (obj: Lukio.Oppiaine, and?: (arvoKoodistoArvo: KoodistoArvo) => void) =>
                openKoodisto(obj, "lukiokielitarjonta", (o: any, koodisto: KoodistoArvo) => {
                    MuokkausUtils.nestedSet(o, "kieliKoodiUri", ",", koodisto.koodiUri);
                    MuokkausUtils.nestedSet(obj, "kieliKoodiArvo", ",", koodisto.koodiArvo);
                    MuokkausUtils.nestedSet(obj, "kieli", ",", koodisto.nimi);
                    if (and) {
                        and(koodisto);
                    }
                }),
            valtakunnallisetKurssiTyypit: () => _.clone(valtakunnallisetKurssiTyypit),
            paikallisetKurssiTyypit: () => _.clone(paikallisetKurssiTyypit),
            kaikkiKurssiTyypit: () => _.clone(kaikkiKurssiTyypit),
            kurssiTyyppiKuvausTekstit: tekstit(
                kaikkiKurssiTyypit,
                id => "lukio-kurssi-tyyppi-otsikko-" + id.toLowerCase(),
                obj => obj.kurssiTyyppiKuvaukset
            ),
            muokattavatOppiaineOsat: osat(["tehtava", "tavoitteet", "arviointi"], "oppiaine-osa-"),
            muokattavatKurssiOsat: osat(
                ["tavoitteet", "keskeinenSisalto", "tavoitteetJaKeskeinenSisalto"],
                "kurssi-osa-"
            )
        };
    })
    .controller("LukioOppiaineController", function(
        $scope,
        $q: IQService,
        $stateParams,
        $state,
        $timeout,
        OpsService,
        LukioOpetussuunnitelmaService: LukioOpetussuunnitelmaServiceI,
        Kaanna,
        $log,
        $modal,
        Editointikontrollit,
        LukioControllerHelpers,
        Varmistusdialogi,
        Notifikaatiot,
        Lukko
    ) {
        $scope.oppiaine = null;
        $scope.editMode = false;
        $scope.kurssiKuvauksetVisible = false;
        $scope.rootOps = true;
        $scope.connected = () =>
            $scope.oppiaine &&
            !$scope.oppiaine.oma &&
            !$scope.rootOps &&
            $scope.oppiaine.maariteltyPohjassa &&
            !$scope.oppiaine.oppiaineId;
        $scope.isReconnectable = () =>
            $scope.oppiaine &&
            $scope.oppiaine.oma &&
            !$scope.rootOps &&
            $scope.oppiaine.maariteltyPohjassa &&
            !$scope.oppiaine.oppiaineId;
        $scope.isEditable = () => $scope.oppiaine && $scope.oppiaine.oma;
        $scope.isDeletable = () =>
            $scope.oppiaine &&
            $scope.oppiaine.oma &&
            (!$scope.oppiaine.maariteltyPohjassa ||
                (!$scope.oppiaine.oppiaineId && $scope.oppiaine.abstrakti) ||
                ($scope.oppiaine.oppiaineId && !$scope.oppiaine.abstrakti));
        $scope.canAddOppimaara = () => $scope.oppiaine && $scope.oppiaine.koosteinen && $scope.oppiaine.oma;
        $scope.canAddFromTarjonta = () =>
            $scope.oppiaine &&
            $scope.oppiaine.koosteinen &&
            $scope.oppiaine.oma &&
            !_.isEmpty($scope.oppiaine.pohjanTarjonta);
        const noOwnCourses = [
            "oppiaineetyleissivistava2_tvk",
            "oppiaineetyleissivistava2_ld",
            "oppiaineetyleissivistava2_to"
        ];
        $scope.canAddKurssi = () =>
            !$scope.editing &&
            $scope.oppiaine &&
            !$scope.oppiaine.koosteinen &&
            !_.any(noOwnCourses, k => k === $scope.oppiaine.koodiUri);
        $scope.tarjottavaTyyppi = () => {
            if (!$scope.canAddFromTarjonta()) {
                return null;
            }
            return OpsService.oppiaineIsKieli($scope.oppiaine)
                ? "kieli"
                : $scope.oppiaine.koodiArvo == "KT" ? "uskonto" : "tuntematon";
        };
        $scope.isKurssiDeletable = kurssi => {
            return (
                kurssi.tyyppi === "VALTAKUNNALLINEN_SOVELTAVA" ||
                _.any(LukioControllerHelpers.paikallisetKurssiTyypit(), t => kurssi.tyyppi == t)
            );
        };

        $scope.removeKurssi = ($event, kurssi) => {
            $event.preventDefault();
            $event.stopPropagation();

            LukioOpetussuunnitelmaService.lukitseKurssi(kurssi.id).then(() => {
                Varmistusdialogi.dialogi({
                    otsikko: "varmista-poista-kurssi",
                    primaryBtn: "poista-kurssi",
                    failureCb: () => LukioOpetussuunnitelmaService.vapautaKurssi(kurssi.id),
                    successCb: () =>
                        LukioOpetussuunnitelmaService.removeKurssi(kurssi.id, $stateParams.id).then(() => {
                            Notifikaatiot.onnistui("kurssin-poisto-onnistui");
                            $timeout(() =>
                                $state.go(
                                    "root.opetussuunnitelmat.lukio.opetus.oppiaine",
                                    {
                                        id: $stateParams.id,
                                        oppiaineId: $stateParams.oppiaineId
                                    },
                                    { reload: true, notify: true }
                                )
                            );
                        })
                })();
            });
        };

        $scope.isKurssiDeletable = kurssi => {
            return (
                kurssi.tyyppi === "VALTAKUNNALLINEN_SOVELTAVA" ||
                _.any(LukioControllerHelpers.paikallisetKurssiTyypit(), t => kurssi.tyyppi == t)
            );
        };

        $scope.removeKurssi = ($event, kurssi) => {
            $event.preventDefault();
            $event.stopPropagation();

            LukioOpetussuunnitelmaService.lukitseKurssi(kurssi.id).then(() => {
                Varmistusdialogi.dialogi({
                    otsikko: "varmista-poista-kurssi",
                    primaryBtn: "poista-kurssi",
                    failureCb: () => LukioOpetussuunnitelmaService.vapautaKurssi(kurssi.id),
                    successCb: () =>
                        LukioOpetussuunnitelmaService.removeKurssi(kurssi.id, $stateParams.id).then(() => {
                            Notifikaatiot.onnistui("kurssin-poisto-onnistui");
                            $timeout(() =>
                                $state.go(
                                    "root.opetussuunnitelmat.lukio.opetus.oppiaine",
                                    {
                                        id: $stateParams.id,
                                        oppiaineId: $stateParams.oppiaineId
                                    },
                                    { reload: true, notify: true }
                                )
                            );
                        })
                })();
            });
        };

        LukioOpetussuunnitelmaService.getOppiaine($stateParams.oppiaineId).then(oa => {
            $scope.oppiaine = oa;
            $scope.muokattavatOsat = LukioControllerHelpers.muokattavatOppiaineOsat(oa);
            $scope.openKoodisto = LukioControllerHelpers.openOppiaineKoodisto($scope.oppiaine);
            $scope.kurssiTyyppiKuvaukset = LukioControllerHelpers.kurssiTyyppiKuvausTekstit($scope.oppiaine);
        });

        LukioOpetussuunnitelmaService.getRakenne().then(r => ($scope.rootOps = r.root));

        $scope.openKurssi = kurssi => {
            $state.go("root.opetussuunnitelmat.lukio.opetus.kurssi", {
                id: $stateParams.id,
                oppiaineId: $stateParams.oppiaineId,
                kurssiId: kurssi.id
            });
        };
        $scope.valtakunnallisetKurssiTyypit = _.map(LukioControllerHelpers.valtakunnallisetKurssiTyypit(), tyyppi => ({
            key: tyyppi,
            tyyppi: tyyppi.split("_")[1].toLowerCase()
        }));
        $scope.paikallisetKurssiTyypit = _.map(LukioControllerHelpers.paikallisetKurssiTyypit(), tyyppi => ({
            key: tyyppi,
            tyyppi: tyyppi.split("_")[1].toLowerCase()
        }));
        $scope.kuvauksetIsEmpty = (tyypit: any[]) => {
            return !_.any(tyypit, t => {
                return (
                    t &&
                    $scope.oppiaine &&
                    ($scope.oppiaine.kurssiTyyppiKuvaukset[t.key] ||
                        ($scope.oppiaine.perusteen && $scope.oppiaine.perusteen.kurssiTyyppiKuvaukset[t.key]))
                );
            });
        };

        $scope.deleteOppiaine = () => {
            var maara = $scope.oppiaine.oppiaineId != null;
            Varmistusdialogi.dialogi({
                otsikko: maara ? "varmista-poista-oppimaara" : "varmista-poista-oppiaine",
                primaryBtn: "poista",
                successCb: () =>
                    LukioOpetussuunnitelmaService.deleteOppiaine($stateParams.oppiaineId).then(() => {
                        if (maara) {
                            Notifikaatiot.onnistui("oppimaaran-poisto-onnistui");
                            $timeout(() =>
                                $state.go(
                                    "root.opetussuunnitelmat.lukio.opetus.oppiaine",
                                    {
                                        id: $stateParams.id,
                                        oppiaineId: $scope.oppiaine.oppiaineId
                                    },
                                    { reload: true, notify: true }
                                )
                            );
                        } else {
                            Notifikaatiot.onnistui("oppiaineen-poisto-onnistui");
                            $timeout(() =>
                                $state.go("root.opetussuunnitelmat.lukio.opetus.oppiaineet", {
                                    id: $stateParams.id
                                })
                            );
                        }
                    })
            })();
        };

        var palautaYlempi = () => {
            LukioOpetussuunnitelmaService.lukitseOppiaine($stateParams.oppiaineId).then(() =>
                LukioOpetussuunnitelmaService.palautaYlempaan($stateParams.oppiaineId).then(res => {
                    Notifikaatiot.onnistui("yhteyden-palautus-onnistui");
                    $timeout(() =>
                        $state.go(
                            "root.opetussuunnitelmat.lukio.opetus.oppiaine",
                            {
                                id: $stateParams.id,
                                oppiaineId: res.id
                            },
                            { reload: true, notify: true }
                        )
                    );
                })
            );
        };

        $scope.connectOppiaine = () => {
            Varmistusdialogi.dialogi({
                otsikko: "varmista-palauta-yhteys",
                primaryBtn: "palauta-yhteys",
                successCb: () => palautaYlempi()
            })();
        };

        var cloneOppiaine = () => {
            LukioOpetussuunnitelmaService.kloonaaOppiaineMuokattavaksi($stateParams.oppiaineId).then(res => {
                Notifikaatiot.onnistui("yhteyden-katkaisu-onnistui");
                $timeout(() =>
                    $state.go(
                        "root.opetussuunnitelmat.lukio.opetus.oppiaine",
                        {
                            id: $stateParams.id,
                            oppiaineId: res.id
                        },
                        { reload: true, notify: true }
                    )
                );
            });
        };

        $scope.disconnectOppiaine = () => {
            // Lukitusta ei tarivta, koska luodaan käytännössä uusi oppiaine
            Varmistusdialogi.dialogi({
                otsikko: "varmista-katkaise-yhteys",
                primaryBtn: "katkaise-yhteys",
                successCb: () => cloneOppiaine()
            })();
        };

        $scope.addOppimaara = () => {
            $state.go(
                "root.opetussuunnitelmat.lukio.opetus.uusioppiaine",
                {
                    id: $stateParams.id,
                    parentOppiaineId: $stateParams.oppiaineId
                },
                { reload: true, notify: true }
            );
        };

        $scope.addTarjonnasta = pohjanOppiaine => {
            $modal
                .open({
                    templateUrl: "views/opetussuunnitelmat/modals/lukioKieliTarjontaModaali.html",
                    controller: "LukioKielitarjontaModalController",
                    size: "lg",
                    resolve: {
                        opsId: _.constant($stateParams.id),
                        oppiaine: _.constant($scope.oppiaine),
                        valittu: _.constant(pohjanOppiaine)
                    }
                })
                .result.then(
                    res => {
                        $state.go("root.opetussuunnitelmat.lukio.opetus.oppiaine", {
                            id: $stateParams.id,
                            oppiaineId: res.id
                        });
                    },
                    () => {}
                );
        };
        $scope.lisaaKurssi = () => {
            $state.go("root.opetussuunnitelmat.lukio.opetus.uusikurssi", {
                id: $stateParams.id,
                oppiaineId: $stateParams.oppiaineId
            });
        };

        Editointikontrollit.registerCallback({
            validate: function() {
                return $scope.oppiaine.koodiArvo && Kaanna.kaanna($scope.oppiaine.nimi) != null;
            },
            edit: () =>
                $q(resolve => {
                    LukioOpetussuunnitelmaService.lukitseOppiaine($stateParams.oppiaineId).then(() => {
                        $scope.editingOld = true;
                        resolve();
                    });
                }),
            cancel: () =>
                $q(resolve => {
                    LukioOpetussuunnitelmaService.vapautaOppiaine($stateParams.oppiaineId).then(() => {
                        $scope.editMode = false;
                        resolve();
                    });
                }),
            save: () =>
                $q(resolve => {
                    $scope.oppiaine.oppiaineId = $scope.oppiaine.id;
                    LukioOpetussuunnitelmaService.updateOppiaine($scope.oppiaine).then(() => {
                        LukioOpetussuunnitelmaService.vapautaOppiaine($scope.oppiaine.id)
                            .then(() => {
                                $scope.editMode = false;
                                resolve();
                            })
                            .then($state.reload);
                    });
                })
        });

        $scope.toEditMode = () => {
            $scope.editMode = true;
            Editointikontrollit.startEditing();
        };
    })
    .controller("LuoLukioOppiaineController", function(
        $scope,
        $q: IQService,
        $stateParams,
        $state,
        LukioOpetussuunnitelmaService: LukioOpetussuunnitelmaServiceI,
        Kaanna,
        $log,
        Editointikontrollit,
        LukioControllerHelpers
    ) {
        $scope.oppiaine = <Lukio.LukioOppiaineTallennus>{
            nimi: {},
            kuvaus: {},
            koosteinen: false,
            oppiaineId: $stateParams.parentOppiaineId ? $stateParams.parentOppiaineId : null,
            kurssiTyyppiKuvaukset: {}
        };
        $scope.isEditable = () => true;
        $scope.editMode = true;
        $scope.muokattavatOsat = LukioControllerHelpers.muokattavatOppiaineOsat($scope.oppiaine);
        $scope.kurssiTyyppiKuvaukset = LukioControllerHelpers.kurssiTyyppiKuvausTekstit($scope.oppiaine);
        $scope.removeKurssiTyyppiKuvaus = tyyppi => {
            delete $scope.oppiaine.kurssiTyyppiKuvaukset[tyyppi];
        };
        $scope.addKurssiTyyppiKuvaus = tyyppi => {
            $scope.oppiaine.kurssiTyyppiKuvaukset[tyyppi] = LukioControllerHelpers.kielella("");
        };
        Editointikontrollit.registerCallback({
            validate: function() {
                return $scope.oppiaine.koodiArvo && Kaanna.kaanna($scope.oppiaine.nimi) != null;
            },
            edit: () =>
                $q(resolve => {
                    resolve();
                }),
            cancel: () =>
                $q(resolve => {
                    resolve();
                    $state.go("root.opetussuunnitelmat.lukio.opetus.oppiaineet");
                }),
            save: () =>
                $q(resolve => {
                    LukioOpetussuunnitelmaService.saveOppiaine($scope.oppiaine).then(function(ref) {
                        resolve();
                        $state.go("root.opetussuunnitelmat.lukio.opetus.oppiaine", {
                            oppiaineId: ref.id
                        });
                    });
                })
        });
        $scope.isMaara = () => $scope.oppiaine && $scope.oppiaine.oppiaineId != null;
        $scope.openKoodisto = LukioControllerHelpers.openOppiaineKoodisto($scope.oppiaine);
        Editointikontrollit.startEditing();
    })
    .controller("LukioKurssiController", function(
        $scope,
        $q: IQService,
        $stateParams,
        $state,
        LukioControllerHelpers,
        LukioOpetussuunnitelmaService: LukioOpetussuunnitelmaServiceI,
        Kaanna,
        $log,
        Editointikontrollit,
        $timeout,
        Varmistusdialogi
    ) {
        $scope.kurssi = null;
        $scope.oppiaine = null;
        $scope.editMode = false;
        $scope.rootOps = true;
        $scope.kurssiTyypit = _.map(LukioControllerHelpers.paikallisetKurssiTyypit(), t => ({
            tyyppi: t,
            nimi: "lukio-kurssi-tyyppi-otsikko-" + t.toLowerCase()
        }));

        $scope.isPaikallinen = () =>
            $scope.kurssi && _.any(LukioControllerHelpers.paikallisetKurssiTyypit(), t => $scope.kurssi.tyyppi == t);
        $scope.isEditable = () => $scope.kurssi && $scope.kurssi.oma && $scope.isPaikallinen(); // parts such as tyyppi or koodi
        $scope.connected = () => $scope.kurssi && !$scope.kurssi.oma && !$scope.rootOps;
        $scope.isReconnectable = () =>
            $scope.kurssi && $scope.kurssi.oma && !$scope.rootOps && $scope.kurssi.palautettava;
        $scope.isEditAllowed = () => $scope.kurssi && $scope.kurssi.oma;
        $scope.isDeletable = () =>
            ($scope.kurssi && ($scope.kurssi.oma && $scope.isPaikallinen())) ||
            $scope.kurssi.tyyppi === "VALTAKUNNALLINEN_SOVELTAVA";
        LukioOpetussuunnitelmaService.getOppiaine($stateParams.oppiaineId).then(oa => {
            $scope.oppiaine = oa;
        });
        LukioOpetussuunnitelmaService.getRakenne().then(r => ($scope.rootOps = r.root));
        LukioOpetussuunnitelmaService.getKurssi($stateParams.oppiaineId, $stateParams.kurssiId).then(kurssi => {
            $scope.inPeruste = {
                tavoitteetJaKeskeisetSisallot: _.has(kurssi, "perusteen.tavoitteetJaKeskeisetSisallot.teksti"),
                keskeisetSisallot: _.has(kurssi, "perusteen.keskeisetSisallot.teksti"),
                tavoitteet: _.has(kurssi, "perusteen.tavoitteet.teksti")
            };
            $scope.kurssi = kurssi;
            $scope.openKoodisto = LukioControllerHelpers.openKurssiKoodisto($scope.kurssi);
            $scope.muokattavatOsat = LukioControllerHelpers.muokattavatKurssiOsat($scope.kurssi);
        });

        $scope.getTyyppiSelite = () => {
            if ($scope.kurssi) {
                var tyyppi = $scope.kurssi.tyyppi;
                tyyppi = (tyyppi.indexOf("_") !== -1 ? tyyppi.split("_")[1] : tyyppi).toLowerCase();
                return "kurssi-tyyppi-selite-" + tyyppi;
            }
            return "";
        };

        Editointikontrollit.registerCallback({
            validate: () => {
                return (
                    Kaanna.kaanna($scope.kurssi.nimi) != null &&
                    Kaanna.kaanna($scope.kurssi.lokalisoituKoodi) &&
                    $scope.kurssi.tyyppi != null &&
                    $scope.kurssi.laajuus > 0
                );
            },
            edit: () =>
                $q(resolve => {
                    LukioOpetussuunnitelmaService.lukitseKurssi($stateParams.kurssiId).then(() => resolve());
                }),
            cancel: () =>
                $q(resolve => {
                    LukioOpetussuunnitelmaService.vapautaKurssi($stateParams.kurssiId).then(() => {
                        $timeout(() =>
                            $state.go(
                                "root.opetussuunnitelmat.lukio.opetus.kurssi",
                                {
                                    id: $stateParams.id,
                                    oppiaineId: $stateParams.oppiaineId,
                                    kurssiId: $stateParams.kurssiId
                                },
                                { reload: true, notify: true }
                            )
                        );
                        resolve();
                    });
                }),
            save: () =>
                $q(resolve => {
                    LukioOpetussuunnitelmaService.updateKurssi($stateParams.kurssiId, $scope.kurssi).then(() => {
                        LukioOpetussuunnitelmaService.vapautaKurssi($stateParams.kurssiId).then(() => {
                            $timeout(() =>
                                $state.go(
                                    "root.opetussuunnitelmat.lukio.opetus.kurssi",
                                    {
                                        id: $stateParams.id,
                                        oppiaineId: $stateParams.oppiaineId,
                                        kurssiId: $stateParams.kurssiId
                                    },
                                    { reload: true, notify: true }
                                )
                            );
                            resolve();
                        });
                    });
                })
        });

        $scope.edit = () => {
            $scope.editMode = true;
            Editointikontrollit.startEditing();
        };

        $scope.goBack = () => {
            $state.go("root.opetussuunnitelmat.lukio.opetus.oppiaine", {
                oppiaineId: $stateParams.oppiaineId
            });
        };

        $scope.connectKurssi = () => {
            Varmistusdialogi.dialogi({
                otsikko: "varmista-palauta-kurssi",
                primaryBtn: "palauta-yhteys",
                successCb: () =>
                    LukioOpetussuunnitelmaService.lukitseKurssi($stateParams.kurssiId).then(() => {
                        LukioOpetussuunnitelmaService.reconnectKurssi(
                            $stateParams.kurssiId,
                            $stateParams.oppiaineId,
                            $stateParams.id
                        ).then(r => {
                            // ei tarvitse enää vapauttaa, koska jos yhteys taas katkaistaa, luodaanuusi kurssi
                            $timeout(() =>
                                $state.go(
                                    "root.opetussuunnitelmat.lukio.opetus.kurssi",
                                    {
                                        id: $stateParams.id,
                                        oppiaineId: $stateParams.oppiaineId,
                                        kurssiId: r.id
                                    },
                                    { reload: true, notify: true }
                                )
                            );
                        });
                    })
            })();
        };

        $scope.disconnectKurssi = () => {
            // Ei tarvitse lukita, koska luodaan uusi kurssi (paikallinen kopio)
            Varmistusdialogi.dialogi({
                otsikko: "varmista-katkaise-kurssi-yhteys",
                primaryBtn: "katkaise-yhteys",
                successCb: () =>
                    LukioOpetussuunnitelmaService.disconnectKurssi(
                        $stateParams.kurssiId,
                        $stateParams.oppiaineId,
                        $stateParams.id
                    ).then(r => {
                        $timeout(() =>
                            $state.go(
                                "root.opetussuunnitelmat.lukio.opetus.kurssi",
                                {
                                    id: $stateParams.id,
                                    oppiaineId: $stateParams.oppiaineId,
                                    kurssiId: r.id
                                },
                                { reload: true, notify: true }
                            )
                        );
                    })
            })();
        };

        $scope.delete = () => {
            Varmistusdialogi.dialogi({
                otsikko: "varmista-poista-kurssi",
                primaryBtn: "poista-kurssi",
                successCb: () => {
                    LukioOpetussuunnitelmaService.lukitseKurssi($stateParams.kurssiId).then(() =>
                        LukioOpetussuunnitelmaService.removeKurssi($stateParams.kurssiId, $stateParams.id).then(() => {
                            $scope.goBack();
                        })
                    );
                }
            })();
        };
    })
    .controller(
        "LuoLukioKurssiController",
        (
            $scope,
            $q: IQService,
            $stateParams,
            LukioControllerHelpers,
            Editointikontrollit,
            $timeout,
            $state,
            LukioOpetussuunnitelmaService: LukioOpetussuunnitelmaServiceI,
            Kaanna,
            $log
        ) => {
            $scope.kurssi = <Lukio.LuoLukiokurssi>{
                oppiaineId: $stateParams.oppiaineId,
                tyyppi: "PAIKALLINEN_SYVENTAVA",
                nimi: LukioControllerHelpers.kielella(""),
                laajuus: 1
            };
            $scope.editMode = true;
            $scope.oppiaine = null;
            $scope.openKoodisto = LukioControllerHelpers.openKurssiKoodisto($scope.kurssi);
            $scope.kurssiTyypit = _.map(LukioControllerHelpers.paikallisetKurssiTyypit(), t => ({
                tyyppi: t,
                nimi: "lukio-kurssi-tyyppi-otsikko-" + t.toLowerCase()
            }));
            LukioOpetussuunnitelmaService.getOppiaine($stateParams.oppiaineId).then(oa => {
                $scope.oppiaine = oa;
            });
            $scope.muokattavatOsat = LukioControllerHelpers.muokattavatKurssiOsat($scope.kurssi);

            Editointikontrollit.registerCallback({
                validate: () => {
                    return (
                        Kaanna.kaanna($scope.kurssi.nimi) != null &&
                        Kaanna.kaanna($scope.kurssi.lokalisoituKoodi) &&
                        $scope.kurssi.tyyppi != null &&
                        $scope.kurssi.laajuus > 0
                    );
                },
                edit: () =>
                    $q(resolve => {
                        resolve();
                    }),
                cancel: () =>
                    $q(resolve => {
                        $timeout(() =>
                            $state.go("root.opetussuunnitelmat.lukio.opetus.oppiaine", {
                                id: $stateParams.id,
                                oppiaineId: $stateParams.oppiaineId
                            })
                        );
                        resolve();
                    }),
                save: () =>
                    $q(resolve => {
                        LukioOpetussuunnitelmaService.saveKurssi($scope.kurssi).then(res => {
                            $timeout(() =>
                                $state.go("root.opetussuunnitelmat.lukio.opetus.kurssi", {
                                    id: $stateParams.id,
                                    oppiaineId: $stateParams.oppiaineId,
                                    kurssiId: res.id
                                })
                            );
                            resolve();
                        });
                    })
            });
            Editointikontrollit.startEditing();
        }
    )
    .controller("LukioOppiaineSisaltoController", function(
        $scope,
        $q: IQService,
        $stateParams,
        LukioOpetussuunnitelmaService: LukioOpetussuunnitelmaServiceI,
        Kaanna,
        $log
    ) {
        $scope.textHidden = false;
        $scope.editing = false;

        $scope.toggleTextVisible = () => {
            $scope.textHidden = !$scope.textHidden;
        };

        $scope.edit = () => {
            $scope.editing = true;
        };
    })
    .directive("lukioOppiaineOsa", () => {
        return {
            scope: {
                model: "=lukioOppiaineOsa",
                oppiaine: "=?oppiaine",
                perusteenTeksti: "=?perusteenTeksti",
                oppiaineenTeksti: "=?oppiaineenTeksti",
                sisaltoTitle: "=?sisaltoTitle",
                colorbox: "=?colorbox",
                ikoni: "=?ikoni",
                editable: "=?editable",
                editing: "=?editing",
                pohjanTeksti: "=?pohjanTeksti"
            },
            templateUrl: "views/opetussuunnitelmat/directives/oppiaineSisalto.html",
            controller: "LukioOppiaineSisaltoController"
        };
    })
    .controller("LukioPerusteenSisaltoController", $scope => {
        $scope.textHidden = true;

        $scope.toggleTextVisible = () => {
            $scope.textHidden = !$scope.textHidden;
        };
    })
    .directive("lukoiPerusteenSisalto", () => {
        return {
            scope: {
                model: "=lukiPerusteen",
                perusteenTeksti: "=?perusteenTeksti"
            },
            templateUrl: "views/opetussuunnitelmat/directives/lukiPerusteenSisalto.html",
            controller: "LukioPerusteenSisaltoController"
        };
    })
    .controller("LukioKielitarjontaModalController", function(
        $scope,
        $stateParams,
        $modalInstance,
        $q,
        OpsService,
        $log,
        $state,
        opsId,
        oppiaine,
        valittu,
        LukioOpetussuunnitelmaService: LukioOpetussuunnitelmaServiceI,
        Notifikaatiot,
        LukioControllerHelpers,
        Kaanna
    ) {
        const getType = () => {
            if (!_.isString(oppiaine.koodiArvo)) {
                $log.warn("Oppiaineen koodia ei ole määritelty");
                return "";
            }
            if (OpsService.oppiaineIsKieli(oppiaine)) {
                return "kieli";
            } else if (oppiaine.koodiArvo === "KT") {
                return "uskonto";
            } else if (oppiaine.koodiArvo === "AI") {
                return "aidinkieli";
            } else {
                $log.warn("Oppiaineen täytyy olla kieli tai uskonto");
                return "";
            }
        };

        $scope.$type = getType();
        $scope.oppiaine = oppiaine;
        $scope.$valittu = _.cloneDeep(valittu);
        $scope.$valittu.kieli = LukioControllerHelpers.kielella("");
        $scope.$omaNimi =
            !$scope.$type || $scope.$type == "uskonto"
                ? _.cloneDeep($scope.$valittu.nimi)
                : LukioControllerHelpers.kielella("");
        $scope.$concretet = _.reject(oppiaine.perusteen != null ? oppiaine.perusteen.oppimaarat : [],
                om => om.abstrakti);
        $scope.openKieliKoodisto = LukioControllerHelpers.openOppiaineKieliKoodisto($scope.$valittu, koodi => {
            var osat = Kaanna.kaanna($scope.$valittu.nimi).split(/\s*,\s*/),
                kaannettyKieli = Kaanna.kaanna(koodi.nimi);
            if (!Kaanna.kaanna($scope.$omaNimi)) {
                if ($scope.$type == "kieli" && kaannettyKieli && osat.length == 2) {
                    $log.info($scope.$valittu.koodiArvo);
                    $scope.$omaNimi = LukioControllerHelpers.kielella(
                        ($scope.$valittu.koodiArvo == "KX" ? "" : kaannettyKieli) + ", " + osat[1].trim()
                    );
                } else {
                    $scope.$omaNimi = _.cloneDeep($scope.$valittu.nimi);
                }
            }
        });
        $scope.kieliJoToteutettu = () => {
            if ($scope.$type != "kieli") {
                return false;
            }
            return _.any(oppiaine.oppimaarat, om => {
                return (
                    om.tunniste == $scope.$valittu.tunniste &&
                    om.kieliKoodiArvo == $scope.$valittu.kieliKoodiArvo &&
                    ($scope.$valittu.kieliKoodiArvo != "KX" ||
                        _.isEqual(
                            _.omit(_.pick(om.kieli, _.identity), "_id"),
                            _.omit(_.pick($scope.$valittu.kieli, _.identity), "_id")
                        ))
                );
            });
        };

        $scope.ok = () => {
            if (!$scope.kieliJoToteutettu()) {
                // Lisätään uusi, ei tarvitse lukita:
                LukioOpetussuunnitelmaService.addKielitarjonta(
                    oppiaine.id,
                    {
                        tunniste: $scope.$valittu.tunniste,
                        nimi: $scope.$omaNimi,
                        kieliKoodiArvo: $scope.$valittu.kieliKoodiArvo,
                        kieliKoodiUri: $scope.$valittu.kieliKoodiUri,
                        kieli: $scope.$valittu.kieli
                    },
                    opsId
                ).then(res => {
                    $modalInstance.close(res);
                    Notifikaatiot.onnistui("tallennettu-ok");
                }, Notifikaatiot.serverCb);
            }
        };

        $scope.peruuta = $modalInstance.dismiss;
    })
    .controller("LukioTuoAbstraktiOppiaineController", function(
        $scope,
        $stateParams,
        $modalInstance,
        $q,
        OpsService,
        $log,
        $state,
        opsId,
        valittu,
        LukioOpetussuunnitelmaService: LukioOpetussuunnitelmaServiceI,
        Notifikaatiot,
        LukioControllerHelpers,
        Kaanna
    ) {
        $scope.$valittu = _.cloneDeep(valittu);
        $scope.$valittu.kieli = LukioControllerHelpers.kielella("");
        $scope.$omaNimi = _.cloneDeep($scope.$valittu.nimi);

        $scope.ok = () => {
            LukioOpetussuunnitelmaService.addAbstraktiOppiaine(
                {
                    tunniste: $scope.$valittu.tunniste,
                    nimi: $scope.$omaNimi
                },
                opsId
            ).then(res => {
                $modalInstance.close(res);
                Notifikaatiot.onnistui("tallennettu-ok");
            }, Notifikaatiot.serverCb);
        };

        $scope.peruuta = $modalInstance.dismiss;
    });
