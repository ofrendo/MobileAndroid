package org.dhbw.geo.database;

import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import org.dhbw.geo.services.ContextManager;

/**
 * Represents any database object of this application. It contains a method to write the object to the sqlite database and stores the id of the object.
 * @author Matthias
 */
public abstract class DBObject {
    /**
     * the id of the database object. It is used as a primary key.
     */
    private long id;
    /**
     * the flag whether the object already exists on the database. It only checks, if any version had existed on the database at any time.
     * It doesn't check whether the object has been deleted. (An object should <b>not</b> be deleted and recreated at any time! Use {@link #writeToDB()} to update it.)
     */
    private boolean existsOnDB = false;

    public DBObject() {

    }

    public DBObject(long id){
        setId(id);
    }

    /**
     * Writes the object to the sqlite database. It is used for inserts and updates.
     */
    public void writeToDB(){
        try {
            SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
            if(!existsOnDB){
                long id = insertIntoDB(db);
                setId(id);  // just to be sure
            } else {
                updateOnDB(db);
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ContextManager.getContext(), "Couldn't write object to database!", Toast.LENGTH_SHORT);
        }
    }

    protected void setId(long id){
        this.id = id;
        existsOnDB = true;
    }

    public long getId(){
        return id;
    }

    /**
     * Checks, whether the object exists on the database
     * @return the result of the check
     */
    public boolean existsOnDB(){
        return existsOnDB;
    }

    /**
     * Inserts the object into the corresponding database table.
     * @param db the reference to the sqlite database
     * @return the id of the inserted object
     * @see #writeToDB()
     */
    protected abstract long insertIntoDB(SQLiteDatabase db) throws Exception;

    /**
     * Updates the object in the corresponding database table.
     * @param db the reference to the sqlite database
     * @see #writeToDB()
     */
    protected abstract void updateOnDB(SQLiteDatabase db) throws Exception;

    /**
     * Deletes the object from the corresponding database table.
     * A deleted object must <b>not</b> be reused!
     */
    public abstract void deleteFromDB();
}
