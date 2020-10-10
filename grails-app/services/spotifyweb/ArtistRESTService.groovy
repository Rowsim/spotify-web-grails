package spotifyweb

import grails.gorm.transactions.Transactional
import grails.plugins.rest.client.RestBuilder
import org.springframework.http.HttpStatus

import java.util.stream.Collectors

@Transactional
class ArtistRESTService {

    RestBuilder rest

    ArtistRESTService() {
        rest = new RestBuilder()
    }

    def getArtistTopTracksAndAlbumsAndRelated(artistId, authKey) {
        def resultMap = new HashMap()
        resultMap.put('tracks', requestArtistTopTracks(artistId, authKey))
        resultMap.put('albums', requestArtistAlbums(artistId, authKey))
        resultMap.put('related', requestRelatedArtists(artistId, authKey))
        return resultMap
    }

    private List<Track> requestArtistTopTracks(artistId, authKey) {
        try {
            def response = rest.get('https://api.spotify.com/v1/artists/' + artistId + '/top-tracks?country=GB') {
                header('Authorization', 'Bearer ' + authKey)
            }
            if (response.statusCode == HttpStatus.OK) {
                Console.println('https://api.spotify.com/v1/artists/' + artistId + 'top-tracks?country=GB in ArtistRESTService SUCCESS')

                def responseJson = response.getJson()
                def tracksJson = responseJson.getAt('tracks')
                List<Track> tracks = new ArrayList<>()
                tracksJson.eachWithIndex { key, value ->
                    def name = key.getAt('name')
                    if (name != null) {
                        Track track = new Track(spotifyId: key.getAt('id'),
                                name: key.getAt('name'),
                                artists: key.getAt('artists').getAt('name') as ArrayList<String>,
                                durationMs: key.getAt('duration_ms') as Integer,
                                popularity: key.getAt('popularity') as Integer)
                        tracks.add(track)
                    }
                }
                tracks.removeAll(Collections.singleton(null))
                return tracks
            } else {
                Console.println('https://api.spotify.com/v1/artists/' + artistId + 'top-tracks?country=GB in ArtistRESTService FAILED')
            }
        } catch (Exception e) {
            Console.println('EXCEPTION requestArtistTopTracks in ArtistRESTService.. \n' + e.message)
        }
        return null
    }

    private List<AlbumSimplified> requestArtistAlbums(artistId, authKey) {
        int albumLimit = 15
        try {
            def response = rest.get('https://api.spotify.com/v1/artists/' + artistId + '/albums?limit=' + albumLimit) {
                header('Authorization', 'Bearer ' + authKey)
            }
            if (response.statusCode == HttpStatus.OK) {
                Console.println('https://api.spotify.com/v1/artists/' + artistId + '/albums?limit=' + albumLimit + 'in ArtistRESTService SUCCESS')

                def albumJson = response.getJson().getAt('items')
                List<AlbumSimplified> albums = new ArrayList<>()
                albumJson.eachWithIndex { key, value ->
                    def name = key.getAt('name')
                    if (name != null && !checkIfNameAlreadyExistsInList(name, albums)) {
                        AlbumSimplified album = new AlbumSimplified(spotifyAlbumId: key.getAt('id'),
                                name: name,
                                imageUrl: getImageUrl(0, key))
                        albums.add(album)
                    }
                }
                albums.removeAll(Collections.singleton(null))
                return albums
            } else {
                Console.println('https://api.spotify.com/v1/artists/' + artistId + '/albums?limit=' + albumLimit + 'in ArtistRESTService FAILED')
            }
        } catch (Exception e) {
            Console.println('EXCEPTION requestArtistAlbums in ArtistRESTService.. \n' + e.message)
        }
        return null
    }

    boolean checkIfNameAlreadyExistsInList(name, list) {
        boolean result = false
        list.eachWithIndex { key, value ->
            if (key.getAt('name') == name)
            {
                result = true //this would work better in a normal for loop with a break
            }
        }
        return result
    }

    private List<Artist> requestRelatedArtists(artistId, authKey) {
        try {
            def response = rest.get('https://api.spotify.com/v1/artists/' + artistId + '/related-artists') {
                header('Authorization', 'Bearer ' + authKey)
            }
            if (response.statusCode == HttpStatus.OK) {
                Console.println('https://api.spotify.com/v1/artists/' + artistId + '/related-artists in ArtistRESTService SUCCESS')

                def artistsJson = response.getJson().getAt('artists')
                List<Artist> artists = new ArrayList<>()
                artistsJson.eachWithIndex { key, value ->
                    def name = key.getAt('name')
                    if (name != null) {
                        Artist artist = new Artist(spotifyArtistId: key.getAt('id'),
                                name: key.getAt('name'),
                                imageUrl: getImageUrl(0, key),
                                followers: key.getAt('followers').getAt('total') as Integer,
                                popularity: key.getAt('popularity') as Integer,
                                genres: key.getAt('genres'))
                        artists.add(artist)
                    }
                }
                artists.removeAll(Collections.singleton(null))
                return artists
            } else {
                Console.println('https://api.spotify.com/v1/artists/' + artistId + '/related-artists in ArtistRESTService FAILED')
            }
        } catch (Exception e) {
            Console.println('EXCEPTION requestRelatedArtists in ArtistRESTService.. \n' + e.message)
        }
        return null
    }

    //TODO: Make a superclass for these helper methods?
    def getImageUrl(int index, def value) {
        def images = value.getAt('images')
        if (index < images.size()) {
            return images[index].getAt('url')
        }
    }
}
