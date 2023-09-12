package pl.jakup1998.rental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.jakup1998.rental.dto.ApartmentDto;
import pl.jakup1998.rental.dto.ReservationDto;
import pl.jakup1998.rental.model.Media;
import pl.jakup1998.rental.model.User;
import pl.jakup1998.rental.service.ApartmentService;
import pl.jakup1998.rental.service.UserService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/apartments")
public class ApartmentController {
    private final ApartmentService apartmentService;
    @Autowired
    private UserService userService;
    private final String UPLOAD_DIR = "media/";

    @Autowired
    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @GetMapping
    public ResponseEntity<List<ApartmentDto>> getAllApartments(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<ApartmentDto> apartments;
        if (user.hasRole("ROLE_USER")) {
            apartments = apartmentService.getAvailableApartments();
        } else {
            apartments = apartmentService.getAllApartments();
        }
        return ResponseEntity.ok(apartments);
    }
    @GetMapping("/my/rented")
    public ResponseEntity<List<ApartmentDto>> getMyRentedApartments(@AuthenticationPrincipal UserDetails userDetails){
        User user = userService.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<ApartmentDto> apartments = apartmentService.findRentedApartmentsByOwner(user);
        return ResponseEntity.ok(apartments);
    }
    @GetMapping("/my")
    public ResponseEntity<List<ApartmentDto>> getMyApartments(@AuthenticationPrincipal UserDetails userDetails){
        User user = userService.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<ApartmentDto> apartments = apartmentService.findApartmentsByOwner(user.getId());
        return ResponseEntity.ok(apartments);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApartmentDto> getApartmentById(@PathVariable Long id) {
        ApartmentDto apartmentDto = apartmentService.getApartmentById(id);
        return ResponseEntity.ok(apartmentDto);
    }
    @GetMapping("/{id}/reservations")
    public ResponseEntity<List<ReservationDto>> getApartmentReservations(@PathVariable Long id) {
        List<ReservationDto> reservations = apartmentService.getApartmentReservations(id);
        return ResponseEntity.ok(reservations);
    }
    @GetMapping("/{id}/is-rentable")
    public ResponseEntity<Boolean> isApartmentRentable(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername((userDetails.getUsername()));
        return ResponseEntity.ok(apartmentService.isApartmentRentable(id,user.getId()));
    }

    @GetMapping("/{id}/is-owner")
    public ResponseEntity<Boolean> isApartmentOwner(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername((userDetails.getUsername()));
        return ResponseEntity.ok(apartmentService.isApartmentOwner(id,user.getId()));
    }

    @PostMapping
    public ResponseEntity<ApartmentDto> createApartment(@RequestBody ApartmentDto apartmentDto, @AuthenticationPrincipal UserDetails userDetails) {
        String username= userDetails.getUsername();
        User user = userService.findByUsername(username);
        apartmentDto.setOwnerId(user.getId());
        return ResponseEntity.ok(apartmentService.createApartment(apartmentDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApartmentDto> updateApartment(@PathVariable Long id, @RequestBody ApartmentDto apartmentDto) {
        return ResponseEntity.ok(apartmentService.updateApartment(id, apartmentDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApartmentOwn(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        if (apartmentService.isApartmentOwner(id, user.getId())) {
            if(!apartmentService.isApartmentRented(id)){
                apartmentService.deleteApartment(id);
                return new ResponseEntity<>(HttpStatus.OK);
            }else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

        }else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
    @PostMapping("/{id}/media")
    public ResponseEntity<?> uploadImage(@PathVariable Long id, @RequestParam("media") MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType == null ||
                    (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
                return new ResponseEntity<>("Invalid file type. Only images and videos are allowed.", HttpStatus.BAD_REQUEST);
            }

            File directory = new File(UPLOAD_DIR);
            if (!directory.exists() && !directory.mkdir()) {
                return new ResponseEntity<>("Failed to create upload directory", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            Path filePath = Paths.get(UPLOAD_DIR + filename);
            Files.write(filePath, file.getBytes());

            Media media = new Media(filePath.toString());
            apartmentService.addMedia(id, media);

            return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("File upload failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
