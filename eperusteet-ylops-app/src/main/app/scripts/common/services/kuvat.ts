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
    .service("EpImageService", function($q, OpsService, OpsinKuvat, Upload, YlopsResources) {
        this.getAll = function() {
            return OpsinKuvat.query({ opsId: OpsService.getId() }).$promise;
        };

        this.save = function(image) {
            let deferred = $q.defer();
            const url = (YlopsResources.OPS + "/kuvat").replace(":opsId", "" + OpsService.getId());

            Upload.upload({
                url: url,
                file: image,
                fields: {
                    nimi: image.name,
                    width: image.width,
                    height: image.height
                }
            })
                .success(function(data) {
                    deferred.resolve(data);
                })
                .error(function(data) {
                    deferred.reject(data);
                });
            return deferred.promise;
        };

        this.getUrl = function(image) {
            return (YlopsResources.OPS + "/kuvat").replace(":opsId", "" + OpsService.getId()) + "/" + image.id;
        };
    })
    .controller("EpImagePluginController", function($scope, EpImageService, Kaanna, Algoritmit, $timeout) {
        $scope.service = EpImageService;
        $scope.filtered = [];
        $scope.images = [];
        $scope.showPreview = false;
        $scope.model = {
            files: [],
            rejected: [],
            chosen: null
        };
        $scope.scaleError = false;

        $scope.$watch("model.files[0]", function() {
            if (_.isArray($scope.model.files) && $scope.model.files.length > 0) {
                $scope.showPreview = true;
                getDimensions();
            }
        });

        $scope.widthChange = function(img) {
            $scope.scaleError = false;
            let tmp = img.width / img.originalWidth;
            img.height = Math.round(tmp * img.originalHeight);
        };

        $scope.heightChange = function(img) {
            $scope.scaleError = false;
            let tmp = img.height / img.originalHeight;
            img.width = Math.round(tmp * img.originalWidth);
        };

        function getDimensions() {
            let fr = new FileReader();
            fr.onload = function() {
                // file is loaded
                let img = new Image();
                img.onload = function() {
                    // image is loaded; sizes are available
                    $scope.model.files[0].width = img.width;
                    $scope.model.files[0].height = img.height;
                    $scope.model.files[0].originalWidth = img.width;
                    $scope.model.files[0].originalHeight = img.height;
                    $scope.$apply();
                };
                img.src = fr.result; // is the data URL because called with readAsDataURL
            };
            fr.readAsDataURL($scope.model.files[0]);
        }

        $scope.$watch("model.chosen", function() {
            $scope.showPreview = false;
        });

        $scope.rescaleImg = function() {
            $scope.service.update($scope.model.chosen);
        };

        let callback = angular.noop;
        let setDeferred = null;

        function setChosenValue(value) {
            function getMeta(url) {
                let img = new Image();
                img.onload = function() {
                    const el: any = this;
                    $scope.model.chosen.width = el.width;
                    $scope.model.chosen.height = el.height;
                    $scope.model.chosen.originalWidth = el.width;
                    $scope.model.chosen.originalHeight = el.height;
                    $scope.model.chosen.alt = el.alt;
                };
                img.src = url;
            }

            let found = _.find($scope.images, function(image) {
                return image.id === value;
            });

            let imgurl = EpImageService.getUrl(found);
            getMeta(imgurl);

            $scope.model.chosen = found || null;
        }

        function doSort(items) {
            return _.sortBy(items, function(item) {
                return Kaanna.kaanna(item.nimi).toLowerCase();
            });
        }

        $scope.urlForImage = function(image) {
            return $scope.service.getUrl(image);
        };

        $scope.init = function() {
            $scope.service.getAll().then(function(res) {
                $scope.images = res;
                $scope.filtered = doSort(res);
                if (setDeferred) {
                    setChosenValue(_.cloneDeep(setDeferred));
                    setDeferred = null;
                }
            });
        };

        $scope.filterImages = function(value) {
            $scope.filtered = _.filter(doSort($scope.images), function(item) {
                return Algoritmit.match(value, item.nimi);
            });
        };

        // data from angular model to plugin
        $scope.registerListener = function(cb) {
            callback = cb;
        };
        $scope.$watch("model.chosen", function(value) {
            callback(value);
        });

        // data from plugin to angular model
        $scope.setValue = function(value) {
            $scope.$apply(function() {
                if (_.isEmpty($scope.images)) {
                    setDeferred = value;
                } else {
                    setChosenValue(value);
                }
            });
        };

        $scope.closeMessage = function() {
            $scope.message = null;
        };

        $scope.saveNew = function() {
            let image = $scope.model.files[0];
            if (!(image.width > 0 && image.height > 0)) {
                $scope.scaleError = "epimage-plugin-scale-invalid";
                return;
            }

            $scope.service.save(image).then(
                function(res) {
                    $scope.message = "epimage-plugin-tallennettu";
                    $scope.model.files = [];
                    $timeout(function() {
                        $scope.closeMessage();
                    }, 8000);
                    setDeferred = _.clone(res);
                    $scope.init();
                },
                function(res) {
                    $scope.message = res.syy || "epimage-plugin-tallennusvirhe";
                    $scope.model.files = [];
                    $timeout(function() {
                        $scope.closeMessage();
                    }, 8000);
                }
            );
        };
    })
    .filter("kuvalinkit", (EpImageService) => {
        return text => {
            if (_.isEmpty(text)) {
                return text;
            }

            const tmp = angular.element("<div>" + text + "</div>");
            tmp.find("img[data-uid]").each(function() {
                let el = angular.element(this);

                var url = EpImageService.getUrl({ id: el.attr("data-uid") });
                if (el.attr("src") !== url) {
                    el.attr("src", EpImageService.getUrl({ id: el.attr("data-uid") }));
                }

                el.wrap("<figure></figure>");
                if (el.attr("alt")) {
                    el.parent().append("<figcaption style=\"text-align: center;\">" + el.attr("alt") + "</figcaption>");
                }
            });

            return tmp.html();
        };
    });
