package pl.jakup1998.rental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import pl.jakup1998.rental.model.Media;
import pl.jakup1998.rental.service.MediaService;


@RestController
@RequestMapping("/api/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;
    @GetMapping("/{mediaId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long mediaId) {
        Media media = mediaService.findById(mediaId);
        if (media == null) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = Paths.get(media.getMediaPath());
        Resource resource;
        try {
            resource = new InputStreamResource(Files.newInputStream(filePath));
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMedia(@PathVariable Long id){
        mediaService.deleteMediaById(id);
        return new ResponseEntity<>("User deleted",HttpStatus.OK);
    }
}