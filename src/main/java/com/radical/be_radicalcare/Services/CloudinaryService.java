package com.radical.be_radicalcare.Services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService() {
        Dotenv dotenv = Dotenv.load();

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", dotenv.get("CLOUDINARY_CLOUD_NAME"),
                "api_key", dotenv.get("CLOUDINARY_API_KEY"),
                "api_secret", dotenv.get("CLOUDINARY_API_SECRET")));
    }

    public Map upload(MultipartFile file) throws IOException {
        Map uploadResult;
        try {
            uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));
        } catch (IOException e) {
            throw new IOException("Could not upload file to Cloudinary: " + e.getMessage());
        }
        return uploadResult;
    }

    public void delete(String imageUrl) {
        String publicId = extractPublicId(imageUrl);
        try {
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String extractPublicId(String url) {
        return url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.'));
    }

}
