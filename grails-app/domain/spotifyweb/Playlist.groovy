package spotifyweb

class Playlist {
    String spotifyPlaylistId
    String name
    int totalTracks
    String tracksLink
    String imageUrl

    List tracks
    static hasMany = [tracks: Track]
    //static belongsTo = [user: User]
    //Track[] tracks


    String toString() {
        return name
    }

    static constraints = {
    }
}
