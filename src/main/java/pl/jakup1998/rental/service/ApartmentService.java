package pl.jakup1998.rental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jakup1998.rental.dto.ApartmentDto;
import pl.jakup1998.rental.dto.ReservationDto;
import pl.jakup1998.rental.exception.NotFoundException;
import pl.jakup1998.rental.exception.ResourceNotFoundException;
import pl.jakup1998.rental.mapper.ApartmentMapper;
import pl.jakup1998.rental.mapper.ReservationMapper;
import pl.jakup1998.rental.model.Apartment;
import pl.jakup1998.rental.model.Media;
import pl.jakup1998.rental.model.Reservation;
import pl.jakup1998.rental.model.User;
import pl.jakup1998.rental.repository.ApartmentRepository;
import pl.jakup1998.rental.repository.ReservationRepository;
import pl.jakup1998.rental.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    private final ApartmentMapper apartmentMapper;
    private final ReservationMapper reservationMapper;

    @Autowired
    public ApartmentService(ApartmentRepository apartmentRepository, UserRepository userRepository, ReservationRepository reservationRepository, ApartmentMapper apartmentMapper, ReservationMapper reservationMapper) {
        this.apartmentRepository = apartmentRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.apartmentMapper = apartmentMapper;
        this.reservationMapper = reservationMapper;
    }

    public List<ApartmentDto> getAllApartments() {
        List<Apartment> apartments = apartmentRepository.findAll();
        return apartmentMapper.convertToDtoList(apartments);
    }

    public ApartmentDto getApartmentById(Long id) {
        Apartment apartment = apartmentRepository.findById(id).orElseThrow(NotFoundException::new);
        return apartmentMapper.convertToDto(apartment);
    }

    public ApartmentDto createApartment(ApartmentDto apartmentDto) {
        Apartment apartment = apartmentMapper.convertToEntity(apartmentDto);
        return apartmentMapper.convertToDto(apartmentRepository.save(apartment));
    }

    public ApartmentDto updateApartment(Long id, ApartmentDto apartmentDto) {
        Apartment apartment = apartmentRepository.findById(id).orElseThrow(() -> new NotFoundException("Apartment not found"));
        apartment.setOwner(userRepository.findById(apartmentDto.getOwnerId()).orElseThrow(() -> new NotFoundException("User not found")));
        apartment.setRent(apartmentDto.getRent());
        apartment.setName(apartmentDto.getName());
        return apartmentMapper.convertToDto(apartmentRepository.save(apartment));
    }

    public void deleteApartment(Long id) {
        apartmentRepository.deleteById(id);
    }

    public List<ApartmentDto> getApartmentsByOwner(User owner) {
        return apartmentMapper.convertToDtoList(apartmentRepository.findByOwner(owner));
    }

    public void addMedia(Long apartmentId, Media mediaPath) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Apartment", "id", apartmentId));

        mediaPath.setApartment(apartment);

        apartment.getMedia().add(mediaPath);
        apartmentRepository.save(apartment);
    }

    public List<ApartmentDto> getAvailableApartments() {

        List<Apartment> allApartments = apartmentRepository.findAll();

        List<Apartment> availableApartments = allApartments.stream()
                .filter(apartment -> isApartmentAvailable(apartment))
                .collect(Collectors.toList());

        return apartmentMapper.convertToDtoList(availableApartments);
    }

    private boolean isApartmentAvailable(Apartment apartment) {
        List<Reservation> reservations = apartment.getReservations();
        return reservations.stream().noneMatch(Reservation::isConfirmed);
    }

    public boolean isApartmentRentable(Long apartmentId, Long userId) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Apartment", "id", apartmentId));
        if (isApartmentAvailable(apartment)) {
            if (apartment.getOwner().getId() != userId) {
                return true;
            }
        }
        return false;
    }

    public boolean isApartmentOwner(Long apartmentId, Long ownerId) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Apartment", "id", apartmentId));

        if (apartment.getOwner().getId() == ownerId) {
            return true;
        }

        return false;
    }

    public List<ReservationDto> getApartmentReservations(Long apartmentId) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Apartment", "id", apartmentId));
        return reservationMapper.convertToDtoList(apartment.getReservations());
    }

    public List<ApartmentDto> findApartmentsByOwner(Long id) {
        List<Apartment> apartments = apartmentRepository.findByOwnerId(id);
        return apartmentMapper.convertToDtoList(apartments);
    }


    public List<ApartmentDto> findRentedApartmentsByOwner(User user) {
        List<Apartment> apartments = apartmentRepository.findAllByReservationsUserAndReservationsConfirmedTrue(user);
        return apartments.stream()
                .map(apartmentMapper::convertToDto)
                .collect(Collectors.toList());
    }

    public boolean isApartmentRented(Long ApartmentId) {
        return false;
    }
}