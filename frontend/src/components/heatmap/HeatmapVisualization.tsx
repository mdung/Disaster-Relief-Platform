import React, { useEffect, useRef, useState } from 'react';
import maplibregl from 'maplibre-gl';
import { HeatmapService, HeatmapData, HeatmapLayer } from '../../services/heatmapService';
import { MapComponent } from '../map/MapComponent';

interface HeatmapVisualizationProps {
  center: [number, number];
  zoom: number;
  heatmapTypes: string[];
  showHeatmap: boolean;
  showPoints: boolean;
  selectedHeatmapType?: string;
  onHeatmapTypeSelect?: (heatmapType: string) => void;
  onPointClick?: (point: HeatmapData) => void;
}

export const HeatmapVisualization: React.FC<HeatmapVisualizationProps> = ({
  center,
  zoom,
  heatmapTypes,
  showHeatmap,
  showPoints,
  selectedHeatmapType,
  onHeatmapTypeSelect,
  onPointClick
}) => {
  const mapRef = useRef<maplibregl.Map | null>(null);
  const [heatmapData, setHeatmapData] = useState<HeatmapData[]>([]);
  const [heatmapLayers, setHeatmapLayers] = useState<HeatmapLayer[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (mapRef.current) {
      loadHeatmapData();
    }
  }, [center, zoom, heatmapTypes, selectedHeatmapType]);

  const loadHeatmapData = async () => {
    if (!mapRef.current) return;

    setLoading(true);
    setError(null);

    try {
      const bounds = mapRef.current.getBounds();
      const minLon = bounds.getWest();
      const minLat = bounds.getSouth();
      const maxLon = bounds.getEast();
      const maxLat = bounds.getNorth();

      // Load heatmap data for all types
      const dataPromises = heatmapTypes.map(type => 
        HeatmapService.getHeatmapDataByTypeAndBounds(type, minLon, minLat, maxLon, maxLat)
      );
      
      const dataResults = await Promise.all(dataPromises);
      const allData = dataResults.flat();
      setHeatmapData(allData);

      // Load heatmap layers
      const layers = await HeatmapService.getHeatmapLayersInBounds(minLon, minLat, maxLon, maxLat);
      setHeatmapLayers(layers);

      // Update map layers
      updateMapLayers(allData, layers);

    } catch (err) {
      setError('Failed to load heatmap data');
      console.error('Heatmap data loading error:', err);
    } finally {
      setLoading(false);
    }
  };

  const updateMapLayers = (data: HeatmapData[], layers: HeatmapLayer[]) => {
    if (!mapRef.current) return;

    // Remove existing heatmap layers
    heatmapTypes.forEach(type => {
      const layerId = `heatmap-${type.toLowerCase()}`;
      const sourceId = `heatmap-${type.toLowerCase()}-source`;
      
      if (mapRef.current?.getLayer(layerId)) {
        mapRef.current.removeLayer(layerId);
      }
      if (mapRef.current?.getSource(sourceId)) {
        mapRef.current.removeSource(sourceId);
      }
    });

    // Add heatmap sources and layers
    heatmapTypes.forEach(type => {
      const typeData = data.filter(point => point.heatmapType === type);
      if (typeData.length === 0) return;

      const sourceId = `heatmap-${type.toLowerCase()}-source`;
      const layerId = `heatmap-${type.toLowerCase()}`;
      const circleLayerId = `circles-${type.toLowerCase()}`;

      // Add source
      mapRef.current!.addSource(sourceId, HeatmapService.generateHeatmapSource(typeData));

      // Add heatmap layer
      if (showHeatmap) {
        mapRef.current!.addLayer(HeatmapService.generateHeatmapLayer(type));
      }

      // Add circle layer for individual points
      if (showPoints) {
        mapRef.current!.addLayer(HeatmapService.generateCircleLayer(type));
      }
    });
  };

  const handleMapLoad = (map: maplibregl.Map) => {
    mapRef.current = map;
    loadHeatmapData();
  };

  const handleMapClick = (event: maplibregl.MapMouseEvent) => {
    if (!mapRef.current || !onPointClick) return;

    const features = mapRef.current.queryRenderedFeatures(event.point, {
      layers: heatmapTypes.map(type => `circles-${type.toLowerCase()}`)
    });

    if (features.length > 0) {
      const feature = features[0];
      const properties = feature.properties;
      
      // Find the corresponding heatmap data point
      const point = heatmapData.find(p => 
        Math.abs(p.longitude - feature.geometry.coordinates[0]) < 0.0001 &&
        Math.abs(p.latitude - feature.geometry.coordinates[1]) < 0.0001
      );
      
      if (point) {
        onPointClick(point);
      }
    }
  };

  const getHeatmapTypeStats = (heatmapType: string) => {
    const typeData = heatmapData.filter(point => point.heatmapType === heatmapType);
    if (typeData.length === 0) return null;

    const intensities = typeData.map(point => point.intensity);
    const avgIntensity = intensities.reduce((sum, intensity) => sum + intensity, 0) / intensities.length;
    const maxIntensity = Math.max(...intensities);
    const minIntensity = Math.min(...intensities);

    return {
      pointCount: typeData.length,
      avgIntensity,
      maxIntensity,
      minIntensity
    };
  };

  return (
    <div className="relative w-full h-full">
      <MapComponent
        center={center}
        zoom={zoom}
        onMapLoad={handleMapLoad}
        onMapClick={handleMapClick}
        className="w-full h-full"
      />
      
      {loading && (
        <div className="absolute top-4 left-4 bg-white p-2 rounded shadow">
          <div className="flex items-center space-x-2">
            <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
            <span className="text-sm">Loading heatmap data...</span>
          </div>
        </div>
      )}

      {error && (
        <div className="absolute top-4 right-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}

      {/* Heatmap Controls */}
      <div className="absolute bottom-4 left-4 bg-white p-4 rounded shadow max-w-sm">
        <h3 className="font-semibold mb-3">Heatmap Controls</h3>
        
        <div className="space-y-3">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Heatmap Types
            </label>
            <div className="space-y-2 max-h-40 overflow-y-auto">
              {heatmapTypes.map(type => {
                const stats = getHeatmapTypeStats(type);
                const isSelected = selectedHeatmapType === type;
                
                return (
                  <div
                    key={type}
                    className={`flex items-center justify-between p-2 rounded cursor-pointer transition-colors ${
                      isSelected ? 'bg-blue-100 border-blue-500' : 'bg-gray-50 border-gray-200'
                    } border`}
                    onClick={() => onHeatmapTypeSelect?.(type)}
                  >
                    <div className="flex items-center space-x-2">
                      <span className="text-lg">
                        {HeatmapService.getHeatmapTypeIcon(type)}
                      </span>
                      <span className="text-sm font-medium">
                        {HeatmapService.getHeatmapTypeDisplayName(type)}
                      </span>
                    </div>
                    {stats && (
                      <div className="text-xs text-gray-500">
                        {stats.pointCount} pts
                      </div>
                    )}
                  </div>
                );
              })}
            </div>
          </div>

          <div className="flex space-x-2">
            <label className="flex items-center">
              <input
                type="checkbox"
                checked={showHeatmap}
                onChange={() => {
                  // This would be handled by parent component
                }}
                className="mr-2"
              />
              <span className="text-sm">Heatmap</span>
            </label>
            <label className="flex items-center">
              <input
                type="checkbox"
                checked={showPoints}
                onChange={() => {
                  // This would be handled by parent component
                }}
                className="mr-2"
              />
              <span className="text-sm">Points</span>
            </label>
          </div>
        </div>
      </div>

      {/* Heatmap Statistics */}
      <div className="absolute top-4 right-4 bg-white p-4 rounded shadow max-w-sm">
        <h3 className="font-semibold mb-3">Heatmap Statistics</h3>
        
        <div className="space-y-2">
          {heatmapTypes.map(type => {
            const stats = getHeatmapTypeStats(type);
            if (!stats) return null;

            return (
              <div key={type} className="border-b border-gray-200 pb-2 last:border-b-0">
                <div className="flex items-center justify-between mb-1">
                  <span className="text-sm font-medium">
                    {HeatmapService.getHeatmapTypeDisplayName(type)}
                  </span>
                  <span className="text-xs text-gray-500">
                    {stats.pointCount} points
                  </span>
                </div>
                <div className="text-xs text-gray-600">
                  <div>Avg: {HeatmapService.formatIntensity(stats.avgIntensity)}</div>
                  <div>Range: {HeatmapService.formatIntensity(stats.minIntensity)} - {HeatmapService.formatIntensity(stats.maxIntensity)}</div>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Legend */}
      <div className="absolute bottom-4 right-4 bg-white p-4 rounded shadow">
        <h3 className="font-semibold mb-2">Intensity Legend</h3>
        <div className="flex items-center space-x-2">
          <div className="w-4 h-4 bg-blue-500 rounded"></div>
          <span className="text-xs">Low</span>
          <div className="w-4 h-4 bg-green-500 rounded"></div>
          <span className="text-xs">Medium</span>
          <div className="w-4 h-4 bg-yellow-500 rounded"></div>
          <span className="text-xs">High</span>
          <div className="w-4 h-4 bg-red-500 rounded"></div>
          <span className="text-xs">Critical</span>
        </div>
      </div>
    </div>
  );
};



