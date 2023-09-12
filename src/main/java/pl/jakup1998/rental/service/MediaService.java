package pl.jakup1998.rental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jakup1998.rental.model.Media;
import pl.jakup1998.rental.repository.MediaRepository;

@Service
public class MediaService {

    @Autowired
    private MediaRepository mediaRepository;

    public Media findById(Long mediaId) {
        return mediaRepository.findById(mediaId).orElse(null);
    }

    public void deleteMediaById(Long id) {
        mediaRepository.deleteById(id);
    }
}
