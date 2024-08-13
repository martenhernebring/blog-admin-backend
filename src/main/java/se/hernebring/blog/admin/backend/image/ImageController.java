package se.hernebring.blog.admin.backend.image;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.hernebring.blog.admin.backend.image.file.FileDTO;

import java.util.List;

@RestController("imageController")
@RequestMapping(value = "/v1/images")
public class ImageController {

  private final ImageService imageService;

  @Autowired
  public ImageController(ImageService imageService) {
    this.imageService = imageService;
  }

  @Operation(summary = "Get saved images list.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Successfully saved the image",
          content = @Content(mediaType = "application/json",
              array = @ArraySchema(
                  schema = @Schema(implementation = FileDTO.class))))
  })
  @GetMapping(value = "")
  public List<FileDTO> getAllUnsorted() {
    return imageService.getAllUnsorted();
  }
}
