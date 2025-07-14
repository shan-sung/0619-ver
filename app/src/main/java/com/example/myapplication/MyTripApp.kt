package com.example.myapplication

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application()

// 整段程式碼意思
// 告訴 Hilt：「我要用依賴注入，請從 MyApplication 開始建立你的 DI 容器，這樣我就能在整個 App 中使用 @Inject 來取得我需要的物件了。」

// 看不懂 ><