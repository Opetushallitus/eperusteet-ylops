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

/* global _ */
'use strict';

ylopsApp
    .service('VersionHelper', function($modal, $state, $stateParams) {

        this.historyView = (data) => {
            $modal.open({
                templateUrl: 'views/common/modals/versiohelper.html',
                controller: 'HistoryViewController',
                resolve: {
                    versions:  () => {
                        return data;
                    }
                }
            }).result.then((re) => {
                let params = _.clone($stateParams);
                params.versio = (re.openOld) ? '/' + re.versio.numero : "";
                $state.go($state.current.name, params);
            });
        };

    })

    .controller('HistoryViewController', ($scope, versions, $modalInstance) => {
        $scope.versions = versions;
        $scope.close = (versio, current) => {
            if (versio) {
                $modalInstance.close({versio: versio, openOld: !current});
            } else {
                $modalInstance.dismiss();
            }
        };
        $scope.paginate = {
            current: 1,
            perPage: 10
        };
    });