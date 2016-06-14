package com.practice.android.criminalintent.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.practice.android.criminalintent.R;
import com.practice.android.criminalintent.data.PictureUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Joseph on 6/13/16.
 */

public class ImageFragment extends DialogFragment {
    private static final String ARG_PHOTO_FILE = "filepath for photo";

    // Inflated views
    private ImageView mImageView;

    public static ImageFragment newInstance(File path) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO_FILE, path);

        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get arguments
        File photoFile = (File) getArguments().getSerializable(ARG_PHOTO_FILE);

        // Inflate view
        View v = inflater.inflate(R.layout.dialog_image, null);
        Bitmap bitmap = null;
        try {
            bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());
        } catch (IOException e) {
            // do nothing
        }

        mImageView = (ImageView) v.findViewById(R.id.photo_zoom_imageView);
        if (bitmap != null) {
            mImageView.setImageBitmap(bitmap);
        }

        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }
}
