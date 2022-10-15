package org.example;

import org.example.business.ProductService;
import org.example.business.ProductServiceImpl;

public class Main {

    public static void main(String[] args) {

        ApplicationContext applicationContext = new ApplicationContext(Main.class);

        ProductService productService = applicationContext.getBean(ProductService.class);
        productService.save();
    }
}
