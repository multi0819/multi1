package com.multi0819.travelarchive

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.widget.FrameLayout
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Base64
import android.webkit.*
import android.widget.Toast
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

class MainActivity : Activity() {
 private lateinit var web: WebView
 private var chooser: ValueCallback<Array<Uri>>? = null
 private var exportFile: File?=null
 private var exportStream: FileOutputStream?=null
 private var exportName="travel-backup.json"
 private var exportMime="application/json"
 private var saving=false
 private val origin="https://appassets.androidplatform.net/"
 override fun onCreate(state: Bundle?) {
  super.onCreate(state)
  window.statusBarColor=Color.rgb(247,245,239)
  window.navigationBarColor=Color.rgb(255,254,250)
  window.decorView.systemUiVisibility=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
  web=WebView(this)
  val host=FrameLayout(this);host.setBackgroundColor(Color.rgb(247,245,239))
  host.addView(web,FrameLayout.LayoutParams(-1,-1));setContentView(host)
  if(Build.VERSION.SDK_INT>=35) {
   host.setOnApplyWindowInsetsListener { view,insets ->
    val safe=insets.getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout() or WindowInsets.Type.ime())
    view.setPadding(safe.left,safe.top,safe.right,safe.bottom);insets
   };host.requestApplyInsets()
  }
  web.settings.apply {
   javaScriptEnabled=true;domStorageEnabled=true;allowFileAccess=false;allowContentAccess=true
   mixedContentMode=WebSettings.MIXED_CONTENT_NEVER_ALLOW
   userAgentString=userAgentString+" TravelArchive/1.0 (+https://github.com/multi0819/multi1)"
  }
  web.addJavascriptInterface(Bridge(),"AndroidTravel")
  web.webViewClient=object:WebViewClient() {
   override fun shouldInterceptRequest(view:WebView,request:WebResourceRequest):WebResourceResponse? {
    val uri=request.url
    if(uri.scheme=="https" && uri.host=="appassets.androidplatform.net") {
     val name=uri.path?.removePrefix("/")?.ifEmpty { "index.html" } ?: "index.html"
     if(name.matches(Regex("[a-zA-Z0-9._-]+"))) try {
      val mime=when { name.endsWith(".js")->"application/javascript";name.endsWith(".html")->"text/html";name.endsWith(".css")->"text/css";name.endsWith(".jpg")->"image/jpeg";name.endsWith(".png")->"image/png";name.endsWith(".svg")->"image/svg+xml";else->"application/octet-stream" }
      return WebResourceResponse(mime,"UTF-8",assets.open(name))
     } catch(_:Exception) {}
    }
    if(uri.scheme=="https" && uri.host in listOf("a.tile.openstreetmap.org","b.tile.openstreetmap.org","c.tile.openstreetmap.org","tile.openstreetmap.org")) return null
    return WebResourceResponse("text/plain","UTF-8",404,"Not Found",emptyMap(),"".byteInputStream())
   }
   override fun shouldOverrideUrlLoading(view:WebView,request:WebResourceRequest):Boolean {
    val uri=request.url
    if(uri.toString()==origin+"index.html")return false
    if(request.hasGesture() && uri.scheme=="https" && uri.host=="www.openstreetmap.org")try { startActivity(Intent(Intent.ACTION_VIEW,uri)) } catch(_:Exception){}
    return true
   }
  }
  web.webChromeClient=object:WebChromeClient() {
   override fun onJsAlert(v:WebView,url:String,message:String,result:JsResult):Boolean {
    AlertDialog.Builder(this@MainActivity).setMessage(message).setPositiveButton("확인") { _,_->result.confirm() }.setOnCancelListener { result.cancel() }.show();return true
   }
   override fun onJsConfirm(v:WebView,url:String,message:String,result:JsResult):Boolean {
    AlertDialog.Builder(this@MainActivity).setMessage(message).setPositiveButton("확인") { _,_->result.confirm() }.setNegativeButton("취소") { _,_->result.cancel() }.setOnCancelListener { result.cancel() }.show();return true
   }
   override fun onShowFileChooser(v:WebView,callback:ValueCallback<Array<Uri>>,params:FileChooserParams):Boolean {
    chooser?.onReceiveValue(null);chooser=callback
    val image=params.acceptTypes.any { it.startsWith("image/") }
    val intent=Intent(Intent.ACTION_OPEN_DOCUMENT).apply { addCategory(Intent.CATEGORY_OPENABLE);type=if(image) "image/*" else "*/*";putExtra(Intent.EXTRA_ALLOW_MULTIPLE,params.mode==FileChooserParams.MODE_OPEN_MULTIPLE) }
    try { startActivityForResult(intent,100) } catch(_:Exception) { chooser?.onReceiveValue(null);chooser=null;toast("파일 선택기를 열 수 없습니다.") };return true
   }
  }
  web.loadUrl(origin+"index.html")
 }
 private fun toast(text:String) { runOnUiThread { Toast.makeText(this,text,Toast.LENGTH_LONG).show() } }
 inner class Bridge {
  @JavascriptInterface @Synchronized fun saveStart(name:String,mime:String) {
   if(saving) throw IllegalStateException("Save already active")
   exportName=name.replace(Regex("[^a-zA-Z0-9._-]"),"_").take(100);exportMime=mime
   val file=File(cacheDir,"travel-export-${System.nanoTime()}");val stream=FileOutputStream(file)
   exportFile=file;exportStream=stream;saving=true
  }
  @JavascriptInterface @Synchronized fun saveChunk(data:String) { val stream=exportStream ?: throw IllegalStateException("No active export");stream.write(Base64.decode(data,Base64.NO_WRAP)) }
  @JavascriptInterface @Synchronized fun saveAbort() { exportStream?.close();exportStream=null;exportFile?.delete();exportFile=null;saving=false }
  @JavascriptInterface @Synchronized fun saveFinish() {
   val stream=exportStream ?: throw IllegalStateException("No active export");stream.close();exportStream=null
   runOnUiThread { try { startActivityForResult(Intent(Intent.ACTION_CREATE_DOCUMENT).apply { addCategory(Intent.CATEGORY_OPENABLE);type=exportMime;putExtra(Intent.EXTRA_TITLE,exportName) },101) }
    catch(_:Exception) { saveAbort();toast("저장 창을 열 수 없습니다.") } }
  }
  @JavascriptInterface fun printPage() { runOnUiThread {
   val attributes=PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).setMinMargins(PrintAttributes.Margins.NO_MARGINS).setColorMode(PrintAttributes.COLOR_MODE_COLOR).build()
   (getSystemService(PRINT_SERVICE) as PrintManager).print("여행기록",web.createPrintDocumentAdapter("여행기록"),attributes)
  } }
 }
 override fun onActivityResult(request:Int,result:Int,data:Intent?) {
  super.onActivityResult(request,result,data)
  if(request==100) {
   val uris=if(result==RESULT_OK) data?.clipData?.let { clip -> Array(clip.itemCount) { clip.getItemAt(it).uri } } ?: data?.data?.let { arrayOf(it) } else null
   chooser?.onReceiveValue(uris);chooser=null
  } else if(request==101) {
   val file=exportFile;val uri=if(result==RESULT_OK) data?.data else null
   if(uri!=null && file!=null) Thread {
    try { val out=contentResolver.openOutputStream(uri,"wt") ?: throw IllegalStateException("No output stream")
     out.use { target -> file.inputStream().use { it.copyTo(target) } };toast("백업 파일을 저장했습니다.")
    } catch(_:Exception) { toast("파일 저장에 실패했습니다. 다시 시도해 주세요.") }
    finally { synchronized(this) { file.delete();exportFile=null;saving=false } }
   }.start() else { synchronized(this) { file?.delete();exportFile=null;saving=false } }
  }
 }
 @Deprecated("Supported for Android 8+") override fun onBackPressed() {
  web.evaluateJavascript("window.travelBack ? window.travelBack() : false") { handled -> if(handled!="true")finish() }
 }
 override fun onDestroy() { chooser?.onReceiveValue(null);exportStream?.close();web.destroy();super.onDestroy() }
}
