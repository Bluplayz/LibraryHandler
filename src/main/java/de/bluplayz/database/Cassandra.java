package de.bluplayz.database;

import com.datastax.driver.core.*;
import de.bluplayz.logger.Logger;

import java.util.*;

public class Cassandra {

    private Session session;
    private Cluster cluster;
    private KeyspaceMetadata keyspace;
    private String keyspaceName = "network";

    //users
    private PreparedStatement insertUser;
    private PreparedStatement removeUser;
    private PreparedStatement selectUser;
    private PreparedStatement selectAllUsers;

    public Cluster getCluster() {
        return cluster;
    }

    public Session getSession() {
        return session;
    }

    public String getKeyspaceName() {
        return keyspaceName;
    }

    public void connect( Cluster cluster, Session session ) {
        //store session
        this.session = session;

        //store cluster
        this.cluster = cluster;

        //create keyspace if doesnt exists
        keyspace = cluster.getMetadata().getKeyspace( getKeyspaceName() );
        if ( keyspace == null ) {
            String datacenter = cluster.getMetadata().getAllHosts().iterator().next().getDatacenter();
            session.execute( "CREATE KEYSPACE " + getKeyspaceName() + " WITH REPLICATION = {'class': 'NetworkTopologyStrategy', '" + datacenter + "': 3};" );
            keyspace = cluster.getMetadata().getKeyspace( getKeyspaceName() );
        }

        //check tables
        checkTables();

        //init prepared statements
        prepareStatements();

        //insert
        //session.execute( insertUser.bind( UUID.randomUUID(), "Fabi Blu", 18, "Bluplayz", true ) );

        ResultSet rs = session.execute( selectAllUsers.bind() );
        HashMap<Integer, HashMap<String, Object>> data = getData( rs );

        //update( "users", "age", 15, "uuid", UUID.fromString( "8801b5d8-29bd-485c-9e0e-db6b69bf470f" ) );
        //update( "users", "age", 13 );
        //session.execute( removeUser.bind( UUID.fromString( "706cf0c3-23a0-40e8-b098-25389311c1a4" ) ) );

        if(data.size() > 0){
            Object value = data.get( 0 ).get( "uuid" );
        }
    }

    /**
     * update ´tablename´ set ´key´ = ´value´ where ´whereKey´ = ´whereValue´
     */
    public void update(String tablename, String key, Object value, String whereKey, Object whereValue){
        PreparedStatement statement = getSession().prepare( "UPDATE " + getKeyspaceName() + "." + tablename + " SET " + key + "=? WHERE " + whereKey + "=? ;" );
        getSession().execute( statement.bind( value, whereValue ) );
    }

    /**
     * check tables
     */
    private void checkTables() {
        //users
        session.execute( "" +
                "CREATE TABLE IF NOT EXISTS " + getKeyspaceName() + ".users (" +

                "uuid uuid," +
                "name text," +
                "age int," +
                "username text," +
                "online boolean," +

                "PRIMARY KEY (uuid) );" );
    }

    /**
     * get Data in HashMap
     * HashMap<id, HashMap<RowKey, RowValue>>
     */
    public HashMap<Integer, HashMap<String, Object>> getData( ResultSet rs ) {
        HashMap<Integer, HashMap<String, Object>> data = new HashMap<>();

        int i = 0;
        for ( Iterator<Row> it = rs.iterator(); it.hasNext(); ) {
            HashMap<String, Object> rowData = new HashMap<>();
            Row row = it.next();

            for ( ColumnDefinitions.Definition column : row.getColumnDefinitions() ) {
                rowData.put( column.getName(), row.getObject( column.getName() ) );
            }

            data.put( i, rowData );
            i++;
        }

        return data;
    }

    /**
     * prepare statements
     */
    private void prepareStatements() {
        insertUser = session.prepare( "INSERT INTO " + getKeyspaceName() + ".users (uuid, name, age, username, online) VALUES(?,?,?,?,?) ;" );
        selectUser = session.prepare( "SELECT * FROM " + getKeyspaceName() + ".users WHERE uuid= ? ALLOW FILTERING ;" );
        removeUser = session.prepare( "DELETE FROM " + getKeyspaceName() + ".users WHERE uuid= ? ;" );
        selectAllUsers = session.prepare( "SELECT * FROM " + getKeyspaceName() + ".users ;" );

        /*
        selectAutoLoginUUID = session.prepare( "SELECT uuid FROM network.auths WHERE clientid = ?;" );
        selectUsernameFromUUID = session.prepare( "SELECT username FROM network.users WHERE uuid = ?;" );
        selectUUIDAndPasswordFromUsername = session.prepare( "SELECT uuid, password, salt FROM network.users WHERE username = ?;" );
        insertAuthAutoLogin = session.prepare( "INSERT INTO network.auths(uuid, clientid) VALUES(?,?) USING TTL 604800;" );
        selectUUIDFromUsername = session.prepare( "SELECT uuid FROM network.users WHERE username = ?;" );
        insertUser = session.prepare( "INSERT INTO network.users (uuid, password, salt, username) VALUES(?,?,?,?);" );
        */
    }

    /**
     * get all tables with column names
     */
    public HashMap<String, ArrayList<String>> getAllTables() {
        HashMap<String, ArrayList<String>> tables = new HashMap<>();

        Metadata metadata = cluster.getMetadata();
        Collection<TableMetadata> tablesMetadata = metadata.getKeyspace( getKeyspaceName() ).getTables();
        for ( TableMetadata tm : tablesMetadata ) {
            ArrayList<String> columns = new ArrayList<>();
            Collection<ColumnMetadata> columnsMetadata = tm.getColumns();
            for ( ColumnMetadata cm : columnsMetadata ) {
                String columnName = cm.getName();
                columns.add( columnName );
            }

            tables.put( tm.getName(), columns );
        }

        return tables;
    }
}