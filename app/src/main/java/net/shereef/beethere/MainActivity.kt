package net.shereef.beethere

import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.util.*

const val INSTALLATION = "INSTALLATION"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        Fabric.with(this, Answers())
        var sID = ""
        var isFirstStart = false
        val installation = File(filesDir, INSTALLATION)
        try {
            if (!installation.exists()) {
                isFirstStart = true
                val id = UUID.randomUUID().toString()
                Answers.getInstance().logContentView(ContentViewEvent()
                        .putContentName("Application Start")
                        .putContentType("First Start")
                        .putContentId("MainActivity-onCreate")
                        .putCustomAttribute("id", id))
                val out = FileOutputStream(installation)
                out.write(id.toByteArray())
                out.close()
            } else {
                val f = RandomAccessFile(installation, "r")
                val bytes = ByteArray(f.length().toInt())
                f.readFully(bytes)
                f.close()
                sID = String(bytes)
            }
        } catch (e: Exception) {
            Crashlytics.logException(e)
        }
        if (TextUtils.isEmpty(sID))
            sID = "TEMP" + UUID.randomUUID().toString()
        if (!isFirstStart)
            Answers.getInstance().logContentView(ContentViewEvent()
                    .putContentName("Application Start")
                    .putContentType("Returning User")
                    .putContentId("MainActivity-onCreate")
                    .putCustomAttribute("sID", if (TextUtils.isEmpty(sID)) "N/A" else sID))
        /*todo: Check here if there is a user logged in and direct them to the landing page
          else
          take the user to the login screen
        */
        finish()
    }
}
