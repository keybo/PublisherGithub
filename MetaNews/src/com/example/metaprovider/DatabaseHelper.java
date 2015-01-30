

package com.example.metaprovider;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.os.Handler;

import com.example.metaprovider.FeedData.EntryColumns;
import com.example.metaprovider.FeedData.FeedColumns;
import com.example.metaprovider.FeedData.FilterColumns;
import com.example.metaprovider.FeedData.TaskColumns;

public class DatabaseHelper extends SQLiteOpenHelper 
{

    private static final String DATABASE_NAME = "MetaNews.db";
    private static final int DATABASE_VERSION = 4;

    private static final String ALTER_TABLE = "ALTER TABLE ";
    private static final String ADD = " ADD ";

    private final Handler mHandler;

    public DatabaseHelper(Handler handler, Context context) 
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mHandler = handler;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(createTable(FeedColumns.TABLE_NAME, FeedColumns.COLUMNS));
        database.execSQL(createTable(FilterColumns.TABLE_NAME, FilterColumns.COLUMNS));
        database.execSQL(createTable(EntryColumns.TABLE_NAME, EntryColumns.COLUMNS));
        database.execSQL(createTable(TaskColumns.TABLE_NAME, TaskColumns.COLUMNS));

        // Check if we need to import the backup
        File backupFile = new File("http://baburajannamalai.webs.com/Publisher/qaa.json");
        final boolean hasBackup = backupFile.exists();
        mHandler.post(new Runnable()
        { // In order to it after the database is created
            
        	@Override
            public void run() 
            {
                new Thread(new Runnable() 
                { 
                	// To not block the UI
                    @Override
                    public void run() 
                    {
                        try 
                        {
                            if (hasBackup) 
                            {
                              
                            } 
                            else 
                            {
                              
                            }
                        } 
                        catch (Exception ignored)
                        {
                        }
                    }
                }).start();
            }
        });
    }

  

    private String createTable(String tableName, String[][] columns)
    {
        if (tableName == null || columns == null || columns.length == 0)
        {
            throw new IllegalArgumentException("Invalid parameters for creating table " + tableName);
        } 
        else 
        {
            StringBuilder stringBuilder = new StringBuilder("CREATE TABLE ");

            stringBuilder.append(tableName);
            stringBuilder.append(" (");
            for (int n = 0, i = columns.length; n < i; n++)
            {
                if (n > 0) 
                {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(columns[n][0]).append(' ').append(columns[n][1]);
            }
            return stringBuilder.append(");").toString();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) 
    {
        if (oldVersion < 2)
        {
            executeCatchedSQL(
                    database,
                    ALTER_TABLE + FeedColumns.TABLE_NAME + ADD + FeedColumns.REAL_LAST_UPDATE + ' ' + FeedData.TYPE_DATE_TIME);
        }
        if (oldVersion < 3)
        {
            executeCatchedSQL(
                    database,
                    ALTER_TABLE + FeedColumns.TABLE_NAME + ADD + FeedColumns.RETRIEVE_FULLTEXT + ' ' + FeedData.TYPE_BOOLEAN);
        }
        if (oldVersion < 4) 
        {
            executeCatchedSQL(database, createTable(TaskColumns.TABLE_NAME, TaskColumns.COLUMNS));
            // Remove old FeedEx directory (now useless)
            try
            {
                deleteFileOrDir(new File(Environment.getExternalStorageDirectory() + "/FeedEx/"));
            } 
            catch (Exception ignored)
            {
            }
        }
    }

    private void executeCatchedSQL(SQLiteDatabase database, String query)
    {
        try 
        {
            database.execSQL(query);
        } 
        catch (Exception ignored) 
        {
        	
        }
    }

    private void deleteFileOrDir(File fileOrDirectory)
    {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteFileOrDir(child);

        fileOrDirectory.delete();
    }
}
