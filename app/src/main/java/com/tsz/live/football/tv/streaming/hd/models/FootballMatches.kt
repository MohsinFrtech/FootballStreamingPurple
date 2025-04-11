package com.tsz.live.football.tv.streaming.hd.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FootballMatches(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("match_id") var matchId: Int? = null,
    @SerializedName("referee") var referee: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("timezone") var timezone: String? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("timestamp") var timestamp: Long? = null,
    @SerializedName("winner_name") var winnerName: String? = null,
    @SerializedName("home_team") var homeTeam: HomeTeam? = HomeTeam(),
    @SerializedName("away_team") var awayTeam: AwayTeam? = AwayTeam(),
    @SerializedName("goals") var goals: Goals? = Goals(),
    @SerializedName("score") var score: Score? = Score(),
    @SerializedName("status") var status: Status? = Status(),
//    @SerializedName("events") var events: Any=null,
    @SerializedName("periods") var periods: Periods? = Periods(),
    @SerializedName("venue") var venue: Venue? = Venue(),
    @SerializedName("league") var league: League? = League(),
    @SerializedName("season_id") var seasonId: Int? = null,
    @SerializedName("league_id") var leagueId: Int? = null,
    @SerializedName("home_team_id") var homeTeamId: Int? = null,
    @SerializedName("away_team_id") var awayTeamId: Int? = null,
    @SerializedName("venue_id") var venueId: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null
) : Parcelable
data class Events(@SerializedName("time"     ) var time     : Time?   = Time(),
                 @SerializedName("team"     ) var team     : Team?   = Team(),
                 @SerializedName("player"   ) var player   : Player? = Player(),
                 @SerializedName("assist"   ) var assist   : Assist? = Assist(),
                 @SerializedName("type"     ) var type     : String? = null,
                 @SerializedName("detail"   ) var detail   : String? = null,
                 @SerializedName("comments" ) var comments : String? = null)
@Parcelize
data class Time (

    @SerializedName("elapsed" ) var elapsed : Int?    = null,
    @SerializedName("extra"   ) var extra   : String? = null
) : Parcelable

@Parcelize
data class Team (

    @SerializedName("id"   ) var id   : Int?    = null,
    @SerializedName("name" ) var name : String? = null,
    @SerializedName("logo" ) var logo : String? = null
) : Parcelable

@Parcelize
data class Player (

    @SerializedName("id"   ) var id   : String? = null,
    @SerializedName("name" ) var name : String? = null

) : Parcelable

@Parcelize
data class Assist (

    @SerializedName("id"   ) var id   : String? = null,
    @SerializedName("name" ) var name : String? = null

) : Parcelable

@Parcelize
data class HomeTeam(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("logo") var logo: String? = null,
    @SerializedName("winner") var winner: String? = null
) : Parcelable

@Parcelize
data class AwayTeam(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("logo") var logo: String? = null,
    @SerializedName("winner") var winner: String? = null
) : Parcelable

@Parcelize
data class Goals(

    @SerializedName("home") var home: Int? = null,
    @SerializedName("away") var away: Int? = null
) : Parcelable


@Parcelize
data class Halftime(
    @SerializedName("home") var home: Int? = null,
    @SerializedName("away") var away: Int? = null

) : Parcelable

@Parcelize
data class Fulltime(

    @SerializedName("home") var home: String? = null,
    @SerializedName("away") var away: String? = null

) : Parcelable

@Parcelize
data class Extratime(
    @SerializedName("home") var home: String? = null,
    @SerializedName("away") var away: String? = null

) : Parcelable

@Parcelize
data class Penalty(

    @SerializedName("home") var home: String? = null,
    @SerializedName("away") var away: String? = null

) : Parcelable

@Parcelize
data class Score(
    @SerializedName("halftime") var halftime: Halftime? = Halftime(),
    @SerializedName("fulltime") var fulltime: Fulltime? = Fulltime(),
    @SerializedName("extratime") var extratime: Extratime? = Extratime(),
    @SerializedName("penalty") var penalty: Penalty? = Penalty()
) : Parcelable

@Parcelize
data class Status(
    @SerializedName("long") var longName: String? = null,
    @SerializedName("short") var short: String? = null,
    @SerializedName("elapsed") var elapsed: Int? = null
) : Parcelable

