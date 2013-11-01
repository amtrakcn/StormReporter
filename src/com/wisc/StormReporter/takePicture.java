package com.wisc.StormReporter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.wisc.StormReporter.BaseAlbumDirFactory;
import com.wisc.StormReporter.FroyoAlbumDirFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class takePicture extends Activity {

private static final int TAKE_PHOTO = 1;
private static final String BITMAP_STORAGE_KEY = "viewbitmap";
private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
private ImageView mImageView;
private Bitmap mImageBitmap;
private Bitmap currentImageBitmap;
private ImageView currentImageView;
private Uri imageURI;

private String albumName;

private String mCurrentPhotoPath;
private String currentPhotoPath;

private static final String JPEG_FILE_PREFIX = "IMG_";
private static final String JPEG_FILE_SUFFIX = ".jpg";

private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

public takePicture(String albNam) {
this.currentPhotoPath = null;
this.currentImageBitmap = null;
this.currentImageView = null;
this.albumName = albNam;
this.imageURI = null;

if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
} else {
mAlbumStorageDirFactory = new BaseAlbumDirFactory();
}
//getPictureIntent(TAKE_PHOTO);
//dispatchTakePictureIntent(TAKE_PHOTO);
}

//******** Getters ********
public String getCurrentPhotoPath() {
return this.currentPhotoPath;
}
public Bitmap getCurrentPhotoBitmap() {
return this.currentImageBitmap;
}
public ImageView getCurrentImageView(){
return this.currentImageView;
}
public Intent getPictureIntent (int actionCode) {
return dispatchTakePictureIntent(actionCode);
}
public Uri getPictureURI () {
return this.imageURI;
}
// ******* Setters *********

public void setCurrentPhotoBitmap(Bitmap b) {
this.currentImageBitmap = b;
}
public void setCurrentImageView(ImageView v){
this.currentImageView = v;
}
/**
* Starts the process of taking/ processing a picture, adapted from having
* multiple possible inputs, it now simply completes the one request of 
* handling a single picture type.
* @param actionCode: value that doesn't seem to matter
*/
private Intent dispatchTakePictureIntent(int actionCode) {

Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
File f = null;
try {
f = setUpPhotoFile();
mCurrentPhotoPath = f.getAbsolutePath();
takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
} catch (IOException e) {
e.printStackTrace();
f = null;
mCurrentPhotoPath = null;
} catch (Exception e) {
e.printStackTrace();
}
return takePictureIntent;
/*
try {
startActivityForResult(takePictureIntent, actionCode);
} catch (Exception e) {
e.printStackTrace();
}
*/
}

/**
* Sets up file to store the photo in.
* @return File to hold photo.
* @throws IOException
*/
private File setUpPhotoFile() throws IOException {

File f = createImageFile();
mCurrentPhotoPath = f.getAbsolutePath();

return f;
}

/**
* Actually creates a file to store the photo in. Part of setup method.
* @return File to hold photo.
* @throws IOException
*/
private File createImageFile() throws IOException {
// Create an image file name
String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
File albumF = getAlbumDir();
File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
return imageF;
}

/**
* Finds the directory holding the camera's photos.
* @return File for the album.
*/
private File getAlbumDir() {
File storageDir = null;

if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(albumName);

if (storageDir != null) {
if (! storageDir.mkdirs()) {
if (! storageDir.exists()){
Log.d("CameraSample", "failed to create directory");
return null;
}
}
}

} else {
Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
}

return storageDir;
}
/*

private String getAlbumName() {
return getString(R.string.album_name);
}
*/


/**
* Takes the photo from the camera application and displays the picture
* on the screen then stores it in a specific gallery.
* @param intent
*/
public void handleCameraPhoto(Intent intent, ImageView photoView) {

if (mCurrentPhotoPath != null) {
mImageView = photoView;
setPic();
//galleryAddPic();
this.currentPhotoPath = mCurrentPhotoPath;
mCurrentPhotoPath = null;
}

}
/**
* Displays pictures on the screen in small box
*/
private void setPic() {

/* There isn't enough memory to open up more than a couple camera photos */
/* So pre-scale the target bitmap into which the file is decoded */

/* Get the size of the ImageView */
//int targetW = ((mImageView.getWidth())/4)*3;
//int targetH = ((mImageView.getHeight())/4)*3;
int targetH = ((mImageView.getHeight()));
int targetW = ((mImageView.getWidth()));

/* Get the size of the image */
BitmapFactory.Options bmOptions = new BitmapFactory.Options();
bmOptions.inJustDecodeBounds = true;
BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
int photoW = bmOptions.outWidth;
int photoH = bmOptions.outHeight;

/* Figure out which way needs to be reduced less */
int scaleFactor = 1;
if ((targetW > 0) || (targetH > 0)) {
scaleFactor = Math.max(photoW/targetW, photoH/targetH);
}

/* Set bitmap options to scale the image decode target */
bmOptions.inJustDecodeBounds = false;
bmOptions.inSampleSize = scaleFactor;
bmOptions.inPurgeable = true;

/* Decode the JPEG file into a Bitmap */
Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

/* Associate the Bitmap to the ImageView */
//mImageView.setImageBitmap(bitmap);
//mImageView.setVisibility(View.VISIBLE);
this.currentImageBitmap = bitmap;
//this.currentImageView = currentImageView.setImageBitmap(bitmap);

}

/**
* Adds the picture to a specific album to be stored separate from the phone's
* camera memory.
*/
public Intent galleryAddPic() {
Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
File f = new File(currentPhotoPath);
Uri contentUri = Uri.fromFile(f);
this.imageURI = contentUri;
mediaScanIntent.setData(contentUri);
//this.sendBroadcast(mediaScanIntent);
return mediaScanIntent;
}

/** Called when the activity is first created. */
@Override
public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.main);

mImageView = (ImageView) findViewById(R.id.imageView1);
mImageBitmap = null;

if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
} else {
mAlbumStorageDirFactory = new BaseAlbumDirFactory();
}
}


// Some lifecycle callbacks so that the image can survive orientation change
@Override
protected void onSaveInstanceState(Bundle outState) {
outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
super.onSaveInstanceState(outState);
}

@Override
protected void onRestoreInstanceState(Bundle savedInstanceState) {
super.onRestoreInstanceState(savedInstanceState);
mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
mImageView.setImageBitmap(mImageBitmap);
mImageView.setVisibility(
savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? 
ImageView.VISIBLE : ImageView.INVISIBLE
);

}

}

