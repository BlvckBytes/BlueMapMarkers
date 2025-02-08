package me.blvckbytes.bluemap_markers.model;

import de.bluecolored.bluemap.api.markers.Marker;

import java.util.List;

public interface MarkerTemplate {

  String getName();

  List<Marker> build(MapMarker templateDataProvider);

}
