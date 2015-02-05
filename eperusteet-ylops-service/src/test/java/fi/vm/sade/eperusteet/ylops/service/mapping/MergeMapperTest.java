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
package fi.vm.sade.eperusteet.ylops.service.mapping;

import fi.vm.sade.eperusteet.ylops.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.dto.EntityReference;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.Test;

import static org.junit.Assert.assertTrue;



/**
 *
 * @author jhyoty
 */
public class MergeMapperTest {

    @Getter
    @Setter
    public static class Entity {
        private List<Item> items;

        public Entity() {
        }

        public Entity(List<Item> items) {
            this.items = new ArrayList<>(items);
        }

    }

    @Getter
    @Setter
    public static class Dto {
        private List<ItemDto> items;

        public Dto() {
        }

        public Dto(List<ItemDto> items) {
            this.items = new ArrayList<>(items);
        }

    }

    @Test
    public void testCollectionMapping() {
        DefaultMapperFactory factory = new DefaultMapperFactory.Builder().build();
        factory.registerMapper(new ReferenceableCollectionMergeMapper());
        MapperFacade mapper = factory.getMapperFacade();
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();

        Entity e = new Entity(Arrays.asList(new Item(id1), new Item(id2), new Item(id3)));
        Dto d = new Dto(Arrays.asList(new ItemDto(id1), new ItemDto(id2), new ItemDto(id3), new ItemDto(UUID.randomUUID())));
        List<Item> items = new ArrayList<>(e.getItems());
        mapper.map(d, e);
        assertTrue(e.getItems().size() == 4);

        assertTrue(e.getItems().get(0) == items.get(0));
        assertTrue(e.getItems().get(1) == items.get(1));
        assertTrue(e.getItems().get(2) == items.get(2));

        assertTrue(e.getItems().get(3) != items.get(0));
        assertTrue(e.getItems().get(3) != items.get(1));
        assertTrue(e.getItems().get(3) != items.get(2));

    }

    @Getter
    @Setter
    public static class Item implements ReferenceableEntity {
        private UUID id;

        public Item() {
        }

        public Item(UUID id) {
            this.id = id;
        }


        @Override
        public EntityReference getReference() {
            return new EntityReference(id);
        }

    }

    @Getter
    @Setter
    public static class ItemDto implements ReferenceableDto {
        private UUID id;

        public ItemDto(UUID id) {
            this.id = id;
        }
    }
}
