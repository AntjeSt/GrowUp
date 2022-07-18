package com.example.growup.plantModel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.growup.R;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class FileManagerImpl implements FileManager {

    private static FileManager instance;
    private static Context ctx;
    private static final String FILENAME = "plants.txt";


    private FileManagerImpl() {}

    public static FileManager getFileManagerInstance (Context ctx){
        if (FileManagerImpl.instance == null){
            FileManagerImpl.instance = new FileManagerImpl();
        }
        FileManagerImpl.ctx = ctx;
        return FileManagerImpl.instance;
    }

    //testing purpose
    public static FileManager getFileManager(){
        if (FileManagerImpl.instance == null){
            FileManagerImpl.instance = new FileManagerImpl();
        }        return FileManagerImpl.instance;
    }


    @Override
    public void savePlantListToFile(ArrayList <Plant> plants) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = ctx.openFileOutput(FILENAME, ctx.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(plants);
             System.out.println("Saved to " + ctx.getFilesDir()  + "/"+ FILENAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {if (oos != null) {
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        }
    }

    @Override
    public ArrayList<Plant> getPlantListFromFile() {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        ArrayList<Plant> plants = null;
        Object obj = null;

        try {
            fis = ctx.openFileInput(FILENAME);
            ois = new ObjectInputStream(fis);
            obj = ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                    plants = (ArrayList<Plant>) obj;
                    System.out.println("fromfile:" + plants.toString());
                    return plants;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return plants;
    }


    @Override
    public String saveImageToFile(String plantID, Bitmap plantImage) {
        String fileName1 = plantID + ".png";
        FileOutputStream fos = null;
        ByteArrayOutputStream baos = null;
        try {
            fos = ctx.openFileOutput(fileName1, ctx.MODE_PRIVATE);
            baos = new ByteArrayOutputStream();
            plantImage.compress(Bitmap.CompressFormat.PNG, 0 , baos);
            byte[] bitmapdata = baos.toByteArray();
            fos.write(bitmapdata);
            String imagePath = ctx.getFilesDir()  + "/" + fileName1;
            System.out.println(imagePath);
            return imagePath;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {if (baos != null) {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        }
        return null;
    }

    @Override
    public Bitmap retrieveImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            return bitmap;
        }
        else {
        return null;}

    }

    @Override
    public void deletePlantImage(String plantID) {
        String imageName = plantID +".png";
        ctx.deleteFile(imageName);
        System.out.println("deleted Image");
    }


}
