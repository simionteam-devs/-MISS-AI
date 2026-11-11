
package com.missai.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.view.View
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : Activity() {

    private lateinit var messages: LinearLayout
    private lateinit var input: EditText
    private lateinit var scroll: ScrollView

    private val history = mutableListOf<Pair<String, Boolean>>()

    private val prefs by lazy {
        getSharedPreferences("miss_ai_memory", MODE_PRIVATE)
    }

    private val purple = Color.rgb(167, 139, 250)
    private val background = Color.rgb(13, 11, 20)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = background
        window.navigationBarColor = background

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(background)
            setPadding(dp(16), dp(12), dp(16), dp(8))
        }

        // Header
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(8), 0, dp(16))
        }

        val title = TextView(this).apply {
            text = "MISS AI ✦"
            textSize = 27f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
        }

        val subtitle = TextView(this).apply {
            text = "Your personal AI companion"
            textSize = 13f
            setTextColor(purple)
        }

        val status = TextView(this).apply {
            text = "● FOUNDATION PREVIEW  ·  OFFLINE"
            textSize = 10f
            setTextColor(Color.rgb(134, 239, 172))
            setPadding(0, dp(8), 0, 0)
        }

        header.addView(title)
        header.addView(subtitle)
        header.addView(status)
        root.addView(header)

        // Chat area
        scroll = ScrollView(this).apply {
            isFillViewport = true
        }

        messages = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(8), 0, dp(12))
        }

        scroll.addView(messages)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        // Input area
        val bottom = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(10), 0, dp(8))
        }

        input = EditText(this).apply {
            hint = "Talk to Miss..."
            textSize = 15f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
            singleLine = true
            imeOptions = EditorInfo.IME_ACTION_SEND

            background = rounded(
                Color.rgb(35, 30, 47),
                24f
            )

            setPadding(
                dp(16), dp(4), dp(12), dp(4)
            )
        }

        val send = TextView(this).apply {
            text = "➤"
            textSize = 23f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            background = rounded(purple, 24f)
            setPadding(dp(16), dp(10), dp(16), dp(10))
        }

        bottom.addView(
            input,
            LinearLayout.LayoutParams(
                0,
                dp(52),
                1f
            )
        )

        val sendParams = LinearLayout.LayoutParams(
            dp(56),
            dp(52)
        )
        sendParams.marginStart = dp(8)

        bottom.addView(send, sendParams)
        root.addView(bottom)


