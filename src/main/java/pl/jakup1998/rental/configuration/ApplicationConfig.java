package pl.jakup1998.rental.configuration;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.jakup1998.rental.dto.ApartmentDto;
import pl.jakup1998.rental.dto.PaymentDto;
import pl.jakup1998.rental.dto.ReservationDto;
import pl.jakup1998.rental.dto.UserDto;
import pl.jakup1998.rental.mapper.CustomUserMapper;
import pl.jakup1998.rental.model.Apartment;
import pl.jakup1998.rental.model.Payment;
import pl.jakup1998.rental.model.Reservation;
import pl.jakup1998.rental.model.User;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(Apartment.class, ApartmentDto.class);
        modelMapper.createTypeMap(Reservation.class, ReservationDto.class);
        modelMapper.createTypeMap(User.class, UserDto.class);
        modelMapper.createTypeMap(Payment.class, PaymentDto.class);
        return modelMapper;
    }

    @Bean
    public CustomUserMapper userMapper(ModelMapper modelMapper) {

        return new CustomUserMapper(modelMapper);
    }

}
