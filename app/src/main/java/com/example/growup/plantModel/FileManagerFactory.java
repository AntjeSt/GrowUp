package com.example.growup.plantModel;

import android.content.Context;

public class FileManagerFactory {

    public static FileManager produceFileManager (Context ctx){
        FileManager fileManager = FileManagerImpl.getFileManagerInstance(ctx);
        return fileManager;
    }

    // testing purpose
    public static FileManager produceTestingFileManager(){
        FileManager fileManager = FileManagerImpl.getFileManager();
        return fileManager;
    }
}
