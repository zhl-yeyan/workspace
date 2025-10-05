package com.zhl.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;



public class BaseEnumDeserializer extends JsonDeserializer<BaseEnum>{

    @Override
    public BaseEnum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext){
        try{
            String text = jsonParser.getText();
            Field declaredField = jsonParser.getCurrentValue().getClass().getDeclaredField(jsonParser.getCurrentName());
            declaredField.setAccessible(true);
            Class propertyType = BeanUtils.findPropertyType(jsonParser.getCurrentName(), jsonParser.getCurrentValue().getClass());
            return BaseEnum.getEnum(propertyType,text);
        }catch (Exception ex){
            return null;
        }
    }
}
