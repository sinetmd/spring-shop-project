package com.mrn.admin.brand;

import com.mrn.common.entity.Brand;
import com.mrn.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class BrandRepositoryTest {

    @Autowired
    private BrandRepository brandRepository;

    @Test
    void testCreateBrand1() {
        Category laptops = new Category(6);
        Brand acer = new Brand("Acer");

        acer.getCategories().add(laptops);

        Brand savedBrand = brandRepository.save(acer);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);

    }

    @Test
    void testCreateBrand2() {
        Category cellphones = new Category(6);
        Category tablets = new Category(7);

        Brand apple = new Brand("Apple");
        apple.getCategories().addAll(List.of(cellphones, tablets));

        Brand savedBrand = brandRepository.save(apple);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);

    }

    @Test
    void testCreateBrand3() {
        Brand samsung = new Brand("Samsung");

        samsung.getCategories().add(new Category(29)); // category memory
        samsung.getCategories().add(new Category(24)); // category internal hard drive

        Brand savedBrand = brandRepository.save(samsung);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);
    }

    @Test
    void testFindAll() {
        Iterable<Brand> brands = brandRepository.findAll();
        brands.forEach(System.out::println);

        assertThat(brands).isNotEmpty();
    }

    @Test
    void testGetById() {
        final Integer id = 1;
        Brand brand = brandRepository.findById(id).get();

        assertThat(brand.getName()).isEqualTo("Acer");
    }

    @Test
    void testUpdateName() {
        final String brandName = "Samsung Electronics";
        Brand samsung = brandRepository.findById(3).get();

        samsung.setName(brandName);

        Brand savedBrand = brandRepository.save(samsung);

        assertThat(savedBrand.getName()).isEqualTo(brandName);

    }

    @Test
    void testDelete() {
        Integer id = 2;
        brandRepository.deleteById(id);

        Optional<Brand> result = brandRepository.findById(id);

        assertThat(result.isEmpty());
    }
}