@Parcelize
data class Periods(

    @SerializedName("first") var first: Int? = null,
    @SerializedName("second") var second: String? = null

) : Parcelable

@Parcelize
data class Venue(

    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("city") var city: String? = null

) : Parcelable

@Parcelize
data class League(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("logo") var logo: String? = null,
    @SerializedName("flag") var flag: String? = null,
    @SerializedName("season") var season: Int? = null,
    @SerializedName("round") var round: String? = null

) : Parcelable
@Parcelize
data class LeagueData( @SerializedName("id"           ) var id          : Int?    = null,
                       @SerializedName("league_id"    ) var leagueId    : Int?    = null,
                       @SerializedName("name"         ) var name        : String? = null,
                       @SerializedName("league_type"  ) var leagueType  : String? = null,
                       @SerializedName("logo"         ) var logo        : String? = null,
                       @SerializedName("country"      ) var country     : String? = null,
                       @SerializedName("country_code" ) var countryCode : String? = null,
                       @SerializedName("country_flag" ) var countryFlag : String? = null,
                       @SerializedName("created_at"   ) var createdAt   : String? = null,
                       @SerializedName("updated_at"   ) var updatedAt   : String? = null
): Parcelable
@Parcelize
data class Teams (

    @SerializedName("id"         ) var id        : Int?    = null,
    @SerializedName("team_id"    ) var teamId    : Int?    = null,
    @SerializedName("name"       ) var name      : String? = null,
    @SerializedName("logo"       ) var logo      : String? = null,
    @SerializedName("created_at" ) var createdAt : String? = null,
    @SerializedName("updated_at" ) var updatedAt : String? = null

) : Parcelable
@Parcelize
data class LeagueSeasonData(

    @SerializedName("id"         ) var id        : Int?     = null,
    @SerializedName("year"       ) var year      : Int?     = null,
    @SerializedName("current"    ) var current   : Boolean? = null,
    @SerializedName("start"      ) var start     : String?  = null,
    @SerializedName("end"        ) var end       : String?  = null,
    @SerializedName("league_id"  ) var leagueId  : Int?     = null,
    @SerializedName("created_at" ) var createdAt : String?  = null,
    @SerializedName("updated_at" ) var updatedAt : String?  = null
): Parcelable
@Parcelize
data class VenueData(@SerializedName("id"         ) var id        : Int?    = null,
                     @SerializedName("venue_id"   ) var venueId   : Int?    = null,
                     @SerializedName("name"       ) var name      : String? = null,
                     @SerializedName("city"       ) var city      : String? = null,
                     @SerializedName("created_at" ) var createdAt : String? = null,
                     @SerializedName("updated_at" ) var updatedAt : String? = null): Parcelable

@Parcelize
data class TeamPlayersData(@SerializedName("team"    ) var team    : Team?              = Team(),
                           @SerializedName("players" ) var players : ArrayList<Players> = arrayListOf()
): Parcelable
@Parcelize
data class Players (

    @SerializedName("id"       ) var id       : Int?    = null,
    @SerializedName("name"     ) var name     : String? = null,
    @SerializedName("age"      ) var age      : Int?    = null,
    @SerializedName("number"   ) var number   : Int?    = null,
    @SerializedName("position" ) var position : String? = null,
    @SerializedName("photo"    ) var photo    : String? = null

): Parcelable
@Parcelize
data class PlayerStats(
    @SerializedName("player"     ) var player: PlayerData = PlayerData(),
    @SerializedName("statistics" ) var statistics: ArrayList<Statistics> = arrayListOf()): Parcelable
@Parcelize
data class PlayerData(@SerializedName("id"          ) var id          : Int?     = null,
                      @SerializedName("name"        ) var name        : String?  = null,
                      @SerializedName("firstname"   ) var firstname   : String?  = null,
                      @SerializedName("lastname"    ) var lastname    : String?  = null,
                      @SerializedName("age"         ) var age         : Int?     = null,
                      @SerializedName("birth"       ) var birth       : Birth?   = Birth(),
                      @SerializedName("nationality" ) var nationality : String?  = null,
                      @SerializedName("height"      ) var height      : String?  = null,
                      @SerializedName("weight"      ) var weight      : String?  = null,
                      @SerializedName("injured"     ) var injured     : Boolean? = null,
                      @SerializedName("photo"       ) var photo       : String?  = null): Parcelable
