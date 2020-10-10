package spotifyweb

import grails.converters.JSON
import grails.validation.ValidationException

import static org.springframework.http.HttpStatus.*

class PlaylistController {

    PlaylistService playlistService
    PlaylistTracksService playlistTracksService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond playlistService.list(params), model: [playlistCount: playlistService.count()]
    }

    def getTracksForPlaylist(Long id) {
        def tracks = playlistTracksService.getTrackList(id)
        def model = tracks.collect {
            [
                    name      : it.name,
                    spotifyId : it.spotifyId,
                    artists   : it.artists,
                    duration  : it.durationMs,
                    popularity: it.popularity
            ]
        }
        render model as JSON
    }

    def show(Long id) {
        respond playlistService.get(id)
    }

    def create() {
        respond new Playlist(params)
    }

    def save(Playlist playlist) {
        if (playlist == null) {
            notFound()
            return
        }

        try {
            playlistService.save(playlist)
        } catch (ValidationException e) {
            respond playlist.errors, view: 'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'playlist.label', default: 'Playlist'), playlist.id])
                redirect playlist
            }
            '*' { respond playlist, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond playlistService.get(id)
    }

    def update(Playlist playlist) {
        if (playlist == null) {
            notFound()
            return
        }

        try {
            playlistService.save(playlist)
        } catch (ValidationException e) {
            respond playlist.errors, view: 'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'playlist.label', default: 'Playlist'), playlist.id])
                redirect playlist
            }
            '*' { respond playlist, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        playlistService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'playlist.label', default: 'Playlist'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'playlist.label', default: 'Playlist'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
