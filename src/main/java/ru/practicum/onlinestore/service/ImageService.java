package ru.practicum.onlinestore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.practicum.onlinestore.model.Image;
import ru.practicum.onlinestore.repository.ImageRepository;

import java.io.IOException;

@Service
@Transactional
public class ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Transactional(readOnly = true)
    public byte[] getImage(Long imageId) {
        return imageRepository.getImageBytes(imageId);
    }

    public void upload(MultipartFile file) {
        var image = new Image();

        try {
            image.setData(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        imageRepository.save(image);
    }
}
