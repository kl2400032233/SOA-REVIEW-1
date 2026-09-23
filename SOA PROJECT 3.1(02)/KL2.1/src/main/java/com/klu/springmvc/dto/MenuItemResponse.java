package com.klu.springmvc.dto;

import com.klu.springmvc.model.AvailabilityStatus;
import com.klu.springmvc.model.MenuItem;

import java.math.BigDecimal;

public class MenuItemResponse {

    private Long itemId;
    private String name;
    private String description;
    private BigDecimal price;
    private AvailabilityStatus availabilityStatus;

    public MenuItemResponse() {
    }

    public MenuItemResponse(Long itemId, String name, String description, BigDecimal price, AvailabilityStatus availabilityStatus) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.availabilityStatus = availabilityStatus;
    }

    public static MenuItemResponse fromEntity(MenuItem item) {
        return new MenuItemResponse(
                item.getItemId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getAvailabilityStatus()
        );
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

    public AvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }
}
