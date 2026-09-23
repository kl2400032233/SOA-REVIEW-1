package com.klu.springmvc.client;

import com.klu.springmvc.dto.MenuItemDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "menu-service")
public interface MenuServiceClient {

    @GetMapping("/api/menu/{id}")
    MenuItemDto getMenuItemById(@PathVariable("id") Long id);
}
