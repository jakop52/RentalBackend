package pl.jakup1998.rental.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.jakup1998.rental.dto.ApartmentDto;
import pl.jakup1998.rental.model.Apartment;
import pl.jakup1998.rental.model.Reservation;
import pl.jakup1998.rental.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ApartmentMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public ApartmentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ApartmentDto convertToDto(Apartment apartment) {

        return modelMapper.map(apartment, ApartmentDto.class);
    }

    public Apartment convertToEntity(ApartmentDto apartmentDto) {
        return modelMapper.map(apartmentDto, Apartment.class);
    }

    public List<ApartmentDto> convertToDtoList(List<Apartment> reservations) {
        return reservations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ApartmentDto convertToDto(Optional<Apartment> apartment) {
        return modelMapper.map(apartment, ApartmentDto.class);
    }

    public Apartment convertToEntityWithOwnerAndReservations(ApartmentDto apartmentDto, User owner, List<Reservation> reservations){
        Apartment apartment = convertToEntity(apartmentDto);
        apartment.setOwner(owner);
        apartment.setReservations(reservations);
        return apartment;
    }
}
