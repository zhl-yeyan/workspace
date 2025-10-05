package com.zhl.enums;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class BaseEnumSerializer extends JsonSerializer<BaseEnum> {

    @Override
    public void serialize(BaseEnum basicEnum, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(basicEnum.getCode());
    }
}
