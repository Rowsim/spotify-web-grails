package spotifyweb

import grails.gorm.services.Service

@Service(Track)
interface TrackService {

    Track get(Serializable id)

    List<Track> list(Map args)

    Long count()

    void delete(Serializable id)

    Track save(Track track)

}