package com.lyl.pkuhole.utils;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.lyl.pkuhole.model.TopicType;

public class TopicTypeDeserializer implements JsonDeserializer<TopicType> {

	@Override
	public TopicType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		String typeStr = json.getAsString();
		switch (typeStr) {
		case "text":
			return TopicType.TEXT;
		case "image":
			return TopicType.IMAGE;
		case "audio":
			return TopicType.AUDIO;
		default:
			return TopicType.TEXT;
		}
	}

}
