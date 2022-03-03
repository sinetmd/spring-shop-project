package com.mrn.admin.brand;

import com.mrn.common.entity.Brand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

class BrandServiceTest {

    @InjectMocks
    private BrandService brandService;

    @Mock
    private BrandRepository brandRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckUniqueInNewModeReturnDuplicate() {
        Integer id = null;
        String name = "Acer";
        Brand brand = new Brand(name);

        Mockito.when(brandRepository.findByName(name)).thenReturn(brand);

        String result = brandService.checkUnique(id, name);
        assertThat(result).isEqualTo("Duplicate");
    }

    @Test
    void testCheckUniqueInNewModeReturnOK() {
        Integer id = null;
        String name = "AMD";

        Mockito.when(brandRepository.findByName(name)).thenReturn(null);

        String result = brandService.checkUnique(id, name);
        assertThat(result).isEqualTo("OK");
    }

    @Test
    void testCheckUniqueInEditModeReturnDuplicate() {
        Integer id = 1;
        String name = "Canon";
        Brand brand = new Brand(id, name);

        Mockito.when(brandRepository.findByName(name)).thenReturn(brand);

        String result = brandService.checkUnique(2, "Canon");
        assertThat(result).isEqualTo("Duplicate");
    }

    @Test
    void testCheckUniqueInEditModeReturnOK() {
        Integer id = 1;
        String name = "Acer";
        Brand brand = new Brand(id, name);

        Mockito.when(brandRepository.findByName(name)).thenReturn(brand);

        String result = brandService.checkUnique(2, "Canon");
        assertThat(result).isEqualTo("OK");
    }




}