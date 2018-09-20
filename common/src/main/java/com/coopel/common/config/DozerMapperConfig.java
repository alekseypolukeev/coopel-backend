package com.coopel.common.config;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DozerMapperConfig {

    @Autowired(required = false)
    private List<BeanMappingBuilder> bmbs;

    @Bean
    public Mapper dozerBeanMapper() {
        DozerBeanMapperBuilder builder = DozerBeanMapperBuilder.create();
        if (bmbs != null) {
            for (BeanMappingBuilder bmb : bmbs) {
                builder.withMappingBuilder(bmb);
            }
        }
        return builder.build();
    }

//    new BeanMappingBuilder() {
//        @Override
//        protected void configure() {
//            mapping(VehicleDto.class, Vehicle.class, wildcard(false), oneWay())
//                    .fields("id", "id")
//                    .fields("version", "version")
//                    .fields("name", "name")
//                    .fields("year", "year")
//                    .fields("manufacturer", "manufacturer")
//                    .fields("model", "model")
//                    .fields("licensePlate", "licensePlate")
//                    .fields("fuelType", "fuelType")
//                    .fields("odometerType", "odometerType")
//                    .fields("vin", "vin")
//                    .fields("eldId", "eld.id")
//                    .fields("carrierId", "carrier.id");
//        }
//    }
}

