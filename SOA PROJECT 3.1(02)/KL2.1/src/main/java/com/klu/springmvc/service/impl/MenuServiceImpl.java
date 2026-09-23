package com.klu.springmvc.service.impl;

import com.klu.springmvc.dto.AvailabilityUpdateRequest;
import com.klu.springmvc.dto.MenuItemRequest;
import com.klu.springmvc.dto.MenuItemResponse;
import com.klu.springmvc.model.AvailabilityStatus;
import com.klu.springmvc.model.MenuItem;
import com.klu.springmvc.exception.ResourceNotFoundException;
import com.klu.springmvc.repository.MenuItemRepository;
import com.klu.springmvc.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuServiceImpl implements MenuService {

    private final MenuItemRepository menuItemRepository;

    public MenuServiceImpl(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItems(AvailabilityStatus status, String role) {
        if (status != null) {
            return menuItemRepository.findByAvailabilityStatus(status).stream()
                    .map(MenuItemResponse::fromEntity)
                    .collect(Collectors.toList());
        }

        if ("ADMIN".equalsIgnoreCase(role) || "STAFF".equalsIgnoreCase(role)) {
            return menuItemRepository.findAll().stream()
                    .map(MenuItemResponse::fromEntity)
                    .collect(Collectors.toList());
        }

        return menuItemRepository.findByAvailabilityStatus(AvailabilityStatus.AVAILABLE).stream()
                .map(MenuItemResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemResponse getMenuItemById(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
        return MenuItemResponse.fromEntity(item);
    }

    @Override
    public MenuItemResponse createMenuItem(MenuItemRequest request) {
        MenuItem item = new MenuItem();
        item.setName(request.getName().trim());
        item.setDescription(request.getDescription().trim());
        item.setPrice(request.getPrice());
        item.setAvailabilityStatus(request.getAvailabilityStatus());

        MenuItem saved = menuItemRepository.save(item);
        return MenuItemResponse.fromEntity(saved);
    }

    @Override
    public MenuItemResponse updateMenuItem(Long id, MenuItemRequest request) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        item.setName(request.getName().trim());
        item.setDescription(request.getDescription().trim());
        item.setPrice(request.getPrice());
        item.setAvailabilityStatus(request.getAvailabilityStatus());

        MenuItem updated = menuItemRepository.save(item);
        return MenuItemResponse.fromEntity(updated);
    }

    @Override
    public void deleteMenuItem(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
        menuItemRepository.delete(item);
    }

    @Override
    public MenuItemResponse updateAvailability(Long id, AvailabilityUpdateRequest request) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        item.setAvailabilityStatus(request.getAvailabilityStatus());
        MenuItem updated = menuItemRepository.save(item);
        return MenuItemResponse.fromEntity(updated);
    }
}
