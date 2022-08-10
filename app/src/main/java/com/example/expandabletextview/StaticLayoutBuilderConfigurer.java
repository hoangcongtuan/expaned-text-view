package com.example.expandabletextview;

import android.text.StaticLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public interface StaticLayoutBuilderConfigurer {
  void configure(@NonNull StaticLayout.Builder builder);
}