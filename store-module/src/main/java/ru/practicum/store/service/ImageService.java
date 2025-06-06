package ru.practicum.store.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.net.MalformedURLException;

@Service
public class ImageService {

    public Mono<byte[]> getImage(String imageId) throws IOException {
        Resource resource =  new ClassPathResource("static/files/" + imageId);

        if (resource.exists()) {
            return Mono.just(resource.getContentAsByteArray()).subscribeOn(Schedulers.boundedElastic());
        } else {
            throw new MalformedURLException("Image not found: " + imageId);
        }
    }

}
