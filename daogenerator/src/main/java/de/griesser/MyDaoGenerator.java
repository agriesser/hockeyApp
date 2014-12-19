package de.griesser;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

/**
 * Created by gieser on 19.12.2014.
 */
public class MyDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "de.agriesser.hockeyapp.db");
        Entity club = schema.addEntity("Club");
        club.addIdProperty();
        club.addStringProperty("name");
        club.addStringProperty("url");
        club.addStringProperty("imgUrl");

        Entity address = schema.addEntity("Address");
        address.addIdProperty();
        address.addStringProperty("street");
        address.addStringProperty("house_number");
        address.addStringProperty("postal_code");
        address.addStringProperty("city");
        address.addStringProperty("country");
        address.addFloatProperty("longitude");
        address.addFloatProperty("latitude");

        Entity club_address = schema.addEntity("ClubAddress");
        Property club_address_cid = club_address.addLongProperty("club_id").notNull().getProperty();
        Property club_address_aid = club_address.addLongProperty("address_id").notNull().getProperty();
        club.addToMany(club_address, club_address_cid);
        club_address.addToOne(club, club_address_cid);
        club_address.addToOne(address, club_address_aid);

        Entity matchAddress = schema.addEntity("MatchAddress");
        matchAddress.addIdProperty();
        Property matchAddress_address_id = matchAddress.addLongProperty("address_id").notNull().getProperty();
        matchAddress.addToOne(address, matchAddress_address_id);

        Entity association = schema.addEntity("Association");
        association.addIdProperty();
        association.addStringProperty("name");

        Entity clubAssociation = schema.addEntity("ClubAssociation");
        Property clubId = clubAssociation.addLongProperty("club_id").getProperty();
        Property associationId = clubAssociation.addLongProperty("association_id").getProperty();
        clubAssociation.addToOne(club, clubId);
        clubAssociation.addToOne(association, associationId);

        club.addToMany(clubAssociation, clubId);
        association.addToMany(clubAssociation, associationId);

        Entity season = schema.addEntity("Season");
        season.addIdProperty();
        season.addLongProperty("lastAccess").notNull();
        season.addBooleanProperty("isCurrent");
        season.addStringProperty("name");
        Property seasonAssociationId = season.addLongProperty("association_id").getProperty();
        season.addToOne(association, seasonAssociationId);
        association.addToMany(season, seasonAssociationId);

        Entity league = schema.addEntity("League");
        league.addIdProperty();
        league.addStringProperty("name");
        Property leagueAssocId = league.addLongProperty("association_id").getProperty();
        league.addToOne(association, leagueAssocId);
        association.addToMany(league, leagueAssocId);

        Entity league_season = schema.addEntity("LeagueSeason");
        league_season.addIdProperty();
        Property ls_league_id = league_season.addLongProperty("league_id").notNull().getProperty();
        Property ls_season_id = league_season.addLongProperty("season_id").notNull().getProperty();
        league_season.addToOne(league, ls_league_id);
        league_season.addToOne(season, ls_season_id);
        league.addToMany(league_season, ls_league_id);
        season.addToMany(league_season, ls_season_id);

        Entity group = schema.addEntity("Group");
        group.addIdProperty();
        Property group_ls_id = group.addLongProperty("ls_id").notNull().getProperty();
        league_season.addToMany(group, group_ls_id);

        Entity team = schema.addEntity("Team");
        team.addIdProperty();
        team.addStringProperty("name");
        Property team_club = team.addLongProperty("club_id").notNull().getProperty();
        Property team_group_id = team.addLongProperty("group_id").notNull().getProperty();
        team.addToOne(club, team_club);
        group.addToMany(team, team_group_id);

        Entity match = schema.addEntity("Match");
        match.addIdProperty();
        match.addStringProperty("score");
        match.addLongProperty("time");
        match.addStringProperty("note");
        Property homeTeam = match.addLongProperty("team_home_id").notNull().getProperty();
        Property foreignTeam = match.addLongProperty("team_foreign_id").notNull().getProperty();
        Property place = match.addLongProperty("match_address_id").getProperty();
        match.addToOne(matchAddress, place);
        match.addToOne(team, homeTeam).setName("home");
        match.addToOne(team, foreignTeam).setName("foreign");

        Entity table = schema.addEntity("Table");
        table.setTableName("tabelle");
        table.addIdProperty();
        table.addStringProperty("note");
        Property table_group_id = table.addLongProperty("group_id").notNull().getProperty();
        group.addToOne(table, table_group_id);

        Entity line = schema.addEntity("TableLine");
        line.addIdProperty();
        Property line_place = line.addIntProperty("place").getProperty();
        line.addStringProperty("team");
        line.addIntProperty("matches");
        line.addIntProperty("wins");
        line.addStringProperty("score");
        line.addIntProperty("points");
        Property line_table_id = line.addLongProperty("table_id").notNull().getProperty();
        ToMany tableMany = table.addToMany(line, line_table_id);
        tableMany.orderAsc(line_place);


        new DaoGenerator().generateAll(schema, args[0]);
    }
}
