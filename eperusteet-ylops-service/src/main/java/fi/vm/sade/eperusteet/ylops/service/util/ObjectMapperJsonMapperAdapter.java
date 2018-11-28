/*
 *  Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 *  This program is free software: Licensed under the EUPL, Version 1.1 or - as
 *  soon as they will be approved by the European Commission - subsequent versions
 *  of the EUPL (the "Licence");
 *
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  European Union Public Licence for more details.
 */

package fi.vm.sade.eperusteet.ylops.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Optional;

/**
 * User: tommiratamaa
 * Date: 16.11.2015
 * Time: 15.31
 */
public class ObjectMapperJsonMapperAdapter implements JsonMapper {
    private final ObjectMapper mapper;

    protected ObjectMapperJsonMapperAdapter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public static ObjectMapperJsonMapperAdapter of(ObjectMapper mapper) {
        return new ObjectMapperJsonMapperAdapter(mapper);
    }

    @Override
    public <T> T deserialize(Class<T> t, Reader from) throws IOException {
        return mapper.readerFor(t).readValue(from);
    }

    @Override
    public <T> T deserialize(Class<T> t, String from) throws IOException {
        return mapper.readerFor(t).readValue(from);
    }

    @Override
    public <T> void serialize(T obj, Writer to) throws IOException {
        mapper.writer().writeValue(to, obj);
    }

    @Override
    public <T> Optional<T> unwrap(Class<T> clzz) {
        if (clzz.isAssignableFrom(ObjectMapper.class)) {
            return Optional.of((T) mapper);
        }
        return Optional.empty();
    }

    @Override
    public Optional<MappingJackson2HttpMessageConverter> messageConverter() {
        return Optional.of(new MappingJackson2HttpMessageConverter(mapper));
    }

    @Override
    public <T> String serialize(T obj) throws IOException {
        return mapper.writer().writeValueAsString(obj);
    }
}
