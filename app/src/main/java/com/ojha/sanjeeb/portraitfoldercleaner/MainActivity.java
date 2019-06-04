package com.ojha.sanjeeb.portraitfoldercleaner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final int READ_REQUEST_CODE = 1000;
    private static final String TAG = "GoogleActivity";
    public EditText editText;
    public EditText folderText;
    public List<File> emptyFolders = new ArrayList<File>();
    private List<String> folders;
    private File root;

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                showProgressDialog();

                Uri uri = data.getData();
                log("Selected Document is : " + uri.toString());
                DocumentFile documentFile = DocumentFile.fromTreeUri(this, uri);
                if (documentFile.isDirectory()) {

                    if (isExternalStorageDocument(uri)) {
                        String path = uri.getPath();
                        String folder = path.split(":")[1];
                        String finalpath = Environment.getExternalStorageDirectory() + "/" + folder;

                        log("Absolute path=" + finalpath);
                        folderText.setText(finalpath);
                        scanFolders();
                    }


                }
                hideProgressDialog();
            }

        }
    }

    public void log(String MSG) {
        Log.d(TAG, MSG);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.result);
        folderText = findViewById(R.id.text_folder);
        findViewById(R.id.btn_deleteAllFolders).setOnClickListener(this);
        findViewById(R.id.btn_exit).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.btn_SelectFolder).setOnClickListener(this);
        scanFolders();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_exit) {
            this.finish();
        } else if (i == R.id.btn_deleteAllFolders) {
            DeleteFolders();
        } else if (i == R.id.btn_clear) {
            ClearAll();
        } else if (i == R.id.disconnectButton) {
            // revokeAccess();
        } else if (i == R.id.btn_SelectFolder) {
            PerformFileSearch();
        } else if (i == R.id.scanFolder) {
            scanFolders();
        }
    }

    private void PerformFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        // intent.setDataAndType(Uri.fromFile(new File(folderText.getText().toString())),"resource/folder");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    private void ClearAll() {
        editText.setText("");
        emptyFolders.clear();
        scanFolders();
    }


    protected void DeleteFolders() {
        editText.setText("");
        if (!emptyFolders.isEmpty()) {
            int fileCount = emptyFolders.size();
            for (File file : emptyFolders) {
                String deletingFileName = file.getAbsolutePath();
                DeleteFolder(file);
                editText.append(deletingFileName + "(DELETED)");
            }
            emptyFolders.clear();
            editText.append("\nTotal " + String.valueOf(fileCount) + " folder(s) has been deleted.");
        } else {
            editText.setText("There's no emptly folder inside the selected folder.");
        }

    }

    public void scanFolders() {
        try {
            showProgressDialog();
            editText.setText("");
            editText.append("Scanning folders:");
            String folderLocation = folderText.getText().toString();
            root = new File(folderLocation);
            Log.d(TAG, "Checking file " + folderLocation);
            File[] files = root.listFiles();
            for (int i = 0; i < files.length; i++) {

                if (files[i].isDirectory()) {
                    editText.append("\n" + files[i].getName());
                    File[] anotherFile = files[i].listFiles();
                    if (anotherFile == null) {
                        Log.e(TAG, "EMPTY FOLDER Found!" + files[i].getAbsolutePath());
                    } else if (anotherFile.length == 0) {
                        //Blank Folder found
                        Log.d(TAG, String.valueOf(anotherFile.length));
                        //Delete this folder
                        emptyFolders.add(files[i]);
                    }
                }
            }
            hideProgressDialog();
        } catch (Exception ex0) {
            Log.e(TAG, ex0.getMessage() + ex0.getStackTrace());
        }
        //File firstFolder = new File(root.listFiles()[8].getPath());
        // editText.append(("FIrst FOlder: " + firstFolder.getName()));
        //  editText.append("Music absolutepath: " + firstFolder.getAbsolutePath());


    }

    private void DeleteFolder(File blankFolder) {
        String folderPath = blankFolder.getAbsolutePath();
        try {
            blankFolder.delete();
            Log.i(TAG, "Empty folder deleted: " + folderPath);
        } catch (Exception ex) {
            Log.e(TAG, "Cannot delete folder " + folderPath + ", \n" + ex.getMessage());

        }
    }


}
