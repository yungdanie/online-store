package ru.practicum.onlinestore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.practicum.onlinestore.service.ImageService;

import java.io.IOException;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(value = "/{imageId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<byte[]> getImage(@PathVariable String imageId) throws IOException {
        return imageService.getImage(imageId);
    }
}
