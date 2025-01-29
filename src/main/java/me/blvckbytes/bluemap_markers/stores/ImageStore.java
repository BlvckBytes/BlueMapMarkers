package me.blvckbytes.bluemap_markers.stores;

import me.blvckbytes.bluemap_markers.model.ImageDownloadFailure;
import me.blvckbytes.bluemap_markers.model.MapImage;
import me.blvckbytes.bluemap_markers.model.OperationResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ImageStore {

  public List<MapImage> listImages() {
    throw new UnsupportedOperationException();
  }

  public OperationResult<ImageDownloadFailure> downloadImage(String name, String url) {
    throw new UnsupportedOperationException();
  }

  public OperationResult<?> deleteImage(String name) {
    throw new UnsupportedOperationException();
  }

  public @Nullable MapImage getByName(String name) {
    throw new UnsupportedOperationException();
  }

  public OperationResult<?> renameImage(String oldName, String newName) {
    throw new UnsupportedOperationException();
  }
}
