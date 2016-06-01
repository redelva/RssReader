package com.lgq.rssreader.core;

import com.lgq.rssreader.enums.ItemType;
import com.lgq.rssreader.model.Style;

/**
 * Created by redel on 2016-04-09.
 */
public class AppSettings {
    private int font_size = 14;
    private int line_height = 100;
    private Style style = Style.White;
    private ItemType itemType = ItemType.Small;

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public int getLineHeight() {
        return line_height;
    }

    public void setLineHeight(int line_height) {
        this.line_height = line_height;
    }

    public int getFontSize() {
        return font_size;
    }

    public void setFontSize(int font_size) {
        this.font_size = font_size;
    }
}
