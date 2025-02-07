package me.blvckbytes.bluemap_markers.stores;

import me.blvckbytes.bluemap_markers.config.MainSection;
import me.blvckbytes.bluemap_markers.model.ImageDownloadFailure;
import me.blvckbytes.bluemap_markers.model.MapImage;
import me.blvckbytes.bluemap_markers.model.OperationResult;
import me.blvckbytes.bluemap_markers.model.OperationStatus;
import me.blvckbytes.bukkitevaluable.ConfigKeeper;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageStore {

  private final Logger logger;
  private final File assetsFolder;
  private final ConfigKeeper<MainSection> config;

  public ImageStore(Logger logger, ConfigKeeper<MainSection> config, Path webRoot) {
    this.logger = logger;
    this.config = config;

    this.assetsFolder = new File(webRoot.toFile(), "assets");

    if (!this.assetsFolder.isDirectory())
      throw new IllegalStateException("Expected " + this.assetsFolder + " to be BlueMap's assets-folder");

    logger.info("Operating on BlueMap's assets-folder at " + assetsFolder);
  }

  public List<MapImage> listImages() {
    var result = new ArrayList<MapImage>();

    try {
      enumerateImagesInto(assetsFolder, result, new ArrayList<>());
    } catch (Exception e) {
      logger.log(Level.SEVERE, "An error occurred while trying to list all images", e);
    }

    return result;
  }

  private void enumerateImagesInto(File container, List<MapImage> output, List<String> pathParts) {
    var containerFiles = container.listFiles();

    if (containerFiles == null) {
      logger.severe("Could not enumerate contents of the assets-folder at " + container);
      return;
    }

    for (var containerFile : containerFiles) {
      var fileName = containerFile.getName();

      if (containerFile.isDirectory()) {
        pathParts.add(fileName);
        enumerateImagesInto(containerFile, output, pathParts);
        pathParts.remove(pathParts.size() - 1);
        continue;
      }

      if (!containerFile.isFile())
        continue;

      if (!isImageFile(fileName))
        continue;

      output.add(createMapImageFromFile(pathParts, containerFile));
    }
  }

  private boolean isImageFile(String fileName) {
    var lastDotIndex = fileName.lastIndexOf('.');

    if (lastDotIndex < 0)
      return false;

    var extension = fileName.substring(lastDotIndex);

    return config.rootSection.images.imageFileExtensions.contains(extension);
  }

  public OperationResult<ImageDownloadFailure> downloadImage(String name, String url, boolean overwrite) {
    try {
      if (containsFileExtension(name))
        return OperationStatus.INTERNAL_ERROR.asResult(ImageDownloadFailure.NAME_CONTAINS_EXTENSION);

      URL targetUrl;

      try {
        targetUrl = new URL(url);
      } catch (Exception e) {
        return OperationStatus.INTERNAL_ERROR.asResult(ImageDownloadFailure.URL_MALFORMED);
      }

      if (!(targetUrl.openConnection() instanceof HttpURLConnection httpConnection))
        return OperationStatus.INTERNAL_ERROR.asResult(ImageDownloadFailure.URL_INACCESSIBLE);

      httpConnection.setConnectTimeout(config.rootSection.images.httpRequestTimeoutSeconds * 1000);
      httpConnection.setRequestMethod("GET");
      httpConnection.setDoOutput(true);

      var responseCode = httpConnection.getResponseCode();

      if (!(responseCode >= 200 && responseCode <= 299))
        return OperationStatus.INTERNAL_ERROR.asResult(ImageDownloadFailure.URL_INACCESSIBLE);

      var contentType = tryFindContentTypeHeader(httpConnection);

      String fileExtension;

      if (contentType == null || (fileExtension = tryDetermineFileExtension(contentType)) == null)
        return OperationStatus.INTERNAL_ERROR.asResult(ImageDownloadFailure.URL_NO_SUPPORTED_IMAGE);

      var targetFile = Paths.get(assetsFolder.getAbsolutePath(), name + fileExtension).toFile();

      if (targetFile.exists() && !overwrite)
        return OperationStatus.DUPLICATE_IDENTIFIER.asResult(null);

      var targetParent = targetFile.getParentFile();

      if (!targetParent.exists()) {
        if (!targetParent.mkdirs())
          throw new IllegalStateException("Could not make required parent-directories of target-file");
      }

      var httpStream = httpConnection.getInputStream();
      var readBuffer = new byte[4096];

      try (
        var fileOutput = new FileOutputStream(targetFile)
      ) {
        int readBytes;
        while ((readBytes = httpStream.read(readBuffer)) > 0)
          fileOutput.write(readBuffer, 0, readBytes);
      }

      return OperationStatus.SUCCESS.asResult(null);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "An error occurred while trying to download the URL \"" + url + "\" into the image named \"" + name + "\"", e);
      return OperationStatus.INTERNAL_ERROR.asResult(null);
    }
  }

  private boolean containsFileExtension(String name) {
    return new File(name).getName().indexOf('.') >= 0;
  }

  private @Nullable String tryDetermineFileExtension(String contentType) {
    // Format: type/subtype;parameter=value
    var parametersDelimiterIndex = contentType.indexOf(';');

    if (parametersDelimiterIndex >= 0)
      contentType = contentType.substring(0, parametersDelimiterIndex);

    for (var typeEntry : config.rootSection.images.imageFileExtensionByMimeType.entrySet()) {
      if (!typeEntry.getKey().equalsIgnoreCase(contentType))
        continue;

      return typeEntry.getValue();
    }

    return null;
  }

  private @Nullable String tryFindContentTypeHeader(HttpURLConnection connection) {
    for (var headerEntry : connection.getHeaderFields().entrySet()) {
      if (!"content-type".equalsIgnoreCase(headerEntry.getKey()))
        continue;

      var headerValues = headerEntry.getValue();

      if (headerValues.isEmpty())
        return null;

      return headerValues.get(headerValues.size() - 1);
    }

    return null;
  }

  public OperationResult<?> deleteRecursive(String path) {
    try {
      var targetFile = Paths.get(assetsFolder.getAbsolutePath(), path).toFile();

      if (deleteRecursively(targetFile, 0) == 0)
        return OperationStatus.IDENTIFIER_NOT_FOUND.asResult();

      return OperationStatus.SUCCESS.asResult();
    } catch (Exception e) {
      logger.log(Level.SEVERE, "An error occurred while trying to recursively delete path \"" + path + "\"", e);
      return OperationStatus.INTERNAL_ERROR.asResult();
    }
  }

  private int deleteRecursively(File folder, int count) {
    if (!folder.isDirectory())
      return count;

    var files = folder.listFiles();

    if (files == null)
      return count;

    for (var file : files) {
      ++count;

      if (file.isDirectory()) {
        count = deleteRecursively(file, count);
        continue;
      }

      if (!file.delete())
        throw new IllegalStateException("Could not delete file " + file);
    }

    if (!folder.delete())
      throw new IllegalStateException("Could not delete folder " + folder);

    return count;
  }

  public OperationResult<?> deleteImage(String name, boolean force) {
    try {
      var targetFile = getImageFileByName(name);

      if (targetFile == null)
        return OperationStatus.IDENTIFIER_NOT_FOUND.asResult();

      if (!force) {
        // TODO: Check for IDENTIFIER_IN_ACTIVE_USE against existing markers and their variables
      }

      if (!targetFile.delete())
        throw new IllegalStateException("Trying to delete the file \"" + targetFile + "\" had no effect!");

      return OperationStatus.SUCCESS.asResult();
    } catch (Exception e) {
      logger.log(Level.SEVERE, "An error occurred while trying to delete the image named \"" + name + "\"", e);
      return OperationStatus.INTERNAL_ERROR.asResult();
    }
  }

  public @Nullable MapImage getImageByName(String name) {
    try {
      var imageFile = getImageFileByName(name);

      if (imageFile == null)
        return null;

      return createMapImageFromFile(List.of(), imageFile);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "An error occurred while trying to get the image named \"" + name + "\"");
      return null;
    }
  }

  private MapImage createMapImageFromFile(List<String> pathParts, File file) {
    var imageName = file.getName();

    if (!pathParts.isEmpty()) {
      for (var i = pathParts.size() - 1; i >= 0; --i)
        imageName = Paths.get(pathParts.get(i), imageName).toString();
    }

    LocalDateTime createdAt = null, updatedAt = null;

    try {
      var attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

      createdAt = attributes.creationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
      updatedAt = attributes.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    } catch (Exception e) {
      long lastModified = file.lastModified();

      if (lastModified > 0)
        updatedAt = Instant.ofEpochMilli(lastModified).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    return new MapImage(
      imageName, file.getAbsolutePath(), file.length(),
      createdAt, updatedAt
    );
  }

  private @Nullable File getImageFileByName(String name) {
    var targetFile = Paths.get(assetsFolder.getAbsolutePath(), name).toFile();

    if (!targetFile.isFile())
      return null;

    if (!isImageFile(targetFile.getName()))
      return null;

    return targetFile;
  }

  public OperationResult<?> renameImage(String oldName, String newName) {
    try {
      var targetFile = getImageFileByName(oldName);

      if (targetFile == null)
        return OperationStatus.IDENTIFIER_NOT_FOUND.asResult();

      var newFile = Paths.get(assetsFolder.getAbsolutePath(), newName).toFile();

      if (newFile.exists())
        return OperationStatus.DUPLICATE_IDENTIFIER.asResult();

      if (!targetFile.renameTo(newFile))
        throw new IllegalStateException("Renaming the file \"" + targetFile + "\" to \"" + newName + "\" had no effect!");

      return OperationStatus.SUCCESS.asResult();
    } catch (Exception e) {
      logger.log(Level.SEVERE, "An error occurred while trying to rename the file \"" + oldName + "\" to \"" + newName + "\"", e);
      return OperationStatus.INTERNAL_ERROR.asResult();
    }
  }
}
