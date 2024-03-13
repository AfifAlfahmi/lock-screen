package com.afif.lockscreen;

import android.content.pm.PackageInfo;
import java.util.Comparator;

/* compiled from: lambda */
/* renamed from: v13 */
/* loaded from: classes.dex */
public final /* synthetic */ class v13 implements Comparator {

    /* renamed from: n */
    public static final /* synthetic */ v13 f18645n = new v13();

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int compareTo;
        compareTo = ((PackageInfo) obj).packageName.compareTo(((PackageInfo) obj2).packageName);
        return compareTo;
    }
}