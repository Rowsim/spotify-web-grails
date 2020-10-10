package spotifyweb

class User {

    String spotifyId
    String name
    String email
    String imageUrl

    List playlists
    static hasMany = [playlists: Playlist]

    static constraints = {
    }
}