@Parcelize
data class Birth (

    @SerializedName("date"    ) var date    : String? = null,
    @SerializedName("place"   ) var place   : String? = null,
    @SerializedName("country" ) var country : String? = null
): Parcelable

@Parcelize
data class Statistics (

    @SerializedName("team"        ) var team        : Team?        = Team(),
    @SerializedName("league"      ) var league      : League?      = League(),
    @SerializedName("games"       ) var games       : Games?       = Games(),
    @SerializedName("substitutes" ) var substitutes : Substitutes? = Substitutes(),
    @SerializedName("shots"       ) var shots       : Shots?       = Shots(),
    @SerializedName("goals"       ) var goals       : GoalsData?       = GoalsData(),
    @SerializedName("passes"      ) var passes      : Passes?      = Passes(),
    @SerializedName("tackles"     ) var tackles     : Tackles?     = Tackles(),
    @SerializedName("duels"       ) var duels       : Duels?       = Duels(),
    @SerializedName("dribbles"    ) var dribbles    : Dribbles?    = Dribbles(),
    @SerializedName("fouls"       ) var fouls       : Fouls?       = Fouls(),
    @SerializedName("cards"       ) var cards       : Cards?       = Cards(),
    @SerializedName("penalty"     ) var penalty     : PenaltyData?     = PenaltyData()

): Parcelable
@Parcelize
data class PenaltyData (

    @SerializedName("won"      ) var won      : String? = null,
    @SerializedName("commited" ) var commited : String? = null,
    @SerializedName("scored"   ) var scored   : Int?    = null,
    @SerializedName("missed"   ) var missed   : Int?    = null,
    @SerializedName("saved"    ) var saved    : Int?    = null

): Parcelable

@Parcelize
data class Cards (

    @SerializedName("yellow"    ) var yellow    : Int? = null,
    @SerializedName("yellowred" ) var yellowred : Int? = null,
    @SerializedName("red"       ) var red       : Int? = null

): Parcelable

@Parcelize
data class Fouls (

    @SerializedName("drawn"     ) var drawn     : String? = null,
    @SerializedName("committed" ) var committed : String? = null

): Parcelable
@Parcelize
data class Dribbles (

    @SerializedName("attempts" ) var attempts : String? = null,
    @SerializedName("success"  ) var success  : String? = null,
    @SerializedName("past"     ) var past     : String? = null

): Parcelable

@Parcelize
data class Duels (

    @SerializedName("total" ) var total : String? = null,
    @SerializedName("won"   ) var won   : String? = null
): Parcelable

@Parcelize
data class Tackles (

    @SerializedName("total"         ) var total         : String? = null,
    @SerializedName("blocks"        ) var blocks        : String? = null,
    @SerializedName("interceptions" ) var interceptions : String? = null
): Parcelable
@Parcelize
data class Passes (

    @SerializedName("total"    ) var total    : String? = null,
    @SerializedName("key"      ) var key      : String? = null,
    @SerializedName("accuracy" ) var accuracy : String? = null
): Parcelable

@Parcelize
data class GoalsData (

    @SerializedName("total"    ) var total    : Int?    = null,
    @SerializedName("conceded" ) var conceded : Int?    = null,
    @SerializedName("assists"  ) var assists  : String? = null,
    @SerializedName("saves"    ) var saves    : String? = null
): Parcelable

@Parcelize
data class Shots (

    @SerializedName("total" ) var total : String? = null,
    @SerializedName("on"    ) var on    : String? = null

): Parcelable

@Parcelize
data class Substitutes (

    @SerializedName("in"    ) var inn    : Int? = null,
    @SerializedName("out"   ) var out   : Int? = null,
    @SerializedName("bench" ) var bench : Int? = null

): Parcelable

