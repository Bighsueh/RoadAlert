package tw.edu.nfu.hsueh.roadalert.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "這裡可以新增路況報警"
    }
    val text: LiveData<String> = _text
}