package com.klu.springmvc.config;

import com.klu.springmvc.model.AvailabilityStatus;
import com.klu.springmvc.model.MenuItem;
import com.klu.springmvc.repository.MenuItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initMenu(MenuItemRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                logger.info("Initializing sample menu items for academic review...");

                List<MenuItem> items = List.of(
                        new MenuItem(null, "Chicken Biryani", "Aromatic basmati rice cooked with spiced chicken and authentic Indian spices", new BigDecimal("12.99"), AvailabilityStatus.AVAILABLE),
                        new MenuItem(null, "Paneer Biryani", "Fragrant basmati rice layered with marinated paneer cubes and herbs", new BigDecimal("10.99"), AvailabilityStatus.AVAILABLE),
                        new MenuItem(null, "Veg Fried Rice", "Wok-tossed rice with fresh seasonal vegetables and soy seasoning", new BigDecimal("8.99"), AvailabilityStatus.AVAILABLE),
                        new MenuItem(null, "Chicken Fried Rice", "Wok-tossed rice with tender chicken morsels and scrambled eggs", new BigDecimal("10.99"), AvailabilityStatus.AVAILABLE),
                        new MenuItem(null, "Masala Dosa", "Crispy fermented crepe stuffed with spiced potato filling, served with sambar & chutney", new BigDecimal("6.99"), AvailabilityStatus.AVAILABLE),
                        new MenuItem(null, "Idly", "Steamed savory rice cakes served with aromatic lentil sambar and coconut chutney", new BigDecimal("4.99"), AvailabilityStatus.AVAILABLE),
                        new MenuItem(null, "Pizza", "Wood-fired artisanal thin crust pizza topped with mozzarella and fresh basil", new BigDecimal("14.99"), AvailabilityStatus.AVAILABLE),
                        new MenuItem(null, "Burger", "Juicy grilled patty with cheddar cheese, lettuce, tomato in toasted brioche", new BigDecimal("7.99"), AvailabilityStatus.UNAVAILABLE)
                );

                repository.saveAll(items);
                logger.info("Sample menu items initialized successfully: {} items created.", items.size());
            }
        };
    }
}
