package com.github.guignol.intenthook;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public interface AppInfo {
    CharSequence loadLabel(PackageManager pm);

    Drawable loadIcon(PackageManager pm);

    String packageName();
}
