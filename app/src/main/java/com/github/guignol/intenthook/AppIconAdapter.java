package com.github.guignol.intenthook;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AppIconAdapter extends ArrayAdapter<AppInfo> {

    private final PackageManager packageManager;

    public AppIconAdapter(Context context, List<AppInfo> appsList) {
        super(context, R.layout.app_list_row, appsList);
        packageManager = context.getPackageManager();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (null == view) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.app_list_row, null);
        }

        final AppInfo info = getItem(position);
        if (null != info) {
            final TextView appName = (TextView) view.findViewById(R.id.app_name);
            final TextView packageName = (TextView) view.findViewById(R.id.app_package);
            final ImageView icon = (ImageView) view.findViewById(R.id.app_icon);

            appName.setText(info.loadLabel(packageManager));
            packageName.setText(info.packageName());
            icon.setImageDrawable(info.loadIcon(packageManager));
        }
        return view;
    }
}