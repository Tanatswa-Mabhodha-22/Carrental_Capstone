package za.ac.cput.carrental.controller;

/* CarController.java
     CarController Class
     Author: Malwandla Blessing Mahori (230962963)
     Date 17 July 2026 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.ac.cput.carrental.domain.Car;
import za.ac.cput.carrental.service.CarService;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/car")
public class CarController {
    @Autowired
    private CarService carService;

    @PostMapping
    public Car create(@RequestBody Car car) {
        return this.carService.create(car);
    }

    @GetMapping("/{id}")
    public Car read(@PathVariable String id) {
        return this.carService.read(id);
    }

    @PutMapping
    public Car update(@RequestBody Car car) {
        return this.carService.update(car);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        this.carService.delete(id);
    }

    @GetMapping
    public List<Car> getAll() {
        return this.carService.getAll();
    }

    // ---------- Car photos (admin upload) ----------

    private static final Path IMAGE_DIR = Path.of("uploads", "cars");

    // Which cars have an uploaded photo, with the time it was saved (used to refresh the browser cache).
    @GetMapping("/images")
    public Map<String, Long> images() throws IOException {
        Map<String, Long> result = new HashMap<>();
        if (!Files.isDirectory(IMAGE_DIR)) return result;
        try (var files = Files.list(IMAGE_DIR)) {
            files.filter(p -> p.getFileName().toString().endsWith(".jpg")).forEach(p -> {
                String name = p.getFileName().toString();
                try {
                    result.put(name.substring(0, name.length() - 4), Files.getLastModifiedTime(p).toMillis());
                } catch (IOException ignored) {
                    // skip a file we can't read
                }
            });
        }
        return result;
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<?> uploadImage(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        if (!id.matches("[A-Za-z0-9_-]+") || this.carService.read(id) == null) {
            return ResponseEntity.notFound().build();
        }
        if (file.isEmpty() || file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("Choose an image smaller than 5 MB");
        }
        try {
            byte[] bytes = file.getBytes();
            if (ImageIO.read(new ByteArrayInputStream(bytes)) == null) {
                return ResponseEntity.badRequest().body("That file isn't a valid image");
            }
            Files.createDirectories(IMAGE_DIR);
            Path target = IMAGE_DIR.resolve(id + ".jpg");
            Files.write(target, bytes);
            return ResponseEntity.ok(Map.of("version", Files.getLastModifiedTime(target).toMillis()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Could not save the image");
        }
    }

}