package spotifyweb

class Artist {

    String spotifyArtistId
    String name
    String imageUrl
    int followers
    int popularity
    String[] genres

    String toString() {
        return name
    }


    static constraints = {
    }
}
