package com.klu.springmvc.dto;

import java.math.BigDecimal;

public class MenuItemDto {
    private Long itemId;
    private String name;
    private String description;
    private BigDecimal price;
    private String availabilityStatus;

    public MenuItemDto() {
    }

    public MenuItemDto(Long itemId, String name, String description, BigDecimal price, String availabilityStatus) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.availabilityStatus = availabilityStatus;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }
}
