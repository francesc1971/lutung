/**
 *
 */
package com.microtripit.mandrillapp.lutung.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author rschreijer
 * @since Mar 16, 2013
 */
public final class LutungGsonUtils {

	private static final String DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";

	private static final Gson gson = createGson();

	public static Gson getGson() {
		return gson;
	}

	public static Gson createGson() {
		return createGsonBuilder().create();
	}

	public static GsonBuilder createGsonBuilder() {
		return new GsonBuilder()
				.setDateFormat(DATE_FORMAT_STR)
				.registerTypeAdapter(Date.class, new DateDeserializer())
				.registerTypeAdapter(Map.class, new MapSerializer())
                .registerTypeAdapter(MandrillMessage.Recipient.Type.class,
                		new RecipientTypeSerializer());
	}

	public static class DateDeserializer
			implements JsonDeserializer<Date>, JsonSerializer<Date> {

		public final Date deserialize(final JsonElement json,
				final Type typeOfT,
				final JsonDeserializationContext context)
						throws JsonParseException {

			if(!json.isJsonPrimitive()) {
				throw new JsonParseException(
						"Unexpected type for date: " +json.toString());

			}
			try {
				return getNewDefaultDateTimeFormat().parse(json.getAsString());

			} catch(final ParseException e) {
				throw new JsonParseException("Failed to parse date '"
						+json.getAsString()+ "'", e);

			}

		}

		public JsonElement serialize(
				final Date src,
				final Type typeOfSrc,
				final JsonSerializationContext context) {

			return new JsonPrimitive(getNewDefaultDateTimeFormat().format(src));
		}

		private SimpleDateFormat getNewDefaultDateTimeFormat() {
			final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_STR);
			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			return formatter;
		}

	}

	public static class MapSerializer implements JsonSerializer<Map<? extends Object,? extends Object>> {

		public final JsonElement serialize(
				final Map<?, ?> src,
				final Type typeOfSrc,
				final JsonSerializationContext context) {

			Object value;
			final JsonObject json = new JsonObject();
			for(Object key : src.keySet()) {
				value = src.get(key);
				json.add( key.toString(), context.serialize(
						value, value.getClass()) );
			}
			return json;

		}

	}

	public static final class RecipientTypeSerializer
			implements JsonDeserializer<MandrillMessage.Recipient.Type>,
				JsonSerializer<MandrillMessage.Recipient.Type> {

		public final MandrillMessage.Recipient.Type deserialize(
				final JsonElement json,
				final Type typeOfT,
				final JsonDeserializationContext context)
						throws JsonParseException {

			if(!json.isJsonPrimitive()) {
				throw new JsonParseException(
						"Unexpected type for recipient type: " +json.toString());
			}

			return MandrillMessage.Recipient.Type.valueOf(
					json.getAsString().toUpperCase());

		}

		public JsonPrimitive serialize(
				final MandrillMessage.Recipient.Type src,
				final Type typeOfSrc,
				final JsonSerializationContext context) {

			return new JsonPrimitive(src.name().toLowerCase());
		}
	}
}
