package spotifyweb

import grails.converters.JSON
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class TrackController {

    TrackService trackService
    TrackRESTService trackRESTService
    String authKey

    TrackController() {
        Console.println('TrackController INIT')
    }

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond trackService.list(params), model: [trackCount: trackService.count()]
    }

    def playSpotifyTrackFromPlaylist(String playlistId, String spotifyId) {
        authKey = session.getAttribute('authKey')
        def model = [success: trackRESTService.requestSpotifyPlayTrackFromPlaylist(playlistId, spotifyId, authKey)]
        render model as JSON
    }

    def playSpotifyTrack(String spotifyId) {
        authKey = session.getAttribute('authKey')
        def model = [success: trackRESTService.requestSpotifyPlayTrack(spotifyId, authKey)]
        render model as JSON
    }

    def searchSpotifyArtistsTracksAlbums(String query) {
        authKey = session.getAttribute('authKey')
        def albumsArtistsTracksList = trackRESTService.requestSpotifyArtistsTracksAlbums(query, authKey)
        if (albumsArtistsTracksList == false) {
            session.setAttribute('authKey', false)
            redirect controller: "home"
        } else {
            def model =
                    [
                            tracks : albumsArtistsTracksList.tracks,
                            artists: albumsArtistsTracksList.artists,
                            albums : albumsArtistsTracksList.albums
                    ]
            render model as JSON
        }
    }

    def getSpotifyTracksFromAlbum(String albumId) {
        authKey = session.getAttribute('authKey')
        def model = [tracks: trackRESTService.requestSpotifyGetTracksFromAlbum(albumId, authKey)]
        render model as JSON
    }

    def show(Long id) {
        respond trackService.get(id)
    }

    def create() {
        respond new Track(params)
    }

    def save(Track track) {
        if (track == null) {
            notFound()
            return
        }

        try {
            trackService.save(track)
        } catch (ValidationException e) {
            respond track.errors, view: 'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'track.label', default: 'Track'), track.id])
                redirect track
            }
            '*' { respond track, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond trackService.get(id)
    }

    def update(Track track) {
        if (track == null) {
            notFound()
            return
        }

        try {
            trackService.save(track)
        } catch (ValidationException e) {
            respond track.errors, view: 'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'track.label', default: 'Track'), track.id])
                redirect track
            }
            '*' { respond track, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        trackService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'track.label', default: 'Track'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'track.label', default: 'Track'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
