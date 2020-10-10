package spotifyweb

import grails.gorm.transactions.Transactional

@Transactional
class PlaylistTracksService {
    PlaylistService playlistService

    List<Track> getTrackList(Serializable id) {
        def playlist = playlistService.get(id)
        playlist.tracks.removeAll(Collections.singleton(null))
        return playlist.tracks
    }

}
