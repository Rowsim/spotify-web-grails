package spotifyweb

import grails.gorm.transactions.Transactional
import grails.plugins.rest.client.RestBuilder
import org.springframework.http.HttpStatus

@Transactional
class TrackRESTService {
    RestBuilder rest

    TrackRESTService() {
        Console.println('TrackRESTService INIT')
        rest = new RestBuilder()
    }

    //TODO: Create search service (this doens't really belong here)
    def requestSpotifyArtistsTracksAlbums(String query, String authKey) {
        int trackLimit = 12
        int artistLimit = 8
        int albumLimit = 8
        try {
            String searchQuery = '?q=' + query + '&type=artist,track,album'
            def response = rest.get('https://api.spotify.com/v1/search' + searchQuery) {
                header('Authorization', 'Bearer ' + authKey)
            }
            if (response.statusCode == HttpStatus.UNAUTHORIZED) {
                return false
            }
            if (response.statusCode == HttpStatus.OK) {
                Console.println('https://api.spotify.com/v1/search' + searchQuery + 'in TrackRESTService SUCCESS')

                def responseJson = response.getJson()
                def tracksJson = responseJson.getAt('tracks').getAt('items')
                List<Track> tracks = new ArrayList<>()
                tracksJson.eachWithIndex { key, value ->
                    def name = key.getAt('name')
                    if (name != null) //If the item has no name, we probably don't want it
                    {
                        Track track = new Track(spotifyId: key.getAt('id'),
                                name: key.getAt('name'),
                                artists: key.getAt('artists').getAt('name') as ArrayList<String>,
                                durationMs: key.getAt('duration_ms') as Integer,
                                popularity: key.getAt('popularity') as Integer)
                        tracks.add(track) //TODO: add functionality to check if item already exists in db before save
                    }
                }
                tracks.removeAll(Collections.singleton(null))
                trimList(tracks, trackLimit)

                def artistsJson = responseJson.getAt('artists').getAt('items')
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
                trimList(artists, artistLimit)

                def albumJson = responseJson.getAt('albums').getAt('items')
                List<AlbumSimplified> albums = new ArrayList<>()
                albumJson.eachWithIndex { key, value ->
                    def name = key.getAt('name')
                    if (name != null) {
                        AlbumSimplified album = new AlbumSimplified(spotifyAlbumId: key.getAt('id'),
                                name: name,
                                imageUrl: getImageUrl(0, key))
                        albums.add(album)
                    }
                }
                albums.removeAll(Collections.singleton(null))
                trimList(albums, albumLimit)

                def resultMap = new HashMap()
                resultMap.put('tracks', tracks)
                resultMap.put('artists', artists)
                resultMap.put('albums', albums)
                return resultMap
            } else {
                Console.println('https://api.spotify.com/v1/search' + searchQuery + 'in TrackRESTService FAILED')
            }
        } catch (Exception e) {
            Console.println('FAILED requestSpotifyArtistsTracksAlbums in TrackRESTService.. \n' + e.message)
        }
    }

    def getImageUrl(int index, def value) {
        def images = value.getAt('images')
        if (index < images.size()) {
            return images[index].getAt('url')
        }
    }

    boolean requestSpotifyPlayTrackFromPlaylist(String playlistId, String trackId, String authKey) {
        try {
            String contextUri = "spotify:user:rowanpop:playlist:" + playlistId
            String trackUriOffset = "spotify:track:" + trackId.replace("'", "")

            def response = rest.put('https://api.spotify.com/v1/me/player/play') {
                header('Authorization', 'Bearer ' + authKey)
                contentType('application/json')
                body("{\"context_uri\":\"" + contextUri + "\",\"offset\":{\"uri\":\"" + trackUriOffset + "\"}}")
            }
            if (response.statusCode == HttpStatus.NO_CONTENT) {
                Console.println('https://api.spotify.com/v1/me/player/play in TrackRESTService SUCCESS')
                return true
            } else {
                Console.println('https://api.spotify.com/v1/me/player/play in TrackRESTService FAILED')
            }
        } catch (Exception e) {
            Console.println('FAILED requestSpotifyPlayTrackFromPlaylist in TrackRESTService.. \n' + e.message)
        }
        return false
    }

    boolean requestSpotifyPlayTrack(String trackId, String authKey) {
        try {
            String trackUri = "spotify:track:" + trackId.replace("'", "")

            def response = rest.put('https://api.spotify.com/v1/me/player/play') {
                header('Authorization', 'Bearer ' + authKey)
                contentType('application/json')
                body("{\"uris\":[\"" + trackUri + "\"]}")
            }
            if (response.statusCode == HttpStatus.NO_CONTENT) {
                Console.println('https://api.spotify.com/v1/me/player/play in TrackRESTService SUCCESS')
                return true
            } else {
                Console.println('https://api.spotify.com/v1/me/player/play in TrackRESTService FAILED')
            }
        } catch (Exception e) {
            Console.println('Failed requestSpotifyPlayTrack in TrackRESTService.. \n' + e.message)
        }
        return false
    }

    def trimList(list, sizeLimit) {
        if (list.size() > sizeLimit) {
            list.subList(sizeLimit, list.size()).clear()
        }
    }

    def requestSpotifyGetTracksFromAlbum(String albumId, String authKey) {
        try {
            def response = rest.get('https://api.spotify.com/v1/albums/' + albumId + '/tracks?limit=50') {
                header('Authorization', 'Bearer ' + authKey)
            }
            if (response.statusCode == HttpStatus.OK) {
                Console.println('TrackRESTService at requestSpotifyGetTracksFromAlbum SUCCESS')
                List<Track> tracks = new ArrayList<>()
                buildTrackListFromPagingObject(response, tracks, true)
                return tracks
            } else {
                Console.println('TrackRESTService at requestSpotifyGetTracksFromAlbum FAILED')
                return null
            }
        }
        catch (Exception e) {
            Console.println('EXCEPTION in TrackRESTService at requestSpotifyGetTracksFromAlbum ' + e.message)
        }
    }

    def buildTrackListFromPagingObject(response, trackList, fromAlbum = false) {
        if (response.statusCode == HttpStatus.OK) {
            def responseJson = response.getJson()
            def trackObjects
            if (!fromAlbum) {
                trackObjects = responseJson.getAt('items').getAt('track')
            } else {
                trackObjects = responseJson.getAt('items')
            }

            trackObjects.eachWithIndex { key, value ->
                Track track = new Track(spotifyId: key.getAt('id'), name: key.getAt('name'),
                        artists: key.getAt('artists').getAt('name') as ArrayList<String>,
                        durationMs: key.getAt('duration_ms') as Integer).save()
                trackList.add(track)
            }
        } else {
            Console.println(playlistTracksUrl + ' IN PlaylistRESTService FAILED')
        }
    }
}