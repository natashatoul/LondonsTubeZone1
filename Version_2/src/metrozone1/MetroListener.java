package metrozone1;

import java.util.List;

public interface MetroListener {

    // Required for GUI to work

    void onLinesLoaded(List<String> lines);
    void onStationsLoaded(List<String> stations);
    void onStationsByLineFound(String line, String result);
    void onStationInfoFound(String result);
    void onRouteFound(String result);
    void onClosedTracksLoaded(String result);
    void onDelayedTracksLoaded(String result);
    void onConnectionsLoaded(List<Connection> connections);
    void onConnectionStatusChanged(String result);
    void onDelaySet(String result);
    void onDelayRemoved(String result);
    void onError(String errorMessage);
}
