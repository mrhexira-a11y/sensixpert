package com.example.sensixpert

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.webkit.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Activity that loads the ZapUPI payment URL in a WebView.
 * Intercepts redirect URLs to determine payment result (success/failed/timeout).
 * Handles UPI deep links, QR download/share, and cancel dialog.
 *
 * Launch with extras:
 *   - EXTRA_PAYMENT_URL: the payment URL to load
 *   - EXTRA_ORDER_ID: the order ID for callback tracking
 *
 * Returns result via setResult():
 *   - RESULT_OK with EXTRA_STATUS ("success"/"failed"/"timeout") and EXTRA_ORDER_ID
 *   - RESULT_CANCELED with EXTRA_STATUS ("cancel") and EXTRA_ORDER_ID
 */
class PaymentWebViewActivity : ComponentActivity() {

    companion object {
        const val EXTRA_PAYMENT_URL = "payment_url"
        const val EXTRA_ORDER_ID = "order_id"
        const val EXTRA_STATUS = "status"

        private const val SUCCESS_URL = "https://zapupi.com/payment?s=s"
        private const val FAILED_URL = "https://zapupi.com/payment?s=f"
        private const val TIMEOUT_URL = "https://zapupi.com/payment?s=t"
    }

    private lateinit var container: FrameLayout
    private lateinit var webView: WebView
    private var currentOrderId: String = ""
    private var callbackFired: Boolean = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val paymentUrl = intent.getStringExtra(EXTRA_PAYMENT_URL)
        currentOrderId = intent.getStringExtra(EXTRA_ORDER_ID) ?: ""

        if (paymentUrl.isNullOrEmpty()) {
            Toast.makeText(this, "Payment URL not found", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        // ── Build UI programmatically ──
        container = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.WHITE)
            fitsSystemWindows = true
        }

        webView = WebView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        container.addView(webView)
        setContentView(container)

        // ── Handle system bar insets for proper padding ──
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(container) { view, insets ->
            val systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ── WebView settings ──
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            textZoom = 90
            builtInZoomControls = false
            displayZoomControls = false
            allowFileAccess = true
            cacheMode = WebSettings.LOAD_DEFAULT
        }

        // ── navigator.share override JS Interface ──
        webView.addJavascriptInterface(object {
            @JavascriptInterface
            fun shareImageToGPay(base64Data: String) {
                Thread {
                    try {
                        var base64 = base64Data
                        if (base64.contains(",")) {
                            base64 = base64.substring(base64.indexOf(",") + 1)
                        }

                        val imageBytes = Base64.decode(base64, Base64.DEFAULT)

                        val cacheDir = this@PaymentWebViewActivity.cacheDir
                        val imageFile = File(cacheDir, "payment_qr_share.png")
                        FileOutputStream(imageFile).use { fos ->
                            fos.write(imageBytes)
                            fos.flush()
                        }

                        val fileUri: Uri = FileProvider.getUriForFile(
                            this@PaymentWebViewActivity,
                            "${this@PaymentWebViewActivity.packageName}.provider",
                            imageFile
                        )

                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/png"
                            putExtra(Intent.EXTRA_STREAM, fileUri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            setPackage("com.google.android.apps.nbu.paisa.user") // GPay
                        }

                        runOnUiThread {
                            try {
                                startActivity(shareIntent)
                            } catch (e: Exception) {
                                shareIntent.setPackage(null)
                                try {
                                    startActivity(
                                        Intent.createChooser(shareIntent, "Share QR")
                                    )
                                } catch (ex: Exception) {
                                    showToast("Share failed")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        showToast("Share failed: ${e.message}")
                    }
                }.start()
            }
        }, "AndroidShare")

        // ── Download listener ──
        webView.setDownloadListener { url, _, _, _, _ ->
            when {
                url.startsWith("data:image") -> saveQRToGallery(url)
                url.startsWith("blob:") -> fetchBlobAsBase64(url)
                else -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(url)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        startActivity(intent)
                    } catch (e: Exception) {
                        showToast("Cannot open file")
                    }
                }
            }
        }

        // ── WebViewClient ──
        webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return url?.let { handleUrl(it) } ?: false
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return request?.url?.toString()?.let { handleUrl(it) } ?: false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url == "about:blank") return