@Parcelize
data class Games (

    @SerializedName("appearences" ) var appearences : Int?     = null,
    @SerializedName("lineups"     ) var lineups     : Int?     = null,
    @SerializedName("minutes"     ) var minutes     : Int?     = null,
    @SerializedName("number"      ) var number      : String?  = null,
    @SerializedName("position"    ) var position    : String?  = null,
    @SerializedName("rating"      ) var rating      : String?  = null,
    @SerializedName("captain"     ) var captain     : Boolean? = null

): Parcelable
data class LeagueStandings(  @SerializedName("league" ) var league : LeaguesData? = LeaguesData()
)

data class LeaguesData(@SerializedName("id"        ) var id        : Int?                            = null,
                       @SerializedName("name"      ) var name      : String?                         = null,
                       @SerializedName("country"   ) var country   : String?                         = null,
                       @SerializedName("logo"      ) var logo      : String?                         = null,
                       @SerializedName("flag"      ) var flag      : String?                         = null,
                       @SerializedName("season"    ) var season    : Int?                            = null,
                       @SerializedName("standings" ) var standings : ArrayList<ArrayList<Standings>> = arrayListOf())
data class Standings (

    @SerializedName("rank"        ) var rank        : Int?    = null,
    @SerializedName("team"        ) var team        : Team?   = Team(),
    @SerializedName("points"      ) var points      : Int?    = null,
    @SerializedName("goalsDiff"   ) var goalsDiff   : Int?    = null,
    @SerializedName("group"       ) var group       : String? = null,
    @SerializedName("form"        ) var form        : String? = null,
    @SerializedName("status"      ) var status      : String? = null,
    @SerializedName("description" ) var description : String? = null,
    @SerializedName("all"         ) var all         : All?    = All(),
    @SerializedName("home"        ) var home        : Home?   = Home(),
    @SerializedName("away"        ) var away        : Away?   = Away(),
    @SerializedName("update"      ) var update      : String? = null

)
data class Away (

    @SerializedName("played" ) var played : Int?   = null,
    @SerializedName("win"    ) var win    : Int?   = null,
    @SerializedName("draw"   ) var draw   : Int?   = null,
    @SerializedName("lose"   ) var lose   : Int?   = null,
    @SerializedName("goals"  ) var goals  : GoalsDataStand? = GoalsDataStand()

)

data class Home (

    @SerializedName("played" ) var played : Int?   = null,
    @SerializedName("win"    ) var win    : Int?   = null,
    @SerializedName("draw"   ) var draw   : Int?   = null,
    @SerializedName("lose"   ) var lose   : Int?   = null,
    @SerializedName("goals"  ) var goals  : GoalsDataStand? = GoalsDataStand()

)
data class GoalsDataStand (

    @SerializedName("for"     ) var fore     : Int? = null,
    @SerializedName("against" ) var against : Int? = null

)
data class All (

    @SerializedName("played" ) var played : Int?   = null,
    @SerializedName("win"    ) var win    : Int?   = null,
    @SerializedName("draw"   ) var draw   : Int?   = null,
    @SerializedName("lose"   ) var lose   : Int?   = null,
    @SerializedName("goals"  ) var goals  : Goals? = Goals()
)
@Parcelize
data class LeagueTeams(
    @SerializedName("team"  ) var team  : LeagueTeamsData?  = LeagueTeamsData(),
    @SerializedName("venue" ) var venue : LeagueVenuData? = LeagueVenuData()): Parcelable

@Parcelize
data class LeagueTeamsData(@SerializedName("id"       ) var id       : Int?     = null,
                           @SerializedName("name"     ) var name     : String?  = null,
                           @SerializedName("code"     ) var code     : String?  = null,
                           @SerializedName("country"  ) var country  : String?  = null,
                           @SerializedName("founded"  ) var founded  : Int?     = null,
                           @SerializedName("national" ) var national : Boolean? = null,
                           @SerializedName("logo"     ) var logo     : String?  = null): Parcelable

@Parcelize
data class LeagueVenuData( @SerializedName("id"       ) var id       : Int?    = null,
                           @SerializedName("name"     ) var name     : String? = null,
                           @SerializedName("address"  ) var address  : String? = null,
                           @SerializedName("city"     ) var city     : String? = null,
                           @SerializedName("capacity" ) var capacity : Int?    = null,
                           @SerializedName("surface"  ) var surface  : String? = null,
                           @SerializedName("image"    ) var image    : String? = null
): Parcelable