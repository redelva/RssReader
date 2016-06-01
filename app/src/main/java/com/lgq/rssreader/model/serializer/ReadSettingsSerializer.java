package com.lgq.rssreader.model.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.lgq.rssreader.core.AppSettings;

import java.lang.reflect.Type;

/**
 * Created by redel on 2016-04-12.
 */
public class ReadSettingsSerializer implements JsonSerializer<AppSettings> {

    @Override
    public JsonElement serialize(AppSettings src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("font_size", src.getFontSize());
        jsonObject.addProperty("line_height", src.getLineHeight());
        jsonObject.addProperty("item_type", src.getItemType().ordinal());

        final JsonObject theme = new JsonObject();
        theme.addProperty("title_background_color", src.getStyle().getTitleBackgroundColor());
        theme.addProperty("title_font_color", src.getStyle().getTitleFontColor());
        theme.addProperty("web_view_background_color", src.getStyle().getWebViewBackgroundColor());
        theme.addProperty("web_view_font_color", src.getStyle().getWebViewFontColor());
        theme.addProperty("name", src.getStyle().getName());

        jsonObject.add("theme", theme);

        return jsonObject;
    }
}
