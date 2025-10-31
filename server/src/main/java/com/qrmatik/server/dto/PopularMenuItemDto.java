package com.qrmatik.server.dto;

public class PopularMenuItemDto {
    private MenuItemDto item;
    private long count;

    public PopularMenuItemDto() {}

    public PopularMenuItemDto(MenuItemDto item, long count) {
        this.item = item;
        this.count = count;
    }

    public MenuItemDto getItem() { return item; }
    public void setItem(MenuItemDto item) { this.item = item; }

    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}
