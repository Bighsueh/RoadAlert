package tw.edu.nfu.hsueh.roadalert.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "這裡可以查看目前路況狀態"
    }
    val text: LiveData<String> = _text
}