import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.util.SparseArray
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.concurrent.atomic.AtomicInteger

class PermissionReq constructor(private val any: Any) {
    companion object {
        const val TAG = "PermissionReq"
        private val sRequestCode = AtomicInteger(0)
        private val sManifestPermissionSet = mutableSetOf<String>()
        private val sResultArray: SparseArray<Result> = SparseArray()
        fun with(activity: Activity): PermissionReq = PermissionReq(activity)
        fun with(fragment: Fragment): PermissionReq = PermissionReq(fragment)
        fun onRequestPermissionResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            Log.d(TAG, permissions.toString())
            val result = sResultArray[requestCode] ?: return
            sResultArray.remove(requestCode)
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    result.onDenied()
                    return
                }
            }
            result.onGranted()
        }
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

    fun result(result: Result): PermissionReq = apply {
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

        //获取未授权的权限
        val deniedPermissionList = getDeniedPermission(activity, mPermissions)
        if (deniedPermissionList.isEmpty()) {
            mResult.onGranted()
            return
        }

        //请求
        val requestCode = sRequestCode.incrementAndGet()
        requestPermissions(activity, deniedPermissionList.toTypedArray(), requestCode)
        sResultArray.put(requestCode, mResult)
    }

    private fun getActivity(): Activity? = when (any) {
        is Activity -> any
        is Fragment -> any.activity
        else -> null
    }

    private fun initManifestPermission(context: Context) {
        //获取声明文件中的权限
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_PERMISSIONS
        )
        val permissions= packageInfo.requestedPermissions
        sManifestPermissionSet.addAll(permissions)
    }

    private fun getDeniedPermission(context: Context, permissions: Array<String>): List<String> {
        val deniedPermissionList = mutableListOf<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                deniedPermissionList.add(permission)
            }
        }
        return deniedPermissionList
    }

    private fun requestPermissions(
        activity: Activity,
        permissions: Array<out String>,
        requestCode: Int
    ) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }
}