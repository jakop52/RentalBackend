package pl.jakup1998.rental.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.jakup1998.rental.dto.ReservationDto;
import pl.jakup1998.rental.model.Reservation;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservationMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public ReservationMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ReservationDto convertToDto(Reservation reservation) {
        return modelMapper.map(reservation, ReservationDto.class);
    }

    public Reservation convertToEntity(ReservationDto reservationDto) {
        return modelMapper.map(reservationDto, Reservation.class);
    }

    public List<ReservationDto> convertToDtoList(List<Reservation> reservations) {
        return reservations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
