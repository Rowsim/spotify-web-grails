package spotifyweb

import grails.gorm.services.Service

@Service(AlbumSimplified)
interface AlbumSimplifiedService {

    AlbumSimplified get(Serializable id)

    List<AlbumSimplified> list(Map args)

    Long count()

    void delete(Serializable id)

    AlbumSimplified save(AlbumSimplified albumSimplified)

}