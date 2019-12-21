import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.concurrent.atomic.AtomicInteger

class PermisssionReq constructor(val any: Any) {
    companion object {
        const val TAG = "PermissionReq"
        val sRequestCode = AtomicInteger()
        val sManifestPermissionSet = mutableSetOf<String>()
        fun with(activity: Activity): PermisssionReq = PermisssionReq(activity)
        fun with(fragment: Fragment): PermisssionReq = PermisssionReq(fragment)
    }

    interface Result {
        fun onGranted()
        fun onDenied()
    }

    private lateinit var mPermissions: Array<String>
    private lateinit var mResult: Result
    fun permissions(vararg permissions: String) = apply {
        mPermissions = permissions as Array<String>
    }

    fun result(result: Result):PermisssionReq = apply {
        mResult = result
    }

    fun request() {
        val activity = getActivity() ?: return
        //获取声明文件权限
        initManifestPermission(activity)

        //请求权限和声明文件不匹配
        for (permission in mPermissions) {
            if (!sManifestPermissionSet.contains(permission)) {
                mResult.onDenied()
                return
            }
        }

        val deniedPermissionList = getDeniedPermission(activity, mPermissions)


    }

    private fun getActivity(): Activity? = when (any) {
        is Activity -> any
        is Fragment -> any.activity
        else -> null
    }

    private fun initManifestPermission(context: Context?) {
        //TOOD 获取声明文件中的权限
    }

    private fun getDeniedPermission(context: Context , permissions: Array<String>): List<String> {
        val deniedPermissionList = mutableListOf<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissionList.add(permission)
            }
        }
        return deniedPermissionList
    }
}