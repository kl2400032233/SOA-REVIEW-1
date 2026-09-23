package com.klu.springmvc.service;

import com.klu.springmvc.dto.AvailabilityUpdateRequest;
import com.klu.springmvc.dto.MenuItemRequest;
import com.klu.springmvc.dto.MenuItemResponse;
import com.klu.springmvc.model.AvailabilityStatus;

import java.util.List;

public interface MenuService {
    List<MenuItemResponse> getMenuItems(AvailabilityStatus status, String role);
    MenuItemResponse getMenuItemById(Long id);
    MenuItemResponse createMenuItem(MenuItemRequest request);
    MenuItemResponse updateMenuItem(Long id, MenuItemRequest request);
    void deleteMenuItem(Long id);
    MenuItemResponse updateAvailability(Long id, AvailabilityUpdateRequest request);
}
