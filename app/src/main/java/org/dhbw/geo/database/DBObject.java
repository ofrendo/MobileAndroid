package org.dhbw.geo.database;

/**
 * Created by Matthias on 02.06.2015.
 */
public abstract class DBObject {
    protected int id;
    private String tableName;

    public DBObject(){

    }

    public DBObject(String tableName){
        this.tableName = tableName;
    }

    public DBObject(String tableName, int id) {
        this.tableName = tableName;
        this.id = id;
    }

    public String getTableName() {
        return this.tableName;
    }

    public abstract void writeToDB();
    public abstract void deleteFromDB();
}
