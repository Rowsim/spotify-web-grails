package spotifyweb

class BootStrap {

    def init = { servletContext ->
/*        def testTrack = new Track(spotifyPlaylistId: 'testTrack1', name: 'testTrackName1', artists: ['TestArtist', 'TestArtist2'],
                durationMs: 26000, popularity: 75).save()
        def testTrack2 = new Track(spotifyPlaylistId: 'testTrack2', name: 'testTrackName2', artists: ['TestArtistx', 'TestArtistx2'],
                durationMs: 5000, popularity: 25).save()

        def playlist1 = new Playlist(spotifyPlaylistId: 'test1', name: 'Test Playlist', totalTracks: 2,
                tracksLink: 'http://localhost:8080/testlink', imageUrl: 'http://localhost:8080/testlink',
                tracks: [testTrack, testTrack2]).save()

        def playlist2 = new Playlist(spotifyPlaylistId: 'test2', name: 'Test Playlist2', totalTracks: 1,
                tracksLink: 'http://localhost:8080/testlink', imageUrl: 'http://localhost:8080/testlink',
                tracks: [testTrack2]).save()

        def user = new User(spotifyPlaylistId: 'testUser', name: 'testUsername', email: 'test@email.co.uk',
                imageUrl: 'http://localhost:8080/testlink', playlists: [playlist1, playlist2]).save()*/
    }

    def destroy = {
    }
}
