package org.dhbw.geo.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Matthias on 02.06.2015.
 */
public abstract class DBObject {

    private long id;
    private boolean existsOnDB = false;

    public DBObject() {

    }

    public DBObject(long id){
        setId(id);
    }

    public void writeToDB(){
        SQLiteDatabase db = DBHelper.getHelper().getWritableDatabase();
        if(!existsOnDB){
            long id = insertIntoDB(db);
            setId(id);  // just to be sure
        } else {
            updateOnDB(db);
        }
        db.close();
    }

    public void setId(long id){
        this.id = id;
        existsOnDB = true;
    }

    public long getId(){
        return id;
    }

    public boolean existsOnDB(){
        return existsOnDB;
    }

    protected abstract long insertIntoDB(SQLiteDatabase db);
    protected abstract void updateOnDB(SQLiteDatabase db);
    public abstract void deleteFromDB();
}