                // Override navigator.share for QR sharing
                view?.evaluateJavascript(
                    """
                    (function() {
                        navigator.canShare = function() { return true; };
                        navigator.share = function(data) {
                            return new Promise(function(resolve, reject) {
                                try {
                                    if (data && data.files && data.files.length > 0) {
                                        var file = data.files[0];
                                        var reader = new FileReader();
                                        reader.onloadend = function() {
                                            AndroidShare.shareImageToGPay(reader.result);
                                            resolve();
                                        };
                                        reader.onerror = function() { reject(new Error('Read failed')); };
                                        reader.readAsDataURL(file);
                                    } else {
                                        reject(new Error('No files'));
                                    }
                                } catch(e) { reject(e); }
                            });
                        };
                    })();
                    """.trimIndent(),
                    null
                )
            }
        }

        webView.webChromeClient = WebChromeClient()

        // ── Back press handling ──
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showCancelDialog()
            }
        })

        // Set status bar black
        setStatusBarBlack()

        // ── Load payment URL ──
        webView.loadUrl(paymentUrl)
    }

    // ═══════════════════════════════════════════════════════════
    // URL HANDLING
    // ═══════════════════════════════════════════════════════════

    private fun handleUrl(url: String): Boolean {
        when {
            url.startsWith(SUCCESS_URL) -> {
                closeAndCallback("success")
                return true
            }
            url.startsWith(FAILED_URL) -> {
                closeAndCallback("failed")
                return true
            }
            url.startsWith(TIMEOUT_URL) -> {
                closeAndCallback("timeout")
                return true
            }
            url.startsWith("upi://") ||
            url.startsWith("paytmmp://") ||
            url.startsWith("phonepe://") ||
            url.startsWith("gpay://") ||
            url.startsWith("tez://") ||
            url.startsWith("intent://") -> {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return true
            }
        }
        return false
    }

    private fun closeAndCallback(type: String) {
        if (callbackFired) return
        callbackFired = true

        runOnUiThread {
            webView.stopLoading()
            restoreStatusBar()

            val resultIntent = Intent().apply {
                putExtra(EXTRA_STATUS, type)
                putExtra(EXTRA_ORDER_ID, currentOrderId)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun cancelPayment() {
        if (callbackFired) return
        callbackFired = true

        runOnUiThread {
            webView.stopLoading()
            restoreStatusBar()

            val resultIntent = Intent().apply {
                putExtra(EXTRA_STATUS, "cancel")
                putExtra(EXTRA_ORDER_ID, currentOrderId)
            }
            setResult(Activity.RESULT_CANCELED, resultIntent)
            finish()
        }
    }

    // ═══════════════════════════════════════════════════════════
    // QR / BLOB HANDLING
    // ═══════════════════════════════════════════════════════════

    private fun fetchBlobAsBase64(blobUrl: String) {
        val js = """
            javascript:(function() {
                var xhr = new XMLHttpRequest();
                xhr.open('GET', '$blobUrl', true);
                xhr.responseType = 'blob';
                xhr.onload = function() {
                    var reader = new FileReader();
                    reader.onloadend = function() {
                        window.Android.onBlobReceived(reader.result);
                    };
                    reader.readAsDataURL(xhr.response);
                };
                xhr.send();
            })();
        """.trimIndent()

        runOnUiThread {
            webView.addJavascriptInterface(object {
                @JavascriptInterface
                fun onBlobReceived(base64Data: String) {
                    saveQRToGallery(base64Data)
                }
            }, "Android")
            webView.loadUrl(js)
        }
    }

    private fun saveQRToGallery(base64Data: String) {
        Thread {
            try {
                var base64 = base64Data
                if (base64.contains(",")) {
                    base64 = base64.substring(base64.indexOf(",") + 1)
                }

                val imageBytes = Base64.decode(base64, Base64.DEFAULT)
                val fileName = "QR_${
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                }.png"

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                        put(
                            MediaStore.Images.Media.RELATIVE_PATH,
                            "${Environment.DIRECTORY_PICTURES}/ZapUPI"
                        )
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }

                    val uri = contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                    )

                    if (uri != null) {
                        contentResolver.openOutputStream(uri)?.use { os ->
                            os.write(imageBytes)
                            os.flush()
                        }
                        values.clear()
                        values.put(MediaStore.Images.Media.IS_PENDING, 0)
                        contentResolver.update(uri, values, null, null)
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val dir = File(
                        Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES
                        ), "ZapUPI"
                    )
                    if (!dir.exists()) dir.mkdirs()

                    val file = File(dir, fileName)
                    FileOutputStream(file).use { fos ->
                        fos.write(imageBytes)
                        fos.flush()
                    }

                    @Suppress("DEPRECATION")
                    val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file))
                    sendBroadcast(scanIntent)
                }

                showToast("QR saved to Gallery!")
            } catch (e: Exception) {
                showToast("Save failed: ${e.message}")
            }
        }.start()
    }

    // ═══════════════════════════════════════════════════════════
    // CANCEL DIALOG
    // ═══════════════════════════════════════════════════════════

    private fun showCancelDialog() {
        val dialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(24), dp(24), dp(16))
        }

        val bgDrawable = GradientDrawable().apply {
            setColor(Color.WHITE)
            cornerRadius = dp(16).toFloat()
        }
        root.background = bgDrawable

        // Title
        val title = TextView(this).apply {
            text = "Cancel Payment?"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTextColor(Color.parseColor("#1C1B1F"))
            typeface = Typeface.DEFAULT_BOLD
        }
        val titleParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, 0, 0, dp(12)) }
        root.addView(title, titleParams)

        // Message
        val message = TextView(this).apply {
            text = "Are you sure you want to cancel this payment?"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.parseColor("#49454F"))
            setLineSpacing(dp(4).toFloat(), 1f)
        }
        val msgParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, 0, 0, dp(24)) }
        root.addView(message, msgParams)

        // Divider
        val divider = View(this).apply {
            setBackgroundColor(Color.parseColor("#E0E0E0"))
        }
        val dividerParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(1)
        ).apply { setMargins(-dp(24), 0, -dp(24), 0) }
        root.addView(divider, dividerParams)

        // Button row
        val btnRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 2f
        }
        val btnRowParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(-dp(24), 0, -dp(24), -dp(16)) }

        // Cancel button
        val btnCancel = TextView(this).apply {
            text = "Cancel"
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.parseColor("#B3261E"))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, dp(16), 0, dp(16))
            setOnClickListener {
                dialog.dismiss()
                cancelPayment()
            }
        }
        val cancelParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)

        // Vertical divider
        val vDivider = View(this).apply {
            setBackgroundColor(Color.parseColor("#E0E0E0"))
        }
        val vDividerParams = LinearLayout.LayoutParams(dp(1), ViewGroup.LayoutParams.MATCH_PARENT)

        // Wait button
        val btnWait = TextView(this).apply {
            text = "Wait"
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.parseColor("#6750A4"))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, dp(16), 0, dp(16))
            setOnClickListener {
                dialog.dismiss()
            }
        }
        val waitParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)

        btnRow.addView(btnCancel, cancelParams)
        btnRow.addView(vDivider, vDividerParams)
        btnRow.addView(btnWait, waitParams)
        root.addView(btnRow, btnRowParams)

        dialog.setContentView(root)

        dialog.window?.let { win ->
            win.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val lp = win.attributes
            lp.width = (resources.displayMetrics.widthPixels * 0.85f).toInt()
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
            win.attributes = lp
        }

        dialog.show()
    }

    // ═══════════════════════════════════════════════════════════
    // UTILITY
    // ═══════════════════════════════════════════════════════════

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density + 0.5f).toInt()
    }

    private fun showToast(msg: String) {
        runOnUiThread {
            Toast.makeText(this@PaymentWebViewActivity, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setStatusBarBlack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.BLACK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = 0
            }
        }
    }

    private fun restoreStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val originalColor = getThemeStatusBarColor()
            window.statusBarColor = originalColor
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var flags = window.decorView.systemUiVisibility
                if (isColorLight(originalColor)) {
                    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
                window.decorView.systemUiVisibility = flags
            }
        }
    }

    private fun getThemeStatusBarColor(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val typedValue = TypedValue()
            theme.resolveAttribute(android.R.attr.statusBarColor, typedValue, true)
            if (typedValue.data != 0) return typedValue.data
            theme.resolveAttribute(android.R.attr.colorPrimaryDark, typedValue, true)
            if (typedValue.data != 0) return typedValue.data
        }
        return Color.BLACK
    }

    private fun isColorLight(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) +
                0.587 * Color.green(color) +
                0.114 * Color.blue(color)) / 255
        return darkness < 0.5
    }

    override fun onDestroy() {
        webView.stopLoading()
        webView.destroy()
        super.onDestroy()
    }
}
