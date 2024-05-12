package com.vlkevheniy.carmanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlkevheniy.carmanagement.model.Brand;
import com.vlkevheniy.carmanagement.model.Car;
import com.vlkevheniy.carmanagement.dto.BrandRequestDto;
import com.vlkevheniy.carmanagement.dto.BrandResponseDto;
import com.vlkevheniy.carmanagement.repository.BrandRepository;
import com.vlkevheniy.carmanagement.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
public class BrandControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CarRepository carRepository;


    @Test
    public void testGetAllBrands_thenReturns200AndBrandList() throws Exception {
        mockMvc.perform(get("/api/brand"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].name", notNullValue()))
                .andExpect(jsonPath("$[0].country", notNullValue()));

    }

    @Test
    public void testAddBrand_withValidData_thenCreateBrand() throws Exception {
        BrandRequestDto newBrand = new BrandRequestDto("Lamborghini", "Italy");

        MvcResult result = mockMvc.perform(post("/api/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBrand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Lamborghini"))
                .andExpect(jsonPath("$.country").value("Italy"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        BrandResponseDto responseDto = objectMapper.readValue(responseContent, BrandResponseDto.class);

        brandRepository.deleteById(responseDto.getId());
    }

    @Test
    public void testAddBrand_withDuplicateName_thenBadRequest() throws Exception {

        BrandRequestDto newBrand = new BrandRequestDto("Toyota", "Japan");

        mockMvc.perform(post("/api/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBrand)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("A brand with name Toyota already exists.")));
    }

    @Test
    public void testUpdateBrand_Successful() throws Exception {
        BrandRequestDto newBrand = new BrandRequestDto("Lamborghini", "Italy");

        MvcResult result = mockMvc.perform(post("/api/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBrand)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        BrandResponseDto responseDto = objectMapper.readValue(responseContent, BrandResponseDto.class);

        BrandRequestDto updatedDto = new BrandRequestDto("UpdatedLambo", "UpdatedItaly");

        mockMvc.perform(put("/api/brand/" + responseDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.name").value("UpdatedLambo"))
                .andExpect(jsonPath("$.country").value("UpdatedItaly"));

        brandRepository.deleteById(responseDto.getId());
    }

    @Test
    public void testUpdateBrand_DuplicateName() throws Exception {
        BrandRequestDto brandDto = new BrandRequestDto("Audi", "Germany");

        mockMvc.perform(put("/api/brand/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("A brand with name " + brandDto.getName() + " already exists.")));
    }

    @Test
    public void testUpdateBrand_NotFound() throws Exception {
        BrandRequestDto brandDto = new BrandRequestDto("New Name", "New Country");

        mockMvc.perform(put("/api/brand/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Brand not found with ID: 999")));
    }

    @Test
    @Transactional
    public void testDeleteBrand_Successful() throws Exception {
        Brand brand = new Brand();
        brand.setName("Test Brand");
        brand.setCountry("Test Country");
        brand = brandRepository.save(brand);

        Car car = new Car();
        car.setBrand(brand);
        car.setColor("Black");
        car.setPrice(20000.00);
        car.setModel("Test Model");
        car.setYear(2020);

        carRepository.save(car);

        mockMvc.perform(delete("/api/brand/" + brand.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Brand was deleted successfully"));
    }

    @Test
    public void testDeleteBrand_NotFound() throws Exception {
        mockMvc.perform(delete("/api/brand/99999"))
                .andExpect(status().isNotFound());
    }
}
