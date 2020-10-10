package spotifyweb

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class AlbumSimplifiedController {

    AlbumSimplifiedService albumSimplifiedService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond albumSimplifiedService.list(params), model:[albumSimplifiedCount: albumSimplifiedService.count()]
    }

    def show(Long id) {
        respond albumSimplifiedService.get(id)
    }

    def create() {
        respond new AlbumSimplified(params)
    }

    def save(AlbumSimplified albumSimplified) {
        if (albumSimplified == null) {
            notFound()
            return
        }

        try {
            albumSimplifiedService.save(albumSimplified)
        } catch (ValidationException e) {
            respond albumSimplified.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'albumSimplified.label', default: 'AlbumSimplified'), albumSimplified.id])
                redirect albumSimplified
            }
            '*' { respond albumSimplified, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond albumSimplifiedService.get(id)
    }

    def update(AlbumSimplified albumSimplified) {
        if (albumSimplified == null) {
            notFound()
            return
        }

        try {
            albumSimplifiedService.save(albumSimplified)
        } catch (ValidationException e) {
            respond albumSimplified.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'albumSimplified.label', default: 'AlbumSimplified'), albumSimplified.id])
                redirect albumSimplified
            }
            '*'{ respond albumSimplified, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        albumSimplifiedService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'albumSimplified.label', default: 'AlbumSimplified'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'albumSimplified.label', default: 'AlbumSimplified'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
