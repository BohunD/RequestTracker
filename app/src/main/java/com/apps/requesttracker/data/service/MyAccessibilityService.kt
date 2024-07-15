package com.apps.requesttracker.data.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.apps.requesttracker.domain.repository.QueryRepository
import com.apps.requesttracker.data.db.QueryEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class MyAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var queryRepository: QueryRepository

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var lastCapturedUrl: String? = null
    private var canUpdate: Boolean = false
    private var currentBrowser: String? = null
    private var lastRecordId: Int? = null

    override fun onServiceConnected() {
        val info = serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_VISUAL
            notificationTimeout = 300
        }
        serviceInfo = info
    }

    override fun onDestroy() {
        coroutineScope.cancel()
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (event == null) return
        val source = event.source ?: return
        val packageName = event.packageName?.toString() ?: return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            traverseNodeTree(source)
        }

        val viewId = source.viewIdResourceName
        viewId?.let {

            val capturedUrl = captureUrl(source, it)

            if (capturedUrl == null) {
                traverseNodeTree(event.source!!)
            } else {
                handleCapturedUrl(capturedUrl, packageName)
            }

            source.recycle()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun traverseNodeTree(node: AccessibilityNodeInfo) {
        val stack = mutableListOf(node)
        while (stack.isNotEmpty()) {
            val currentNode = stack.removeAt(stack.size - 1)
            val url = currentNode.text?.toString()
            if (url != null) {
                handleCapturedUrl(url, currentBrowser ?: return)
                break
            }
            for (i in 0 until currentNode.childCount) {
                val childNode = currentNode.getChild(i)
                if (childNode != null) {
                    stack.add(childNode)
                }
            }
        }
    }

    private fun handleCapturedUrl(capturedUrl: String, packageName: String) {
        if (capturedUrl != lastCapturedUrl) {
            if (isValidSearchQuery(capturedUrl)) {

                saveQuery(capturedUrl)
                lastCapturedUrl = capturedUrl

            } else if (currentBrowser == packageName && canUpdate && isValidWebsite(capturedUrl)) {
                updateLastQueryWithWebsite(capturedUrl)
            }
            currentBrowser = packageName
        }
    }

    private fun captureUrl(info: AccessibilityNodeInfo, viewId: String): String? {
        val nodes = info.findAccessibilityNodeInfosByViewId(viewId)
        if (nodes.isNullOrEmpty()) {
            return null
        }

        val addressBarNodeInfo = nodes[0]
        val url = addressBarNodeInfo.text?.toString()
        addressBarNodeInfo.recycle()
        return url
    }

    private fun isValidSearchQuery(url: String): Boolean {
        return url.contains("search?q=") && url.contains("google.com")
    }

    private fun isValidWebsite(url: String): Boolean {
        return android.util.Patterns.WEB_URL.matcher(url).matches()
    }

    private fun saveQuery(url: String) {
        val query = QueryEntity(
            requestText = url,
            requestDateTime = System.currentTimeMillis(),
            websiteLink = "null"
        )
        coroutineScope.launch {
            queryRepository.insert(query)
            lastRecordId = queryRepository.getLastQuery()?.id
        }.invokeOnCompletion {
            canUpdate = true
        }
    }

    private fun updateLastQueryWithWebsite(websiteLink: String) {
        lastRecordId?.let { id ->
            coroutineScope.launch {
                queryRepository.updateWebsiteLink(id, websiteLink)
            }
        }
        canUpdate = false
    }

    override fun onInterrupt() {
        Log.d("ACCESSIBILITY_SERVICE", "INTERRUPT")
    }
}
