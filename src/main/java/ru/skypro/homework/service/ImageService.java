package ru.skypro.homework.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.exception.ExceptionConstants;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.util.SecurityUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Сервис для работы с изображениями.
 * <p>
 * Обеспечивает сохранение и обновление изображений объявлений и аватаров пользователей
 * в файловой системе. Принимает только изображения форматов JPEG, PNG, GIF или WebP.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ImageService {

    /** Поддерживаемые MIME-типы изображений: JPEG, PNG, GIF, WebP. */
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    private final AdRepository adRepository;

    @Value("${images.dir.path:images}")
    private String imagesDir;

    @Value("${avatars.dir.path}")
    private String avatarsDir;

    /**
     * Возвращает расширение файла из его имени.
     *
     * @param fileName имя файла
     * @return расширение без точки, или пустая строка, если имя файла пустое или не содержит расширения
     */
    private String getExtension(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * Сохраняет изображение объявления в файловую систему.
     * <p>
     * Файл сохраняется в директорию {@code images.dir.path} с именем {@code {id}.{расширение}}.
     * Существующий файл с тем же именем перезаписывается.
     * </p>
     *
     * @param id   ID объявления (используется как имя файла)
     * @param image загружаемый файл изображения
     * @return путь к сохранённому файлу
     * @throws jakarta.validation.ValidationException если формат файла не поддерживается
     * @throws IOException                            если не удалось сохранить файл
     */
    public String saveImage(Integer id, MultipartFile image) throws IOException {
        String contentType = image.getContentType();

        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new ValidationException(ExceptionConstants.INVALID_IMAGE_FORMAT);
        }

        String fileName = id + "." + getExtension(image.getOriginalFilename());

        Path filePath = Path.of(imagesDir, fileName);
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
        Files.write(filePath, image.getBytes());
        return filePath.toString();
    }

    /**
     * Обновляет изображение объявления (только автор или администратор).
     * <p>
     * Удаляет старое изображение, сохраняет новое и обновляет путь в сущности {@link Ad}.
     * </p>
     *
     * @param id          ID объявления
     * @param image        новый файл изображения
     * @param userDetails данные авторизованного пользователя
     * @return содержимое нового изображения в виде массива байт
     * @throws jakarta.persistence.EntityNotFoundException  если объявление не найдено
     * @throws org.springframework.security.access.AccessDeniedException если у пользователя нет прав
     * @throws IOException                                   если не удалось сохранить файл
     */
    public byte[] updateImage(Integer id, MultipartFile image, UserDetails userDetails) throws IOException {
        Ad ad = adRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.AD_NOT_FOUND)) ;

        SecurityUtils.checkModifyPermission(ad.getAuthor(), userDetails);

        String contentType = image.getContentType();

        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new ValidationException(ExceptionConstants.INVALID_IMAGE_FORMAT);
        }

        if (ad.getImage() != null && !ad.getImage().isEmpty()) {
            Files.deleteIfExists(Path.of(ad.getImage()));
        }

        String fileName = id + "." + getExtension(image.getOriginalFilename());

        Path filePath = Path.of(imagesDir, fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, image.getBytes());
        ad.setImage(filePath.toString());
        adRepository.save(ad);
        return Files.readAllBytes(filePath);
    }

    /**
     * Сохраняет аватар пользователя в файловую систему.
     * <p>
     * Файл сохраняется в директорию {@code avatars.dir.path} с именем {@code {id}.{расширение}}.
     * </p>
     *
     * @param id    ID пользователя (используется как имя файла)
     * @param image загружаемый файл аватара
     * @return путь к сохранённому файлу
     * @throws jakarta.validation.ValidationException если формат файла не поддерживается
     * @throws IOException                            если не удалось сохранить файл
     */
    public String userPhotoUser(Integer id, MultipartFile image) throws IOException {
        String contentType = image.getContentType();

        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new ValidationException(ExceptionConstants.INVALID_IMAGE_FORMAT);
        }

        String fileName = id + "." + getExtension(image.getOriginalFilename());

        Path filePath = Path.of(avatarsDir, fileName);
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
        Files.write(filePath, image.getBytes());
        return filePath.toString();
    }
}
