package spotifyweb

import grails.gorm.services.Service

@Service(Artist)
interface ArtistService {

    Artist get(Serializable id)

    List<Artist> list(Map args)

    Long count()

    void delete(Serializable id)

    Artist save(Artist artist)

}