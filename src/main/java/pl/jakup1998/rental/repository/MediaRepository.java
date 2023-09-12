package pl.jakup1998.rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.jakup1998.rental.model.Media;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

}
