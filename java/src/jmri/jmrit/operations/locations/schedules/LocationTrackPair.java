package jmri.jmrit.operations.locations.schedules;

import jmri.jmrit.operations.locations.Location;
import jmri.jmrit.operations.locations.Track;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * An Object representing a location and track.
 *
 * @author Daniel Boudreau Copyright (C) 2009
 *
 */
@API(status = MAINTAINED)
public class LocationTrackPair {

    Location _location;
    Track _track;

    public LocationTrackPair(Location location, Track track) {
        _location = location;
        _track = track;
    }

    // for combo boxes
    @Override
    public String toString() {
        return _location.getName() + " (" + _track.getName() + ")";
    }

    public Track getTrack() {
        return _track;
    }

    public Location getLocation() {
        return _location;
    }

}
