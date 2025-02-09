package me.blvckbytes.bluemap_markers.model;

import de.bluecolored.bluemap.api.markers.Marker;

import java.util.List;
import java.util.Map;

public interface MarkerTemplate {

  Map<String, VariableValue<?>> getDefinedVariables();

  String getName();

  List<Marker> build(MapMarker templateDataProvider);

}
