<!doctype html>
<html>
<head>
    <meta name="layout" content="default"/>
    <title>SpotifyWeb</title>

    <asset:stylesheet src="home.css"/>

    <asset:javascript src="jquery-3.1.1.js"/>
    <asset:javascript src="underscorejs-1.9.1.js"/>
</head>

<body>
<div id="bg-image" class="bg-image"></div>

<div class="content">
    <g:if test="${isAuth}">
        <div id="barTop" class="barTop"></div>

        <div class="spotifyControls">
            <button id="spotifyTogglePlay">
                <i id="togglePlayIcon" class="far fa-play-circle"></i>
            </button>

            <button id="spotifyShuffle">
                <i class="fas fa-random" onclick="changeSpotifyShuffleStatus()"></i>
            </button>

            <button id="spotifyNextTrack">
                <i class="fas fa-forward"></i>
            </button>

            <button id="spotifyPrevTrack">
                <i class="fas fa-backward"></i>
            </button>



            <input type="range" min="0" max="100" value="25" class="slider" id="volumeSlider">

        </div>

        <div class="trackInfo">
            <h1 id="currentTrack">Nothing - Playing</h1>
        </div>

        <div class="albumImage" id="albumImageDiv">
            <img id="albumImage" src="https://i.imgur.com/VeLU01i.jpg"/>
        </div>


        <div class="searchBoxContainer">
            <div class="searchBox">
                <input type="text" class="searchBox" id="searchBox1" placeholder="Search"/>
                <button id="searchButton" onclick="requestSpotifySearch()">Go</button>
            </div>
        </div>


        <div class="leftNavigationContainer ::-webkit-scrollbar">

                    <!-- Playlists overlay -->
        <div id="leftPlaylistNav" class="overlay">

            <!-- Button to close the overlay navigation -->
            <i class="closebtn far fa-times-circle" onclick="closeLeftNav('leftPlaylistNav')"></i>

            <!-- Overlay content -->
            <div class="overlay-content">
                <div class="playlistContainerTitle">
                    <h1>${user.name}</h1>

        <h1>Playlists:</h1>
        </div>
        <div class="playlistContainer" id="playlistContainer">
            <g:each in="${user.playlists}" var="playlist">
                <h1 onclick="setPlaylistTracks('${playlist.id}', '${playlist.spotifyPlaylistId}');
                closeLeftNav('leftPlaylistNav');
                openLeftNav('leftNavPlaylistTracks')">
                    ${playlist.toString()}
                </h1>
            </g:each>

        </div>
        </div>

                <!-- Playlist tracks overlay -->
        <div id="leftNavPlaylistTracks" class="overlayPlaylistTracks">

            <!-- Button to close the overlay navigation -->
            <i class="closebtn far fa-times-circle" onclick="closeLeftNav('leftNavPlaylistTracks')"></i>
            <i class="fas fa-arrow-left backbtn" onclick="backLeftNav('leftNavPlaylistTracks')"></i>

            <!-- Overlay content -->
            <div class="overlay-content">

                <div class="playlistContainerTitle">
                    <h1>${user.name}</h1>
                </div>

                <div class="playlistContainer" id="playlistTracksContainer">
                </div>
            </div>

        </div>
        <img id="openLeftPlaylistNav" src="${user.imageUrl}" onclick="openLeftNavBtn()"/>
        </div>

        <!-- Search results overlay -->
        <div id="topOverlay" class="topOverlay">

            <i class="closebtn far fa-times-circle" onclick="closeTopNav('topOverlay')"></i>
            <i class="fas fa-arrow-left backbtn" onclick="previousSpotifySearch()"></i>
            <!-- Overlay content -->
            <div class="topOverlay-content">

                <div id="searchTrackResults" class="resultsContainer">
                </div>

                <div id="searchArtistResults" class="resultsContainer">
                </div>

                <div id="searchAlbumResults" class="resultsContainer">
                </div>

            </div>
        </div>




        <script src="https://sdk.scdn.co/spotify-player.js"></script>

        <script>
            window.paused = true;
            window.shuffle = false;

            /*Init player*/
            window.onSpotifyWebPlaybackSDKReady = () => {
                const token = "${authKey}";
                player = new Spotify.Player({
                    name: 'SpotifyWeb',
                    getOAuthToken: cb => {
                        cb(token);
                    },
                    volume: 0.25
                });

                // Error handling
                player.addListener('initialization_error', ({message}) => {
                    console.error(message);
                });
                player.addListener('authentication_error', ({message}) => {
                    console.error(message);
                });
                player.addListener('account_error', ({message}) => {
                    console.error(message);
                });
                player.addListener('playback_error', ({message}) => {
                    console.error(message);
                });

                // Playback status updates
                player.addListener('player_state_changed', ({
                                                                position,
                                                                duration,
                                                                track_window: {current_track}
                                                            }) => {
                    let albumCoverUrl = current_track.album.images[2].url;
                    let artistName = current_track.artists[0].name;
                    let trackName = current_track.name;

                    document.getElementById('currentTrack')
                        .innerHTML = artistName + "  -   " + trackName;

                    setTimeout(setAlbumImages(albumCoverUrl), 400);

                    console.log('Currently Playing', current_track);
                    console.log('Artist name', current_track.artists[0].name);
                    console.log('Track name', current_track.name);
                    console.log('Position in Song', position);
                    console.log('Duration of Song', duration);
                });

                // Ready
                player.addListener('ready', ({device_id}) => {
                    console.log('Ready with Device ID', device_id);
                    changeSpotifyDevice(device_id);
                });

                // Not Ready
                player.addListener('not_ready', ({device_id}) => {
                    console.log('Device ID has gone offline', device_id);
                });

                // Connect to the player!
                player.connect();
            };

            /*Other startup stuff TODO: probably make an init method*/
            $("#searchBox1").on('keyup', _.debounce(requestSpotifySearch, 900));

            /*Toggle Play/Pause*/
            document.getElementById('spotifyTogglePlay').onclick = function () {
                player.togglePlay().then(() => {
                    paused = !paused;
                    if (paused) {
                        document.getElementById('togglePlayIcon').className = "far fa-play-circle";
                        document.getElementById('albumImage').classList.remove('animateImg');

                    } else {
                        document.getElementById('togglePlayIcon').className = "far fa-pause-circle";
                        document.getElementById('albumImage').className = 'animateImg';
                    }
                    console.log('Playback toggled');
                });
            };

            /*Next track btn*/
            document.getElementById('spotifyNextTrack').onclick = function () {
                player.nextTrack().then(() => {
                    console.log('Skipped to next track');

                    addFadeToAlbumImage();
                    setTimeout(removeFadeAndAddAnimationToAlbumImg, 500);
                })
            };

            /*Prev track btn*/
            document.getElementById('spotifyPrevTrack').onclick = function () {
                player.previousTrack().then(() => {
                    console.log('Set to previous track');

                    addFadeToAlbumImage();
                    setTimeout(removeFadeAndAddAnimationToAlbumImg, 1000);
                })
            };

            /*Volume slider*/
            document.getElementById('volumeSlider').oninput = function () {
                player.setVolume(this.value / 100).then(() => {
                    console.log('Volume updated');
                })
            };

            function playAnimations() {
                paused = false;
                document.getElementById('togglePlayIcon').className = "far fa-pause-circle";
                document.getElementById('albumImage').classList.add('animateImg');
            }

            function setShuffleColour(shuffleState) {
                if (shuffleState === true) {
                    $("#spotifyShuffle").css('color', 'rosybrown')
                } else {
                    $("#spotifyShuffle").css('color', 'white')
                }
            }

            function setAlbumImages(url) {
                document.getElementById('albumImage').src = url;
                document.getElementById('bg-image').style.backgroundImage = "url('" + url + "')";
            }

            function addFadeToAlbumImage() {
                document.getElementById('albumImage').classList.add('fade-in');
                document.getElementById('bg-image').classList.add('fade-in');
            }

            function removeFadeAndAddAnimationToAlbumImg() {
                document.getElementById('albumImage').classList.remove('fade-in');
                document.getElementById('bg-image').classList.remove('fade-in');

                document.getElementById('albumImage').classList.add('animateImg');
            }

            function openLeftNavBtn() {
                let x = document.getElementById('leftNavPlaylistTracks');
                let c = document.getElementById('leftPlaylistNav');
                if (x.offsetWidth !== 0
                    || c.offsetWidth !== 0) {
                    closeLeftNav('leftPlaylistNav');
                    closeLeftNav('leftNavPlaylistTracks')
                } else {
                    openLeftNav('leftPlaylistNav');
                }
            }

            function openLeftNav(elementId) {
                document.getElementById(elementId).style.width = "25%";
                closeTopNav('topOverlay');
            }

            function closeLeftNav(elementId) {
                document.getElementById(elementId).style.width = "0%";
            }

            function backLeftNav(elementId) {
                document.getElementById(elementId).style.width = "0%";
                openLeftNav('leftPlaylistNav');
            }

            function openTopNav(elementId) {
                closeLeftNav('leftPlaylistNav');
                closeLeftNav('leftNavPlaylistTracks');
                document.getElementById(elementId).style.height = "25%";
                document.getElementById('albumImageDiv').style.top = "30%";

            }

            function closeTopNav(elementId) {
                document.getElementById(elementId).style.height = "0%";
                document.getElementById('albumImageDiv').style.top = "calc(50% - 320px)";
            }

            function previousSpotifySearch() {
                requestSpotifySearch(false);//for now back button will just search for whatever is still in search bar
            }


            /* REST Ajax requests */
            function changeSpotifyShuffleStatus() {
                let shuffleStatus = shuffle;
                $.ajax({
                    url: "${g.createLink(controller:'player',action:'changeSpotifyShuffleStatus')}",
                    data: {state: !shuffleStatus},
                    success: function (data) {
                        console.log(data);
                    },
                    error: function (request, status, error) {
                        console.log(error)
                    },
                    complete: function () {
                        setShuffleColour(!shuffleStatus);
                        shuffle = !shuffle;
                    }
                })
            }

            function changeSpotifyDevice(deviceId) {
                $.ajax({
                    url: "${g.createLink(controller:'player',action:'changeDevice')}",
                    data: {deviceId: deviceId},
                    success: function (data) {
                        console.log(data);
                    },
                    error: function (request, status, error) {
                        console.log(error)
                    },
                    complete: function () {
                        setTimeout(checkSpotifyPlayAndShuffleStatus, 500); //Delay because sending request at the same time causes issues
                    }
                })
            }

            function checkSpotifyPlayAndShuffleStatus() {
                $.ajax({
                    url: "${g.createLink(controller:'player',action:'checkPlayStatus')}",
                    success: function (data) {
                        console.log('Play status    ' + data.playing);
                        if (data.playing) {
                            playAnimations();
                        }
                        shuffle = data.shuffle;
                        setShuffleColour(data.shuffle);
                    },
                    error: function (request, status, error) {
                        console.log(error)
                    }
                })
            }

            function setPlaylistTracks(playlistId, spotifyPlaylistId) {
                console.log('setplaylisttracks', playlistId);
                let e = document.getElementById('playlistTracksContainer');

                $.ajax({
                    url: "${g.createLink(controller:'playlist',action:'getTracksForPlaylist')}",
                    data: {id: playlistId},
                    success: function (data) {
                        console.log(data);
                        e.innerHTML = '';
                        let i = 0;

                        data.forEach(function (entry) {
                            e.innerHTML += "<h2 id=" + entry.spotifyId + ">" + entry.name + " - " + millisToMinutesAndSeconds(entry.duration) + "</h2>";

                        });

                        data.forEach(function (entry) {
                            $('#' + entry.spotifyId).on('click', (function (e) {
                                requestSpotifyPlayTrackFromPlaylist(spotifyPlaylistId, entry.spotifyId);
                                e.stopPropagation();
                            }));
                        })
                    },
                    error: function (request, status, error) {
                        console.log(error)
                    },
                    complete: function () {

                    }
                });
            }

            function requestSpotifyPlayTrackFromPlaylist(playlistId, trackId) {
                closeLeftNav("leftNavPlaylistTracks");

                $.ajax({
                    url: "${g.createLink(controller:'track',action:'playSpotifyTrackFromPlaylist')}",
                    data: {playlistId: playlistId, spotifyId: trackId},
                    success: function (data) {
                        paused = false;
                        document.getElementById('togglePlayIcon').className = "far fa-pause-circle";
                        addFadeToAlbumImage();
                        removeFadeAndAddAnimationToAlbumImg();
                        console.log(data);
                    },
                    error: function (request, status, error) {
                        console.log(error)
                    },
                    complete: function () {

                    }
                });
            }

            function requestSpotifyPlayTrack(trackId) {
                closeTopNav('topOverlay');

                $.ajax({
                    url: "${g.createLink(controller:'track',action:'playSpotifyTrack')}",
                    data: {spotifyId: trackId},
                    success: function (data) {
                        paused = false;
                        document.getElementById('togglePlayIcon').className = "far fa-pause-circle";
                        addFadeToAlbumImage();
                        removeFadeAndAddAnimationToAlbumImg();
                        console.log(data);
                    },
                    error: function (request, status, error) {
                        console.log(error)
                    },
                    complete: function () {

                    }
                });
            }

            function requestSpotifyArtistTopTracksAndAlbumsAndRelated(artistObject) {
                let trackResults = document.getElementById('searchTrackResults');
                let artistResults = document.getElementById('searchArtistResults');
                let albumResults = document.getElementById('searchAlbumResults');
                trackResults.innerHTML = '<h2> <img src="' + artistObject.imageUrl + '" class=searchResultImage>' + artistObject.name + '</h2>';
                artistResults.innerHTML = 'RELATED ARTISTS';
                albumResults.innerHTML = 'ALBUMS';

                $.ajax({
                    url: "${g.createLink(controller:'artist',action:'getArtistTopTracksAndAlbumsAndRelated')}",
                    data: {artistId: artistObject.spotifyArtistId},
                    success: function (data) {
                        /*Top Tracks*/
                        setTracksInnerHtml(data, trackResults);
                        /*Related Artists*/
                        setArtistsInnerHtml(data, artistResults);
                        /*Albums*/
                        setAlbumInnerHtml(data, albumResults);
                    },
                    error: function (request, status, error) {
                        console.log(error)
                    }
                })
            }

            function requestSpotifyAlbumTracks(albumObject) {
                let trackResults = document.getElementById('searchTrackResults');
                let artistResults = document.getElementById('searchArtistResults');
                let albumResults = document.getElementById('searchAlbumResults');
                trackResults.innerHTML = 'Other Albums'; //TODO: Related albums?
                artistResults.innerHTML = '<h2>' + albumObject.name + '</h2><img src="' + albumObject.imageUrl + '" class=tracksAlbumImage>';
                albumResults.innerHTML = '';
                $.ajax({
                    url: "${g.createLink(controller:'track',action:'getSpotifyTracksFromAlbum')}",
                    data: {albumId: albumObject.spotifyAlbumId},
                    success: function(data) {
                        console.log(data + '     albumObject');
                        setTracksInnerHtml(data, albumResults)
                    }
                })
            }

            function setTracksInnerHtml(data, htmlElement) {
                data.tracks.forEach(function (entry) {
                    htmlElement.innerHTML += "<h2 id=" + entry.spotifyId + ">" + entry.name + " - " + millisToMinutesAndSeconds(entry.durationMs) + "</h2>";
                });

                data.tracks.forEach(function (entry) {
                    $('#' + entry.spotifyId).on('click', (function (e) {
                        requestSpotifyPlayTrack(entry.spotifyId);
                        e.stopPropagation();
                    }));
                });
            }

            function setArtistsInnerHtml(data, htmlElement) {
                data.artists.forEach(function (entry) {
                    htmlElement.innerHTML += "<h2 id=" + entry.spotifyArtistId + ">" + "<img src=\"" + entry.imageUrl + "\"" + " class='searchResultImg'>" + entry.name + "</h2>";
                });

                data.artists.forEach(function (entry) {
                    $('#' + entry.spotifyArtistId).on('click', (function (e) {
                        requestSpotifyArtistTopTracksAndAlbumsAndRelated(entry);
                        e.stopPropagation();
                    }));
                });
            }

            function setAlbumInnerHtml(data, htmlElement) {
                data.albums.forEach(function (entry) {
                    htmlElement.innerHTML += "<h2 id=" + entry.spotifyAlbumId + ">" + "<img src=\"" + entry.imageUrl + "\"" + " class='searchResultImg'>" + entry.name + "</h2>";
                });

                data.albums.forEach(function (entry) {
                    $('#' + entry.spotifyAlbumId).on('click', (function (e) {
                        requestSpotifyAlbumTracks(entry);
                        e.stopPropagation();
                    }));
                });
            }

            function requestSpotifySearch(openOverlay) {
                console.log('requestSpotifySearch called...');
                let query = $("#searchBox1").val();
                if (query == null || query.trim() === '') {
                    return false;
                }
                let trackResults = document.getElementById('searchTrackResults');
                let artistResults = document.getElementById('searchArtistResults');
                let albumResults = document.getElementById('searchAlbumResults');
                trackResults.innerHTML = 'SONGS';
                artistResults.innerHTML = 'ARTISTS';
                albumResults.innerHTML = 'ALBUMS';

                $.ajax({
                    url: "${g.createLink(controller:'track',action:'searchSpotifyArtistsTracksAlbums')}",
                    data: {query: query},
                    success: function (data) {
                        console.log(data);
                        /*Tracks*/
                        data.tracks.forEach(function (entry) {
                            trackResults.innerHTML += "<h2 id=" + entry.spotifyId + ">" + entry.artists[0] + " - " + entry.name + " - " + millisToMinutesAndSeconds(entry.durationMs) + "</h2>";
                        });

                        data.tracks.forEach(function (entry) {
                            $('#' + entry.spotifyId).on('click', (function (e) {
                                requestSpotifyPlayTrack(entry.spotifyId);
                                e.stopPropagation();
                            }));
                        });

                        /*Artists*/
                        setArtistsInnerHtml(data, artistResults);

                        /*Albums*/
                        setAlbumInnerHtml(data, albumResults);
                    },
                    error: function (request, status, error) {
                        console.log(error)
                    },
                    complete: function () {
                        if (openOverlay) {
                            openTopNav('topOverlay');
                        }
                    }
                });
            }


            /*Utility*/
            function millisToMinutesAndSeconds(millis) {
                let minutes = Math.floor(millis / 60000);
                let seconds = ((millis % 60000) / 1000).toFixed(0);
                return minutes + ":" + (seconds < 10 ? '0' : '') + seconds;
            }

        </script>

    </g:if>
    <g:else>
        <div id="login">
            <i class="fab fa-spotify"></i>
            <g:form class="loginBtn">
                <g:actionSubmit value="Spotify Login" action="authenticateSpotify"/>
            </g:form>
        </div>
    </g:else>
</div>

</body>

</html>
