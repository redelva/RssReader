package com.lgq.rssreader.model.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.lgq.rssreader.core.AppSettings;
import com.lgq.rssreader.enums.ItemType;
import com.lgq.rssreader.model.Style;

import java.lang.reflect.Type;

/**
 * Created by redel on 2016-04-12.
 */
public class ReadSettingsDeserializer implements JsonDeserializer<AppSettings> {
    @Override
    public AppSettings deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        AppSettings settings = new AppSettings();

        settings.setFontSize(json.getAsJsonObject().get("font_size").getAsInt());
        settings.setLineHeight(json.getAsJsonObject().get("line_height").getAsInt());
        settings.setItemType(ItemType.values()[json.getAsJsonObject().get("item_type").getAsInt()]);

        Style theme = new Style();

        theme.setTitleBackgroundColor(json.getAsJsonObject().get("theme").getAsJsonObject().get("title_background_color").getAsString());
        theme.setTitleFontColor(json.getAsJsonObject().get("theme").getAsJsonObject().get("title_font_color").getAsString());
        theme.setWebViewBackgroundColor(json.getAsJsonObject().get("theme").getAsJsonObject().get("web_view_background_color").getAsString());
        theme.setWebViewFontColor(json.getAsJsonObject().get("theme").getAsJsonObject().get("web_view_font_color").getAsString());
        theme.setName(json.getAsJsonObject().get("theme").getAsJsonObject().get("name").getAsString());

        settings.setStyle(theme);

        return settings;
    }
}
