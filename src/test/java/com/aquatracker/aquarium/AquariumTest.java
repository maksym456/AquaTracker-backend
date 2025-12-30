package com.aquatracker.aquarium;

import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import java.util.Set;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class AquariumTest {

private static Validator validator;

@BeforeAll
static void setUpValidator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
}
    @Test
    void shouldSetAndGetAllBasicFieldsCorrectly() {
        Aquarium aquarium = new Aquarium();

        aquarium.setName("Moje piękne akwarium");
        aquarium.setVolumeLiters(120);
        aquarium.setTemperatureC(25.5);
        aquarium.setWaterType("sweet"); 
        aquarium.setBiotope("Amazonia");
        aquarium.setPh(7.2);
        aquarium.setHardness(8);
        aquarium.setHardnessDGH(10); 
        aquarium.setDescription("Piękne akwarium roślinne z neonkami");
        aquarium.setCreatedAt(LocalDateTime.of(2025, 12, 29, 12, 0));
        assertThat(aquarium.getName()).isEqualTo("Moje piękne akwarium");
        assertThat(aquarium.getVolumeLiters()).isEqualTo(120);
        assertThat(aquarium.getTemperatureC()).isEqualTo(25.5);
        assertThat(aquarium.getWaterType()).isEqualTo("sweet");
        assertThat(aquarium.getBiotope()).isEqualTo("Amazonia");
        assertThat(aquarium.getPh()).isEqualTo(7.2);
        assertThat(aquarium.getHardness()).isEqualTo(8);
        assertThat(aquarium.getDescription()).isEqualTo("Piękne akwarium roślinne z neonkami");
        assertThat(aquarium.getCreatedAt()).isEqualTo(LocalDateTime.of(2025, 12, 29, 12, 0));
    }

    @Test
    void shouldReturnHardnessDGHWhenSet() {
        Aquarium aquarium = new Aquarium();
        aquarium.setHardnessDGH(12);
        assertThat(aquarium.getHardnessDGH()).isEqualTo(12);
        assertThat(aquarium.getHardness()).isEqualTo(12);
    }

    @Test
    void shouldKeepOriginalHardnessWhenHardnessDGHIsSetLater() {
        Aquarium aquarium = new Aquarium();
        aquarium.setHardness(7);
        aquarium.setHardnessDGH(15);
        assertThat(aquarium.getHardnessDGH()).isEqualTo(15);
        assertThat(aquarium.getHardness()).isEqualTo(7);
    }

    @Test
    void shouldReturnZeroValuesForNewAquarium() {
        Aquarium aquarium = new Aquarium();
        assertThat(aquarium.getVolumeLiters()).isEqualTo(0);
        assertThat(aquarium.getTemperatureC()).isEqualTo(0.0);
        assertThat(aquarium.getPh()).isNull();
        assertThat(aquarium.getHardness()).isNull();
        assertThat(aquarium.getHardnessDGH()).isNull();
        assertThat(aquarium.getName()).isNull();
    }
    @Test
    void shouldFailValidationWhenNameBlank() {
        Aquarium aquarium = new Aquarium();
        aquarium.setVolumeLiters(100);      // poprawne
        aquarium.setTemperatureC(25.0);     // poprawne (powyżej 15.0)
        aquarium.setPh(7.0);                // poprawne (poniżej 9.0)
        Set<ConstraintViolation<Aquarium>> violations = validator.validate(aquarium);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("wymagana");
    }

    @Test
    void shouldFailValidationWhenVolumeTooSmall() {
        Aquarium aquarium = new Aquarium();
        aquarium.setName("Test");          
        aquarium.setTemperatureC(25.0);     
        aquarium.setPh(7.0);                
        aquarium.setVolumeLiters(0);        // zły volume

        Set<ConstraintViolation<Aquarium>> violations = validator.validate(aquarium);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("większa niż 0");
    }

    @Test
    void shouldFailValidationWhenTemperatureTooLow() {
        Aquarium aquarium = new Aquarium();
        aquarium.setName("Test");           
        aquarium.setVolumeLiters(100);      
        aquarium.setPh(7.0);                
        aquarium.setTemperatureC(10.0);     

        Set<ConstraintViolation<Aquarium>> violations = validator.validate(aquarium);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("zbyt niska");
    }

    @Test
    void shouldFailValidationWhenPhTooHigh() {
        Aquarium aquarium = new Aquarium();
        aquarium.setName("Test");          
        aquarium.setVolumeLiters(100);      
        aquarium.setTemperatureC(25.0);    
        aquarium.setPh(10.0);              

        Set<ConstraintViolation<Aquarium>> violations = validator.validate(aquarium);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("zbyt wysokie");
    }

    @Test
    void shouldFailWithMultipleViolations() {
        Aquarium aquarium = new Aquarium();
        aquarium.setVolumeLiters(0);
        aquarium.setTemperatureC(10.0);
        aquarium.setPh(10.0);

        Set<ConstraintViolation<Aquarium>> violations = validator.validate(aquarium);
        assertThat(violations).hasSize(4);  
    }
}