package org.example.business;

import org.example.Component;
import org.example.Transactional;

@Component
public class ProductServiceImpl implements ProductService {


    @Override
    @Transactional
    public void save() {
        System.out.println("SAVE PRODUCT");
    }
}
