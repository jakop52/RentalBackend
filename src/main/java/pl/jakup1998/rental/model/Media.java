package pl.jakup1998.rental.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


@Entity
public class Media {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String mediaPath;

    @ManyToOne
    @JoinColumn(name = "apartment_id", nullable = false)
    @JsonIgnore
    private Apartment apartment;


    public Media(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public Media(String mediaPath, Apartment apartment) {
        this.mediaPath = mediaPath;
        this.apartment = apartment;
    }

    public Media() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }
}
