package com.ikramjamro.chitchat

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp  // It make to enable the dependency on all the application

class CCApplication : Application()