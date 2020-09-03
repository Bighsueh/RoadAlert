package tw.edu.nfu.hsueh.roadalert.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "歡迎使用路況報警系統！！"
    }
    val text: LiveData<String> = _text
}