package com.missai.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : Activity() {

    private lateinit var messages: LinearLayout
    private lateinit var input: EditText
    private lateinit var scroll: ScrollView

    private val prefsName = "miss_ai_memory"

    private val backgroundColor = Color.rgb(17, 15, 29)
    private val purpleColor = Color.rgb(125, 75, 220)
    private val assistantColor = Color.rgb(42, 36, 58)
    private val textColor = Color.WHITE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor

        createInterface()
        loadHistory()
    }

    private fun createInterface() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(backgroundColor)
            setPadding(dp(16), dp(12), dp(16), dp(12))
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(12), dp(8), dp(16))
        }

        val title = TextView(this).apply {
            text = "MISS AI"
            textSize = 28f
            setTextColor(textColor)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Your personal AI companion"
            textSize = 13f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        header.addView(title)
        header.addView(subtitle)

        root.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        scroll = ScrollView(this).apply {
            fillViewport = true
            isSmoothScrollingEnabled = true
        }

        messages = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(8), 0, dp(12))
        }

        scroll.addView(messages)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val bottom = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(10), 0, dp(4))
        }

        input = EditText(this).apply {
            hint = "Message MISS AI..."
            textSize = 15f
            setTextColor(textColor)
            setHintTextColor(Color.GRAY)
            singleLine = true
            imeOptions = EditorInfo.IME_ACTION_SEND
            setPadding(dp(14), dp(12), dp(14), dp(12))
            background = rounded(
                assistantColor,
                24
            )
        }

        val sendButton = Button(this).apply {
            text = "Send"
            textSize = 14f
            setTextColor(Color.WHITE)
            background = rounded(purpleColor, 24)
            isAllCaps = false
            setPadding(dp(12), 0, dp(12), 0)
        }

        bottom.addView(
            input,
            LinearLayout.LayoutParams(
                0,
                dp(52),
                1f
            )
        )

        val buttonParams = LinearLayout.LayoutParams(
            dp(76),
            dp(52)
        )

        buttonParams.marginStart = dp(8)

        bottom.addView(sendButton, buttonParams)

        root.addView(bottom)

        setContentView(root)

        sendButton.setOnClickListener {
            sendMessage()
        }

        input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }
    }

    private fun sendMessage() {

        val message = input.text.toString().trim()

        if (message.isEmpty()) return

        input.setText("")

        addMessage("user", message)
        saveHistory()

        val reply = getStarterReply(message)

        addMessage("assistant", reply)
        saveHistory()
    }

    private fun getStarterReply(message: String): String {

        val text = message.lowercase()

        return when {

            text.contains("hello") ||
            text.contains("hi") ||
            text.contains("hey") ||
            text.contains("hii") -> {
                "Hey! I'm MISS AI. What's on your mind today?"
            }

            text.contains("your name") -> {
                "I'm MISS AI, your personal AI companion."
            }

            text.contains("how are you") -> {
                "I'm running smoothly! Tell me, how is your day going?"
            }

            text.contains("thank") -> {
                "You're welcome! What shall we talk about next?"
            }

            text.contains("who made you") -> {
                "I'm MISS AI. I'm being developed as your personal AI companion."
            }

            text.contains("bye") -> {
                "See you later! I'll be here when you return."
            }

            else -> {
                "I received your message: \"$message\"\n\n" +
                "My real AI intelligence is not connected yet. " +
                "That will be our next development step."
            }
        }
    }

    private fun addMessage(role: String, message: String) {

        val isUser = role == "user"

        val bubble = TextView(this).apply {
            text = message
            textSize = 15f
            setTextColor(textColor)
            setPadding(
                dp(14),
                dp(11),
                dp(14),
                dp(11)
            )
            maxWidth = dp(290)

            background = rounded(
                if (isUser) purpleColor else assistantColor,
                18
            )
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = if (isUser) Gravity.END else Gravity.START
            setPadding(0, dp(5), 0, dp(5))
        }

        row.addView(bubble)

        messages.addView(
            row,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        scroll.post {
            scroll.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    private fun saveHistory() {

        try {
            val array = JSONArray()

            for (i in 0 until messages.childCount) {

                val row = messages.getChildAt(i) as LinearLayout

                if (row.childCount == 0) continue

                val bubble = row.getChildAt(0) as TextView

                val role = if (
                    (bubble.background as? GradientDrawable) != null
                ) {
                    // Determine sender from bubble alignment.
                    if (row.gravity == Gravity.END) "user"
                    else "assistant"
                } else {
                    "assistant"
                }

                val item = JSONObject()
                item.put("role", role)
                item.put("text", bubble.text.toString())

                array.put(item)
            }

            getSharedPreferences(
                prefsName,
                MODE_PRIVATE
            ).edit().putString(
                "messages",
                array.toString()
            ).apply()

        } catch (_: Exception) {
            // Keep the app running if saving fails.
        }
    }

    private fun loadHistory() {

        try {
            val saved = getSharedPreferences(
                prefsName,
                MODE_PRIVATE
            ).getString("messages", "[]") ?: "[]"

            val array = JSONArray(saved)

            for (i in 0 until array.length()) {

                val item = array.getJSONObject(i)

                addMessage(
                    item.optString("role", "assistant"),
                    item.optString("text", "")
                )
            }

        } catch (_: Exception) {
            addMessage(
                "assistant",
                "Hey! I'm MISS AI. Welcome back."
            )
        }

        if (messages.childCount == 0) {
            addMessage(
                "assistant",
                "Hey! I'm MISS AI. I'm ready to chat with you."
            )
        }
    }

    private fun rounded(
        color: Int,
        radius: Int
    ): GradientDrawable {

        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
