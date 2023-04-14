import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.examples.poseestimation.R

class Video : AppCompatActivity(){

    private val REQUEST_VIDEO_PICK = 1001;
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_VIDEO_PICK)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        val videoView = findViewById<VideoView>(R.id.videoView)

        if (requestCode == REQUEST_VIDEO_PICK && resultCode == RESULT_OK && data != null) {
            val videoUri = data.data
            videoView.setVideoURI(videoUri)
            videoView.start()
        }
    }



}